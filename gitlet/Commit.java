package gitlet;

import java.io.Serializable;
import java.util.HashMap;
import java.time.Instant;

import java.util.Date; // TODO: You'll likely use this in this class

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit {

    private String message;
    private Instant timestamp;
    private HashMap<String, String> blobs;

    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */

    public Commit (String message, Instant timestamp, HashMap<String, String> blobs) {
        this.message = message;
        this.timestamp = timestamp;
        this.blobs = blobs;
    }

    public String getMessage () {
        return message;
    }

    public Instant getTimestamp () {
        return timestamp;
    }

    public HashMap<String, String> getBlobs () {
        return blobs;
    }

    public String getId() {
        return Utils.sha1((Object) Utils.serialize((Serializable) this));
    }
}
