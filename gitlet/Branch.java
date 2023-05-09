package gitlet;

import java.io.*;
import java.nio.file.Files;
import java.util.TreeMap;

/** Branch class for Gitlet, responsible for creating and removing branches.
 *
 * @author Zitong Shi
 */

public class Branch {
    String currentDirectory = System.getProperty("user.dir");
    String serializablePath = currentDirectory + "/.gitlet/branch";
    String head = "HEAD";
    String pointersPath = serializablePath + "/pointers.txt";

    public void branch(String branchName) {

        TreeMap<String, String> commitPointers = getInput(pointersPath);

        if (commitPointers.containsKey(branchName)) {
            System.out.println("current branch already exists");
            return;
        }

        String headBranchName = commitPointers.get(head);
        String currentCommit = commitPointers.get(headBranchName);
        commitPointers.put(branchName, currentCommit);
        String jointName = "";

        if (headBranchName.compareTo(branchName) < 0) {
            jointName = headBranchName + "/" + branchName;
        } else {
            jointName = branchName + "/" + headBranchName;
        }
        commitPointers.put(jointName, currentCommit);

        String latestLogPath = serializablePath + "/removedMark" + headBranchName + ".txt";
        String newPath = serializablePath + "/removedMark" + headBranchName + ".txt";
        if (new File(latestLogPath).exists()) {
            try {
                Files.copy(new File(latestLogPath).toPath(),
                        new File(newPath).toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        writeOutput(commitPointers, pointersPath);
    }

    public void rmBranch(String branchName) {
        TreeMap<String, String> commitPointers = getInput(pointersPath);
        commitPointers.remove(branchName);
        String deletePath = serializablePath + "/removedMark" + branchName + ".txt";
        File file = new File(deletePath);
        file.delete();

        writeOutput(commitPointers, pointersPath);
    }

    public TreeMap<String, String> getInput(String filePath) {
        TreeMap<String, String> commitPointers = new TreeMap<>();

        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(
                    new FileInputStream(filePath));
            commitPointers = (TreeMap<String, String>) objectInputStream.readObject();
            objectInputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return  commitPointers;
    }

    public void writeOutput(TreeMap<String, String> commitPointers, String filePath) {
        try {
            ObjectOutputStream objectOutputStream =
                    new ObjectOutputStream(new FileOutputStream(filePath));
            objectOutputStream.writeObject(commitPointers);
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
