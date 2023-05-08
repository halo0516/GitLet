package gitlet;

import java.io.*;
import java.util.*;

public class Log {
    static File commitPath = new File(System.getProperty("user.dir") + "/.gitlet/commits");
    static File ptrPath = new File(System.getProperty("user.dir") + "/.gitlet/branch/pointers.txt");
    TreeMap<String, String> ptrs;
    String currentBranch;
    String currentCommit;
    String parentID;
    String commitFile;
    String logMsg;
    String timeStamp;

    public void log() {
        // If the Commit directory exists and is not empty, proceed
        if (commitPath.listFiles() != null) {
            try {
                // Load the pointers map from the HEAD pointer file
                FileInputStream input = new FileInputStream(ptrPath);
                ObjectInputStream objInput = new ObjectInputStream(input);
                this.ptrs = (TreeMap) objInput.readObject();
                input.close();
                objInput.close();
            } catch (IOException | ClassNotFoundException e) {
                return;
            }

            // Get the current branch and commit ID from the pointers map
            this.currentBranch = ptrs.get("HEAD");
            this.currentCommit = ptrs.get(currentBranch);

            // Load the parent commit ID from the parentHash.txt file of the current commit
            File optPath = new File(commitPath + "/" + currentCommit + "/parentHash.txt");
            if (optPath.exists()) {
                this.parentID = new String(Utils.readContents(optPath));
            }

            // Loop through the parent commits and print out their information
            while (optPath.exists()) {
                this.logMsg = new String(Utils.readContents(
                    new File(commitPath + "/" + currentCommit + "/logMsg.txt")));
                this.timeStamp = new String(Utils.readContents(
                    new File(commitPath + "/" + currentCommit + "/timeStamp.txt")));
                System.out.println("=============");
                System.out.println("Commit: " + currentCommit);
                System.out.println("TimeStamp: " + timeStamp);
                System.out.println("Log Message: " + logMsg);
                System.out.println();

                // Update the current commit ID to the parent commit ID
                currentCommit = parentID;
                if (optPath.exists()) {
                    parentID = new String(Utils.readContents(optPath));
                }
            }

            this.logMsg = new String(Utils.readContents(
                new File(commitFile + "/" + currentCommit + "/logMsg.txt")));
            this.timeStamp = new String(Utils.readContents(
                new File(commitFile + "/" + currentCommit + "/timeStamp.txt")));
            System.out.println("=============");
            System.out.println("Commit: " + currentCommit);
            System.out.println("TimeStamp: " + timeStamp);
            System.out.println("Log Message: " + logMsg);
            System.out.println();
        }
    }
    public void globallog() {
        // If the Commit directory exists and is not empty, proceed
        if (commitPath.listFiles() != null) {
            // Loop through all the commit directories and print out their information
            for (File commit : commitPath.listFiles()) {
                this.logMsg = new String(Utils.readContents(
                    new File(commit + "/logMsg.txt")));
                this.timeStamp = new String(Utils.readContents(
                    new File(commit + "/timeStamp.txt")));
                System.out.println("=============");
                System.out.println("Commit: " + commit.getName());
                System.out.println("TimeStamp: " + timeStamp);
                System.out.println("Log Message: " + logMsg);
                System.out.println();
            }
        }
    }


}
