# Mini Twitter (CS3560 – Programming Assignment 2)

A desktop Mini Twitter built with **Java Swing**. The program has a centralized
Admin Control Panel for creating users and groups, opening per-user views,
following other users, posting tweets, and running a few analytics.

The assignment's focus is applying design patterns to build an extensible
system. All four required patterns are used: **Singleton, Observer, Visitor,
and Composite**.

## How to build and run

Requires a JDK (developed and tested with JDK 24; any JDK 8+ should work).

### Windows (one click)
Double-click `run.bat`, or from a terminal:

```bat
run.bat
```

### Any platform (command line)
```bash
# Compile every source file into out/
javac -d out src/minitwitter/Driver.java src/minitwitter/model/*.java src/minitwitter/observer/*.java src/minitwitter/visitor/*.java src/minitwitter/ui/*.java

# Run
java -cp out minitwitter.Driver
```

The entry point is `minitwitter.Driver` (a `main()` method that launches the
Admin Control Panel).

## How to use

1. **Add users / groups** – type an ID in the *User Id* / *Group Id* field and
   click *Add User* / *Add Group*. New entries are added under the currently
   selected group in the tree (or under **Root** if nothing/ a user is
   selected). Groups show a folder icon; users show a file icon.
2. **Open a user view** – select a user in the tree and click *Open User View*.
   You can open multiple user views for different users at the same time.
3. **Follow** – in a user view, type another user's ID and click *Follow User*.
4. **Post a tweet** – type a message and click *Post Tweet*. The tweet appears
   in your own news feed and, automatically, in every follower's news feed and
   open user view.
5. **Analytics** – the four buttons at the bottom of the Admin Control Panel
   pop up the total users, total groups, total tweet messages, and the
   percentage of positive messages.

## Design patterns

| Pattern       | Where it lives | What it does |
|---------------|----------------|--------------|
| **Singleton** | `ui.AdminControlPanel` | One shared control panel for the whole app, reached via `getInstance()`. It also owns the `Root` group and enforces unique IDs. |
| **Composite** | `model.Entry` (component), `model.User` (leaf), `model.UserGroup` (composite) | Users and groups are treated uniformly. A group holds any mix of users and sub-groups recursively, with `Root` at the top. |
| **Observer**  | `observer.Subject`, `observer.Observer`, implemented by `model.User` | A user is a *Subject* to its followers and an *Observer* of the users it follows. Posting a tweet notifies all followers, whose feeds (and open views) refresh automatically. The view refreshes through a feed-change hook on `User`. |
| **Visitor**   | `visitor.Visitor` + `UserTotalVisitor`, `GroupTotalVisitor`, `MessageTotalVisitor`, `PositivePercentageVisitor` | Each analytic is a visitor that traverses the Composite tree, so new analytics can be added without changing `User`/`UserGroup`. |

## Project layout

```
src/minitwitter/
  Driver.java                     # main() – entry point
  model/
    Entry.java                    # Composite component
    User.java                     # Composite leaf + Observer Subject/Observer
    UserGroup.java                # Composite composite
  observer/
    Subject.java                  # Observer pattern: subject contract
    Observer.java                 # Observer pattern: observer contract
  visitor/
    Visitor.java                  # Visitor contract
    UserTotalVisitor.java
    GroupTotalVisitor.java
    MessageTotalVisitor.java
    PositivePercentageVisitor.java
  ui/
    AdminControlPanel.java        # Singleton main window (Figure 1)
    UserView.java                 # Per-user window (Figure 2)
```

## Design decisions / notes

- **IDs are globally unique** across both users and groups, because following a
  user is done by ID and the same ID should never be ambiguous.
- The **`Root` group is counted** as a group by the group-total analytic.
- **Positive words** (good, great, excellent, awesome, …) are defined in
  `PositivePercentageVisitor` and can be extended freely. Only the message body
  (the text after `author:`) is scanned.
- Deleting users/groups and unfollowing are not implemented, as the assignment
  does not require them.
