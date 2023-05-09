package gitlet;

import java.io.File;
import java.util.Objects;

/**
 * Find class provides functionality for searching commits by their commit messages
 * in the Gitlet version control system.
 *
 * @author Lang Qin, Yuxiang Wang
 */
public class Find {
    private static final File COMMIT_DIR =
            new File(System.getProperty("user.dir") + "/.gitlet/Commits");

    /**
     * Searches for and displays the commit IDs with the given commit message.
     *
     * @param message the commit message to search for
     */
    public void find(String message) {
        boolean tracker = false;

        if (COMMIT_DIR.listFiles() != null) {
            for (File f : Objects.requireNonNull(COMMIT_DIR.listFiles())) {
                String logmsg = readLogMessage(f.getName());

                if (logmsg.equals(message)) {
                    tracker = true;
                    System.out.println(f.getName());
                }
            }

            if (!tracker) {
                System.out.println("Found no commit with that message.");
            }
        }
    }

    /**
     * Reads the log message of a commit with the given commit ID.
     *
     * @param commitID the ID of the commit to read the log message from
     * @return the log message of the commit
     */
    private String readLogMessage(String commitID) {
        return new String(Utils.readContents(
                new File(COMMIT_DIR + "/" + commitID + "/logMessage.txt")));
    }
}
