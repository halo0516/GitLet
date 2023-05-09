package gitlet;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
    public void testGitlet() throws IOException {
        // Initialize the Gitlet version control system
        Init init = new Init();
        init.init();

        // Ensure the .gitlet directory is created
        File gitDir = new File(tempDir.toFile(), ".gitlet");
        assertTrue(gitDir.exists());

        // Add the test file to the staging area
        Stage stage = new Stage();
        stage.add(testFile);

        // Check if the test file is in the staging area
        File stagedFile = new File(gitDir, "stage/test.txt");
        assertTrue(stagedFile.exists());
        stage.status();

        // Remove the test file from the staging area
        stage.rm(testFile);

        // Ensure the test file is removed from the staging area
        assertFalse(stagedFile.exists());

        // Check the status of the Gitlet repository
        stage.status();
    }
}
