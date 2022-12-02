package edu.lehigh.cse216.mlc325.backend;

import com.google.api.client.util.DateTime;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.google.api.client.util.DateTime;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        // Testing uploadInfo initialization
        super( testName );
        App.UploadInfo uploadInfo = new App.UploadInfo("(3DFKasdfONS2d34u2DKFLDFJ:sasdf", "www.test.com", new DateTime("1985-04-12T23:20:50.52Z"));
        assertEquals("(3DFKasdfONS2d34u2DKFLDFJ:sasdf", uploadInfo.fileID);
        assertEquals("www.test.com", uploadInfo.webViewLink);
        assertEquals(new DateTime("1985-04-12T23:20:50.52Z"), uploadInfo.viewedbyMeTime);

        // Testing PostData DB constructor
        Database.PostData postData = new Database.PostData(1, "title", "msg", 1, "userid", "username", 0, "link");
        assertEquals(1, postData.mId);
        assertEquals("title" ,postData.mTitle);
        assertEquals("msg" ,postData.mMessage);
        assertEquals("userid" ,postData.mUserId);
        assertEquals("username",postData.mUsername);
        assertEquals(0, postData.mSafePost);
        assertEquals("link", postData.mLink);

        // Testing CommentData DB constructor
        Database.CommentData commentData = new Database.CommentData(1, 2, "userID", "comment", "www.link.com", 0);
        assertEquals(1, commentData.mPostId);
        assertEquals(2 ,commentData.mCommentId);
        assertEquals("userID" ,commentData.mUserId);
        assertEquals("comment" ,commentData.mComment);
        assertEquals("www.link.com",commentData.mCommentLink);
        assertEquals(0, commentData.mSafeComment);

    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
}
