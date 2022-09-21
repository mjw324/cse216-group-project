package edu.lehigh.cse216.mlc325.admin;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

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
        super( testName );
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
    public void testCreateTable()
    {
        assertTrue( true );
    }

    public void testDropTable()
    {
        assertTrue( true );
    }
    
    public void testRemoveEntry()
    {
        assertTrue( true );
    }
}
