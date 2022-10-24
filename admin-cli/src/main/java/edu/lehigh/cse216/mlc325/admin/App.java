package edu.lehigh.cse216.mlc325.admin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Map;

/**
 * App is our basic admin app.  For now, it is a demonstration of the six key 
 * operations on a database: connect, insert, update, query, delete, disconnect
 */
public class App {

    /**
     * Print the menu for our program
     */
    static void menu() {
        System.out.println("Main Menu");
        System.out.println("  [:] Create All Tables");
        System.out.println("  [T] Create ideasTable");
        System.out.println("  [P] Create profileTable");
        System.out.println("  [C] Create commentTable");
        System.out.println("  [V] Create votesTable");
        //System.out.println("\n");

        System.out.println("  [;] Drop All Tables");
        System.out.println("  [D] Drop ideasTable");
        System.out.println("  [U] Drop profileTable");
        System.out.println("  [S] Drop commentTable");
        System.out.println("  [K] Drop votesTable");
        //System.out.println("\n");

        System.out.println("  [1] Query for a specific row of ideasTable");
        System.out.println("  [2] Query for a specific row of profileTable");
        System.out.println("  [3] Query for a specific row of commentTable");
        System.out.println("  [4] Query for a specific row of votesTable");
        //System.out.println("\n");

        System.out.println("  [*] Query for all rows: ideasTable");
        System.out.println("  [&] Query for all rows: profileTable");
        System.out.println("  [$] Query for all rows: commentTable");
        System.out.println("  [!] Query for all rows: votesTable");
        //System.out.println("\n");

        System.out.println("  [-] Delete a row: ideasTable");
        System.out.println("  [M] Delete a row: profileTable");
        System.out.println("  [N] Delete a row: commentTable");
        System.out.println("  [B] Delete a row: votesTable");

        System.out.println("  [+] Insert a new row: ideasTable");
        System.out.println("  [X] Insert a new row: profileTable");
        System.out.println("  [Z] Insert a new row: commentTable");
        System.out.println("  [L] Insert a new row: votesTable");

        System.out.println("  [~] Update a row: ideasTable");
        System.out.println("  [J] Update a row: profileTable");
        System.out.println("  [H] Update a row: commentTable");
        System.out.println("  [G] Update a row: votesTable");

        System.out.println("  [q] Quit Program");
        System.out.println("  [?] Help (this message)");
    }

