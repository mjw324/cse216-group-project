package edu.lehigh.cse216.mlc325.admin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Map;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
// Import Google's JSON library
import com.google.gson.*;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import java.util.Hashtable;
import java.util.HashMap;


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
        System.out.println("  [F] Create linksTable");
        //System.out.println("\n");
        System.out.println("  [A] Alter table");

        System.out.println("  [;] Drop All Tables");
        System.out.println("  [D] Drop ideasTable");
        System.out.println("  [U] Drop profileTable");
        System.out.println("  [S] Drop commentTable");
        System.out.println("  [K] Drop votesTable");
        System.out.println("  [I] Drop linksTable");
        //System.out.println("\n");

        System.out.println("  [1] Query for a specific row of ideasTable");
        System.out.println("  [2] Query for a specific row of profileTable");
        System.out.println("  [3] Query for a specific row of commentTable");
        System.out.println("  [4] Query for a specific row of votesTable");
        System.out.println("  [5] Query for a specific row of linksTable");
        //System.out.println("\n");

        System.out.println("  [*] Query for all rows: ideasTable");
        System.out.println("  [&] Query for all rows: profileTable");
        System.out.println("  [$] Query for all rows: commentTable");
        System.out.println("  [!] Query for all rows: votesTable");
        System.out.println("  [%] Query for all rows: linksTable");
        //System.out.println("\n");

        System.out.println("  [-] Delete a row: ideasTable");
        System.out.println("  [M] Delete a row: profileTable");
        System.out.println("  [N] Delete a row: commentTable");
        System.out.println("  [B] Delete a row: votesTable");
        System.out.println("  [E] Delete a row: linksTable");

        System.out.println("  [+] Insert a new row: ideasTable");
        System.out.println("  [X] Insert a new row: profileTable");
        System.out.println("  [Z] Insert a new row: commentTable");
        System.out.println("  [L] Insert a new row: votesTable");
        System.out.println("  [Y] Insert a new row: linksTable");

        System.out.println("  [~] Update a row: ideasTable");
        System.out.println("  [J] Update a row: profileTable");
        System.out.println("  [H] Update a row: commentTable");
        System.out.println("  [G] Update a row: votesTable");
        System.out.println("  [O] Update a row: linksTable");

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
        String actions = ":TPCVAF;DUSKI12345*&$!%-MNBE+XZLY~JHGOq?";

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
     * @throws IOException
     */
    public static void main(String[] argv) throws IOException {
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
            }else if (action == 'F') {
                createLinksTable(db);
            } else if(action == 'A'){
                alterTable(db);
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
            } else if (action == 'I') {
                dropLinksTable(db);
            } else if (action == '1') {
                queryPost(db, in);
            } else if (action == '2') {
                queryProfile(db, in);
            } else if (action == '3') {
                queryComment(db, in);
            } else if (action == '4') {
                queryVote(db, in);
            }else if (action == '5') {
                queryLink(db, in);
            } else if (action == '*') {
                queryAllPosts(db);
            } else if (action == '&') {
                queryAllProfile(db);
            } else if (action == '$') {
                queryAllComment(db);
            } else if (action == '!') {
                queryAllVotes(db);
            } else if (action == '%') {
                queryAllLinks(db);
            }else if (action == '-') {
                deleteRowPost(db, in);
            } else if (action == 'M') {
                deleteRowProfile(db, in);
            } else if (action == 'N') {
                deleteRowComment(db, in);
            } else if (action == 'B') {
                deleteRowVote(db, in);
            } else if (action == 'E') {
                deleteRowLink(db, in);
            } else if (action == '+') {
                addRow(db, in);
            } else if (action == 'X') {
                addRowProfile(db, in);
            } else if (action == 'Z') {
                addRowComment(db, in);
            } else if (action == 'L') {
                addRowVote(db, in);
            } else if (action == 'Y') {
                addRowLink(db, in);
            } else if (action == '~') {
                updateRow(db, in);
            } else if (action == 'J') {
                updateRowProfile(db, in);
            } else if (action == 'H') {
                updateRowComment(db, in);
            } else if (action == 'G') {
                updateRowVote(db, in);
            }
            else if (action == 'O') {
                updateRowLink(db, in);
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
        db.createLinksTable();
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
    public static void createLinksTable(Database db){
        db.createLinksTable();
    }
    public static void alterTable(Database db){
        db.AlterTable();
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
        db.dropLinksTable();
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
    public static void dropLinksTable(Database db){
        db.dropLinksTable();
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
            System.out.println("  [" + res.mCommentId + "] " + res.mPostId);
            System.out.println("  --> " + res.mComment);
            System.out.println("  UserId: " + res.mUserId);
            System.out.println("    Link:" + res.mCommentLink);
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
    public static void queryLink(Database db, BufferedReader in){
        int id = getInt(in, "Enter the row ID");
        if (id == -1)
            return;
        Database.LinkData res = db.selectOneLink(id);
        if (res != null) {
            System.out.println("  [" + res.mLinkId + "] " + res.mPostId);
            //System.out.println("  --> " + res.mMessage);
            System.out.println("  user: " + res.mUserId);
            System.out.println("  recent activity: "+ res.mDate);
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
            System.out.println("  [" + dr.mCommentId + "] " + dr.mPostId);
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

    public static void queryAllLinks(Database db){
        ArrayList<Database.LinkData> res = db.selectAllLinks();
        if (res == null)
            return;
        System.out.println("  Current LinkTable Contents");
        System.out.println("  -------------------------");
        for (Database.LinkData dr : res) {
            System.out.println(" [" + dr.mLinkId + "]" +" ["+dr.mFileId+ "] "+ dr.mUserId+"  "+   dr.mDate);
        }
    }

    // public static int alterRow(Database db , BufferedReader in){
    //     String table = getString(in, "Enter the name of the table you want to alter");
    //     int option = getInt(in, "Enter which you would like to do: 1) ADD /n 2) MODIFY /n3) DELETE");
    //     if(option == 1){

    //     }

    //     return 1;
    // }
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
        String id = getString(in, "Enter the user ID");
        if (id == "")
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

    public static int deleteRowLink(Database db, BufferedReader in) throws IOException{
        String service_account_info = "{\"type\"  : \"service_account\",\n \"project_id\": \"whispering-sands-78580\",\n\"private_key_id\": \"27701bd8c7a4ff15805c0e76807da892f1545f74\",\n\"private_key\": \"-----BEGIN PRIVATE KEY-----\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCfI9Bp5IKf5I6F\nKhMvz9SusP/zP4l78yVVTwRD0LBqVN5/cEAJnz+X8p5yCA/oPpvS+DlPUqbos0X/\nO/msUmJfXK8xDliax09/OqWX+f7bTfDcLYyuQpfeNbhKTE3qncUJZ3AXhxrHWbCc\nLbdmGbJr5K9PZTWYS0+HDVkJLMCkZCLnvBNFuz1fRA5YxFpILT3Rj5boCHq9gz9x\nWjoA0QVGrSVWLdrBZ2HGtypEoBGA5NXj1R7GZiuC0OwO36FWbM7kkzIcXDsNB0C9\nKehTIKKS/nfAfZ1JpR2AWmlYoi6fx1Hz8jdA6inAQXkJS5Mtxam0CjK5IOHeDJiD\nSu5r7D1HAgMBAAECggEAEPln9zOJf0veAR7YxxEa9WXUbHoKzG7F56XdpZdyib+d\nr+glhyE2TkXHArtmFEvr9lfFp8wAGPKuSlLNBxq5JW95vE4fGKXzuSAPSPjrEdyW\nuVVzxk6SFlXn31I+Pq0vYW5oCrUSynqq7NhaJzN3JYJR9LJYl3LzpksVLnZYjJrP\nyCWIcSEBZCL2xANod5cIzYVKxsOgRwBxigcxv8YDOCgxwhVf3LUD6rAwF07crmNO\nK4I2PtcXLritD75ig8zJEkCs4qlhbTDxJ4bvKBZqibEmEGci4jU+L6dQmp/L0h07\nGfkoLdQAYZoUVcc1G2eZNclAUpXdqwx+XjCt85HiqQKBgQDez5DLGWnVtH8lhhzy\nUaWa08mtr2jdNUHxIBsh9SVPSWNi5dbJt2h/Zle5IEZ/VU+2w/IpzYoNeUTGiw6E\n/nc1PcPeP6wWwEfY0Fqd2otnD2yP54ukVuGUVtElt1EX2M+t6ALO7IweZrNfvj1g\nKC7LCXvwu+QPCkCQthhH+fNEmwKBgQC22Ens2ahvt/0qGKgYtouHVWTT0dXQfLFZ\ndSVR+HcSuOlyX3oXZWV9DeJQCr2ZJ+iZbKgdR2CXKlROUiQcclpT34KQFKlA7fG5\nYS3S6V60LfJs/z6WX6kqbMe+fKr6zSFSKiBtNonJqhtKh9P/uoZnbFFplWUltL9x\nEJSiHVt2xQKBgBtpkBfcvY+kUExOjrslXmmJCvQKc61bgwxmddAcuAVkMw0U1/Mj\nVIDwF3TYSrQZy9/hhaas+gIkXFjM/PFR2Vq8iZ+LV+HIsE41fCCVpbb9R88AnsaO\nRdyZPcwVHK4BZ7OuqsHIioim/ASYhDaTWwZx2UTJ6QoMqdrj/GLGlq5nAoGAa+Mn\ni5fKqVD5ErPFy/86STp76fhwnzpUMyLKSJnBOMzfAluP4Oo1fhqJJQ2RXiOMPas9\nbzlEpy2U3TnekOJwpfjGQ1nNnMBJ10aeEUseVFagKuxY88WyPZQ+MAnDoYUUWjT9\nOTPrDZFP1SRcVRKsZ64kQ5ahPiRuqbpM2XNVGrkCgYBu4JhoDBBnbmywfHqEjhw/\ncHlwYoxfjUPImmDecI2hvFjJ0biQ3g+IQuSr6KSbZE6Bx96r0GDZ+G4fI0w+Zav/\n0CgeRyv6mLcB1Ruvhcs0bP1o1x2e0bWFSw487mrSExizMhl4MGuzX6soG9jISHip\ngoU+7k2OWhLq3eddlu1Prg==\n-----END PRIVATE KEY-----\n\",\n\"client_email\": \"the-buzz-google-cloud@whispering-sands-78580.iam.gserviceaccount.com\",\n\"client_id\": \"107391883808698709977\",\n\"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n\"token_uri\": \"https://oauth2.googleapis.com/token\",\n\"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n\"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/the-buzz-google-cloud%40whispering-sands-78580.iam.gserviceaccount.com\"}";
        String id = getString(in, "Enter link");
        
        // if(fileid ==" "){
        //     fileid = " ";
        // }
        try { 
        InputStream google_service_secret = new ByteArrayInputStream(service_account_info.getBytes(StandardCharsets.UTF_8));
        System.out.println("Passed inputstream");
        GoogleCredentials credentials = GoogleCredentials.fromStream(google_service_secret).createScoped(Arrays.asList(DriveScopes.DRIVE_FILE));
        System.out.println("Passed googlecred");
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
        System.out.println("Passed requestInitializer");
        Drive service = new Drive.Builder(new NetHttpTransport(), 
            GsonFactory.getDefaultInstance(),
            requestInitializer)
            .setApplicationName("Drive Upload")
            .build();
        System.out.println(service.files().list().execute());
        String fileid = getString(in, "Enter a fileid if from google drive, else just press space and enter");
        if(fileid!= " "){
        
            
            service.files().delete(fileid)
                .setFields("id, webViewLink, viewedByMeTime")
                .execute();
            System.out.println(service.files().list().execute());
            // Creating permission for anyone to read file at webViewLink
            Permission newPermission = new Permission();
            newPermission.setType("anyone");
            newPermission.setRole("reader");
          }  
        } catch(GoogleJsonResponseException e) {
            System.out.println("Error");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    // Returns empty obj if failed
        if (id == "")
            return-1;
        int res = db.deleteRowLink(id);
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
        String link = getString(in, "Enter the link");
        if (title.equals("") || message.equals("") || userid.equals(""))
            return -1;
        int res = db.insertRow(title, message, userid, link);
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
        int userid = getInt(in, "Enter userId");
        String comment = getString(in, "Enter comment");
        String link = getString(in, "Enter link ");
        if (postid<0 || userid<0 || comment.equals(""))
            return -1;
        int res = db.insertRowComment(userid, postid,comment, link);
        System.out.println(res + " rows added");
        return res;
    }

    public static int addRowVote(Database db, BufferedReader in){
        int postid = getInt(in, "Enter postId");
        int userid = getInt(in, "Enter userId");
        int votes = getInt(in, "Enter votes");
        if (postid<0 || userid<0 || votes<0)
            return -1;
        int res = db.insertRowVote(postid,userid,votes);
        System.out.println(res + " rows added");
        return res;
    }

    public static int addRowLink(Database db, BufferedReader in){
        String linkid = getString(in, "Enter linkid");
        String fileid = getString(in, "Enter fileid");
        int userId = getInt(in, "Enter userId");
        int postId = getInt(in, "Enter postId");
        int commentId = getInt(in, "Enter commentid");
        String recentActivity = getString(in, "Enter recent Activity Date");
        if (userId<0 || postId<0 )
            return -1;
        int res = db.insertRowLink(linkid,fileid, userId,postId,commentId,recentActivity);
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
        String id = getString(in, "Enter the user ID :> ");
        if (id.equals(""))
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

    public static int updateRowComment(Database db, BufferedReader in){
        int id = getInt(in, "Enter the comment row ID :> ");
        if (id == -1)
        return -1;
        int postId = getInt(in, "Enter postId: ");
        int userId = getInt(in, "Enter userId: ");
        String newComment = getString(in, "Enter new comment: ");
        String link = getString(in, "Enter new link");
        int safe = getInt(in, "Update 0 for safe, 1 for not safe comment");
        int res = db.updateOneComment(id, postId, userId, newComment, link, safe);
        if (res != -1)
            System.out.println("  " + res + " rows updated");
        return res;
    }

    public static int updateRowVote(Database db, BufferedReader in){
        int id = getInt(in, "Enter the vote row ID :> ");
        if (id == -1)
        return -1;
        int userId = getInt(in, "Enter userId: ");
        int votes = getInt(in, "Enter votes: ");
        int res = db.updateOneVotes(id, userId, votes);
        if (res != -1)
            System.out.println("  " + res + " rows updated");
        return res;
    }

    public static int updateRowLink(Database db, BufferedReader in){
        String link = getString(in, "Enter the Link ID :> ");
        if (link == "")
        return -1;
        String fileid = getString(in, "Enter fileid");
        int userId = getInt(in, "Enter userId: ");
        int postId = getInt(in, "Enter postId: ");
        int commentId = getInt(in, "Enter commentId: ");
        String recentActivity = getString(in, "Enter recent activity");
        int res = db.updateOneLink(link, fileid, userId, postId,commentId, recentActivity);
        if (res != -1)
            System.out.println("  " + res + " rows updated");
        return res;
    }
}