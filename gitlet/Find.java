package gitlet;
import java.io.*;

public class Find {
    String logMsg;
    String msg;
    boolean track;
    static File commitPath = new File(System.getProperty("user.dir") + "/.gitlet/Commits");
    public void find(String msg) {
        this.msg = msg;
        track = false;
        if (commitPath.listFiles() != null) {
            for (File f : commitPath.listFiles()) {
                logMsg = new String(Utils.readContents(
                    new File(commitPath + "/" + f.getName() + "logMessage.txt")));
                if (logMsg.equals(msg)) {
                    track = true;
                    System.out.println(f.getName());
                }
            }
            if (!track) {
                System.out.println("There exist no commit with that message.");
            }
        }
    }
}
