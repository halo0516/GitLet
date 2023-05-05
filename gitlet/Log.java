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
        if (commitPath.listFiles() != null) {
            try {
                FileInputStream input = new FileInputStream(ptrPath);
                ObjectInputStream objInput = new ObjectInputStream(input);
                this.ptrs = (TreeMap) objInput.readObject();
                input.close();
                objInput.close();
            } catch (IOException | ClassNotFoundException e) {
                return;
            }
            this.currentBranch = ptrs.get("HEAD");
            this.currentCommit = ptrs.get(currentBranch);

            File optPath = new File(commitPath + "/" + currentCommit + "/parentHash.txt");
            if (optPath.exists()) {
                this.parentID = new String(Utils.readContents(optPath));
            }
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
        if (commitPath.listFiles() != null) {
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
