package gitlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Log class provides functionality for displaying commit logs in a Git-like version control system.
 * It also provides methods for loading the head reference and updating the parent ID.
 * Usage:
 * <p>
 *     java gitlet.Main log
 *     java gitlet.Main global-log
 * </p>
 * 
 * @author Lang Qin, Yuxiang Wang
 */
public class Log {
    private static final File COMMIT_DIR =
            new File(System.getProperty("user.dir") + "/.gitlet/commits");
    private static final File HEAD_DIR = new File(System.getProperty("user.dir")
            + "/.gitlet/branch/pointers.txt");

    private TreeMap<String, String> head;
    private String currentCommit;
    private String parentID;

    /**
     * Displays the commit log for the current branch.
     */
    public void log() {
        if (COMMIT_DIR.listFiles() != null) {
            loadHead();

            String currentBranch = head.get("HEAD");
            currentCommit = head.get(currentBranch);

            while (new File(COMMIT_DIR + "/" + currentCommit + "/parentHash.txt").exists()) {
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
        if (COMMIT_DIR.listFiles() != null) {
            for (File f : Objects.requireNonNull(COMMIT_DIR.listFiles())) {
                currentCommit = f.getName();
                displayCommitInfo();
            }
        }
    }

    /**
     * Loads the head reference from the serialized file.
     */
    private void loadHead() {
        try (FileInputStream fileIn = new FileInputStream(HEAD_DIR);
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
        if (new File(COMMIT_DIR + "/" + currentCommit + "/parentHash.txt").exists()) {
            parentID = new String(Utils.readContents(
                    new File(COMMIT_DIR + "/" + currentCommit + "/parentHash.txt")));
        }
    }

    /**
     * Displays commit information for the currentCommit.
     */
    private void displayCommitInfo() {
        String logmsg = new String(Utils.readContents(new File(COMMIT_DIR
                + "/" + currentCommit + "/logMessage.txt")));
        String timestamp = new String(Utils.readContents(new File(COMMIT_DIR
                + "/" + currentCommit + "/timeStamp.txt")));
        System.out.println("===");
        System.out.println("Commit " + currentCommit);
        System.out.println(timestamp);
        System.out.println(logmsg);
        System.out.println();
    }
}
