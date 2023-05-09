package gitlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.TreeMap;

/**
 * Merge class is responsible for performing the merge operation in the gitlet
 * version control system.
 *
 * @author Zitong Shi
 */

public class Merge {
    String workDirectory = System.getProperty("user.dir");
    String stagingPath = workDirectory + "/.gitlet/stage";
    String branchPath = workDirectory + "/.gitlet/branch";
    String pointersPath = branchPath + "/pointers.txt";
    String commitPath = branchPath + "/.gitlet/commit";
    String splitCommitID = "";
    String branchCommitID = "";
    String currHeadCommitID = "";
    String currBranchName = "";
    TreeMap<String, String> commitPointers;

    /**
     * Performs the merge operation for the given branch name.
     *
     * @param branchName the branch to be merged with the current branch
     * @throws IOException if an I/O error occurs during the merge operation
     */
    public void merge(String branchName) throws IOException {
        //check for staging, if staging, cannot merge
        if (hasStagingFile()) {
            System.out.println("Cann't merge, please discard or commit " +
                    "the staging file");
        }

        //read in the commit map
        commitPointers = getInput(pointersPath);

        //find relevant commit
        currBranchName = commitPointers.get("HEAD");
        currHeadCommitID = commitPointers.get(currBranchName);
        branchCommitID = commitPointers.get(branchName);
        //findSplitCommit
        splitCommitID = getSplitPoint(currBranchName, branchName);

        //category files headBranch
        HashSet<String> headUnModifiedFile = new HashSet<>();
        HashSet<String> headModifiedFile = new HashSet<>();
        HashSet<String> headDeleledFile = new HashSet<>();
        categorizeFiles(currHeadCommitID, splitCommitID, headUnModifiedFile, headModifiedFile,
                headDeleledFile);

        //category files headBranch
        HashSet<String> branchUnModifiedFile = new HashSet<>();
        HashSet<String> branchModifiedFile = new HashSet<>();
        HashSet<String> branchDeleledFile = new HashSet<>();
        categorizeFiles(branchCommitID, splitCommitID, branchUnModifiedFile, branchModifiedFile,
                branchDeleledFile);


        String currCommitPath = commitPath + currHeadCommitID;
        String splitCommitPath = commitPath + splitCommitID;
        String branchCommit = commitPath + branchCommitID;

        //Case1: only branch modified the file
        for (String fileName: branchModifiedFile) {
            if (!headUnModifiedFile.contains(fileName)) {
                continue;
            }
            //replace the file in the wd to branch
            Files.copy(new File(branchCommit + "/" + fileName).toPath(),
                    new File(workDirectory + "/" + fileName).toPath());
            // stage the file
            Files.copy(new File(branchCommit + "/" + fileName).toPath(),
                    new File(stagingPath + "/" + fileName).toPath());
        }

        //Case2: only branch modified the file
        File[] branchFiles = new File(commitPath + branchCommitID).listFiles();
        for (File file: branchFiles) {
            String fileName = file.getName();
            File currFile = new File(currCommitPath + "/" + fileName);
            File split = new File(splitCommitPath + "/" + fileName);
            if (!currFile.exists() && !split.exists()) {
                new Checkout().checkoutFile(fileName, branchCommitID);
                // stage the file
                Files.copy(new File(branchCommit + "/" + fileName).toPath(),
                     new File(stagingPath + "/" + fileName).toPath());
            }
        }

         //case 3:in split and unmodified by curr but delete by branch
        File[] splitFiles = new File(commitPath + splitCommitID).listFiles();
        for (File file: splitFiles) {
            if (headUnModifiedFile.contains(file.getName()) &&
                !new File(branchCommit + "/" + file.getName()).exists()) {
                //delete from the wd
                new File(workDirectory + "/" + file.getName()).delete();
            }
        }

        String commitName = "Megerd head with" + branchName;
        new Commit(commitName, false).commit(false);
    }

    private void categorizeFiles(String currCommitID, String splitCommitID,
                                  HashSet<String> unModifiedFile,
                                  HashSet<String> modifiedFile,
                                  HashSet<String> deletedFile) {

        File[] currFiles = new File(commitPath + currCommitID).listFiles();
        File[] splitPointFiles = new File(commitPath + splitCommitID).listFiles();

        for (File currFile: currFiles) {
            if (isUtilFile(currFile)) {
                continue;
            }

            if (new File(commitPath + splitCommitID + "/" + currFile.getName()).exists()) {
                File splitFile = new File(commitPath + splitCommitID + "/" + currFile.getName());
                if (splitFile.exists()) {
                    if (isSame(currFile, splitFile)) {
                        unModifiedFile.add(currFile.getName());
                    } else {
                        modifiedFile.add(currFile.getName());
                    }
                } else {
                    modifiedFile.add(currFile.getName());
                }
            }
        }

        for (File splitFile: splitPointFiles) {
            if (new File(commitPath + currCommitID + "/" + splitFile.getName()).exists()) {
                continue;
            } else {
                deletedFile.add(splitFile.getName());
            }
        }
    }

    private boolean isSame(File currFile, File splitFile) {
        String currHashID = Utils.sha1(new String(Utils.readContents(currFile)));
        String splitHashID = Utils.sha1(new String(Utils.readContents(splitFile)));

        return currHashID.equals(splitHashID);
    }

    private boolean isUtilFile(File file) {
        String fileName = file.getName();
        HashSet<String> utilFileNames = new HashSet<>();
        utilFileNames.add("logMessage.txt");
        utilFileNames.add("timeStamp.txt");
        utilFileNames.add("parentHash.txt");
        return utilFileNames.contains(fileName);
    }

    private String getSplitPoint(String currBranchName, String branchName) {
        String splitKey = "";
        if (currBranchName.compareTo(branchName) < 0) {
            splitKey = currBranchName + "/" + branchName;
        } else {
            splitKey = branchName + "/" + currBranchName;
        }

        return commitPointers.get(splitKey);
    }

    private boolean hasStagingFile() {
        File[] stagingFile = new File(stagingPath).listFiles();
        return stagingFile.length > 0;
    }

    public TreeMap<String, String> getInput(String filePath) {
        TreeMap<String, String> commitPointers = new TreeMap<>();

        try {
            ObjectInputStream objectInputStream =
                    new ObjectInputStream(new FileInputStream(filePath));
            commitPointers = (TreeMap<String, String>) objectInputStream.readObject();
            objectInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return  commitPointers;
    }
}
