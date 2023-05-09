package gitlet;

import java.nio.file.Files;
import java.util.Objects;
import java.util.TreeMap;
import java.io.File;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.TreeSet;

/**
 * Reset class provides functionality for resetting the working directory
 * to a specific commit in a Git-like version control system.
 *
 * @author Lang Qin, Zitong Shi
 */
public class Reset {
    private static final File COMMIT_DIR
            = new File(System.getProperty("user.dir") + "/.gitlet/commits");
    private static final File STAGE_DIR
            = new File(System.getProperty("user.dir") + "/.gitlet/stage");
    private static final File BRANCH_DIR
            = new File(System.getProperty("user.dir") + "/.gitlet/branch");
    private static final File WROKING_DIR = new File(System.getProperty("user.dir"));

    /**
     * Resets the working directory to the specified commit.
     *
     * @param iD the ID of the commit to reset the working directory to
     */
    public void reset(String iD) {
        TreeSet<String> tracked = new TreeSet<>();
        int inputLength = iD.length();
        boolean commitExists = false;
        TreeMap<String, String> pointers = loadPointers();
        String fullID = getFullCommitID(iD, inputLength);

        if (fullID.isEmpty()) {
            System.out.println("No commit with that id exists.");
            return;
        }

        clearStagingArea();
        String currentBranch = pointers.get(pointers.get("HEAD"));

        if (!canReset(tracked, currentBranch, fullID)) {
            return;
        }

        resetFilesInWorkingDirectory(currentBranch, fullID);
        pointers.put(pointers.get("HEAD"), fullID);
        savePointers(pointers);
    }

    private TreeMap<String, String> loadPointers() {
        TreeMap<String, String> pointers;
        try (ObjectInputStream inp = new ObjectInputStream(
                new FileInputStream(BRANCH_DIR + "/pointers.txt"))) {
            pointers = (TreeMap<String, String>) inp.readObject();
        } catch (IOException | ClassNotFoundException excp) {
            pointers = new TreeMap<>();
        }
        return pointers;
    }

    private String getFullCommitID(String iD, int inputLength) {
        String fullID = "";
        for (File commit : COMMIT_DIR.listFiles()) {
            String commitID = commit.getName().substring(0, inputLength);
            if (iD.equals(commitID)) {
                fullID = commit.getName();
                break;
            }
        }
        return fullID;
    }

    private void clearStagingArea() {
        for (File f : STAGE_DIR.listFiles()) {
            f.delete();
        }
    }

    private boolean canReset(TreeSet<String> tracked, String currentBranch, String fullID) {
        File cbPath = new File(COMMIT_DIR + "/" + currentBranch);
        File gPath = new File(COMMIT_DIR + "/" + fullID);
        for (File f : WROKING_DIR.listFiles()) {
            if (new File(gPath + "/" + f.getName()).exists()
                    && !new File(cbPath + "/" + f.getName()).exists()) {
                System.out.println("There is an untracked file in the way;"
                        + " delete it or add it first.");
                return false;
            }
        }
        return true;
    }

    private void resetFilesInWorkingDirectory(String currentBranch, String fullID) {
        File cbPath = new File(COMMIT_DIR + "/" + currentBranch);
        File gPath = new File(COMMIT_DIR + "/" + fullID);

        deleteFilesFromCurrentBranch(cbPath);
        copyFilesFromTargetCommit(gPath);
    }

    private void deleteFilesFromCurrentBranch(File cbPath) {
        for (File f : Objects.requireNonNull(cbPath.listFiles())) {
            if (!(f.getName().equals("logMessage.txt"))
                    && !(f.getName().equals("timeStamp.txt"))
                    && !(f.getName().equals("parentHash.txt"))) {
                (new File(WROKING_DIR + "/" + f.getName())).delete();
            }
        }
    }

    private void copyFilesFromTargetCommit(File gPath) {
        for (File f : Objects.requireNonNull(gPath.listFiles())) {
            if (!(f.getName().equals("logMessage.txt"))
                    && !(f.getName().equals("timeStamp.txt"))
                    && !(f.getName().equals("parentHash.txt"))) {
                File version = new File(WROKING_DIR + "/" + f.getName());
                if (version.exists()) {
                    version.delete();
                }
                try {
                    Files.copy(f.toPath(), (new File(WROKING_DIR + "/" + f.getName())).toPath());
                } catch (IOException e) {
                    System.out.println("Failed to copy");
                }
            }
        }
    }

    private void savePointers(TreeMap<String, String> pointers) {
        File outFile = new File(BRANCH_DIR + "/pointers.txt");
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outFile))) {
            out.writeObject(pointers);
        } catch (IOException excp) {
            System.out.print("Map serialization failed.");
        }
    }
}
