package edu.lehigh.cse216.mlc325.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.net.URISyntaxException;
import java.net.URI;

import java.util.ArrayList;


public class Database {
    /**
     * The connection to the database.  When there is no connection, it should
     * be null.  Otherwise, there is a valid open connection
     */
    private Connection mConnection;

    /**
     * A prepared statement for getting all data in the database
     */
    private PreparedStatement mSelectAllPost;
    private PreparedStatement mSelectAllProfile;
    private PreparedStatement mSelectAllComments;
    private PreparedStatement mSelectAllVote;

    /**
     * A prepared statement for getting one row from the database
     */
    private PreparedStatement mSelectOnePost;
    private PreparedStatement mSelectOneProfile;
    private PreparedStatement mSelectOneComment;
    private PreparedStatement mSelectOneVote;
    
    private PreparedStatement mSelectPostComments;

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
    private PreparedStatement mInsertOnePost;
    private PreparedStatement mInsertOneProfile;
    private PreparedStatement mInsertOneComment;
    private PreparedStatement mInsertOneVote;

    /**
     * A prepared statement for updating a single row in the database
     */
    private PreparedStatement mUpdateOneIdea;

    /**
     * A prepared statement for updating a single row in the votes database
     */
    private PreparedStatement mUpdateOneVote;
    /**
     * A prepared statement for updating a single row in the profile database
     */
    private PreparedStatement mUpdateOneProfile;
    /**
     * A prepared statement for updating a single row in the comment database
     */
    private PreparedStatement mUpdateOneComment;

    private PreparedStatement mDislikeNum;
    private PreparedStatement mLikeNum;

    /**
     * A prepared statement for creating the table in our database
     */
    private PreparedStatement mCreatePostTable;
    private PreparedStatement mCreateProfileTable;
    private PreparedStatement mCreateCommentTable;
    private PreparedStatement mCreateVotesTable;
    private PreparedStatement mCreateLinkTable;


    /**
 * DataRow holds a row of information.  A row of information consists of
 * an identifier, strings for a "title" and "message", and a creation date.
 * 
 * Because we will ultimately be converting instances of this object into JSON
 * directly, we need to make the fields public.  That being the case, we will
 * not bother with having getters and setters... instead, we will allow code to
 * interact with the fields directly.
 */

public static class DataRow{

}

public static class PostData extends DataRow{
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
     * The sum of votes for the idea
     */
    public int mVotes;
    public String mUserId;
    public String mUsername;
    public int mSafePost;
    public String mLink;

    /**
     * Create a new DataRow with the provided id and title/message, and a 
     * creation date based on the system clock at the time the constructor was
     * called
     * 
     * @param postId The id to associate with this row.  Assumed to be unique 
     *           throughout the whole program.
     * 
     * @param title The title string for this row of data
     * 
     * @param message The message string for this row of data
     * 
     * @param votes The number of votes for this row of data
     */
    PostData(int postId, String title, String message, int votes, String userId, String username, int safePost) {
        mId = postId;
        mTitle = title;
        mMessage = message;
        mVotes=votes;
        mUserId = userId;
        mSafePost = safePost;
        mUsername = username;
    }

    /**
     * Copy constructor to create one datarow from another
     */
    PostData(PostData data) {
        mId = data.mId;
        // NB: Strings and Dates are immutable, so copy-by-reference is safe
        mTitle = data.mTitle;
        mMessage = data.mMessage;
        mVotes = data.mVotes;
        mUserId = data.mUserId;
        mSafePost = data.mSafePost;
        mUsername = data.mUsername;
    }
}

public static class ProfileData extends DataRow{
    public final String mId;
    public String mSO;
    public String mGI;
    public String mEmail;
    public String mUsername;
    public String mNote;
    public int mSafeUser;

    ProfileData(String id, String SO, String GI, String email, String username, String note, int safeUser) {
        mId = id;
        mSO = SO;
        mGI = GI;
        mEmail = email;
        mUsername = username;
        mNote = note;
        mSafeUser = safeUser;
    }

    ProfileData(ProfileData data) {
        mId = data.mId;
        mSO = data.mSO;
        mGI = data.mGI;
        mEmail = data.mEmail;
        mUsername = data.mUsername;
        mNote = data.mNote;
        mSafeUser = data.mSafeUser;
    }
}

public static class CommentData extends DataRow{
    public final int mPostId;
    public int mCommentId;
    public String mUserId;
    public String mComment;
    public int mSafeComment;
    public String mCommentLink;


