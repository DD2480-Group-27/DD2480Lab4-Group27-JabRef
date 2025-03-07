# DD2480 Lab4 Report

# Report for assignment 4

## Project

Name: JabRef

URL: [https://github.com/DD2480-Group-27/DD2480Lab4-Group27-JabRef.git](https://github.com/DD2480-Group-27/DD2480Lab4-Group27-JabRef.git)

JabRef is a cross-platform citation management tool which allows users to search acorss scientific catalogues like GoogleScholar, to organise the citations, to customise the citation style, and to use the citations in their papers, and to 

## Onboarding experience

We changed the project from the last lab (geometryapi) . The main reason is we were unfamiliar with the mathematics behind the 2D and 3D gemoetry and therefore we were really having a bad time understanding the logic behind. The project we chose this time is based on two main aspects: 1) the quality of issue structure , 2) the initial build result on the project. The JabRef community is a active one, and also the issue structure is well-formatted (with labels for the issue and detailed description of the issue). We therefore considered this project and look for issues that haven’t been asigned. To get assigned on an issue, we simply have to type `/assign-me` on the comment, then the system will automatically assign you to the issue with a welcoming message and hands-on links. 

The build and run of the project is documented thoroughly, so most of our group can build it successfully. However, the initial test run has 78 fails, which we think is normal compared to other projects and considering that it still have so many open issues. 

The experience is of course different because first the criteria on choosing the project is different from last lab, we indeed spent almost 3 days in selecting the project and the issue we will be working on. 

## Effort spent

Edgar:

1. plenary discussions/meetings;
~4 hours
2. discussions within parts of the group;
~1 hour
3. reading documentation;
~1 hour
4. configuration and setup;
~1 hours
    1. Checked out some generic gradle and gradlew documentation to get a grasp
    2. Checked out JabRef’s documentation related to building the project on your own machine and successfully ran/built/tested the project from terminal
    3. Imported the project in IntelliJ IDE to use IDE’s numerous tools for code navigation and debugging
5. analyzing code/output;
~4 hours (mostly static analysis)
~1 hour (checking on the fix and fix’s consequences)
6. writing documentation;
~0.5 hours
7. writing code;
~0.5 hours
8. running code?
~4 hours (debugging)

---

Yoyo:

1. plenary discussions/meetings;  ~3 hours
2. discussions within parts of the group; ~1 hour
3. reading documentation; ~3-4 hours (including trying out on the JabRef)
4. configuration and setup; 2 hours
    1. I followed the instructions in the documentation on cloning, building and running the JabRef, but I have not been able to succesfully run it inside my Intellij using Gradle run
    2. So I spent so much time in trying out different ways to make it work but still the error pops
    3. However, the jabref starts when I type `./gradlew run` in the terminal
    4. So, I am not able to debug or do output analyse
5. analyzing code/output;  1.5 hour
6. writing documentation; 30 minutes
7. writing code; ~2.5 hours
8. running code? ~30mins

---

Linus:

1. plenary discussions/meetings;
    
    ~ 3 hours
    
2. discussions within parts of the group;
    
    ~ 1 hour
    
3. reading documentation;
    
    ~ 1 hour
    
4. configuration and setup;
    
    ~ 2 hours
    
5. analyzing code/output;
    
    ~ 5 hours
    
6. writing documentation;
    
    ~ 5 min
    
7. writing code;
    
    ~ 1 hour
    
8. running code?
    
    ~ 4 hours (debugging/analyzing code)
    

---

Kristin:

1. plenary discussions/meetings;
    
    ~ 3 hours
    
