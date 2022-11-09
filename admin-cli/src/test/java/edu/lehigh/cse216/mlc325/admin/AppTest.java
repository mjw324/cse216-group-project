package edu.lehigh.cse216.mlc325.admin;
import edu.lehigh.cse216.mlc325.admin.Database;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    Database db = Database.getDatabase("postgres://xgdepqsdstmfkm:a8aac1d03b480b99c72a4820929f6e7e68c71df4f0a5477bb6f1c5a44bf35039@ec2-3-220-207-90.compute-1.amazonaws.com:5432/d9a3fbla0rorpl?sslmode=require");
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    //test add and drop table were not implimented because 
    //running the add and drop methods would result in data loss
    //This functionality will have to be tested manually

    /**
     * Ensure that adding an entry works correctly
     */
    public void testAddEntry()
    {
        assertFalse(db==null);
        String test = "Unit test title \nUnit test message \n29843127498";
        Reader inputString = new StringReader(test);
        BufferedReader input = new BufferedReader(inputString);
        assertTrue( App.addRow(db, input) == 1 );
    }

    /**
     * Ensure that removing an entry works correctly
     */
    public void testRemoveEntry()
    {
        ArrayList<Database.DataRow> res = db.selectAllPosts();
        int id = 0;
        for (Database.DataRow dr : res) {
            id = dr.mPostId;
        }
        String test = "" + id;
        Reader inputString = new StringReader(test);
        BufferedReader input = new BufferedReader(inputString);
        assertTrue(App.deleteRowPost(db, input) == 1);
    }

    public void testAddEntryProfile()
    {
        assertFalse(db==null);
        String test = "1\nUnit test SO \nUnit test GI \nUnit Test email \nunit test username \nunit test note";
        Reader inputString = new StringReader(test);
        BufferedReader input = new BufferedReader(inputString);
        assertTrue( App.addRowProfile(db, input) == 1 );
    }

    public void testRemoveProfile()
    {
        ArrayList<Database.ProfileData> res = db.selectAllProfile();
        String id = "";
        for (Database.ProfileData dr : res) {
            id = dr.mUserId;
        }
        //System.out.println(id);
        String test = "" + id;
        Reader inputString = new StringReader(test);
        BufferedReader input = new BufferedReader(inputString);
        assertTrue(App.deleteRowProfile(db, input) == 1);
    }

    /**
     * Ensure that the constructor populates every field of the object it
     * creates
     */
    public void testConstructor() {
        String title = "Test Title";
        String content = "Test Content";
        int id = 17;
        int votes = 4;
        String userid = "testuserid";
        int safe = 1;
        assertTrue(true);
        Database.DataRow d = new Database.DataRow(id, title, content, votes, userid, safe);
        assertTrue(d.mTitle.equals(title));
        assertTrue(d.mMessage.equals(content));
        assertTrue(d.mPostId == id);
        assertTrue(d.mVotes == votes);
        assertTrue(d.mUserId == userid);
        assertTrue(d.mSafePost == safe);
        assertFalse(d.mCreated == null);
    }

    /**
     * Ensure that the copy constructor works correctly
     */
    public void testCopyconstructor() {
        String title = "Test Title For Copy";
        String content = "Test Content For Copy";
        int id = 177;
        int votes = 3;
        String userid = "23";
        int safePost = 0;
        assertTrue(true);
        Database.DataRow d = new Database.DataRow(id, title, content, votes, userid, safePost);
        Database.DataRow d2 = new Database.DataRow(d);
        assertTrue(d2.mTitle.equals(d.mTitle));
        assertTrue(d2.mMessage.equals(d.mMessage));
        assertTrue(d2.mPostId == d.mPostId);
        assertTrue(d2.mVotes == d.mVotes);
        assertTrue(d2.mCreated.equals(d.mCreated));
    }

    public void testConstructorProfile() {
        String userid = "testuserid";
        String SO = "Test SO field";
        String GI = "Test GI field";
        String email = "test@lehigh.edu";
        String username = "";
        String note = "";
        int safe = 0;
        assertTrue(true);
        Database.ProfileData d = new Database.ProfileData(userid, SO, GI, email, username, note, safe);
        assertTrue(d.mUserId.equals(userid));
        assertTrue(d.mSO.equals(SO));
        assertTrue(d.mGI.equals(GI));
        assertTrue(d.mEmail.equals(email));
        assertTrue(d.mUsername.equals(username));
        assertTrue(d.mNote.equals(note));
        assertTrue(d.mSafeUser == safe);
        assertFalse(d.mCreated == null);
    }

    public void testCopyconstructorProfile() {
        String userid = "testuserid";
        String SO = "Test SO field";
        String GI = "Test GI field";
        String email = "test@lehigh.edu";
        String username = "";
        String note = "";
        int safe = 0;
        assertTrue(true);
        Database.ProfileData d = new Database.ProfileData(userid, SO, GI, email, username, note, safe);
        Database.ProfileData d2 = new Database.ProfileData(d);
        assertTrue(d2.mUserId.equals(d.mUserId));
        assertTrue(d2.mSO.equals(d.mSO));
        assertTrue(d2.mGI.equals(d.mGI));
        assertTrue(d2.mEmail.equals(d.mEmail));
        assertTrue(d2.mUsername.equals(d.mUsername));
        assertTrue(d2.mNote.equals(d.mNote));
        assertTrue(d2.mSafeUser == d.mSafeUser);
    }
}
