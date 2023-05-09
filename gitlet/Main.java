package gitlet;

import java.io.File;
import java.io.IOException;


/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Lang Qin
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */

    public static void main(String[] args) throws IOException {
        if (!isValidInput(args)) {
            return;
        }

        String firstArg = args[0];
        switch (firstArg) {
            case "init" -> (new Init()).init();
            case "add" -> (new Stage()).add(new File(args[1]));
            case "rm" -> (new Stage()).rm(new File(args[1]));
            case "global-log" -> (new Log()).globalLog();
            case "log" -> (new Log()).log();
            case "find" -> find(args);
            case "commit" -> commit(args);
            case "checkout" -> checkout(args);
            case "branch" -> (new Branch()).branch(args[1]);
            case "rm-branch" -> (new Branch()).rmBranch(args[1]);
            case "reset" -> (new Reset()).reset(args[1]);
            case "merge" -> (new Merge()).merge(args[1]);
            default -> {}
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