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
### Commits:
#### Usage:java gitlet.Main commit [message]

#### Description: Saves a snapshot of tracked files in the current commit and staging area so they can be restored at a later time, creating a new commit. The commit is said to be tracking the saved files.
### Checkout:
#### Usages:
java gitlet.Main checkout -- [file name]
java gitlet.Main checkout [commit id] -- [file name]
java gitlet.Main checkout [branch name]

#### Description:
Takes the version of the file as it exists in the head commit and puts it in the working directory, overwriting the version of the file that’s already there if there is one. 
Takes the version of the file as it exists in the commit with the given id, and puts it in the working directory, overwriting the version of the file that’s already there if there is one.
Takes all files in the commit at the head of the given branch, and puts them in the working directory, overwriting the versions of the files that are already there if they exist.
### log
#### Usage: java gitlet.Main log

#### Description: Starting at the current head commit, display information about each commit backwards along the commit tree until the initial commit, following the first parent commit links, ignoring any second parents found in merge commits. This set of commit nodes is called the commit’s history.\

### branch
#### Usage: java gitlet.Main branch [branch name]

#### Description: Creates a new branch with the given name, and points it at the current head commit. A branch is nothing more than a name for a reference (a SHA-1 identifier) to a commit node. This command does NOT immediately switch to the newly created branch (just as in real Git). Before you ever call branch, your code should be running with a default branch called “master”.

### merge
#### Usage: java gitlet.Main merge [branch name]

#### Description: Merges files from the given branch into the current branch.

## Explanation & Use of Class Material

## Outstanding Questions

