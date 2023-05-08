package gitlet;
import java.io.*;

public class Find {
    String logMsg;
    String msg;
    boolean track;
    static File commitPath = new File(System.getProperty("user.dir") + "/.gitlet/Commits");
    public void find(String msg) {
        // Assign the input parameter to the instance variable msg
        this.msg = msg;
        // Set track to false by default
        track = false;
        // If the log message matches the input message
        if (commitPath.listFiles() != null) {
            for (File f : commitPath.listFiles()) {
                logMsg = new String(Utils.readContents(
                    new File(commitPath + "/" + f.getName() + "logMessage.txt")));
                // If the log message matches the input message
                if (logMsg.equals(msg)) {
                    track = true;
                    System.out.println(f.getName());
                }
            }

            // If track is still false, meaning no matching commit was found
            if (!track) {
                System.out.println("There exist no commit with that message.");
            }
        }
    }
}
