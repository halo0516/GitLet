# GitLet

*2023Spring CIT5940 Final Project*

## Group Members:

| Name  | Email |
| ------------- | ------------- |
| Yuxiang Wang  | ywang20@seas.upenn.edu  |
| Zitong Shi  | zshis@seas.upenn.edu  |
| Lang Qin  | langqin@seas.upenn.edu  |
| Jiaying Hou  | jyhou@seas.upenn.edu  |

## Project Description:  

*Gitlet* is a version-control system that mimics some of the basic features of Git. It is implemented in Java and is a command-line application. It is designed to be used by a single user on a single computer. It is not a distributed version-control system.

A version-control system is essentially a backup system for related collections of files. The main functionality that Gitlet supports is:

1. Saving the contents of entire directories of files, called *commits*.

2. Restoring a version of one or more files or entire commits, called *checking out*.

3. Viewing the history of your backups, called *log*.

4. Maintaining related sequences of commits, called *branches*.

5. Merging changes made in one branch into another.

This project is adapted from University of California, Berkeley's CS 61B Data Structures course project. The original project description can be found [here](https://cs61bl.org/su20/projects/gitlet/#acknowledgments).

## Intended Features:
### 1. Commits:
**Usage:**

```shell
java gitlet.Main commit [message]
```

**Description:** 

Saves a snapshot of tracked files in the current commit and staging area so they can be restored at a later time, creating a new commit. The commit is said to be tracking the saved files.

<div style="text-align: center;">
<img src="https://inst.eecs.berkeley.edu/~cs61b/fa19/materials/proj/proj3/image/before_and_after_commit.png" alt="Before and After Commit" width="300" height="200">
</div>

### 2. Checkout:

**Usage:**
```shell
java gitlet.Main checkout [commit id] -- [file name]
java gitlet.Main checkout [branch name]
```

**Description:** 

Takes the version of the file as it exists in the head commit and puts it in the working directory, overwriting the version of the file that’s already there if there is one. 
Takes the version of the file as it exists in the commit with the given id, and puts it in the working directory, overwriting the version of the file that’s already there if there is one.
Takes all files in the commit at the head of the given branch, and puts them in the working directory, overwriting the versions of the files that are already there if they exist.
### 3. log

**Usage:** 
```shell
java gitlet.Main log
java gitlet.Main global-log
```

**Description:** 
 
Starting at the current head commit, display information about each commit backwards along the commit tree until the initial commit, following the first parent commit links, ignoring any second parents found in merge commits. This set of commit nodes is called the commit’s history.

### 4. branch
**Usage:** 
```shell
java gitlet.Main branch [branch name]
java gitlet.Main rm-branch [branch name]
```

**Description:** 
 
Creates a new branch with the given name, and points it at the current head commit. A branch is nothing more than a name for a reference (a SHA-1 identifier) to a commit node. This command does NOT immediately switch to the newly created branch (just as in real Git). Before you ever call branch, your code should be running with a default branch called “master”.

<div style="text-align: center;">
<img src="https://inst.eecs.berkeley.edu/~cs61b/fa19/materials/proj/proj3/image/split_point.png" alt="Before and After Commit" width="400" height="200">
</div>

### 5. merge
**Usage:** 
```shell
java gitlet.Main merge [branch name]
```

**Description:**

Merges files from the given branch into the current branch.

<div style="text-align: center;">
<img src="https://inst.eecs.berkeley.edu/~cs61b/fa19/materials/proj/proj3/image/two_developed_versions.png" alt="Before and After Commit" width="400" height="200">
</div>

### 6. status
**Usage:**
```shell
java gitlet.Main status
```

**Description:**

Tell the user what branches currently exist, and marks the current branch with a *. Also tell the user what files have been staged or marked for untracking.

### 7. add / remove
**Usage:**
```shell
java gitlet.Main add [file name]
java gitlet.Main rm [file name]
```

**Description:**

Adds a copy of the file as it currently exists to the staging area. Untrack the file; that is, indicate (somewhere in the .gitlet directory) that it is not to be included in the next commit, even if it is tracked in the current commit (which will become the next commit’s parent).

## Explanation & Use of Class Material
### 1. Linear Data Structures

In *Gitlet*, the system needs to saver versions of the project periodically whil collaborating with others on a project. If any part of the project is messed up, a previously commited version can be restored. Besides, collaborators should be able to make changes embodied in a commit, and others can incorporate (namely, "merge") corresponding changes into their own versions.
As a result, a coherent set of files needs to be set up to help retrieve an early version of the project or merge an updated part, where the **linked list** does its work. To revert to the state of the files at a certain commit, the system would go to the corresponding node in the linked list and restore the copies of files found there. At the macro-level, the system will use a head pointer to keep track of where in the linked list users currently are. As users make commits, the head pointer will stay at the front of the linked list, indicating that the latest commit reflects the current state of the files.

