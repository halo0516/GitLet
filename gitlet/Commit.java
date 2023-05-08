package gitlet;

import java.text.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.nio.file.Files;
import java.util.Date;

/** Represents a gitlet commit object.
 *  does at a high level.
 *
 *  @author Yuxiang Wang
 */
public class Commit implements Serializable {
    static File commitPath = new File(System.getProperty("user.dir") + "/.gitlet/commits");
    static File serialPath = new File(System.getProperty("user.dir") + "/.gitlet/branch");
    static File stagePath = new File(System.getProperty("user.dir") + "/.gitlet/stage");
    static File removePath = new File(System.getProperty("user.dir") + "/.gitlet/remove");
    File prevFile;
    String logMsg;
    String timeStamp;
    String parentHash;
    String commitHash;
    TreeSet<String> stageSet = new TreeSet<>();
    TreeMap<String, String> ptrs;
    TreeSet<String> removedMark;
    TreeSet<String> trace;


    public String getMessage() {
        return "Commit{"
                + "logMessage= '" + logMsg + '\''
                + ", timeStamp= " + timeStamp + '\''
                + ", parentHash= " + parentHash + '\''
                + '\'' + '}';
    }

    /**
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    public Commit(String message, Boolean init) {
        this.logMsg = message;

        DateFormat dateF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.timeStamp = dateF.format(new Date());

        try {
            ObjectInputStream input = new ObjectInputStream(
                new FileInputStream(serialPath + "/pointers.txt"));
            this.ptrs = (TreeMap<String, String>) input.readObject();
            input.close();
        } catch (IOException | ClassNotFoundException e) {
            this.ptrs = new TreeMap<>();
        }

        if (ptrs.containsKey("HEAD")) {
            this.parentHash = ptrs.get(ptrs.get("HEAD"));
        }

        try {
            ObjectInputStream input = new ObjectInputStream(
                new FileInputStream(serialPath + "/tracked.txt"));
            this.trace = (TreeSet<String>) input.readObject();
            input.close();
        } catch (IOException | ClassNotFoundException e) {
            this.trace = new TreeSet<>();
        }

        try {
            ObjectInputStream input = new ObjectInputStream(
                new FileInputStream(serialPath + "/removeMark" + ptrs.get("HEAD") + ".txt"));
            this.removedMark = (TreeSet<String>) input.readObject();
            input.close();
        } catch (IOException | ClassNotFoundException e) {
            this.removedMark = new TreeSet<>();
        }

        if (!init) {
            this.prevFile = (new File(
                commitPath, this.parentHash));
        }
        stagePath.listFiles();
        for (File f : stagePath.listFiles()) {
            stageSet.add(f.getName());
        }
    }

    public void commit(boolean init) throws IOException {
        // If no changes have been staged and there are no files in the removal directory, exit the method
        if (stageSet.isEmpty() && !init && Objects.requireNonNull(removePath.listFiles()).length == 0) {
            System.out.println("No changes added to the commit.");
            return;
        }

        // If there are files in the removal directory, delete them
        Objects.requireNonNull(removePath.listFiles());
        for (File f : Objects.requireNonNull(removePath.listFiles())) {
            f.delete();
        }

        // If there is no commit message, prompt the user to enter one
        if (logMsg == null || logMsg.isEmpty()) {
            System.out.println("Please enter a commit message.");
        }

        // Generate a SHA-1 hash for the commit message
        commitHash = Utils.sha1(this.getMessage());

        // Create a new folder with the commit hash as its name
        File newFolder = new File(commitPath, commitHash);
        if (!newFolder.mkdir()) {
            System.out.println("Error:");
            System.out.println("    Failed to create new folder.");
        }

        // Create a file for the commit message and write the message to it
        File logMsgFile = new File(commitPath + "/" + commitHash, "logMsg.txt");
        Utils.writeContents(logMsgFile, (Object) logMsg.getBytes());

        // Create a file for the timestamp and write the timestamp to it
        File timeStampFile = new File(commitPath + "/" + commitHash, "timeStamp.txt");
        Utils.writeContents(timeStampFile, (Object) timeStamp.getBytes());

        // If there is a parent commit, create a file for the parent commit hash and write the hash to it
        if (parentHash != null) {
            File parentHashFile = new File(commitPath + "/" + commitHash, "parentHash.txt");
            Utils.writeContents(parentHashFile, (Object) parentHash.getBytes());
        }

        // If there are files in the previous commit, copy them over to the new commit directory
        if (prevFile != null) {
            for (File f : Objects.requireNonNull(prevFile.listFiles())) {
                // If the file is not in the stage set or is a metadata file, skip it
                if (!stageSet.contains(f.getName())
                    && !(f.getName().equals("logMsg.txt"))
                    && !(f.getName().equals("timeStamp.txt"))
                    && !(f.getName().equals("parentHash.txt"))
                    && !(removedMark.contains(f.getName()))) {
                    // Copy the file to the new commit directory
                    Files.copy(f.toPath(), Paths.get(commitPath + "/"
                        + commitHash + "/" + f.getName()));
                }
            }
        }

        // Move all staged files to the new commit directory and add them to the trace list
        if (!stageSet.isEmpty()) {
            for (File f: Objects.requireNonNull(stagePath.listFiles())) {
                if (!f.renameTo(new File(commitPath + "/" + commitHash + "/" + f.getName()))) {
                    System.out.println("Error:");
                    System.out.println("    Failed to move staged files.");
                }
                trace.add(f.getName());
            }
        }

        // Update the parent hash pointer
        if (!ptrs.isEmpty()) {
            this.parentHash = ptrs.get(ptrs.get("HEAD"));
        }

        // If there is no parent hash, set the HEAD and master pointers to the new commit hash
        if (parentHash == null) {
            ptrs.put("HEAD", "master");
            ptrs.put("master", commitHash);
        } else {
            // Otherwise, update the pointer for the current branch to the new commit hash
            ptrs.put(ptrs.get("HEAD"), commitHash);
        }

        // Serialize the trace list and write it to a file
        File ptrsFile = new File(serialPath + "/pointers.txt");
        try {
            ObjectOutputStream output = new ObjectOutputStream(
                new FileOutputStream(ptrsFile));
            output.writeObject(trace);
            output.close();
        } catch (IOException e) {
            System.out.println("Serialization failed");
        }
    }
}
