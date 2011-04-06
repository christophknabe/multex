package multex; //ReflectionCauseGetterTest.java

//2002-07-02  Knabe  Creation

import org.junit.Test;
import org.junit.Assert;

/**JUnit batch test driver for getting the cause of an exception using the chaining facilities of JDK 1.4,
 * and if this does not determine a cause, using reflection.
  Compilable and useful only from JDK 1.4.
*/
public class ReflectionCauseGetterTest extends Assert {


private static final Throwable _low = new IllegalArgumentException("XXX");

@Test public void invocationTargetException(){ //since JDK 1.1
    final Throwable top = new java.lang.reflect.InvocationTargetException(_low);
    final Throwable cause = Util.getCause(top);
    assertSame(_low, cause);
}

@Test public void exceptionInInitializerError(){ //since JDK 1.1
    final Throwable top = new java.lang.reflect.InvocationTargetException(_low);
    final Throwable cause = Util.getCause(top);
    assertSame(_low, cause);
}

@Test public void aSQLWarning(){ //since JDK 1.1
	final java.sql.SQLWarning low = new java.sql.SQLWarning("XXX");
    final java.sql.SQLWarning top = new java.sql.SQLWarning();
    top.setNextWarning(low);
    final Throwable cause = Util.getCause(top);
    assertSame(low, cause);
}

@Test public void aSQLException(){ //since JDK 1.1
    final java.sql.SQLException low = new java.sql.SQLException("XXX");
    final java.sql.SQLException top = new java.sql.SQLException();
    top.setNextException(low);
    final Throwable cause = Util.getCause(top);
    assertSame(low, cause);
}

/**Tests a problem occured with Derby 10.5.3.0_1. It seems to throw sometimes a org.apache.derby.client.am.SqlException,
 * which returns itself recurringly by getNextException().
 */
@Test public void recursiveSQLException(){ //since JDK 1.1
    final java.sql.SQLException top = new java.sql.SQLException("TOP");
    top.setNextException(top);
    final Throwable cause = Util.getCause(top);
    assertNull(cause);
}


}//ReflectionCauseGetterTest