### 2. Trees

In addition to maintain older and newer versions of files, *Gitlet* will be able to maintain differing versions. For instance, given two different ideas about how to proceed, *Gitlet* allows users to save both versions and switch between them at will. In this "commit tree," each version can be developed separately. In short, there will be two pointers into the tree, representing the furthest point of each branch. At any given time, only one of these is the currently active pointer, which will be the head pointer in the commit tree. Additionally, the head pointer is the pointer at the front of the current branch.

### 3. Hashing

In real *Git*, every object, or every "blob" (which is every commit in the case of *Gitlet*), has a unique integer ID that serves as a reference to the object. An interesting feature of *Git* is that these IDs are universal. That is, unlike a typical Java implementation, two objects with exactly the same content will have the same ID on all systems. In the case of blobs, same content means the same file contents; in the case of commmits, same content means the same metadata, the same mapping of names to references, and the same parent reference. To accomplish this feature, *Git* uses a cryptographic hash function called SHA-1 (Secure Hash 1) that produces a 160-bit integer hash from any sequence of bytes, which will be the approach used in *Gitlet* as well.

## Design Overview

### 1. `.gitlet` Directory

The `.gitlet` directory will be the main directory that stores all the information about the commits, blobs, and branches. It will contain the following subdirectories:

- `commit`: Commits directory is where commits are stored. This is similar to the `.git/objects` directory in Git. The `.git/objects` directory in a Git repository is essential for storing and managing all the objects that Git creates and uses during its operation. Git objects are the internal representation of various elements in a Git repository, such as commits, trees, and blobs. There are four main types of Git objects:
          
  - Blob: A blob object represents the content of a file. It stores the file data but not the file name or other metadata. Each unique file content has a corresponding unique blob object.

  - Tree: A tree object represents a directory. It stores references to the blobs and trees that are contained in the directory, along with the file names and other metadata.

  - Commit: A commit object represents a snapshot of the repository. It is used to mark specific points in the repository's history, typically for versioning or release purposes. The `.git/objects` directory organizes these objects in a content-addressable storage system. Each object is identified by a unique SHA-1 hash, which is based on its content.

- `branch`: Branch directory is where branches are stored. This is similar to the `.git/refs` directory in Git. This is the same as the Serialized directory in our Study Guide. The `.git/refs` directory in a Git repository is essential for storing and managing references. A reference is a pointer to a commit object. It is used to keep track of the commit that represents the tip of a branch. There is a special reference called HEAD, stored in the .git directory itself as `.git/HEAD`. The HEAD reference points to the current branch, and by extension, to the latest commit on that branch. When you switch branches, the HEAD reference is updated to point to the new branch.

- `stage`: Stage directory is where files are staged before they are committed. This is similar to the .git/index file in Git. The .git/index file in a Git repository is a crucial component of Git's version control system. It serves as a staging area and maintains a snapshot of the current state of the working directory. The file keeps track of important information about the files in the repository, such as file names, timestamps, file modes, and file content hashes. When you make changes to files in your working directory and add them using git add, the changes are staged in the index file. The index file then serves as an intermediate state between the working directory and the actual Git repository. When you run git commit the changes staged in the index are saved as a new commit in the repository.

- `remove`: Remove directory is a customized directory unlike any other existing `.git` subdirectories. This directory is to store the
files removed by the user using the `gitlet rm` command. The files in this directory will be removed from the working directory and the staging area. This directory is to be used when the user wants to untrack a file that was tracked in the current commit. The user can then restore the file by using the `gitlet checkout` command.

### 2. Operation Implementation

We divide our project into `Java` classes based on the operations that we need to implement. For example, `add` and `rm` will move/remove certain files to/from the staging area of the git. Then, we construct a `Stage` object responsible for the two operations. Similarly, we have `Commit` and `Branch` objects to handle the commit and branch operations. Finally, to drive the complete `Gitlet` version control system, we use `Main` to process the input arguments.

## Outstanding Questions
### 1. SHA-1

Since the implementation of *Gitlet* plans on using SHA-1 to simulate the corresponding *Git* feature, further materials on SHA-1 might be needed to handle the possibility of a hashing collision and potential bugs associated with the collision. Furthermore, specific implementations might be needed since there might be a demand to distinguish between hashes for commits and hashes for blobs, which will affect the implementations of the data structures for commits and blobs.

### 2. Going Remote(?)
Although the tentative ideas mimic most of *Git*'s local features to backup users' own files and maintain multiple versions of them, another powerful feature that *Git* has is its remote features that allow collaboration with other people over the internet, while the users all have access to a shared history of all the changes that all the collaborators have made. As the course already exposes us to the internet connection (such as the News Aggregator assignment), we want to explore further if *Git*'s remote features can be achieved in *Gitlet* if time permits, and may need some assistance on this part if we finally decide to have some of *Git*'s online features achieved in *Gitlet*.
