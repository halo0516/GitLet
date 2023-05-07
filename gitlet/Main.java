package gitlet;

import java.io.File;
import java.io.IOException;


/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    private static final String GITLET_DIR = ".gitlet/";

    public static void main(String[] args) throws IOException {
        // TODO: what if args is empty?
        //String firstArg = args[0];
        String firstArg = " ";
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                Init i = new Init();
                i.init();
                break;
            case "add":
                Stage s = new Stage();
                s.add(new File(args[1]));
                break;
            // TODO: FILL THE REST IN
            case "commit":
                Commit c = new Commit(args[1], false);
                c.commit(false);
                break;
            case "rm":
                s = new Stage();
                s.rm(new File(args[1]));
                break;
            case "log":
                Log l = new Log();
                l.log();
                break;
            case "global-log":
                l = new Log();
                l.globallog();
            case "find":
                Find f = new Find();
                f.find(args[1]);
                break;
            case "status":
                s = new Stage();
                s.status();
            case "checkout":
                // TODO
                break;
            case "branch":
                // TODO
                break;
            case "rm-branch":
                // TODO
                break;
            case "reset":
                // TODO
                break;
            case "merge":
                // TODO
                break;
        }
    }
}