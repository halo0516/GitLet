package gitlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Log class provides functionality for displaying commit logs in a Git-like version control system.
 * 
 * Usage:
 * <p>
 *     java gitlet.Main log
 *     java gitlet.Main global-log
 * </p>
 * 
 * @author Lang Qin, Yuxiang Wang
 */
public class Log {
    private static final File cPath = new File(System.getProperty("user.dir") + "/.gitlet/commits");
    private static final File headPath = new File(System.getProperty("user.dir")
            + "/.gitlet/branch/pointers.txt");

    private TreeMap<String, String> head;
    private String currentCommit;
    private String parentID;

    /**
     * Displays the commit log for the current branch.
     */
    public void log() {
        if (cPath.listFiles() != null) {
            loadHead();

            String currentBranch = head.get("HEAD");
            currentCommit = head.get(currentBranch);

            while (new File(cPath + "/" + currentCommit + "/parentHash.txt").exists()) {
                displayCommitInfo();
                currentCommit = parentID;
                updateParentID();
            }

            displayCommitInfo();
        }
    }

    /**
     * Displays the commit log for all branches.
     */
    public void globalLog() {
        if (cPath.listFiles() != null) {
            for (File f : Objects.requireNonNull(cPath.listFiles())) {
                currentCommit = f.getName();
                displayCommitInfo();
            }
        }
    }

    /**
     * Loads the head reference from the serialized file.
     */
    private void loadHead() {
        try (FileInputStream fileIn = new FileInputStream(headPath);
             ObjectInputStream inp = new ObjectInputStream(fileIn)) {
            this.head = (TreeMap<String, String>) inp.readObject();
        } catch (IOException | ClassNotFoundException excp) {
            return;
        }
    }

    /**
     * Updates the parentID from the currentCommit.
     */
    private void updateParentID() {
        if (new File(cPath + "/" + currentCommit + "/parentHash.txt").exists()) {
            parentID = new String(Utils.readContents(
                    new File(cPath + "/" + currentCommit + "/parentHash.txt")));
        }
    }

    /**
     * Displays commit information for the currentCommit.
     */
    private void displayCommitInfo() {
        String logmsg = new String(Utils.readContents(new File(cPath
                + "/" + currentCommit + "/logMessage.txt")));
        String timestamp = new String(Utils.readContents(new File(cPath
                + "/" + currentCommit + "/timeStamp.txt")));
        System.out.println("===");
        System.out.println("Commit " + currentCommit);
        System.out.println(timestamp);
        System.out.println(logmsg);
        System.out.println();
    }
}
