package multex;

//Jdk1_4ExceptionChainTest.java

//2002-07-02  Knabe  Creation

/**JUnit batch test driver for getting the cause of an exception chaining facilities of JDK 1.4.
  Compilable and useful only on JDK 1.4.
*/
public class ReflectionCauseGetterTest extends junit.framework.TestCase {


//Verwaltungsoperationen:

public ReflectionCauseGetterTest(final String name) {
  super(name);
}
public static void main (final String[] args) {
  junit.textui.TestRunner.run(suite());
}
public static junit.framework.Test suite() {
  return new junit.framework.TestSuite(ReflectionCauseGetterTest.class);
}

//Einzelne Testschritte:

private static final Throwable _low = new IllegalArgumentException("XXX");

public void testInvocationTargetException(){ //since JDK 1.1
    final Throwable top = new java.lang.reflect.InvocationTargetException(_low);
    final Throwable cause = Util.getCause(top);
    assertSame(_low, cause);
}

public void testExceptionInInitializerError(){ //since JDK 1.1
    final Throwable top = new java.lang.reflect.InvocationTargetException(_low);
    final Throwable cause = Util.getCause(top);
    assertSame(_low, cause);
}

public void testSQLWarning(){ //since JDK 1.1
	final java.sql.SQLWarning low = new java.sql.SQLWarning("XXX");
    final java.sql.SQLWarning top = new java.sql.SQLWarning();
    top.setNextWarning(low);
    final Throwable cause = Util.getCause(top);
    assertSame(low, cause);
}

public void testSQLException(){ //since JDK 1.1
	final java.sql.SQLException low = new java.sql.SQLException("XXX");
    final java.sql.SQLException top = new java.sql.SQLException();
    top.setNextException(low);
    final Throwable cause = Util.getCause(top);
    assertSame(low, cause);
}


}//Jdk1_4ExceptionChainTest