package gitlet;

import java.io.*;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.TreeMap;

public class Reset {
    String currentDirectory = System.getProperty("user.dir");
    String serializablePath = currentDirectory + "/.gitlet/Serialized";
    String commitPath = currentDirectory + "/.gitlet/Commits";
    String stagingPath = currentDirectory + "/.gitlet/Staging Area";
    String pointersPath = serializablePath +"/pointers.txt";
    String head = "HEAD";
    public void reset(String commitID) {
        TreeMap<String, String> commitPointers = getInput(pointersPath);

        //check the commitID exits && get the fullID;
        File[] commitFiles = new File(commitPath).listFiles();
        boolean hasCommit = false;
        String commitFullID = "";
        for (File curr:commitFiles) {
            String shortID = curr.getName().substring(0, commitID.length());
            if(shortID.equals(commitID)) {
                hasCommit = true;
                commitFullID = curr.getName();
                break;
            }
        }

        if(hasCommit == false) {
            System.out.println("commit doesn't exist");
            return;
        }

        //delete all files in the staging area
        File[] stagingFiles = new File(stagingPath).listFiles();
        for (int i = 0; i < stagingFiles.length; i++) {
            stagingFiles[i].delete();
        }


        String currCommitID = commitPointers.get(commitPointers.get(head));
        File currCommitPPath = new File(commitPath + "/" + currCommitID);
        File newCommitPath = new File(commitPath + "/" + commitFullID);

        //delete files from work directory under the current commit
        for(File file: currCommitPPath.listFiles()) {
            if(!isUtilFile(file)) {
                File workDirectoryFile = new File(currentDirectory + "/" + file.getName());
                workDirectoryFile.delete();
            }
        }

        //copy files from the reset commit  to the work directory
        for(File file: currCommitPPath.listFiles()) {
            if(isUtilFile(file)) {
                continue;
            }
            // files copy overwite the file at the destination
            File workDirectoryFile = new File(currentDirectory + "/" + file.getName());
            try {
                Files.copy(file.toPath(),workDirectoryFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //update the reset commitID to the tree
        commitPointers.put(commitPointers.get(head), commitFullID);
        //output the source
        writeOutput(commitPointers, pointersPath);
    }

    private boolean isUtilFile(File file) {
        String fileName = file.getName();
        HashSet<String> utilFileNames = new HashSet<>();
        utilFileNames.add("logMessage.txt");
        utilFileNames.add("timeStamp.txt");
        utilFileNames.add("parentHash.txt");
        return utilFileNames.contains(fileName);
    }
    
    public TreeMap<String, String> getInput(String filePath) {
        TreeMap<String, String> commitPointers = new TreeMap<>();

        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(filePath));
            commitPointers = (TreeMap<String, String>)  objectInputStream.readObject();
            objectInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
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
