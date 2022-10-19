package edu.lehigh.cse216.mlc325.backend;

import java.net.URISyntaxException;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;

import java.util.Date;

public class Database {
    /**
     * The connection to the database.  When there is no connection, it should
     * be null.  Otherwise, there is a valid open connection
     */
    private Connection mConnection;

    /**
     * A prepared statement for getting all data in the database
     */
    private PreparedStatement mSelectAll;

    /**
     * A prepared statement for getting one idea from the idea table
     */
    private PreparedStatement mSelectOneIdea;
    /**
     * A prepared statement for getting one profile from the profile table
     */
    private PreparedStatement mSelectOneUser;
    /**
     * A prepared statement for getting one comment from the comment table
     */
    private PreparedStatement mSelectOneComment;
    /**
     * A prepared statement for getting one comment from the comment table
     */
    private PreparedStatement mSelectOneVote;

    /**
     * A prepared statement for deleting a row from the database
     */
    private PreparedStatement mDeleteOne;

    /**
     * A prepared statement for inserting into the idea database
     */
    private PreparedStatement mInsertOneIdea;
    /**
     * A prepared statement for inserting into the votes database
     */
    private PreparedStatement mInsertOneVote;
    /**
     * A prepared statement for inserting into the comment database
     */
    private PreparedStatement mInsertOneComment;
    /**
     * A prepared statement for inserting into the user database
     */
    private PreparedStatement mInsertOneUser;

    /**
     * A prepared statement for updating a single row in the database
     */
    private PreparedStatement mUpdateOne;

    private PreparedStatement mLikeOne;
    private PreparedStatement mDislikeOne;

    private PreparedStatement mLikeNum;
    private PreparedStatement mDislikeNum;

    /**
     * A prepared statement for creating the table in our database
     */
    private PreparedStatement mCreateTable;

    /**
     * A prepared statement for dropping the table in our database
     */
    private PreparedStatement mDropTable;

/**
 * DataRow holds a row of information.  A row of information consists of
 * an identifier, strings for a "title" and "content", and a creation date.
 * 
 * Because we will ultimately be converting instances of this object into JSON
 * directly, we need to make the fields public.  That being the case, we will
 * not bother with having getters and setters... instead, we will allow code to
 * interact with the fields directly.
 */
public static class DataRow {
    /**
     * The unique identifier associated with this element.  It's final, because
     * we never want to change it.
     */
    public final int mId;

    /**
     * The title for this row of data
     */
    public String mTitle;

    /**
     * The content for this row of data
     */
    public String mMessage;
    public int mVotes;

    /**
     * The creation date for this row of data.  Once it is set, it cannot be 
     * changed
     */
    public final Date mCreated;

    /**
     * Create a new DataRow with the provided id and title/content, and a 
     * creation date based on the system clock at the time the constructor was
     * called
     * 
     * @param id The id to associate with this row.  Assumed to be unique 
     *           throughout the whole program.
     * 
     * @param title The title string for this row of data
     * 
     * @param content The content string for this row of data
     */
    DataRow(int id, String title, String message, int likes) {
        mId = id;
        mTitle = title;
        mMessage = message;
        mVotes = likes;
        mCreated = new Date();
    }

    /**
     * Copy constructor to create one datarow from another
     */
    DataRow(DataRow data) {
        mId = data.mId;
        // NB: Strings and Dates are immutable, so copy-by-reference is safe
        mTitle = data.mTitle;
        mMessage = data.mMessage;
        mVotes=data.mVotes;
        mCreated = data.mCreated;
    }
}
    /**
     * The Database constructor is private: we only create Database objects 
     * through the getDatabase() method.
     */
    private Database() {
    }

