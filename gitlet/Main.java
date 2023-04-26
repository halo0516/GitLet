package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashMap;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    private static final String GITLET_DIR = ".gitlet/";
    public static void main(String[] args) {

        // TODO: what if args is empty?
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                init();
                // TODO: handle the `init` command
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                break;
            // TODO: FILL THE REST IN
        }
    }
    private static void init() {
        Path cwd = Paths.get("").toAbsolutePath();
        Path gitletDir = cwd.resolve(GITLET_DIR);
        if (Files.exists(gitletDir)) {
            System.out.println("A gitlet version-control system "
                    + "already exists in the current directory.");
            return;
        }

        try {
            Files.createDirectories(gitletDir);
            Commit initCommit = new Commit("initial commit", Instant.ofEpochSecond(0), new HashMap<String, String>());
            String initCommitId = initCommit.getId();
            File commitFile = gitletDir.resolve("commit").resolve(initCommitId).toFile();
            Utils.writeObject(commitFile, initCommitId);
            HashMap<String, String> commitMap = new HashMap<>();
            commitMap.put("master",initCommitId);
            File branchFile = gitletDir.resolve("branch").resolve("master").toFile();
            Utils.writeObject(branchFile, commitMap);
            System.out.println("Initialization Complete.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
