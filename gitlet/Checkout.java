package gitlet;

import java.io.*;
import java.nio.file.Files;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Driver class to check out files from the commit history.
 * <p>
 *     Usage: java gitlet.Main checkout -- [file name]
 *     Usage: java gitlet.Main checkout [commit id] -- [file name]
 *     Usage: java gitlet.Main checkout [branch name]
 * </p>
 * @author Lang Qin
 */

public class Checkout {

    private static final File workingDir = new File(System.getProperty("user.dir"));
    private static final File commitsDir = new File(workingDir, ".gitlet/commits");
    private static final File branchDir = new File(workingDir, ".gitlet/branch");


    /**
     * Takes the version of the file as it exists in the head commit and
     * puts it in the working directory, overwriting the version of the
     * file that’s already there if there is one.
     * <p>
     * Usage: java gitlet.Main checkout -- [file name]
     *
     * @param fileName the name of the file to be checked out
     */
    public void checkoutFile(String fileName) {
        // load the branch history
        TreeMap<String, String> branchHistory;
        try {
            ObjectInputStream in = new ObjectInputStream(
                    new FileInputStream(new File(branchDir, "pointers.txt")));
            branchHistory = (TreeMap<String, String>) in.readObject();
            in.close();
        } catch (IOException | ClassNotFoundException e) {
            branchHistory = new TreeMap<>();
        }

        // load the commit history
        String currBranch = branchHistory.get(branchHistory.get("HEAD"));
        File fileToCheckout = new File(commitsDir,  currBranch + "/" + fileName);

        if (!fileToCheckout.exists()) {
            System.out.println("Warning:");
            System.out.println("    File does not exist in that commit.");
            return;
        }

        // set the file to the version in the head commit
        File fileInWorkingDir = new File(workingDir, fileToCheckout.getName());

        if (fileInWorkingDir.exists()) {
            if (!fileInWorkingDir.delete()) {
                System.out.println("Error:");
                System.out.println("    Could not delete file in working directory.");
                return;
            }
        }

        try {
            Files.copy(fileToCheckout.toPath(), (new File(workingDir, fileName)).toPath());
        } catch (IOException e) {
            System.out.println("Error:");
            System.out.println("    Could not copy file to working directory.");
        }
    }


    /**
     * Takes version of the file as it exists in the commit
     * within the given id, and puts it in the working directory,
     * overwriting the version of the file that's already there if
     * there is one. The new version of the file is not staged.
     * <p>
     * Usage: Usage: java gitlet.Main checkout [commit id] -- [file name]
     */
    public void checkoutFile(String fileName, String ID) {
        // load the branch history
        TreeMap<String, String> branchHistory;
        try {
            ObjectInputStream in = new ObjectInputStream(
                    new FileInputStream(new File(branchDir, "pointers.txt")));
            branchHistory = (TreeMap<String, String>) in.readObject();
            in.close();
        } catch (IOException | ClassNotFoundException e) {
            branchHistory = new TreeMap<>();
        }

        // find the commit with the given ID
        String commitID = null;
        for (File commit : Objects.requireNonNull(commitsDir.listFiles())) {
            String currID = commit.getName().substring(0, ID.length());
            if (ID.equals(currID)) {
                commitID = commit.getName();
                break;
            }
        }
        if (commitID == null) {
            System.out.println("Warning:");
            System.out.println("    No commit with that id exists.");
            return;
        }

        // load the commit history
        File fileToCheckout = new File(commitsDir,  commitID + "/" + fileName);

        if (!fileToCheckout.exists()) {
            System.out.println("Warning:");
            System.out.println("    File does not exist in that commit.");
            return;
        }

        // set the file to the version in the head commit
        File fileInWorkingDir = new File(workingDir, fileToCheckout.getName());

        if (fileInWorkingDir.exists()) {
            if (!fileInWorkingDir.delete()) {
                System.out.println("Error:");
                System.out.println("    Could not delete file in working directory.");
                return;
            }
        }

        try {
            Files.copy(fileToCheckout.toPath(), (new File(workingDir, fileName)).toPath());
        } catch (IOException e) {
            System.out.println("Error:");
            System.out.println("    Could not copy file to working directory.");
        }
    }