    /**
     * Ask the user to enter a menu option; repeat until we get a valid option
     * 
     * @param in A BufferedReader, for reading from the keyboard
     * 
     * @return The character corresponding to the chosen menu option
     */
    static char prompt(BufferedReader in) {
        // The valid actions:
        String actions = ":TPCV;DUSK1234*&$!-MNB+XZL~JHGq?";

        // We repeat until a valid single-character option is selected        
        while (true) {
            System.out.print("[" + actions + "] :> ");
            String action;
            try {
                action = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            if (action.length() != 1)
                continue;
            if (actions.contains(action)) {
                return action.charAt(0);
            }
            System.out.println("Invalid Command");
        }
    }

    /**
     * Ask the user to enter a String message
     * 
     * @param in A BufferedReader, for reading from the keyboard
     * @param message A message to display when asking for input
     * 
     * @return The string that the user provided.  May be "".
     */
    static String getString(BufferedReader in, String message) {
        String s;
        try {
            System.out.print(message + " :> ");
            s = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return s;
    }

    /**
     * Ask the user to enter an integer
     * 
     * @param in A BufferedReader, for reading from the keyboard
     * @param message A message to display when asking for input
     * 
     * @return The integer that the user provided.  On error, it will be -1
     */
    static int getInt(BufferedReader in, String message) {
        int i = -1;
        try {
            System.out.print(message + " :> ");
            i = Integer.parseInt(in.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return i;
    }

    /**
     * The main routine runs a loop that gets a request from the user and
     * processes it
     * 
     * @param argv Command-line options.  Ignored by this program.
     */
    public static void main(String[] argv) {
        // get the Postgres configuration from the environment
        Map<String, String> env = System.getenv();

        String db_url = "postgres://xgdepqsdstmfkm:a8aac1d03b480b99c72a4820929f6e7e68c71df4f0a5477bb6f1c5a44bf35039@ec2-3-220-207-90.compute-1.amazonaws.com:5432/d9a3fbla0rorpl";
        db_url = db_url + "?sslmode=require";
        //String db_url = env.get("DATABASE_URL");
        //db_url = db_url + "?sslmode=require";

        // Get a fully-configured connection to the database, or exit 
        // immediately
        Database db = Database.getDatabase(db_url);
        if (db == null)
            return;

        // Start our basic command-line interpreter:
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            // Get the user's request, and do it
            char action = prompt(in);
            if (action == '?') {
                menu();
            } else if (action == 'q') {
                break;
            } else if (action == ':') {
                createTables(db);
            } else if (action == 'T') {
                createPostTable(db);
            } else if (action == 'P') {
                createProfileTable(db);
            } else if (action == 'C') {
                createCommentTable(db);
            } else if (action == 'V') {
                createVotesTable(db);
            } else if (action == ';') {
                dropTables(db);
            } else if (action == 'D') {
                dropPostTable(db);
            } else if (action == 'U') {
                dropProfileTable(db);
            } else if (action == 'S') {
                dropCommentTable(db);
            } else if (action == 'K') {
                dropVotesTable(db);
            } else if (action == '1') {
                queryPost(db, in);
            } else if (action == '2') {
                queryProfile(db, in);
            } else if (action == '3') {
                queryComment(db, in);
            } else if (action == '4') {
                queryVote(db, in);
            } else if (action == '*') {
                queryAllPosts(db);
            } else if (action == '&') {
                queryAllProfile(db);
            } else if (action == '$') {
                queryAllComment(db);
            } else if (action == '!') {
                queryAllVotes(db);
            } else if (action == '-') {
                deleteRowPost(db, in);
            } else if (action == 'M') {
                deleteRowProfile(db, in);
            } else if (action == 'N') {
                deleteRowComment(db, in);
            } else if (action == 'B') {
                deleteRowVote(db, in);
            } else if (action == '+') {
                addRow(db, in);
            } else if (action == 'X') {
                addRowProfile(db, in);
            } else if (action == 'Z') {
                addRowComment(db, in);
            } else if (action == '~') {
                updateRow(db, in);
            } else if (action == 'J') {
                updateRowProfile(db, in);
            } else if (action == 'H') {
                //updateRowComment(db, in);
            } else if (action == 'G') {
                //updateRowVote(db, in);
            }
        }
        // Always remember to disconnect from the database when the program 
        // exits
        db.disconnect();
    }


    //methods to handle admin actions

    /**
     * CREATE TABLES
     * Uses prepared statements in admin/database
     * 
     * @param db the database in which to create the new table
     */
    public static void createTables(Database db){
        db.createPostTable();
        db.createProfileTable();
        db.createCommentTable();
        db.createVotesTable();
    }
    public static void createPostTable(Database db){
        db.createPostTable();
    }
    public static void createProfileTable(Database db){
        db.createProfileTable();
    }
    public static void createCommentTable(Database db){
        db.createCommentTable();
    }
    public static void createVotesTable(Database db){
        db.createVotesTable();
    }

    /**
     * DROP TABLES
     * Uses prepared statements in admin/database
     * 
     * @param db the database in which to drop the table
     */
    public static void dropTables(Database db){
        db.dropPostTable();
        db.dropProfileTable();
        db.dropCommentTable();
        db.dropVotesTable();
    }
    public static void dropPostTable(Database db){
        db.dropPostTable();
    }
    public static void dropProfileTable(Database db){
        db.dropProfileTable();
    }
    public static void dropCommentTable(Database db){
        db.dropCommentTable();
    }
    public static void dropVotesTable(Database db){
        db.dropVotesTable();
    }

    //class DataRow, ProfileData, CommentData, UserVotesData
    
    /**
     * Query a specific row in the database
     * 
     * @param db the database to query a rwo from
     * @param in the bufferedreader to input the row id
     */
    public static void queryPost(Database db, BufferedReader in){
        int id = getInt(in, "Enter the row ID");
        if (id == -1)
            return;
        Database.DataRow res = db.selectOnePost(id);
        if (res != null) {
            System.out.println("  [" + res.mPostId + "] " + res.mTitle);
            System.out.println("  --> " + res.mMessage);
            System.out.println("  votes: " + res.mVotes);
        }
    }

    public static void queryProfile(Database db, BufferedReader in){
        String id = getString(in, "Enter the userid");
        if (id == "")
            return;
        Database.ProfileData res = db.selectOneProfile(id);
        if (res != null) {
            System.out.println("  [" + res.mUserId + "] " + res.mUsername);
            System.out.println("  --> " + res.mEmail);
            System.out.println("  SO: " + res.mSO);
            System.out.println("  GI: " + res.mGI);
            System.out.println("  Note: " + res.mNote);
            System.out.println("  Safe(0 is yes): " + res.mSafeUser);
        }
    }
    public static void queryComment(Database db, BufferedReader in){
        int id = getInt(in, "Enter the row ID");
        if (id == -1)
            return;
        Database.CommentData res = db.selectOneComment(id);
        if (res != null) {
            System.out.println("  [" + res.mPostId + "] " + res.mCommentId);
            System.out.println("  --> " + res.mComment);
            System.out.println("  UserId: " + res.mUserId);
        }
    }
    public static void queryVote(Database db, BufferedReader in){
        int id = getInt(in, "Enter the row ID");
        if (id == -1)
            return;
        Database.UserVotesData res = db.selectOneVote(id);
        if (res != null) {
            System.out.println("  [" + res.mPostId + "] " + res.mUserId);
            //System.out.println("  --> " + res.mMessage);
            System.out.println("  votes: " + res.mVotes);
        }
    }

    //All Posts All tables

    public static void queryAllPosts(Database db){
        ArrayList<Database.DataRow> res = db.selectAllPosts();
        if (res == null)
            return;
        System.out.println("  Current ideasTable Contents");
        System.out.println("  -------------------------");
        for (Database.DataRow dr : res) {
            if(dr.mSafePost == 0){
                System.out.println("  [" + dr.mPostId + "] " + dr.mTitle);
            }
        }
    }

    public static void queryAllProfile(Database db){
        ArrayList<Database.ProfileData> res = db.selectAllProfile();
        if (res == null)
            return;
        System.out.println("  Current profileTable Contents");
        System.out.println("  -------------------------");
        for (Database.ProfileData dr : res) {
            if(dr.mSafeUser == 0){
                System.out.println("  [" + dr.mUserId + "] " + dr.mUsername);
            }
        }
    }

    public static void queryAllComment(Database db){
        ArrayList<Database.CommentData> res = db.selectAllComments();
        if (res == null)
            return;
        System.out.println("  Current commentTable Contents");
        System.out.println("  -------------------------");
        for (Database.CommentData dr : res) {
            System.out.println("  [" + dr.mPostId + "] " + dr.mCommentId);
        }
    }

    public static void queryAllVotes(Database db){
        ArrayList<Database.UserVotesData> res = db.selectAllVotes();
        if (res == null)
            return;
        System.out.println("  Current votesTable Contents");
        System.out.println("  -------------------------");
        for (Database.UserVotesData dr : res) {
            System.out.println("  [" + dr.mPostId + "] " + dr.mVotes);
        }
    }

    /**
     * Delete a row from the database 
     * 
     * @param db the database
     * @param in the bufferedReader to input the row id
     * @return the number of rows deleted (expected 1)
     */
    public static int deleteRowPost(Database db, BufferedReader in){
        int id = getInt(in, "Enter the row ID");
        if (id == -1)
            return-1;
        int res = db.deleteRowPost(id);
        if (res != -1) 
            System.out.println("  " + res + " rows deleted");
        return res;
    }

    public static int deleteRowProfile(Database db, BufferedReader in){
        int id = getInt(in, "Enter the row ID");
        if (id == -1)
            return-1;
        int res = db.deleteRowProfile(id);
        if (res != -1) 
            System.out.println("  " + res + " rows deleted");
        return res;
    }

    public static int deleteRowComment(Database db, BufferedReader in){
        int id = getInt(in, "Enter the row ID");
        if (id == -1)
            return-1;
        int res = db.deleteRowComment(id);
        if (res != -1) 
            System.out.println("  " + res + " rows deleted");
        return res;
    }

    public static int deleteRowVote(Database db, BufferedReader in){
        int id = getInt(in, "Enter the row ID");
        if (id == -1)
            return-1;
        int res = db.deleteRowVote(id);
        if (res != -1) 
            System.out.println("  " + res + " rows deleted");
        return res;
    }

    /**
     * Add a row from the database 
     * 
     * @param db the database
     * @param in the bufferedReader to input the row id
     * @return the number of rows added (expected 1)
     */
    public static int addRow(Database db, BufferedReader in){
        String title = getString(in, "Enter the title");
        String message = getString(in, "Enter the message");
        String userid = getString(in, "Enter userid");
        if (title.equals("") || message.equals("") || userid.equals(""))
            return -1;
        int res = db.insertRow(title, message, userid);
        System.out.println(res + " rows added");
        return res;
    }

    public static int addRowProfile(Database db, BufferedReader in){
        String useridtoken = getString(in, "Enter userid token");
        String SO = getString(in, "Enter your Sexual Orientation");
        String GI = getString(in, "Enter your gender identity");
        String email = getString(in, "Enter your email");
        String username = getString(in, "Enter your username");
        String note = getString(in, "Enter a note");
        if (useridtoken.equals("") || SO.equals("") || GI.equals("") || email.equals("") || username.equals("") || note.equals(""))
            return -1;
        int res = db.insertRowProfile(useridtoken, SO, GI, email, username, note);
        System.out.println(res + " rows added");
        return res;
    }

    public static int addRowComment(Database db, BufferedReader in){
        int postid = getInt(in, "Enter postId");
        int commentid = getInt(in, "Enter commentId");
        int userid = getInt(in, "Enter userId");
        String comment = getString(in, "Enter comment");
        if (postid<0 || commentid<0 || userid<0 || comment.equals(""))
            return -1;
        int res = db.insertRowComment(postid,commentid,userid,comment);
        System.out.println(res + " rows added");
        return res;
    }

    /**
     * Update a row from the database
     * 
     * @param db the database
     * @param in the bufferedReader to input the row id and updates
     * @return the number of rows updated (expected 1)
     */
    public static int updateRow(Database db, BufferedReader in){
        int id = getInt(in, "Enter the row ID :> ");
        if (id == -1)
        return -1;
        String newMessage = getString(in, "Enter the new message");
        int votes = getInt(in, "Enter the new votes :> ");
        int safe = getInt(in, "Update 0 for safe, 1 for not safe post");
        int res = db.updateOne(id, newMessage, votes, safe);
        if (res != -1)
            System.out.println("  " + res + " rows updated");
        return res;
    }

    public static int updateRowProfile(Database db, BufferedReader in){
        int id = getInt(in, "Enter the row ID :> ");
        if (id == -1)
        return -1;
        String newSO = getString(in, "Enter your SO: ");
        String newGI = getString(in, "Enter your GI: ");
        String newEmail = getString(in, "Enter the email: ");
        String newUsername = getString(in, "Enter the username: ");
        String newNote = getString(in, "Enter new note: ");
        int safe = getInt(in, "Update 0 for safe, 1 for not safe profile");
        int res = db.updateOneProfile(id, newSO, newGI, newEmail,newUsername, newNote, safe);
        if (res != -1)
            System.out.println("  " + res + " rows updated");
        return res;
    }

}