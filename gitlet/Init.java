package gitlet;

import java.io.File;

/**
 * Driver class to initialize Gitlet, the tiny stupid version-control system.
 * This program is inspired by Git, but a very simplified version of it.
 * <p>
 *     Usage: java gitlet.Main init
 * <p>
 * Fundamentally, this program stores files in a hidden directory named .gitlet.
 * The .gitlet directory will contain all the meta-information about the files
 * and commits. The .gitlet directory will contain the following subdirectories:
 * <ul>
 *     <li>Commits: stores all the commits</li>
 *     <li>Branches: stores all the branches</li>
 *     <li>Staging: stores all the files that are staged</li>
 * </ul>
 */

public class Init {

    public void init() {
        File workingDir = new File(System.getProperty("user.dir"));
        File gitDir = new File(workingDir, ".gitlet");

        if (gitDir.exists()) {
            System.out.println("Warning:");
            System.out.println("    A gitlet version-control system "
                    + "already exists in the current directory.");
            return;
        }

        if (!gitDir.mkdir()) {
            System.out.println("Error:");
            System.out.println("    Could not create .gitlet directory.");
            return;
        }

        /*
          Stage directory is where files are staged before they are committed.
          This is similar to the .git/index file in Git.
          </>
          The .git/index file in a Git repository is a crucial component of Git's
          version control system. It serves as a staging area and maintains a snapshot
          of the current state of the working directory. The file keeps track of important
          information about the files in the repository, such as file names, timestamps,
          file modes, and file content hashes.
          </>
          When you make changes to files in your working directory and add them using git add,
          the changes are staged in the index file. The index file then serves as an intermediate
          state between the working directory and the actual Git repository. When you run git commit,
          the changes staged in the index are saved as a new commit in the repository.
         */
        File stageDir = new File(gitDir, "stage/");
        if (!stageDir.mkdir()) {
            System.out.println("Error:");
            System.out.println("    Could not create stage directory.");
            cleanUp();
            return;
        }

        /*
          Commits directory is where commits are stored.
          This is similar to the .git/objects directory in Git.
          </>
          The .git/objects directory in a Git repository is essential for storing and managing all the
          objects that Git creates and uses during its operation. Git objects are the internal representation
          of various elements in a Git repository, such as commits, trees, and blobs.
          </>
          There are four main types of Git objects:
          - Blob: A blob object represents the content of a file. It stores the file data but not the file name
                  or other metadata. Each unique file content has a corresponding unique blob object.

          - Tree: A tree object represents a directory. It stores references to the blobs and trees that are
                  contained in the directory, along with the file names and other metadata.

          - Commit: A commit object represents a snapshot of the repository. It is used to mark specific points in
                    the repository's history, typically for versioning or release purposes.
          </>
          The .git/objects directory organizes these objects in a content-addressable storage system. Each object is
          identified by a unique SHA-1 hash, which is based on its content.
         */
        File commitDir = new File(gitDir, "commits/");
        if (!commitDir.mkdir()) {
            System.out.println("Error:");
            System.out.println("    Could not create commits directory.");
            cleanUp();
            return;
        }

        /*
         * Branch directory is where branches are stored.
         * This is similar to the .git/refs directory in Git.
         * This is the same as the Serialized directory in our Study Guide.
         * </>
         * The .git/refs directory in a Git repository is essential for storing and managing references.
         * A reference is a pointer to a commit object. It is used to keep track of the commit that represents
         * the tip of a branch.
         * </>
         * There is a special reference called HEAD, stored in the .git directory itself as .git/HEAD. The HEAD
         * reference points to the current branch, and by extension, to the latest commit on that branch. When you
         * switch branches, the HEAD reference is updated to point to the new branch.
         */
        File branchDir = new File(gitDir, "branch/");
        if (!branchDir.mkdir()) {
            System.out.println("Error:");
            System.out.println("    Could not create branch directory.");
            cleanUp();
        }
    }

    /**
     * Cleans up the .gitlet directory if the init command fails.
     */
    private void cleanUp() {
        File workingDir = new File(System.getProperty("user.dir"));
        File gitDir = new File(workingDir, ".gitlet");
        Utils.deleteDirectory(gitDir);
    }
}