    /**
     * Get a fully-configured connection to the database
     * 
     * @param ip   The IP address of the database server
     * @param port The port on the database server to which connection requests
     *             should be sent
     * @param user The user ID to use when connecting
     * @param pass The password to use when connecting
     * 
     * @return A Database object, or null if we cannot connect properly
     */
    static Database getDatabase(String db_url) {
        // Create an un-configured Database object
        Database db = new Database();

        String userTable = "profileTable";
        String ideaTable = "ideasTable";
        String commentTable = "commentTable";
        String votesTable = "votesTable";

        // Give the Database object a connection, fail if we cannot get one
        try {
            Class.forName("org.postgresql.Driver");
            URI dbUri = new URI(db_url);
            String username = dbUri.getUserInfo().split(":")[0];
            String password = dbUri.getUserInfo().split(":")[1];
            String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
            Connection conn = DriverManager.getConnection(dbUrl, username, password);
            if (conn == null) {
                System.err.println("Error: DriverManager.getConnection() returned a null object");
                return null;
            }
            db.mConnection = conn;
        } catch (SQLException e) {
            System.err.println("Error: DriverManager.getConnection() threw a SQLException");
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException cnfe) {
            System.out.println("Unable to find postgresql driver");
            return null;
        } catch (URISyntaxException s) {
            System.out.println("URI Syntax Error");
            return null;
        }

        // Attempt to create all of our prepared statements.  If any of these 
        // fail, the whole getDatabase() call should fail
        try {

            // Standard CRUD operations

            //TODO update the params
            db.mInsertOneIdea = db.mConnection.prepareStatement("INSERT INTO " + ideaTable + " VALUES (default, ?, ?, 0)");
            db.mInsertOneUser = db.mConnection.prepareStatement("INSERT INTO " + userTable + " VALUES (default, ?, ?, 0)");
            db.mInsertOneComment = db.mConnection.prepareStatement("INSERT INTO " + commentTable + " VALUES (default, ?, ?, 0)");
            db.mInsertOneVote = db.mConnection.prepareStatement("INSERT INTO " + votesTable + " VALUES (default, ?, ?, 0)");
            
            db.mSelectAll = db.mConnection.prepareStatement("SELECT id, title, message, votes FROM tblData");
            
            db.mSelectOneIdea = db.mConnection.prepareStatement("SELECT * from " + ideaTable + " WHERE id=?");
            db.mSelectOneUser = db.mConnection.prepareStatement("SELECT * from " + userTable + " WHERE id=?");
            db.mSelectOneComment = db.mConnection.prepareStatement("SELECT * from " + commentTable + " WHERE id=?");
            db.mSelectOneVote = db.mConnection.prepareStatement("SELECT * from " + votesTable + " WHERE id=?");
            
            db.mUpdateOne = db.mConnection.prepareStatement("UPDATE tblData SET message = ?, votes = ? WHERE id = ?"); //Add likes to this

            db.mLikeOne = db.mConnection.prepareStatement("UPDATE " + ideaTable + " SET votes = votes + 1 WHERE id = ?");
            db.mDislikeOne = db.mConnection.prepareStatement("UPDATE " + ideaTable + " SET votes = votes - 1 WHERE id = ?");

            db.mLikeNum = db.mConnection.prepareStatement("UPDATE "+ ideaTable + " SET votes = votes + ? WHERE id = ?");
            db.mDislikeNum = db.mConnection.prepareStatement("UPDATE "+ ideaTable + " SET votes = votes - ? WHERE id = ?");
        } catch (SQLException e) {
            System.err.println("Error creating prepared statement");
            e.printStackTrace();
            db.disconnect();
            return null;
        }
        return db;
    }

    /**
     * Close the current connection to the database, if one exists.
     * 
     * NB: The connection will always be null after this call, even if an 
     *     error occurred during the closing operation.
     * 
     * @return True if the connection was cleanly closed, false otherwise
     */
    boolean disconnect() {
        if (mConnection == null) {
            System.err.println("Unable to close connection: Connection was null");
            return false;
        }
        try {
            mConnection.close();
        } catch (SQLException e) {
            System.err.println("Error: Connection.close() threw a SQLException");
            e.printStackTrace();
            mConnection = null;
            return false;
        }
        mConnection = null;
        return true;
    }


