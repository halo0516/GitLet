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

    private <T> T readObjectFromFile(String filePath, T defaultValue) {
        try (ObjectInputStream inp = new ObjectInputStream(new FileInputStream(filePath))) {
            return (T) inp.readObject();
        } catch (IOException | ClassNotFoundException excp) {
            return defaultValue;
        }
    }

    private void writeObjectToFile(String filePath, Object obj) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(obj);
        } catch (IOException excp) {
            System.out.print("Object serialization failed.");
        }
    }
}
