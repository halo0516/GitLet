package gitlet;

import java.io.*;
import java.nio.file.Files;
import java.util.TreeMap;

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
            Files.copy(fileToCheckout.toPath(), fileInWorkingDir.toPath());
        } catch (IOException e) {
            System.out.println("Error:");
            System.out.println("    Could not copy file to working directory.");
        }
    }
}
