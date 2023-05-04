package gitlet;

import java.text.*;
import java.time.Instant;
import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.nio.file.Files;
import java.util.Date;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    static File commitPath = new File(System.getProperty("user.dir") + "/.gitlet/Commits");
    static File serialPath = new File (System.getProperty("user.dir") + "/.gitlet/Serialized");
    static File stagePath = new File(System.getProperty("user.dir") + "/.gitlet/Staging Area");
    static File removePath = new File(System.getProperty("user.dir") + "/.gitlet/Removed Files");
    File prevFile;
    String logMsg;
    String timeStamp;
    String parentHash;
    String commitHash;
    TreeSet<String> stageSet = new TreeSet<>();
    TreeMap<String, String> ptrs;
    TreeSet removedMark = new TreeSet<>();
    TreeSet<String> trace = new TreeSet<>();

    public String getMessage() {
        return "Commit{"
                + "logMessage= '" + logMsg + '\''
                + ", timeStamp= " + timeStamp + '\''
                + ", parentHash= " + parentHash + '\''
                + '\'' + '}';
    }

    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */

    public Commit (String message, Boolean init) {
        this.logMsg = message;
        DateFormat dateF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.timeStamp = dateF.format(new Date());
        try {
            ObjectInputStream input = new ObjectInputStream(
                new FileInputStream(serialPath + "/pointers.txt"));
            this.ptrs = (TreeMap) input.readObject();
            input.close();
        } catch (IOException | ClassNotFoundException e) {
            this.ptrs = new TreeMap<>();
        }
        if (ptrs.containsKey("HEAD")) {
            this.parentHash = ptrs.get("HEAD");
        }

        try {
            ObjectInputStream input = new ObjectInputStream(
                new FileInputStream(serialPath + "/tracked.txt"));
            this.trace = (TreeSet) input.readObject();
            input.close();
        } catch(IOException | ClassNotFoundException e) {
            this.trace = new TreeSet<>();
        }
        try {
            ObjectInputStream input = new ObjectInputStream(
                new FileInputStream(serialPath + "/removeMark" + ptrs.get("HEAD") +".txt"));
            this.removedMark = (TreeSet) input.readObject();
            input.close();
        }catch (IOException | ClassNotFoundException e) {
            this.removedMark = new TreeSet<>();
        }
        if(!init) {
            this.prevFile = (new File(new File(System.getProperty("user.dir") + "/.gitlet/Commits") + this.parentHash));
        }
        if (stagePath.listFiles().length != 0) {
            for (File f : stagePath.listFiles()) {
                stageSet.add(f.getName());
            }
        }
    }

    public void commit(boolean init) throws IOException {
        if (stageSet.isEmpty() && !init && removePath.listFiles().length == 0) {
            System.out.println("No changes added to the commit.");
            return;
        }
        if (removePath.listFiles().length > 0) {
            for (File f : removePath.listFiles()) {
                f.delete();
            }
        }

        if (logMsg == null || logMsg.isEmpty() || logMsg.equals(" ")) {
            System.out.println("Please enter a commit message.");
        }
        commitHash = Utils.sha1(this.getMessage());
        File newFolder = new File(commitPath, commitHash);
        newFolder.mkdir();
        File logMsgFile = new File(commitPath + "/" + commitHash, "/logMsg.txt");
        Utils.writeContents(logMsgFile, logMsg.getBytes());
        File timeStampFile = new File(commitPath + "/" + commitHash, "/timeStamp.txt");
        Utils.writeContents(timeStampFile, timeStamp.getBytes());
        if (parentHash != null) {
            File parentHashFile = new File(commitPath + "/" + commitHash, "/parentHash.txt");
            Utils.writeContents(parentHashFile, parentHash.getBytes());
        }
        if (prevFile != null) {
            for (File f : prevFile.listFiles()) {
                if (!stageSet.contains(f.getName())
                    && !(f.getName().equals("logMsg.txt"))
                    && !(f.getName().equals("timeStamp.txt"))
                    && !(f.getName().equals("parentHash.txt"))
                    && !(removedMark.contains(f.getName()))) {
                    Files.copy(f.toPath(), Paths.get(commitPath + "/"
                        + commitHash + "/" + f.getName()));
                }
            }
        }
        if (!stageSet.isEmpty()) {
            for (File f: stagePath.listFiles()) {
                f.renameTo(new File(commitPath + "/" + commitHash + "/" + f.getName()));
                trace.add(f.getName());
            }
        }
        if(!ptrs.isEmpty()) {
            this.parentHash = ptrs.get(ptrs.get("HEAD"));
        }
        if (parentHash == null) {
            ptrs.put("HEAD", "master");
            ptrs.put("master", commitHash);
        } else {
            ptrs.put(ptrs.get("HEAD"), commitHash);
        }

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
