package gitlet;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

public class LocalTest {

    private static final File TEST_DIR =
            new File(System.getProperty("user.dir") + "/test/");

    private void write(String filename, String contents) throws IOException {
        File file = new File(TEST_DIR, filename);
        Files.write(file.toPath(), contents.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testAll() throws IOException {
        write("test.txt", "This is a test file.");
        write("test2.txt", "This is a test file.");
        write("test3.txt", "This is a test file.");

        Main.main("init".split(" "));
        Main.main("add test.txt".split(" "));
        Main.main("add test2.txt".split(" "));
        Main.main("add test3.txt".split(" "));

        Main.main("commit first".split(" "));
        Main.main("status".split(" "));
        Main.main("log".split(" "));
        Main.main("global-log".split(" "));

        Main.main("branch other".split(" "));
        Main.main("checkout other".split(" "));

        write("test4.txt", "This is a test file.");
        Main.main("add test4.txt".split(" "));
        Main.main("commit second".split(" "));
        Main.main("status".split(" "));

        Main.main("branch other2".split(" "));
        Main.main("checkout other2".split(" "));
        Main.main("rm test4.txt".split(" "));
        Main.main("status".split(" "));
        Main.main("commit third".split(" "));

        Main.main("checkout other".split(" "));
        Main.main("merge other2".split(" "));
        Main.main("status".split(" "));

        Main.main("rm-branch other2".split(" "));
        Main.main("checkout test.txt".split(" "));
    }


}
