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
        System.out.println("  [T] Create tblData");
        System.out.println("  [D] Drop tblData");
        System.out.println("  [1] Query for a specific row");
        System.out.println("  [*] Query for all rows");
        System.out.println("  [-] Delete a row");
        System.out.println("  [+] Insert a new row");
        System.out.println("  [~] Update a row");
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
        String actions = "TD1*-+~q?";

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

        //String db_url = "postgres://xgdepqsdstmfkm:a8aac1d03b480b99c72a4820929f6e7e68c71df4f0a5477bb6f1c5a44bf35039@ec2-3-220-207-90.compute-1.amazonaws.com:5432/d9a3fbla0rorpl";
        //db_url = db_url + "?sslmode=require";
        String db_url = env.get("DATABASE_URL");

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
            } else if (action == 'T') {
                createTable(db);
            } else if (action == 'D') {
                dropTable(db);
            } else if (action == '1') {
                query(db, in);
            } else if (action == '*') {
                queryAll(db);
            } else if (action == '-') {
                deleteRow(db, in);
            } else if (action == '+') {
                addRow(db, in);
            } else if (action == '~') {
                updateRow(db, in);
            }
        }
        // Always remember to disconnect from the database when the program 
        // exits
        db.disconnect();
    }


    //methods to handle admin actions

    /**
     * Create table tblData
     * Uses prepared statements in admin/database
     * 
     * @param db the database in which to create the new table
     */
    public static void createTable(Database db){
        db.createTable();
    }

    /**
     * Drop table tblData
     * Uses prepared statements in admin/database
     * 
     * @param db the database in which to drop the table
     */
    public static void dropTable(Database db){
        db.dropTable();
    }

    
    /**
     * Query a specific row in the database
     * 
     * @param db the database to query a rwo from
     * @param in the bufferedreader to input the row id
     */
    public static void query(Database db, BufferedReader in){
        int id = getInt(in, "Enter the row ID");
        if (id == -1)
            return;
        Database.DataRow res = db.selectOne(id);
        if (res != null) {
            System.out.println("  [" + res.mId + "] " + res.mTitle);
            System.out.println("  --> " + res.mMessage);
            System.out.println("  votes: " + res.mVotes);
        }
    }

    /**
     * Query all rows of a database
     * 
     * @param db the database to query from
     */
    public static void queryAll(Database db){
        ArrayList<Database.DataRow> res = db.selectAll();
        if (res == null)
            return;
        System.out.println("  Current Database Contents");
        System.out.println("  -------------------------");
        for (Database.DataRow dr : res) {
            System.out.println("  [" + dr.mId + "] " + dr.mTitle);
        }
    }

    /**
     * Delete a row from the database 
     * 
     * @param db the database
     * @param in the bufferedReader to input the row id
     * @return the number of rows deleted (expected 1)
     */
    public static int deleteRow(Database db, BufferedReader in){
        int id = getInt(in, "Enter the row ID");
        if (id == -1)
            return-1;
        int res = db.deleteRow(id);
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
        if (title.equals("") || message.equals(""))
            return -1;
        int res = db.insertRow(title, message);
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
        int res = db.updateOne(id, newMessage, votes);
        if (res != -1)
            System.out.println("  " + res + " rows updated");
        return res;
    }

}