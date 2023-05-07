package gitlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.TreeMap;
import java.util.TreeSet;

public class Stage {
    
    static File workingDirectory = new File(System.getProperty("user.dir"));
    static File bPath = new File(System.getProperty("user.dir") + "/.gitlet/branch");
    TreeMap<String, String> id = new TreeMap<>();
    TreeMap<String, String> ptr = new TreeMap<>();
    TreeSet<String> tracked = new TreeSet<>();
    TreeSet<String> removed = new TreeSet<>();
    
    public void add(File f) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(bPath + "/ID.txt"));
            id = (TreeMap) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            id = new TreeMap<>();
        }
        
        try {
            ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(bPath + "/removeMark" + ptr.get("HEAD") + ".txt"));
            removed = (TreeSet) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            removed = new TreeSet<>();
        }
        
        if (!f.exists()) {
            System.out.println("File does not exist.");
        } else if ((new File(workingDirectory + "/.gitlet/remove/" + f.getName())).exists()) {
            (new File(workingDirectory + "/.gitlet/remove/" + f.getName())).delete();
        } else {
            String name = f.getName();
            String hash = Utils.sha1(new String(Utils.readContents(f)));
            String hashID = id.get(name);
            
            if (!id.containsKey(name)) {
                id.put(name, hash);
                removed.remove(name);
                try {
                    Files.copy(f.toPath(), Paths.get(workingDirectory + "/.gitlet/stage/" + name));
                } catch (IOException e) {
                    return;
                }
            } else if (!hashID.equals(hash)) {
                id.replace(name, hashID, hash);
                try {
                    Files.copy(f.toPath(), Paths.get(".gitlet/stage/" + name));
                } catch (IOException e) {
                    return;
                }
                if (removed.contains(name)) {
                    removed.remove(name);
                }
            }
        }
        
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(bPath + "/ID.txt"));
            oos.writeObject(id);
            oos.close();
        } catch (IOException e) {
            System.out.println("Map serialization failed.");
        }
        
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(bPath + "/removeMark" + ptr.get("HEAD") + ".txt"));
            oos.writeObject(removed);
            oos.close();
        } catch (IOException e) {
            System.out.println("Map serialization failed.");
        }
        
    }
    
    public void rm(File f) {
        try {
            ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(bPath + "/pointers.txt"));
            ptr = (TreeMap) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            ptr = new TreeMap<>();
        }
        
        try {
            ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(bPath + "/tracked.txt"));
            tracked = (TreeSet) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            tracked = new TreeSet<>();
        }
        
        try {
            ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(bPath + "/removeMark" + ptr.get("HEAD") + ".txt"));
            tracked = (TreeSet) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            removed = new TreeSet<>();
        }
        
        String head = ptr.get("HEAD");
        String branch = ptr.get(head);
        String commitHash = ptr.get(branch);
        
        File commit = new File(workingDirectory + "/.gitlet/commits/" + commitHash);
        File stage = new File(workingDirectory + "/.gitlet/stage/" + f.getName());
        
        if (tracked.contains(f.getName())) {
            tracked.remove(f.getName());
            removed.add(f.getName());
            File wd = new File(workingDirectory + "/" + f.getName());
            if (wd.exists()) {
                wd.delete();
            }
            try {
                new File(workingDirectory + "/.gitlet/remove/" + f.getName()).createNewFile();
            } catch (IOException e) {
                System.out.println("File not moved to remove folder");
            }
            if (stage.exists()) {
                stage.delete();
            }
        } else if (!tracked.contains(f.getName()) && stage.exists()) {
            stage.delete();
        } else if (!tracked.contains(f.getName()) && !stage.exists()) {
            System.out.println("No reason to remove the file.");
            return;
        }
        
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(bPath + "/tracked.txt"));
            oos.writeObject(tracked);
            oos.close();
        } catch (IOException e) {
            System.out.println("Map serialization failed.");
        }
        
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(bPath + "/removeMark" + ptr.get("HEAD") + ".txt"));
            oos.writeObject(removed);
            oos.close();
        } catch (IOException e) {
            System.out.println("Map serialization failed.");
        }
    }
    
    public void status() {
        TreeMap<String, String> ptrs;
        
        try {
            ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(bPath + "/pointers.txt"));
            ptrs = (TreeMap) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            ptrs = new TreeMap<>();
        }
        
        System.out.println("====== Branches ======");
        for (String key : ptrs.keySet()) {
            if (!key.equals("HEAD") && !key.contains("/")) {
                System.out.println("*" + key);
            } else {
                System.out.println(key);
            }
        }
        System.out.println("\n");
        
        File staged = new File(workingDirectory + "/.gitlet/stage");
        System.out.println("====== Staged Files ======");
        for (File file : staged.listFiles()) {
            System.out.println(file.getName());
        }
        System.out.println("\n");
        
        File rmvd = new File(workingDirectory + "/.gitlet/remove");
        System.out.println("====== Removed Files ======");
        for (File file : rmvd.listFiles()) {
            System.out.println(file.getName());
        }
        System.out.println("\n");
    }
}
