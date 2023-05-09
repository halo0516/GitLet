package gitlet;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

public class GitletTest {

    @TempDir
    Path tempDir;
    private File testFile;

    @BeforeEach
    public void setUp() throws IOException {
        // Create a test file in the temporary directory
        testFile = new File(tempDir.toFile(), "test.txt");
        Files.write(testFile.toPath(), "This is a test file.".getBytes());
        testFile.deleteOnExit();

        // Set the user directory to the temporary directory
        System.setProperty("user.dir", tempDir.toString());
    }

    @AfterEach
    public void tearDown() {
        // Clean up temporary directory and files
        Utils.deleteDirectory(tempDir.toFile());
    }

    @Test
    public void testMain() throws IOException {
        Main.main("init".split(" "));
        Main.main("branch other".split(" "));
        Main.main("add hello2.txt".split(" "));
        Main.main("add hello3.txt".split(" "));
        Main.main("commit first".split(" "));
        Main.main("status".split(" "));
        Main.main("checkout master".split(" "));
        Main.main("checkout other".split(" "));

        Main.main("init".split(" "));
        Main.main("branch other".split(" "));
        Main.main("add hello2.txt".split(" "));
        Main.main("add hello3.txt".split(" "));
        Main.main("add hello4.txt".split(" "));
        Main.main("commit hello".split(" "));
        Main.main("rm hello2.txt".split(" "));
        Main.main("status".split(" "));

        // checkout tests
        Main.main("init".split(" "));
        Main.main("branch other".split(" "));
        Main.main("add hello2.txt".split(" "));
        Main.main("add hello3.txt".split(" "));
        Main.main("add hello4.txt".split(" "));
        Main.main("status".split(" "));
        Main.main("commit first".split(" "));

        Main.main("checkout other".split(" "));
        Main.main("rm hello2.txt".split(" "));
        Main.main("status".split(" "));
        Main.main("commit hello2.txt".split(" "));
    }

    @Test
    public void testCheckout() throws IOException {
        write("test.txt", "This is a test file.");
        write("test2.txt", "This is a test file 2.");

        Main.main("init".split(" "));
        Main.main("add test.txt".split(" "));
        Main.main("add test2.txt".split(" "));
        Main.main("commit first".split(" "));
        Main.main("status".split(" "));
        Main.main("log".split(" "));

        Main.main("branch other".split(" "));
        Main.main("branch other2".split(" "));
        Main.main("checkout other".split(" "));
        write("test3.txt", "This is a test file 3.");
        Main.main("add test3.txt".split(" "));
        Main.main("commit second".split(" "));

        Main.main("checkout master".split(" "));
        Main.main("status".split(" "));

    }


    private void write(String filename, String contents) throws IOException {
        File file = new File(tempDir.toFile(), filename);
        Files.write(file.toPath(), contents.getBytes());
    }

    @Test
    public void testIntiAndStage() throws IOException {
        // Initialize the Gitlet version control system
        Init init = new Init();
        init.init();

        // Ensure the .gitlet directory is created
        File gitDir = new File(tempDir.toFile(), ".gitlet");
        assertTrue(gitDir.exists());

        // Add the test file to the staging area
        Stage stage = new Stage();
        stage.add(testFile);
        Log log = new Log();
        log.log();

        // Check if the test file is in the staging area
        File stagedFile = new File(gitDir, "stage/test.txt");
        assertTrue(stagedFile.exists());
        stage.status();

        // Create a commit with a log message
        Commit commit = new Commit("Commit 1", false);
        commit.commit(false);

        // Check find command
        Find find = new Find();
        find.find("Commit 1");

        // Remove the test file from the staging area
        (new Stage()).rm(testFile);

        // Ensure the test file is removed from the staging area
        assertFalse(stagedFile.exists());

        // Check the status of the Gitlet repository
        stage.status();

        // global log
        log.globalLog();
    }

    @Test
    public void testCheckoutFile() throws IOException {
        // Initialize Gitlet and create a commit
        Init init = new Init();
        init.init();

        // Add test file and commit
        Stage stage = new Stage();
        stage.add(testFile);
        Commit commit = new Commit("Initial commit", true);
        commit.commit(false);

        // Use the Checkout class to check out the test file
        Checkout checkout = new Checkout();
        checkout.checkoutFile(testFile.getName());
    }

    @Test
    public void testBranch() throws IOException {
        // Initialize Gitlet and create a commit
        Init init = new Init();
        init.init();

        // Add test file and commit
        Stage stage = new Stage();
        stage.add(testFile);
        Commit commit = new Commit("Initial commit", true);
        commit.commit(true);

        // Create a new branch
        Branch branch = new Branch();
        branch.branch("new_branch");

        // Check if the new branch was created successfully
        File branchPointersFile =
                new File(System.getProperty("user.dir") + "/.gitlet/branch/pointers.txt");
        try {
            FileInputStream fis = new FileInputStream(branchPointersFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            TreeMap<String, String> commitPointers = (TreeMap<String, String>) ois.readObject();
            assertTrue(commitPointers.containsKey("new_branch"));
            ois.close();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            fail("Failed to read branch pointers");
        }
    }

    @Test
    public void testRmBranch() throws IOException {
        // Initialize Gitlet and create a commit
        Init init = new Init();
        init.init();

        // Add test file and commit
        Stage stage = new Stage();
        stage.add(testFile);
        Commit commit = new Commit("Initial commit", true);
        commit.commit(true);

        // Create a new branch
        Branch branch = new Branch();
        branch.branch("new_branch");

        // Remove the new branch
        branch.rmBranch("new_branch");

        // Check if the new branch was removed successfully
        File branchPointersFile =
                new File(System.getProperty("user.dir") + "/.gitlet/branch/pointers.txt");
        try {
            FileInputStream fis = new FileInputStream(branchPointersFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            TreeMap<String, String> commitPointers = (TreeMap<String, String>) ois.readObject();
            assertFalse(commitPointers.containsKey("new_branch"));
            ois.close();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            fail("Failed to read branch pointers");
        }
    }

    @Test
    void testCheckoutBranch() throws IOException {
        // go through the init, stage, commit, and branch process
        // then test checkout branch
        Init init = new Init();
        init.init();

        Stage stage = new Stage();
        stage.add(testFile);
        Commit commit = new Commit("Initial commit", true);
        commit.commit(true);

        Branch branch = new Branch();
        branch.branch("new_branch");

        Checkout checkout = new Checkout();
        checkout.checkoutBranch("new_branch");

        Branch branch2 = new Branch();
        branch2.branch("new_branch2");

        (new Checkout()).checkoutBranch("new_branch1");
    }
}
