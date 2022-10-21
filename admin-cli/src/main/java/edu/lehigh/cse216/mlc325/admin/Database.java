package edu.lehigh.cse216.mlc325.admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.net.URISyntaxException;
import java.net.URI;

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
    private PreparedStatement mSelectAllProfile;
    private PreparedStatement mSelectAllComment;
    private PreparedStatement mSelectAllVote;

    /**
     * A prepared statement for getting one row from the database
     */
    private PreparedStatement mSelectOnePost;
    private PreparedStatement mSelectOneProfile;
    private PreparedStatement mSelectOneComment;
    private PreparedStatement mSelectOneVote;

    /**
     * A prepared statement for deleting a row from the database
     */
    private PreparedStatement mDeleteOnePost;
    private PreparedStatement mDeleteOneProfile;
    private PreparedStatement mDeleteOneComment;
    private PreparedStatement mDeleteOneVote;

    /**
     * A prepared statement for inserting into the database
     */
    private PreparedStatement mInsertOne;
    private PreparedStatement mInsertOneProfile;
    private PreparedStatement mInsertOneComment;

    /**
     * A prepared statement for updating a single row in the database
     */
    private PreparedStatement mUpdateOne;

    /**
     * A prepared statement for upvoting a single row in the database
     */
    private PreparedStatement mLikeOne;

    /**
     * A prepared statement for downvoting a single row in the database
     */
    private PreparedStatement mDislikeOne;

    private PreparedStatement mDislikeNum;

    private PreparedStatement mLikeNum;

    /**
     * A prepared statement for creating the table in our database
     */
    private PreparedStatement mCreatePostTable;
    private PreparedStatement mCreateProfileTable;
    private PreparedStatement mCreateCommentTable;
    private PreparedStatement mCreateVotesTable;

    /**
     * A prepared statement for dropping the table in our database
     */
    private PreparedStatement mDropPostTable;
    private PreparedStatement mDropProfileTable;
    private PreparedStatement mDropCommentTable;
    private PreparedStatement mDropVotesTable;

    /**
 * DataRow holds a row of information.  A row of information consists of
 * an identifier, strings for a "title" and "message", and a creation date.
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
     * The message for this row of data
     */
    public String mMessage;

    /**
     * The creation date for this row of data.  Once it is set, it cannot be 
     * changed
     */
    public final Date mCreated;

    /**
     * The sum of votes for the idea
     */
    public int mVotes;
    public String mUserId;
    public int mSafePost;

    /**
     * Create a new DataRow with the provided id and title/message, and a 
     * creation date based on the system clock at the time the constructor was
     * called
     * 
     * @param id The id to associate with this row.  Assumed to be unique 
     *           throughout the whole program.
     * 
     * @param title The title string for this row of data
     * 
     * @param message The message string for this row of data
     * 
     * @param votes The number of votes for this row of data
     */
    DataRow(int id, String title, String message, int votes, String Userid, int safePost) {
        mId = id;
        mTitle = title;
        mMessage = message;
        mVotes=votes;
        mUserId = Userid;
        mSafePost = safePost;
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
        mVotes = data.mVotes;
        mUserId = data.mUserId;
        mSafePost = data.mSafePost;
        mCreated = data.mCreated;
    }
}

public static class ProfileData {
    public final int mId;
    public String mSO;
    public String mGI;
    public String mEmail;
    public String mUsername;
    public String mNote;
    public int mSafeUser;
    public final Date mCreated;

    ProfileData(int id, String SO, String GI, String email, String username, String note, int safeUser) {
        mId = id;
        mSO = SO;
        mGI = GI;
        mEmail = email;
        mUsername = username;
        mNote = note;
        mSafeUser = safeUser;
        mCreated = new Date();
    }

    ProfileData(ProfileData data) {
        mId = data.mId;
        mSO = data.mSO;
        mGI = data.mGI;
        mEmail = data.mEmail;
        mUsername = data.mUsername;
        mNote = data.mNote;
        mSafeUser = data.mSafeUser;
        mCreated = data.mCreated;
    }
}