    CommentData(int postId, int commentId, String userId, String comment, String commentLink, int safeComment) {
        mPostId = postId;
        mCommentId = commentId;
        mUserId = userId;
        mComment = comment;
        mSafeComment = safeComment;
        mCommentLink = commentLink;
    }

    CommentData(CommentData data) {
        mPostId = data.mPostId;
        mCommentId = data.mCommentId;
        mUserId = data.mUserId;
        mComment = data.mComment;
        mSafeComment = data.mSafeComment;
        mCommentLink = data.mCommentLink;
    }
}

public static class UserVotesData extends DataRow{
    public final int mPostId;
    public String mUserId;
    public int mVotes;

    UserVotesData(int postId, String userId, int votes) {
        mPostId = postId;
        mUserId = userId;
        mVotes = votes;
    }

    UserVotesData(UserVotesData data) {
        mPostId = data.mPostId;
        mUserId = data.mUserId;
        mVotes = data.mVotes;
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

            String userTable = "profileTable";
            String ideaTable = "ideasTable";
            String commentTable = "commentTable";
            String votesTable = "votesTable";
    

            
            //For reference only, don't need to create in backend
            db.mCreatePostTable = db.mConnection.prepareStatement(
                "CREATE TABLE " + ideaTable + " (postid SERIAL PRIMARY KEY, title VARCHAR(128) "
                + "NOT NULL, message VARCHAR(1024) NOT NULL, votes INT NOT NULL, userid VARCHAR(1024) NOT NULL, link VARCHAR(1024), safe INT NOT NULL)");
            db.mCreateProfileTable = db.mConnection.prepareStatement(
                "CREATE TABLE " + userTable + " (userid VARCHAR(128), SO VARCHAR(128) "
                + "NOT NULL, GI VARCHAR(1024) NOT NULL, email VARCHAR(1024) NOT NULL, username VARCHAR(1024) NOT NULL, note VARCHAR(1024) NOT NULL, safeP INT NOT NULL)");
                db.mCreateCommentTable = db.mConnection.prepareStatement(
                    "CREATE TABLE commentTable (commentid SERIAL PRIMARY KEY, userid VARCHAR(128) "
                    + "NOT NULL, postid INT NOT NULL, comment VARCHAR(1024) NOT NULL, link VARCHAR(1024), safe INT NOT NULL)");
            db.mCreateVotesTable = db.mConnection.prepareStatement(
                "CREATE TABLE " + votesTable + " (postid INT NOT NULL userid VARCHAR(1024) "
                + "NOT NULL, votes INT NOT NULL)");
            db.mCreateLinkTable = db.mConnection.prepareStatement(
                "CREATE TABLE " + votesTable + " (postid INT NOT NULL, userid VARCHAR(1024) "
                + "NOT NULL, last_accessed VARCHAR(1024))");

            // Standard CRUD operations
            db.mDeleteOnePost = db.mConnection.prepareStatement("DELETE FROM " + ideaTable + " WHERE postid = ?");
            db.mDeleteOneProfile = db.mConnection.prepareStatement("DELETE FROM " + userTable + " WHERE userid = ?");
            db.mDeleteOneComment = db.mConnection.prepareStatement("DELETE FROM " + commentTable + " WHERE commentid = ?");
            db.mDeleteOneVote = db.mConnection.prepareStatement("DELETE FROM " + votesTable + " WHERE postid = ? AND WHERE userid=?");

            db.mInsertOnePost = db.mConnection.prepareStatement("INSERT INTO " + ideaTable + " VALUES (default, ?, ?, 0, ?, 0) RETURNING *");
            db.mInsertOneProfile = db.mConnection.prepareStatement("INSERT INTO " + userTable + " VALUES (?, ?, ?, ?, ?, ?, 0)");
            db.mInsertOneComment = db.mConnection.prepareStatement("INSERT INTO " + commentTable + " VALUES (default, ?, ?, ?, ?, 0)");
            db.mInsertOneVote = db.mConnection.prepareStatement("INSERT INTO " + votesTable + " VALUES (?, ?, ?)");

            db.mSelectAllPost = db.mConnection.prepareStatement("SELECT postid, title, message, votes, " + ideaTable + ".userid, username, safe FROM " + ideaTable + " LEFT JOIN " + userTable + " ON " + ideaTable + ".userid = " + userTable + ".userid");
            db.mSelectAllProfile = db.mConnection.prepareStatement("SELECT userid, SO, GI, email, username, note FROM " + userTable);
            db.mSelectAllComments = db.mConnection.prepareStatement("SELECT commentid, userid, postid, comment, link, safe FROM " + commentTable);
            db.mSelectAllVote = db.mConnection.prepareStatement("SELECT postid, userid, votes FROM " + votesTable);
            
            db.mSelectPostComments = db.mConnection.prepareStatement("SELECT commentid, userid, postid, comment, link, safe FROM " + commentTable + " WHERE postid = ?");
            
            db.mSelectOnePost = db.mConnection.prepareStatement("SELECT * from " + ideaTable + " WHERE postid=?");
            db.mSelectOneProfile = db.mConnection.prepareStatement("SELECT * from " + userTable + " WHERE userid=?");
            db.mSelectOneComment = db.mConnection.prepareStatement("SELECT * from " + commentTable + " WHERE commentid=?");
            db.mSelectOneVote = db.mConnection.prepareStatement("SELECT * from " + votesTable + " WHERE postid=? AND WHERE userid=?");

            db.mUpdateOneIdea = db.mConnection.prepareStatement("UPDATE " + ideaTable + " SET message = ?, votes ? WHERE postid = ?");
            db.mUpdateOneVote = db.mConnection.prepareStatement("UPDATE " + votesTable + " SET votes = ? WHERE postid = ? AND WHERE userid = ?");
            db.mUpdateOneProfile = db.mConnection.prepareStatement("UPDATE " + userTable + " SET GI = ?, SO = ?, username = ?, note= ? WHERE userid = ?");
            db.mUpdateOneComment = db.mConnection.prepareStatement("UPDATE " + commentTable + " SET comment = ?, link = ?, safe = ? WHERE commentid = ?");

            db.mLikeNum = db.mConnection.prepareStatement("UPDATE " + ideaTable + " SET votes = votes + ? WHERE postid = ?");
            db.mDislikeNum = db.mConnection.prepareStatement("UPDATE " + ideaTable + " SET votes = votes - ? WHERE postid = ?");
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
     * @return The postID from the new row
     */
    int insertRowIdea(String title, String message, String userid) {
        int postID = -1;
        try {
            mInsertOnePost.setString(1, title);
            mInsertOnePost.setString(2, message);
            mInsertOnePost.setString(3, userid);
            ResultSet result = mInsertOnePost.executeQuery();
            if(result.next()) {
                postID = result.getInt(1);
            }
            

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return postID;
    }

    /**
     * Insert a comment into the database
     * 
     * @param title The title for this new row
     * @param message The message for this new row
     * 
     * @return The number of rows that were inserted
     */
    int insertRowComment(String userId, int postId, String comment, String link) {
        int count = 0;
        try {
            mInsertOneComment.setString(1, userId);
            mInsertOneComment.setInt(2, postId);
            mInsertOneComment.setString(3, comment);
            mInsertOneComment.setString(4, link);
            count += mInsertOneComment.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    int insertRowProfile(String userId, String SO, String GI, String email, String username, String note) {
        int count = 0;
        try {
            mInsertOneProfile.setString(1, userId);
            mInsertOneProfile.setString(2, SO);
            mInsertOneProfile.setString(3, GI);
            mInsertOneProfile.setString(4, email);
            mInsertOneProfile.setString(5, username);
            mInsertOneProfile.setString(6, note);
            count += mInsertOneProfile.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    int insertRowVote(int postid, String userid, int vote) {
        int count = 0;
        try {
            mInsertOneVote.setInt(1, postid);
            mInsertOneVote.setString(2, userid);
            mInsertOneVote.setInt(3, vote);
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
    ArrayList<PostData> selectAllPosts() {
        ArrayList<PostData> res = new ArrayList<PostData>();
        try {
            ResultSet rs = mSelectAllPost.executeQuery();
            while (rs.next()) {
                PostData row = new PostData(rs.getInt("postid"), rs.getString("title"), rs.getString("message"), rs.getInt("votes"), rs.getString("userid"), rs.getString("username"), rs.getInt("safe"));
                if(row.mSafePost==0) //safe post
                    res.add(row);
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
                ProfileData user = new ProfileData(rs.getString("userid"), rs.getString("SO"), rs.getString("GI"), rs.getString("email"),rs.getString("username"),rs.getString("note"),rs.getInt("safe"));
                if(user.mSafeUser==0) //safe user
                    res.add(user);
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
            ResultSet rs = mSelectAllComments.executeQuery();
            while (rs.next()) {
                CommentData comment = new CommentData(rs.getInt("postid"), rs.getInt("commentid"), rs.getString("userid"), rs.getString("comment"), rs.getString("link"), rs.getInt("safe"));
                if(safeUser(comment.mUserId))
                    res.add(comment);
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
                res.add(new UserVotesData(rs.getInt("postid"), rs.getString("userid"), rs.getInt("votes")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    ArrayList<CommentData> selectPostComments(int postId) {
        ArrayList<CommentData> res = new ArrayList<CommentData>();
        try {
            mSelectPostComments.setInt(1, postId);
            ResultSet rs = mSelectPostComments.executeQuery();
            while (rs.next()) {
                CommentData comment = new CommentData(rs.getInt("postid"), rs.getInt("commentid"), rs.getString("userid"), rs.getString("comment"), rs.getString("link"), rs.getInt("safe"));
                //if(safeUser(comment.mUserId))
                res.add(comment);
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
    PostData selectOnePost(int id) {
        PostData res = null;
        try {
            mSelectOnePost.setInt(1, id);
            ResultSet rs = mSelectOnePost.executeQuery();
            if (rs.next()) {
                res = new PostData(rs.getInt("postid"), rs.getString("title"), rs.getString("message"),rs.getInt("votes"),rs.getString("userid"), rs.getString("username"), rs.getInt("safe"));
                if (res.mSafePost != 0){ //unsafe post
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    ProfileData selectOneProfile(String id) {
        ProfileData res = null;
        try {
            mSelectOneProfile.setString(1, id);
            ResultSet rs = mSelectOneProfile.executeQuery();
            if (rs.next()) {
                res = new ProfileData(rs.getString("userid"), rs.getString("SO"), rs.getString("GI"),rs.getString("email"),rs.getString("username"),rs.getString("note"),rs.getInt("safeP"));
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
                res = new CommentData(rs.getInt("postid"), rs.getInt("commentid"), rs.getString("userid"), rs.getString("comment"), rs.getString("link"), rs.getInt("safe"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    UserVotesData selectOneVote(int postId, String userId) {
        UserVotesData res = null;
        try {
            mSelectOneVote.setInt(1, postId);
            mSelectOneVote.setString(2, userId);
            ResultSet rs = mSelectOneVote.executeQuery();
            if (rs.next()) {
                res = new UserVotesData(rs.getInt("postid"), rs.getString("userid"), rs.getInt("votes"));
            }
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
    int like(int id, int numVotes) {
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
    int dislike(int id, int numVotes) {
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

    int deleteRowVote(int id, String userId) {
        int res = -1;
        try {
            mDeleteOneVote.setInt(1, id);
            mDeleteOneVote.setString(2, userId);
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
    int updateOneIdea(int id, String message, int votes) {
        int res = -1;
        try {
            mUpdateOneIdea.setString(1, message);
            mUpdateOneIdea.setInt(2, id);
            mUpdateOneIdea.setInt(3, votes);
            res = mUpdateOneIdea.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }
    /**
     * Update the vote for a row in the database
     * 
     * @param id The id of the row to update
     * @param userId The new mesaage
     * 
     * @return The number of rows that were updated.  -1 indicates an error.
     */
    int updateOneVote(int id, String userId, int votes) {
        int res = -1;
        try {
            mUpdateOneVote.setInt(1, votes);
            mUpdateOneVote.setInt(2, id);
            mUpdateOneVote.setString(3, userId);
            res = mUpdateOneVote.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }
    /**
     * Update the vote for a row in the database
     * 
     * @param id The id of the row to update
     * @param userId The new mesaage
     * 
     * @return The number of rows that were updated.  -1 indicates an error.
     */
    int updateOneProfile(String userid, String GI, String SO, String username, String note) {
        int res = -1;
        try {
            mUpdateOneProfile.setString(1, GI);
            mUpdateOneProfile.setString(2, SO);
            mUpdateOneProfile.setString(3, username);
            mUpdateOneProfile.setString(4, note);
            mUpdateOneProfile.setString(5, userid);
            res = mUpdateOneProfile.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }
    /**
     * Update a comment from commentID
     * 
     * @param id The id of the row to update
     * @param comment The new mesaage
     * 
     * @return The number of rows that were updated.  -1 indicates an error.
     */
    int updateOneComment(int id, String comment, String link, int safe) {
        int res = -1;
        try {
            mUpdateOneComment.setString(1, comment);
            mUpdateOneComment.setString(2, link);
            mUpdateOneComment.setInt(4, id);
            mUpdateOneComment.setInt(3, safe);
            res = mUpdateOneComment.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    boolean safeUser(String userID){
        ProfileData user = selectOneProfile(userID);
        if(user == null){
            return false;
        }
        return user.mSafeUser==0; 
    }
}