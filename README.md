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
### 2. Checkout:

**Usage:**
```shell
java gitlet.Main checkout -- [file name]
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
```

**Description:** 
 
Starting at the current head commit, display information about each commit backwards along the commit tree until the initial commit, following the first parent commit links, ignoring any second parents found in merge commits. This set of commit nodes is called the commit’s history.

### 4. branch
**Usage:** 
```shell
java gitlet.Main branch [branch name]
```

**Description:** 
 
Creates a new branch with the given name, and points it at the current head commit. A branch is nothing more than a name for a reference (a SHA-1 identifier) to a commit node. This command does NOT immediately switch to the newly created branch (just as in real Git). Before you ever call branch, your code should be running with a default branch called “master”.

### 5. merge
**Usage:** 
```shell
java gitlet.Main merge [branch name]
```

**Description:** 
 
Merges files from the given branch into the current branch.

## Explanation & Use of Class Material
### 1. Linear Data Structures

In *Gitlet*, the system needs to saver versions of the project periodically whil collaborating with others on a project. If any part of the project is messed up, a previously commited version can be restored. Besides, collaborators should be able to make changes embodied in a commit, and others can incorporate (namely, "merge") corresponding changes into their own versions.
As a result, a coherent set of files needs to be set up to help retrieve an early version of the project or merge an updated part, where the **linked list** does its work. To revert to the state of the files at a certain commit, the system would go to the corresponding node in the linked list and restore the copies of files found there. At the macro-level, the system will use a head pointer to keep track of where in the linked list users currently are. As users make commits, the head pointer will stay at the front of the linked list, indicating that the latest commit reflects the current state of the files.

### 2. Trees

In addition to maintain older and newer versions of files, *Gitlet* will be able to maintain differing versions. For instance, given two different ideas about how to proceed, *Gitlet* allows users to save both versions and switch between them at will. In this "commit tree," each version can be developed separately. In short, there will be two pointers into the tree, representing the furthest point of each branch. At any given time, only one of these is the currently active pointer, which will be the head pointer in the commit tree. Additionally, the head pointer is the pointer at the front of the current branch.

### 3. Hashing

In real *Git*, every object, or every "blob" (which is every commit in the case of *Gitlet*), has a unique integer ID that serves as a reference to the object. An interesting feature of *Git* is that these IDs are universal. That is, unlike a typical Java implementation, two objects with exactly the same content will have the same ID on all systems. In the case of blobs, same content means the same file contents; in the case of commmits, same content means the same metadata, the same mapping of names to references, and the same parent reference. To accomplish this feature, *Git* uses a cryptographic hash function called SHA-1 (Secure Hash 1) that produces a 160-bit integer hash from any sequence of bytes, which will be the approach used in *Gitlet* as well.

## Outstanding Questions
### 1. SHA-1

Since the implementation of *Gitlet* plans on using SHA-1 to simulate the corresponding *Git* feature, further materials on SHA-1 might be needed to handle the possibility of a hashing collision and potential bugs associated with the collision. Furthermore, specific implementations might be needed since there might be a demand to distinguish between hashes for commits and hashes for blobs, which will affect the implementations of the data structures for commits and blobs.

### 2. Going Remote(?)
Although the tentative ideas mimic most of *Git*'s local features to backup users' own files and maintain multiple versions of them, another powerful feature that *Git* has is its remote features that allow collaboration with other people over the internet, while the users all have access to a shared history of all the changes that all the collaborators have made. As the course already exposes us to the internet connection (such as the News Aggregator assignment), we want to explore further if *Git*'s remote features can be achieved in *Gitlet* if time permits, and may need some assistance on this part if we finally decide to have some of *Git*'s online features achieved in *Gitlet*.
