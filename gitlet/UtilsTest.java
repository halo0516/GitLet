package gitlet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @TempDir
    Path tempDir;

    File testFile;
    File gitletDir;

    @BeforeEach
    void setUp() {
        testFile = new File(tempDir.toFile(), "test.txt");
        gitletDir = new File(tempDir.toFile(), ".gitlet");
        gitletDir.mkdir();
    }

    @Test
    void testSha1() {
        String input1 = "hello";
        String input2 = "world";
        String sha1 = Utils.sha1(input1, input2);

        assertEquals(40, sha1.length());
        assertNotEquals(Utils.sha1(input1), Utils.sha1(input2));
    }

    @Test
    void testSha1List() {
        List<Object> vals = new ArrayList<>();
        vals.add("hello");
        vals.add("world");

        String sha1 = Utils.sha1(vals);
        assertEquals(40, sha1.length());
    }

    @Test
    void testRestrictedDelete() {
        Utils.writeContents(testFile, "test");
        assertTrue(testFile.exists());

        File testGitletFile = new File(gitletDir, "test.txt");
        Utils.writeContents(testGitletFile, "test");
        assertTrue(testGitletFile.exists());
    }

    @Test
    void testJoin() {
        File joined = Utils.join(tempDir.toFile(), "subdir", "file.txt");
        assertEquals(new File(tempDir.toFile(), "subdir/file.txt"), joined);
    }

    @Test
    void testSerialize() {
        String content = "This is a test content.";
        byte[] serialized = Utils.serialize(content);
        assertNotNull(serialized);
    }

    @Test
    void testDeleteDirectory() {
        File subDir = new File(tempDir.toFile(), "subdir");
        subDir.mkdir();
        File nestedFile = new File(subDir, "nested.txt");
        Utils.writeContents(nestedFile, "test");

        assertTrue(subDir.exists());
        assertTrue(nestedFile.exists());

        Utils.deleteDirectory(subDir);
        assertFalse(subDir.exists());
        assertFalse(nestedFile.exists());
    }

    @Test
    void testWriteContentsAndReadContents() throws IOException {
        String content = "This is a test content.";

        Utils.writeContents(testFile, content);
        assertTrue(testFile.exists());

        byte[] fileContent = Utils.readContents(testFile);
        assertNotNull(fileContent);
        assertEquals(content, new String(fileContent));
    }

    @Test
    void testWriteContentsAndReadContentsAsString() {
        String content = "This is a test content.";

        Utils.writeContents(testFile, content);
        assertTrue(testFile.exists());

        String fileContent = Utils.readContentsAsString(testFile);
        assertNotNull(fileContent);
        assertEquals(content, fileContent);
    }

    @Test
    void testPlainFilenamesIn() {
        String content1 = "This is content 1.";
        String content2 = "This is content 2.";
        File file1 = new File(tempDir.toFile(), "file1.txt");
        File file2 = new File(tempDir.toFile(), "file2.txt");

        Utils.writeContents(file1, content1);
        Utils.writeContents(file2, content2);

        List<String> plainFiles = Utils.plainFilenamesIn(tempDir.toFile());
        assertNotNull(plainFiles);
        assertEquals(2, plainFiles.size());
        assertTrue(plainFiles.contains(file1.getName()));
        assertTrue(plainFiles.contains(file2.getName()));
    }
}
