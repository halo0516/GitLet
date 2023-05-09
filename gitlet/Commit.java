package gitlet;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Represents a gitlet commit object, which is responsible for storing the metadata
 * of a commit.
 *
 * @author Yuxiang Wang
 */

public class Commit implements Serializable {
    private static final String GITLET_DIR = System.getProperty("user.dir") + "/.gitlet";
    private static final File COMMIT_DIR = new File(GITLET_DIR + "/commits");
    private static final File SERIALIZED_DIR = new File(GITLET_DIR + "/branch");
    private static final File STAGING_AREA = new File(GITLET_DIR + "/stage");
    private static final File REMOVED_FILES = new File(GITLET_DIR + "/remove");

    private File prevFiles;
    private final String logMessage;
    private final String timeStamp;
    private String parentHash;
    private final TreeSet<String> stageSet = new TreeSet<>();
    private final TreeMap<String, String> pointers;
    private final TreeSet<String> removedMark;
    private final TreeSet<String> tracked;

    @Override
    public String toString() {
        return "Commit{" +
                "logMessage='" + logMessage + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", parentHash='" + parentHash + '\'' +
                '}';
    }


    /**
     * Constructs a Commit object using a log message and an initial flag.
     * Initializes the commit's log message, timestamp, pointers, tracked files, and removedMark set.
     * If the commit is not initial, sets the previous commit's files.
     * Also adds files from the staging area to the stageSet.
     *
     * @param msg The log message for the commit.
     * @param initial A boolean indicating whether the commit is an initial commit or not.
     */
    public Commit(String msg, boolean initial) {
        this.logMessage = msg;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.timeStamp = df.format(new Date());
        this.pointers = readObjectFromFile(SERIALIZED_DIR + "/pointers.txt", new TreeMap<>());
        this.tracked = readObjectFromFile(SERIALIZED_DIR + "/tracked.txt", new TreeSet<>());
        this.removedMark = readObjectFromFile(SERIALIZED_DIR + "/removedMark" + pointers.get("HEAD") + ".txt",
                new TreeSet<>());

        if (pointers.containsKey("HEAD")) {
            this.parentHash = pointers.get(pointers.get("HEAD"));
        }

        if (!initial) {
            this.prevFiles = new File(COMMIT_DIR + "/" + this.parentHash);
        }

        for (File f : Objects.requireNonNull(STAGING_AREA.listFiles())) {
            stageSet.add(f.getName());
        }
    }

    /**
     * Commits changes to the repository by creating a new commit object and storing it.
     * If there are no changes added to the commit, and it is not initial, prints a message and returns.
     * Deletes files in the removed files directory, and checks if the log message is provided.
     * Creates a new commit folder and writes the commit's log message, timestamp, and parent hash to files.
     * Copies files from the previous commit to the new commit folder and moves files from the staging area.
     * Updates pointers and tracked files and writes them to serialized files.
     *
     * @param initial A boolean indicating whether the commit is an initial commit or not.
     * @throws IOException If an I/O error occurs during the commit process.
     */
    public void commit(boolean initial) throws IOException {
        if (stageSet.isEmpty() && !initial && Objects.requireNonNull(REMOVED_FILES.listFiles()).length == 0) {
            System.out.println("No changes added to the commit.");
            return;
        }

        for (File f : Objects.requireNonNull(REMOVED_FILES.listFiles())) {
            if (!f.delete()) {
                System.out.println("File deletion failed.");
                return;
            }
        }

        if (logMessage == null || logMessage.isEmpty()) {
            System.out.println("Please enter a commit message.");
            return;
        }

        String commitHash = Utils.sha1(this.toString());
        File newFolder = new File(COMMIT_DIR, commitHash);
        if (!newFolder.mkdir()) {
            System.out.println("Commit folder creation failed.");
            return;
        }
        Utils.writeContents(new File(COMMIT_DIR + "/" + commitHash + "/logMessage.txt"),
                (Object) logMessage.getBytes());
        Utils.writeContents(new File(COMMIT_DIR + "/" + commitHash + "/timeStamp.txt"),
                (Object) timeStamp.getBytes());

        if (parentHash != null) {
            Utils.writeContents(new File(COMMIT_DIR + "/" + commitHash + "/parentHash.txt"),
                    (Object) parentHash.getBytes());
        }

        if (prevFiles != null) {
            for (File f : Objects.requireNonNull(prevFiles.listFiles())) {
                if (!stageSet.contains(f.getName()) &&
                        !f.getName().equals("logMessage.txt") &&
                        !f.getName().equals("timeStamp.txt") &&
                        !f.getName().equals("parentHash.txt") &&
                        !removedMark.contains(f.getName())) {
                    Files.copy(f.toPath(), Paths.get(COMMIT_DIR + "/" + commitHash + "/" + f.getName()));
                }
            }
        }

        if (!stageSet.isEmpty()) {
            for (File f : Objects.requireNonNull(STAGING_AREA.listFiles())) {
                if (f.renameTo(new File(COMMIT_DIR + "/" + commitHash + "/" + f.getName()))) {
                    System.out.println("File renaming failed.");
                    return;
                }
                tracked.add(f.getName());
            }
        }

        if (!pointers.isEmpty()) {
            this.parentHash = pointers.get(pointers.get("HEAD"));
        }

        if (parentHash == null) {
            pointers.put("HEAD", "master");
            pointers.put("master", commitHash);
        } else {
            pointers.put(pointers.get("HEAD"), commitHash);
        }

        writeObjectToFile(SERIALIZED_DIR + "/pointers.txt", pointers);
        writeObjectToFile(SERIALIZED_DIR + "/tracked.txt", tracked);
    }

    /**
     * [Helper Method]
     * Reads an object from a file and returns the object, or returns a default value if an exception occurs.
     * Utilizes Java's built-in object serialization to read the object from the file.
     * Catches IOException and ClassNotFoundException and returns the provided default value in case of an error.
     *
     * @param filePath The path to the file containing the serialized object.
     * @param defaultValue The default value to be returned if an exception occurs during deserialization.
     * @param <T> The type of the object to be read from the file.
     * @return The deserialized object if successful, or the default value if an exception occurs.
     */
    private <T> T readObjectFromFile(String filePath, T defaultValue) {
        try (ObjectInputStream inp = new ObjectInputStream(new FileInputStream(filePath))) {
            return (T) inp.readObject();
        } catch (IOException | ClassNotFoundException excp) {
            return defaultValue;
        }
    }

    /**
     * [Helper Method]
     * Writes an object to a file using Java's built-in object serialization.
     * Catches IOException and prints an error message if serialization fails.
     *
     * @param filePath The path to the file where the serialized object will be written.
     * @param obj The object to be serialized and written to the file.
     */
    private void writeObjectToFile(String filePath, Object obj) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(obj);
        } catch (IOException excp) {
            System.out.print("Object serialization failed.");
        }
    }
}