public static class CommentData {
    public final int mPostId;
    public int mCommentId;
    public int mUserId;
    public String mComment;
    public final Date mCreated;

    CommentData(int postId, int commentId, int userId, String comment) {
        mPostId = postId;
        mCommentId = commentId;
        mUserId = userId;
        mComment = comment;
        mCreated = new Date();
    }

    CommentData(CommentData data) {
        mPostId = data.mPostId;
        mCommentId = data.mCommentId;
        mUserId = data.mUserId;
        mComment = data.mComment;
        mCreated = data.mCreated;
    }
}

public static class UserVotesData {
    public final int mPostId;
    public int mUserId;
    public int mVotes;
    public final Date mCreated;

    UserVotesData(int postId, int userId, int votes) {
        mPostId = postId;
        mUserId = userId;
        mVotes = votes;
        mCreated = new Date();
    }

    UserVotesData(UserVotesData data) {
        mPostId = data.mPostId;
        mUserId = data.mUserId;
        mVotes = data.mVotes;
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

        // Give the Database object a connection, fail if we cannot get one
        try {
            Class.forName("org.postgresql.Driver");
            URI dbUri = new URI(db_url);
            String username = dbUri.getUserInfo().split(":")[0];
            String password = dbUri.getUserInfo().split(":")[1];
            String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
            System.out.println(dbUrl);
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
            // NB: we can easily get ourselves in trouble here by typing the
            //     SQL incorrectly.  We really should have things like "tblData"
            //     as constants, and then build the strings for the statements
            //     from those constants.

            // Note: no "IF NOT EXISTS" or "IF EXISTS" checks on table 
            // creation/deletion, so multiple executions will cause an exception
            db.mCreatePostTable = db.mConnection.prepareStatement(
                "CREATE TABLE tblData (id SERIAL PRIMARY KEY, title VARCHAR(128) "
                + "NOT NULL, message VARCHAR(1024) NOT NULL, votes INT NOT NULL, userid VARCHAR(1024) NOT NULL, safe INT NOT NULL)");
            db.mCreateProfileTable = db.mConnection.prepareStatement(
                "CREATE TABLE profileTable (id SERIAL PRIMARY KEY, SO VARCHAR(128) "
                + "NOT NULL, GI VARCHAR(1024) NOT NULL, email VARCHAR(1024) NOT NULL, username VARCHAR(1024) NOT NULL, note VARCHAR(1024) NOT NULL, safeP INT NOT NULL)");
            db.mCreateCommentTable = db.mConnection.prepareStatement(
                "CREATE TABLE commentTable (id SERIAL PRIMARY KEY, userid INT "
                + "NOT NULL, commentid INT NOT NULL, comment VARCHAR(1024) NOT NULL)");
            db.mCreateVotesTable = db.mConnection.prepareStatement(
                "CREATE TABLE votesTable (id SERIAL PRIMARY KEY, votesid INT "
                + "NOT NULL, votes INT NOT NULL)");
            
            db.mDropPostTable = db.mConnection.prepareStatement("DROP TABLE tblData");
            db.mDropProfileTable = db.mConnection.prepareStatement("DROP TABLE profileTable");
            db.mDropCommentTable = db.mConnection.prepareStatement("DROP TABLE commentTable");
            db.mDropVotesTable = db.mConnection.prepareStatement("DROP TABLE votesTable");

            // Standard CRUD operations
            db.mDeleteOnePost = db.mConnection.prepareStatement("DELETE FROM tblData WHERE id = ?");
            db.mDeleteOneProfile = db.mConnection.prepareStatement("DELETE FROM profileTable WHERE id = ?");
            db.mDeleteOneComment = db.mConnection.prepareStatement("DELETE FROM commentTable WHERE id = ?");
            db.mDeleteOneVote = db.mConnection.prepareStatement("DELETE FROM votesTable WHERE id = ?");

            db.mInsertOne = db.mConnection.prepareStatement("INSERT INTO tblData VALUES (default, ?, ?, 0, ?, 0)");
            db.mInsertOneProfile = db.mConnection.prepareStatement("INSERT INTO profileTable VALUES (default, ?, ?, ?, ?, ?, 0)");
            db.mInsertOneComment = db.mConnection.prepareStatement("INSERT INTO commentTable VALUES (default, ?, ?, ?)");

            db.mSelectAll = db.mConnection.prepareStatement("SELECT id, title, message, votes FROM tblData");
            db.mSelectAllProfile = db.mConnection.prepareStatement("SELECT id, SO, GI, email, username, note FROM profileTable");
            db.mSelectAllComment = db.mConnection.prepareStatement("SELECT id, userid, commentid, comment FROM commentTable");
            db.mSelectAllVote = db.mConnection.prepareStatement("SELECT id, votesid, votes FROM votesTable");

            db.mSelectOnePost = db.mConnection.prepareStatement("SELECT * from tblData WHERE id=?");
            db.mSelectOneProfile = db.mConnection.prepareStatement("SELECT * from profileTable WHERE id=?");
            db.mSelectOneComment = db.mConnection.prepareStatement("SELECT * from commentTable WHERE id=?");
            db.mSelectOneVote = db.mConnection.prepareStatement("SELECT * from votesTable WHERE id=?");

            db.mUpdateOne = db.mConnection.prepareStatement("UPDATE tblData SET message = ?, votes ? WHERE id = ?");

            db.mLikeOne = db.mConnection.prepareStatement("UPDATE tblData SET votes = votes + 1 WHERE id = ?");
            db.mDislikeOne = db.mConnection.prepareStatement("UPDATE tblData SET votes = votes - 1 WHERE id = ?");
            db.mLikeNum = db.mConnection.prepareStatement("UPDATE tblData SET votes = votes + votes = ? WHERE id = ?");
            db.mDislikeNum = db.mConnection.prepareStatement("UPDATE tblData SET votes = votes - votes = ? WHERE id = ?");
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
     * @param message The message for this new row
     * 
     * @return The number of rows that were inserted
     */
    int insertRow(String title, String message, String userid) {
        int count = 0;
        try {
            mInsertOne.setString(1, title);
            mInsertOne.setString(2, message);
            mInsertOne.setString(3, userid);
            count += mInsertOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    int insertRowProfile(String SO, String GI, String email, String username, String note) {
        int count = 0;
        try {
            mInsertOneProfile.setString(1, SO);
            mInsertOneProfile.setString(2, GI);
            mInsertOneProfile.setString(3, email);
            mInsertOneProfile.setString(4, username);
            mInsertOneProfile.setString(5, note);
            count += mInsertOneProfile.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    int insertRowComment(int postid, int commentid, int userid, String comment) {
        int count = 0;
        try {
            mInsertOneProfile.setInt(1, postid);
            mInsertOneProfile.setInt(2, commentid);
            mInsertOneProfile.setInt(3, userid);
            mInsertOneProfile.setString(4, comment);
            count += mInsertOneProfile.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Query the database for a list of all titles and their IDs
     * 
     * @return All rows, as an ArrayList
     */
    ArrayList<DataRow> selectAllPosts() {
        ArrayList<DataRow> res = new ArrayList<DataRow>();
        try {
            ResultSet rs = mSelectAll.executeQuery();
            while (rs.next()) {
                res.add(new DataRow(rs.getInt("id"), rs.getString("title"), rs.getString("message"), rs.getInt("votes"), rs.getString("userid"),rs.getInt("safe")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    ArrayList<ProfileData> selectAllProfile() {
        ArrayList<ProfileData> res = new ArrayList<ProfileData>();
        try {
            ResultSet rs = mSelectAllProfile.executeQuery();
            while (rs.next()) {
                res.add(new ProfileData(rs.getInt("id"), rs.getString("SO"), rs.getString("GI"), rs.getString("email"),rs.getString("username"),rs.getString("note"),rs.getInt("safe")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    ArrayList<CommentData> selectAllComments() {
        ArrayList<CommentData> res = new ArrayList<CommentData>();
        try {
            ResultSet rs = mSelectAllComment.executeQuery();
            while (rs.next()) {
                res.add(new CommentData(rs.getInt("id"), rs.getInt("userid"), rs.getInt("commentid"), rs.getString("comment")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    ArrayList<UserVotesData> selectAllVotes() {
        ArrayList<UserVotesData> res = new ArrayList<UserVotesData>();
        try {
            ResultSet rs = mSelectAllVote.executeQuery();
            while (rs.next()) {
                res.add(new UserVotesData(rs.getInt("id"), rs.getInt("id"), rs.getInt("id")));
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
     * @return The data for the requested row, or null if the ID was invalid
     */
    DataRow selectOnePost(int id) {
        DataRow res = null;
        try {
            mSelectOnePost.setInt(1, id);
            ResultSet rs = mSelectOnePost.executeQuery();
            if (rs.next()) {
                res = new DataRow(rs.getInt("id"), rs.getString("title"), rs.getString("message"),rs.getInt("votes"),rs.getString("userid"),rs.getInt("safe"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    ProfileData selectOneProfile(int id) {
        ProfileData res = null;
        try {
            mSelectOneProfile.setInt(1, id);
            ResultSet rs = mSelectOneProfile.executeQuery();
            if (rs.next()) {
                res = new ProfileData(rs.getInt("id"), rs.getString("SO"), rs.getString("GI"),rs.getString("email"),rs.getString("username"),rs.getString("note"),rs.getInt("safe"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    CommentData selectOneComment(int id) {
        CommentData res = null;
        try {
            mSelectOneComment.setInt(1, id);
            ResultSet rs = mSelectOneComment.executeQuery();
            if (rs.next()) {
                res = new CommentData(rs.getInt("id"), rs.getInt("userid"), rs.getInt("commentid"), rs.getString("comment"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    UserVotesData selectOneVote(int id) {
        UserVotesData res = null;
        try {
            mSelectOneVote.setInt(1, id);
            ResultSet rs = mSelectOneVote.executeQuery();
            if (rs.next()) {
                res = new UserVotesData(rs.getInt("id"), rs.getInt("votesid"), rs.getInt("votes"));
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
    int deleteRowPost(int id) {
        int res = -1;
        try {
            mDeleteOnePost.setInt(1, id);
            res = mDeleteOnePost.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    int deleteRowProfile(int id) {
        int res = -1;
        try {
            mDeleteOneProfile.setInt(1, id);
            res = mDeleteOneProfile.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    int deleteRowComment(int id) {
        int res = -1;
        try {
            mDeleteOneComment.setInt(1, id);
            res = mDeleteOneComment.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    int deleteRowVote(int id) {
        int res = -1;
        try {
            mDeleteOneVote.setInt(1, id);
            res = mDeleteOneVote.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Update the message for a row in the database
     * 
     * @param id The id of the row to update
     * @param message The new mesaage
     * 
     * @return The number of rows that were updated.  -1 indicates an error.
     */
    int updateOne(int id, String message, int votes) {
        int res = -1;
        try {
            mUpdateOne.setString(1, message);
            mUpdateOne.setInt(2, id);
            mUpdateOne.setInt(3, votes);
            res = mUpdateOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Create tblData.  If it already exists, this will print an error
     */
    void createPostTable() {
        try {
            mCreatePostTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    void createProfileTable() {
        try {
            mCreateProfileTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    void createCommentTable() {
        try {
            mCreateCommentTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    void createVotesTable() {
        try {
            mCreateVotesTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove tblData from the database.  If it does not exist, this will print
     * an error.
     */
    void dropPostTable() {
        try {
            mDropPostTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    void dropProfileTable() {
        try {
            mDropProfileTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    void dropCommentTable() {
        try {
            mDropCommentTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    void dropVotesTable() {
        try {
            mDropVotesTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}