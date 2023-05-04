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

    public static void main(String[] args) throws IOException{

        // TODO: what if args is empty?
        //String firstArg = args[0];
        String firstArg = " ";
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                break;
            // TODO: FILL THE REST IN
            case "commit":
                Commit c = new Commit(args[1], false);
                c.commit(false);
                break;
        }
    }
}