    /**
     * Takes all files in the commit at the head of the given branch,
     * and puts them in the working directory, overwriting the versions
     * of the files that are already there if they exist. Also, at the
     * end of this command, the given branch will now be considered the
     * current branch (HEAD). Any files that are tracked in the current
     * branch but are not present in the checked-out branch are deleted.
     * The staging area is cleared, unless the checked-out branch is the
     * current branch (see Failure cases below).
     * <p>
     * Usage: java gitlet.Main checkout [branch name]
     *
     * @param branchName the name of the branch to be checked out
     */

    public void checkoutBranch(String branchName) {
        // load the branch history
        TreeMap<String, String> branchHistory;
        try {
            ObjectInputStream in = new ObjectInputStream(
                    new FileInputStream(new File(branchDir, "pointers.txt")));
            branchHistory = (TreeMap<String, String>) in.readObject();
            in.close();
        } catch (IOException | ClassNotFoundException e) {
            branchHistory = new TreeMap<>();
        }

        // load the commit history
        String currBranch = branchHistory.get(branchHistory.get("HEAD"));
        String newBranch = branchHistory.get(branchName);
        if (newBranch == null) {
            System.out.println("Warning:");
            System.out.println("    No such branch exists.");
            return;
        }
        if (newBranch.equals(currBranch)) {
            System.out.println("Warning:");
            System.out.println("    Current branch is the target branch.");
            return;
        }

        // check if the working directory is clean
        if (!branchHistory.get("HEAD").equals(currBranch)) {
            if (!branchHistory.get("HEAD").equals(newBranch)) {
                System.out.println("Warning:");
                System.out.println("    There is an untracked file in the way; delete it or add it first.");
                return;
            }
        }

        // load the commit history
        File newBranchDir = new File(commitsDir, newBranch);
        File currBranchDir = new File(commitsDir, currBranch);

        // process the files in the target branch
        for (File f : Objects.requireNonNull(newBranchDir.listFiles())) {
            // check if the file is tracked in the current branch
            if (!(new File(currBranchDir, f.getName())).exists()
                && (new File(workingDir, f.getName())).exists()) {
                System.out.println("Warning:");
                System.out.println("    There is an untracked file: "  + f.getName() + "; delete it or add it first.");
                return;
            }

            // skipp the logMessage.txt, timeStamp.txt, and parentHash.txt
            if ((f.getName().equals("logMessage.txt")) || (f.getName().equals("timeStamp.txt")
                || (f.getName().equals("parentHash.txt")))) {
                continue;
            }

            // replace the file in the working directory with the file in the new branch
            File fileToCheckout = new File(newBranchDir, f.getName());
            if (!fileToCheckout.exists()) {
                System.out.println("Error:");
                System.out.println("    File does not exist in that commit.");
                return;
            }

            File fileInWorkingDir = new File(workingDir, fileToCheckout.getName());
            if (fileInWorkingDir.exists()) {
                if (!fileInWorkingDir.delete()) {
                    System.out.println("Error:");
                    System.out.println("    Could not delete file in working directory.");
                    return;
                }
            }

            try {
                Files.copy(fileToCheckout.toPath(), (new File(workingDir, f.getName())).toPath());
            } catch (IOException e) {
                System.out.println("Error:");
                System.out.println("    Could not copy file to working directory.");
            }
        }

        // process the files in the current branch
        currBranchDir = new File(commitsDir, currBranch);
        for (File f : Objects.requireNonNull(currBranchDir.listFiles())) {
            // ignore the logMessage.txt, timeStamp.txt, and parentHash.txt
            if ((f.getName().equals("logMessage.txt")) || (f.getName().equals("timeStamp.txt")
                || (f.getName().equals("parentHash.txt")))) {
                continue;
            }

            // check if the file is tracked in the current branch
            if (!(new File(newBranchDir, f.getName())).exists()) {
                if (!f.delete()) {
                    System.out.println("Error:");
                    System.out.println("    Could not delete file in working directory.");
                    return;
                }
            }
        }

        // update the branch history
        branchHistory.put("HEAD", branchName);
        File pointers = new File(branchDir, "pointers.txt");
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(pointers));
            out.writeObject(branchHistory);
            out.close();
        } catch (IOException e) {
            System.out.println("Error:");
            System.out.println("    Could not write to pointers.txt.");
        }
    }
}
