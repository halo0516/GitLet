package gitlet;

import java.io.*;
import java.nio.file.Files;
import java.util.TreeMap;

/**
 The Branch class represents the branches of a Git repository.
 It contains methods to create and remove branches.
 Branches are stored in a TreeMap with the branch name as the key and the commit ID as the value.
 The class also contains instance variables for the current directory, the path to the branch data file, the HEAD pointer,
 and the path to the file containing the branch pointers.
 * @author: Zitong Shi
 **/
public class Branch {
    String currentDirectory = System.getProperty("user.dir");
    String serializablePath = currentDirectory + "/.gitlet/branch";
    String head = "HEAD";
    String pointersPath = serializablePath + "/pointers.txt";

    /**
     * Creates a new branch with the given name and points it to the current commit.
     * If a branch with the given name already exists, nothing is done.
     * The branch data is stored in a TreeMap with the branch name as the key and the commit ID as the value.
     * The method also creates a joint name between the new branch and the current branch and stores it in the TreeMap.
     * Lastly, the method copies the latest log file from the current branch to the new branch, if it exists.
     *
     * @param branchName The name of the branch to be created.
     */
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
    /**
     * Removes the branch with the given name from the repository.
     * The method removes the branch from the TreeMap containing the branch data.
     * It also deletes the log file associated with the branch, if it exists.
     *
     * @param branchName The name of the branch to be removed.
     */
    public void rmBranch(String branchName) {
        TreeMap<String, String> commitPointers = getInput(pointersPath);
        commitPointers.remove(branchName);
        String deletePath = serializablePath + "/removedMark" + branchName + ".txt";
        File file = new File(deletePath);
        file.delete();

        writeOutput(commitPointers, pointersPath);
    }
    /**
     * Reads the branch data from a file and returns it as a TreeMap.
     * The branch data is stored in a file specified by the filePath parameter.
     * The TreeMap contains the branch names as keys and the commit IDs as values.
     *
     * @param filePath The path to the file containing the branch data.
     * @return A TreeMap containing the branch names and commit IDs.
     */
    public TreeMap<String, String> getInput(String filePath) {
        TreeMap<String, String> commitPointers = new TreeMap<>();

        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(
                    new FileInputStream(filePath));
            commitPointers = (TreeMap<String, String>) objectInputStream.readObject();
            objectInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return  commitPointers;
    }

    /**
     * Writes the branch data to a file.
     * The branch data is stored in a TreeMap specified by the commitPointers parameter.
     * The TreeMap contains the branch names as keys and the commit IDs as values.
     * The data is written to a file specified by the filePath parameter.
     *
     * @param commitPointers A TreeMap containing the branch names and commit IDs.
     * @param filePath The path to the file where the branch data will be written.
     */
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