package gitlet;

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
        if (!isValidInput(args)) {
            return;
        }

        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                (new Init()).init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                break;
            case "global-log":
                (new Log()).globalLog();
                break;
            case "log":
                (new Log()).log();
                break;
            case "find":
                find(args);
                break;
            case "commit":
                commit(args);
                break;
            case "checkout":
                checkout(args);
                break;
        }
    }

    /**
     * Handles the `find` command.
     * @param args the arguments passed to the `find` command
     */
    private static void find(String[] args) {
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            return;
        }

        Find f = new Find();
        f.find(args[1]);
    }

    /**
     * Handles the `commit` command.
     * @param args the arguments passed to the `commit` command
     * @throws IOException if an I/O error occurs
     */
    private static void commit(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Please enter a commit message.");
            return;
        }

        Commit c = new Commit(args[1], false);
        c.commit(false);
    }

    /** Handles the `checkout` command.
     * @param args the arguments passed to the `checkout` command
     */
    private static void checkout(String[] args) {
        if (args.length < 1 || args.length > 4) {
            System.out.println("Incorrect operands.");
            return;
        }

        Checkout co = new Checkout();
        if (args.length == 2) {
            co.checkoutBranch(args[1]);
        } else {
            if (args[1].equals("--")) {
                co.checkoutFile(args[2]);
            }
        }
    }

    /**
     * Checks if the input operands are valid.
     * @param args the input operands
     * @return true if the input operands are valid, false otherwise
     */
    private static boolean isValidInput(String[] args) {
        if (args.length == 0) {
            System.out.println("Error:");
            System.out.println("    Please enter a command.");
            return false;
        }
        return true;
    }
}