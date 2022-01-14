package multex; //ExceptionTreeTest.java

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import multex.test.MultexAssert;

//2011-03-25  Knabe  Modified to DerbySqlExceptionTest
//2003-05-??  Knabe  Modified to ExceptionTreeTest
//2003-05-??  Knabe  Modified to ExceptionChainTest
//2002-05-30  Knabe  Created from Stack_bt following example junit.samples.VectorTest

/** Tests the problems which appeared with Derby 10.5.3.0_1.
 * This test is formulated without a dependency of Derby.
*/
public class DerbySqlExceptionTest extends MultexAssert {


    @Before public void setUpMaxRecursionDepth(){
        Util.setMaxRecursionDepth(5);
    }
    
    @Test public void testRecursiveSQLException1(){ //since JDK 1.1
        final java.sql.SQLException top = new java.sql.SQLException("TOP");
        top.setNextException(top);
        final Throwable  cause = Util.getCause(top);
        assertEquals(null, cause);
    }

    @Test public void testRecursiveSQLException2(){ //since JDK 1.1
        final java.sql.SQLException low = new java.sql.SQLException("LOW");
        final java.sql.SQLException top = new java.sql.SQLException("TOP");
        /*{
            top.setNextException(top);
            final Throwable  cause = Util.getCause(top);
            assertEquals(null, cause);
        }*/
        {
            top.setNextException(low);
            final Throwable  cause = Util.getCause(top);
            assertEquals(low, cause);
        }
    }
    
    @Test public void testRecursiveSQLException3(){ //since JDK 1.1
        final java.sql.SQLException low1 = new java.sql.SQLException("LOW1");
        final java.sql.SQLException low2 = new java.sql.SQLException("LOW2");
        final java.sql.SQLException top = new java.sql.SQLException("TOP");
        top.setNextException(low1);
        top.setNextException(low2); //Hängt hinten an die Kette dran.
        final Throwable  cause1 = Util.getCause(top);
        assertEquals(low1, cause1);
        final Throwable  cause2 = Util.getCause(cause1);
        assertEquals(low2, cause2);
    }
    
    @Test public void testInfiniteCausalSQLChain(){
        final java.sql.SQLException low = new java.sql.SQLException("LOW");
        final java.sql.SQLException top = new java.sql.SQLException("TOP");
        {
            top.setNextException(low);
            final Throwable  cause = Util.getCause(top);
            assertEquals(low, cause);
        }
        {
            top.setNextException(top); //Should be forbidden, as it creates an infinite recursive list.
            final Throwable  cause = Util.getCause(low);
            assertEquals(top, cause); //Confirms the infiniteness, which is necessary in order to test infinite chains.
        }
        final String multexStackTrace = Msg.getStackTrace(top);
        assertIsStart( ""
            + "... SEVERE: Exceeding maximum causal recursion depth of 5." + Util.lineSeparator
            + Util.wasCausing + Util.lineSeparator
            + "++++java.sql.SQLException: TOP" + Util.lineSeparator
            , multexStackTrace
        );
        final String multexMessages = Msg.getMessages(top);
        assertLongStringEquals(""
                + "..." + Util.lineSeparator
                + "+CAUSE: ..." + Util.lineSeparator
                + "++CAUSE: ..." + Util.lineSeparator
                + "+++CAUSE: java.sql.SQLException: LOW" + Util.lineSeparator
                + "++++CAUSE: java.sql.SQLException: TOP" + Util.lineSeparator
                + "+++++CAUSE: SEVERE problem when reporting an exception: Exceeding maximum causal recursion depth of 5." + Util.lineSeparator
                + "..." + Util.lineSeparator
                , multexMessages
            );
        
        final Throwable topExc = Util.getContainedException(top, SQLException.class);
        assertEquals(top, topExc);
    }
    
    @Test public void infiniteCausalThrowableChainGetContainedException(){
        final IllegalArgumentException low = new IllegalArgumentException("LOW");
        final InvocationTargetException mid = new InvocationTargetException(low, "MID");
        final Failure top = new Failure("TOP", mid);
        low.initCause(top); //Creates illegal cycle.       
        final Throwable topExc = Util.getContainedException(top, Failure.class);
        assertEquals(top, topExc);
        final Throwable midExc = Util.getContainedException(top, InvocationTargetException.class);
        assertEquals(mid, midExc);
        final Throwable lowExc = Util.getContainedException(top, IllegalArgumentException.class);
        assertEquals(low, lowExc);
        final Throwable notFoundExc = Util.getContainedException(top, NoSuchMethodException.class);
        assertEquals(Util.INFINITE_EXCEPTION_CHAIN, notFoundExc);
    }
    
    @Test public void infiniteCausalThrowableChainGetOriginalException(){
        final IllegalArgumentException low = new IllegalArgumentException("LOW");
        final InvocationTargetException mid = new InvocationTargetException(low, "MID");
        final Failure top = new Failure("TOP", mid);
        low.initCause(top); //Creates illegal cycle.               
        final Throwable notFoundExc = Util.getOriginalException(top);
        assertEquals(Util.INFINITE_EXCEPTION_CHAIN, notFoundExc);
    }

    /**Tests a problem which occured with Multex 8.2, and Derby 10.5.3.0_1.
     * The method getSQLException of Derby SqlException wraps the latter into a new JDBC SQLException.
     * This method was erroneously assumed to be a getCause-method by MulTEx 8.2 thus constructing an infinite circle of causes.
     */
    @Test public void errorInMultexGetCauseWithDerbyException(){
      final SqlException top = new SqlException();
      final Throwable causeOut = Util.getCause(top);
      assertEquals(null, causeOut);
      final SQLException wrappedAsSQLException = top.getSQLException();
      assert(wrappedAsSQLException.getCause() == top);
      assertEquals("java.sql.SQLException: multex.DerbySqlExceptionTest$SqlException", wrappedAsSQLException.toString());
      //Tests, that we don't run into endless recursion:
      Msg.getMessages(top); 
      Msg.getStackTrace(top);
    }
    
    /**This class imitates the strange behavior of the org.apache.derby.client.am.SqlException.
     * It has a method getSQLException, which returns the SqlException wrapped into a SQLException.
     * This method was erroneously understood as a cause getter by MulTEx up o 8.2.
     * @author knabe 2011-03-25
     */
    public static final class SqlException extends Exception {
        
        private final SQLException _wrappedException = new SQLException(this);

        public SQLException getSQLException() {
            return _wrappedException;
        }
        
    }
    

}