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
    /*public void testAddEntry()
    {
        assertFalse(db==null);
        String test = "Unit test title \nUnit test message";
        Reader inputString = new StringReader(test);
        BufferedReader input = new BufferedReader(inputString);
        assertTrue( App.addRow(db, input) == 1 );
    }*/
    
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
        assertTrue( App.deleteRowPost(db, input) == 1);
    }

    /**
     * Ensure that the constructor populates every field of the object it
     * creates
     */
    /*public void testConstructor() {
        String title = "Test Title";
        String content = "Test Content";
        int id = 17;
        int votes = 4;
        assertTrue(true);
        Database.DataRow d = new Database.DataRow(id, title, content, votes);
        assertTrue(d.mTitle.equals(title));
        assertTrue(d.mMessage.equals(content));
        assertTrue(d.mId == id);
        assertTrue(d.mVotes == votes);
        assertFalse(d.mCreated == null);
    }*/

    /**
     * Ensure that the copy constructor works correctly
     */
    /*public void testCopyconstructor() {
        String title = "Test Title For Copy";
        String content = "Test Content For Copy";
        int id = 177;
        int votes = 3;
        assertTrue(true);
        Database.DataRow d = new Database.DataRow(id, title, content, votes);
        Database.DataRow d2 = new Database.DataRow(d);
        assertTrue(d2.mTitle.equals(d.mTitle));
        assertTrue(d2.mMessage.equals(d.mMessage));
        assertTrue(d2.mId == d.mId);
        assertTrue(d2.mVotes == d.mVotes);
        assertTrue(d2.mCreated.equals(d.mCreated));
    }*/
}