2. discussions within parts of the group;
~ 1 hour
3. reading documentation;
~ 4 hours
4. configuration and setup;
~ 3 hours 30 min in total
    1. At first I followed the JabRef installation guide: [https://docs.jabref.org/installation](https://docs.jabref.org/installation), cloning the repo (our repo, not the one in the guide) and building with Gradle - which takes some time since the project is so big.
    2. It also took some time to find the right documentation, and understand everything. So the first set-up and configuration was about 1 hour.
    3. Old files from old builds interfered with my examination of code behaviour, so I ended up cloning the repo 4 times during these days - 
    4. Each time also having to delete the old repo + extra files and then re-building etc. I had to poke around a lot of different folders and directories to find the things to delete and research what was safe to delete and how to sold my issues.
    5. Several times the program hung up or I had to restart my computer
5. analyzing code/output;
~ 4 hours 15 min
6. writing documentation;
~ 1.5 hours
7. writing code;
~ 2 hours 
8. running code?
~ 3 hours 35 min

For setting up tools and libraries (step 4), enumerate all dependencies
you took care of and where you spent your time, if that time exceeds
30 minutes.

---

Henrik:

1. plenary discussions/meetings; 
    
    ~3 hours
    
2. discussions within parts of the group; 
    
    ~1 hour
    
3. reading documentation; 
    
    ~3 hours
    
4.  configuration and setup; 
    1. Trying to get VS code to run the code and pass tests 
        
        ~3 hours 
        
    2. Switching over to and learning how to use IntelliJ 
        
        ~2 hours
        
5. analyzing code/output; 
    
    ~2 hours (understaning how the code works)
    
    ~4 hours (checking on usages of function to understand how the change will impact other parts of the code)
    
6. writing documentation; 
    
    ~0 hours
    
7. writing code; 
    
    ~30 min
    
8. running code? 
    
    ~2 hours 
    

---

## Overview of issue(s) and work done.

Title: Fix File Renaming 

URL: [https://github.com/JabRef/jabref/issues/12556](https://github.com/JabRef/jabref/issues/12556)

It is a bug in the naming of the file. Basically now when the user drag and drop a pdf into the JabRef, the citation key is not generated before the BibEntry is created for the file, therefore the file name is missing the citation key.

Scope :
This bug implies searching for the failure point in the code involving the interations. This project uses JavaFX as graphic library and library for the interactions coming from the users. The interaction will then trigger a stack of method calls with effect the file copy with a name following the defined convention in the software (which was not respected before the fix). The code in question is mostly located in the gui package but also part of the model and the logic packages.

A UML diagram for the concerned code location is shown below:

![UML Diagram](./ImportHandler__importFilesInBackground.drawio.svg)

## Functionnality requirement:

Having the imported pdf file on drag and drop from file explorer named in compliance with the regex defined in JabRef (by default “””[bibtexKey] - [title]”””)

## Code changes

### Patch

```jsx
diff --git a/src/main/java/org/jabref/gui/externalfiles/ImportHandler.java b/src/main/java/org/jabref/gui/externalfiles/ImportHandler.java
index 5555ef0..5883e16 100644
--- a/src/main/java/org/jabref/gui/externalfiles/ImportHandler.java
+++ b/src/main/java/org/jabref/gui/externalfiles/ImportHandler.java
@@ -159,6 +159,8 @@ public class ImportHandler {
                                     entry.clearField(StandardField.FILE);
                                     // Modifiers do not work on macOS: https://bugs.openjdk.org/browse/JDK-8264172
                                     // Similar code as org.jabref.gui.preview.PreviewPanel.PreviewPanel
+				                             generateKeys(Collections.singletonList(entry));
+
                                     DragDrop.handleDropOfFiles(List.of(file), transferMode, fileLinker, entry);
                                     entriesToAdd.addAll(pdfEntriesInFile);
                                     addResultToList(file, true, Localization.lang("File was successfully imported as a new entry"));

```

Link to git commit with code change:
https://github.com/DD2480-Group-27/DD2480Lab4-Group27-JabRef/commit/69abc12e31d9c042fca8a9de618914557b238acd

## Test results

Overall results with link to a copy or excerpt of the logs (before/after
refactoring).

Before the fix, there were 2 fails on all the test suites (which contains >1000 tests). After the fix, the tests failed were still the same, which indicate the fix didn’t affect other parts of the code. 

For this fix, we have tried to add a test (`testCitationKeyGeneratedBeforeDragDrop`) in [`importHandlerTest.java`](http://importHandlerTest.java) . The test is not complete due to the obstacles we encountered on setting up the whole UI such that the drag and drop could be simulated. (We are still understanding how we can achieve that so that the drag and drop event that we mock can be fired in the `MainTable` . The ultimate goal of this test is to assertTrue the citation key to be `RESTful2023`  instead of `Optional.empty` after the fix. 

## Overall experience

Overall, this lab made us realize how complex a full software with a graphic interface is, particularly with the user interaction handling. The bug we have fixed involves dropping a file in the interface which made it really hard to test and also very misleading to get back to where the issue is happening (long code analysis session for everyone). The time needed for each of us to grasp the context of the problem was unfortunately considerably longer than the time spent fixing the problem itself which in fact was really compact. As mentioned, the testing of the fix implies recreating the whole interface and its interactions in a test environment which would be really heavy and is probably why this bug still existed.

## Essence evaluation

This assignment posed new practical challenges for us in our teamwork, because half of the group was away for some of the time, and we could not meet up physically to discuss. We were also not able to work during the same hours of the day, but we complemented eachother quite well there, picking up the work where a previous person left. Fortunately, by the time of this assignment we had become quite comfortable and as a team  - somewhere at “in-place/working well” in the Essence Way-of-Working. We had established tools and practices for diciding work and asking for help, and this helped us to still work efficiently. The project this time required us to use a new technical tool - Gradle - which, among other things might have set us back in developing forward, since we had to navigate new things. We would say we are still at the same Essence stage.