    /**
     * Insert a row into the database
     * 
     * @param title The title for this new row
     * @param content The content for this new row
     * 
     * @return The number of rows that were inserted
     */
    int insertRow(String title, String message) {
        int count = 0;
        try {
            mInsertOne.setString(1, title);
            mInsertOne.setString(2, message);
            count += mInsertOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Query the database for a list of all titles and their IDs
     * 
     * @return All rows, as an ArrayList f
     */
    ArrayList<DataRow> selectAll() {
        ArrayList<DataRow> res = new ArrayList<DataRow>();
        try {
            ResultSet rs = mSelectAll.executeQuery(); 
            while (rs.next()) {
                res.add(new DataRow(rs.getInt("id"), rs.getString("title"), rs.getString("message"), rs.getInt("votes")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get all data for a specific row, by ID
     * 
     * @param id The id of the row being requested
     * 
     *
     * @return The data for the requested row, or null if the ID was invalid
     */
    DataRow selectOneIdea(int id) {
        DataRow res = null;
        try {
            mSelectOneIdea.setInt(1, id);
            ResultSet rs = mSelectOneIdea.executeQuery();
            if (rs.next()) {
                res = new DataRow(rs.getInt("id"), rs.getString("title"), rs.getString("message"), rs.getInt("votes"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Delete a row by ID
     * 
     * @param id The id of the row to delete
     * 
     * @return The number of rows that were deleted.  -1 indicates an error.
     */
    int deleteRow(int id) {
        int res = -1;
        try {
            mDeleteOne.setInt(1, id);
            res = mDeleteOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Like post, by ID
     * 
     * @param id The id of the row being requested
     * 
     *
     * @return The data for the requested row, or null if the ID was invalid
     */
    int oneLike(int id) {
        int res = -1;
        try {
            mLikeOne.setInt(1, id);
            res = mLikeOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Dislike post, by ID
     * 
     * @param id The id of the row being requested
     * 
     *
     * @return The data for the requested row, or null if the ID was invalid
     */
    int oneDislike(int id) {
        int res = -1;
        try {
            mDislikeOne.setInt(1, id);
            res = mDislikeOne.executeUpdate();
            /*mSelectOne.setInt(1, id);
            ResultSet rs = mSelectOne.executeQuery();
            if(rs.getInt("votes") > 0){
                mDislikeOne.setInt(1, id);
                res = mDislikeOne.executeUpdate();
            }else{
                System.err.println("No");
            }*/
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }
    
    /**
     * Dislike post, by ID
     * 
     * @param id The id of the row being requested
     * @param numVotes number of votes
     * 
     *
     * @return The data for the requested row, or null if the ID was invalid
     */
    int numLike(int id, int numVotes) {
        int res = -1;
        try {
            mLikeNum.setInt(1, numVotes);
            mLikeNum.setInt(2, id);
            res = mLikeNum.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Number of Dislikes posts not just 1, by ID
     * 
     * @param id The id of the row being requested
     * @param numVotes number of votes
     *
     * @return The data for the requested row, or null if the ID was invalid
     */
    int numDislike(int id, int numVotes) {
        int res = -1;
        try {
            mDislikeNum.setInt(1, numVotes);
            mDislikeNum.setInt(2, id);
            res = mDislikeNum.executeUpdate();
            /*mSelectOne.setInt(1, id);
            ResultSet rs = mSelectOne.executeQuery();
            if(rs.getInt("votes") > 0){
                mDislikeNum.setInt(1, numVotes);
                mDislikeNum.setInt(2, id);
                res = mDislikeNum.executeUpdate();
            }else{
                System.err.println("No");
            }*/
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }


    int updateOne(int id, String message, int likes) {
        int res = -1;
        try {
            mUpdateOne.setString(1, message);
            mUpdateOne.setInt(2, likes);
            mUpdateOne.setInt(3,id);
            res = mUpdateOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }
}