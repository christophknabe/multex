package multex; //ExceptionChainTest.java

import org.junit.Test;
import org.junit.Assert;
import multex.test.NotextException3Chain;

//2003-07-01  Knabe  Test case for Failure.getOriginalException()
//2003-05-??  Knabe  Modified to ExceptionChainTest
//2002-05-30  Knabe  Created from Stack_bt following example junit.samples.VectorTest

/**JUnit batch test driver for the exception chaining facilities of MulTEx,
  JDK 1.1, and individual legacy-chained exceptions.
*/
public class ExceptionChainTest extends Assert {


@Test public void jdk1_1Exceptions(){
    //Construct a chain of the 4 legacy-chained exceptions in JDK 1.1:
    final java.sql.DataTruncation ex5 = new java.sql.DataTruncation(7,false,true,20,5);
    final java.sql.SQLWarning   ex4 = new java.sql.SQLWarning();
    ex4.setNextWarning(ex5);
    final java.sql.SQLException ex3 = new java.sql.SQLException();
    ex3.setNextException(ex4);
    final ExceptionInInitializerError ex2 = new ExceptionInInitializerError(ex3);
    final Throwable ex1 = new java.lang.reflect.InvocationTargetException(ex2);

    //Get the causes by Failure.getCause(Throwable) and check them:
    final Throwable ex1Cause = Util.getCause(ex1);
    assertEquals(ex2, ex1Cause);
    final Throwable ex1CauseCause = Util.getCause(ex1Cause);
    assertEquals(ex3, ex1CauseCause);
    final Throwable ex1CauseCauseCause = Util.getCause(ex1CauseCause);
    assertEquals(ex4, ex1CauseCauseCause);
    final Throwable ex1CauseCauseCauseCause = Util.getCause(ex1CauseCauseCause);
    assertEquals(ex5, ex1CauseCauseCauseCause);
    final Throwable ex1CauseCauseCauseCauseCause = Util.getCause(ex1CauseCauseCauseCause);
    assertEquals(null, ex1CauseCauseCauseCauseCause);

    //Check in a more automated way:
    checkCauseChain(ex1, new Throwable[]{ex2,ex3,ex4,ex5});
}

@Test public void legacyExceptions(){
    //Construct a chain of the legacy-chained exceptions in JDK 1.2:
    final java.io.FileNotFoundException ex5 = new java.io.FileNotFoundException("report.lis");
    final java.awt.print.PrinterIOException ex4 = new java.awt.print.PrinterIOException(ex5);
    final LegacyException ex3 = new LegacyException(ex4);
    final java.security.PrivilegedActionException ex2 = new java.security.PrivilegedActionException(ex3);
    final ClassNotFoundException ex1 = new ClassNotFoundException("java.lang.Boolean", ex2);

    //Now check the chain:
    checkCauseChain(ex1, new Throwable[]{ex2,ex3,ex4,ex5});
}

@Test public void exceptionsWithoutThrowableGetter(){
    //Construct a chain of cause-aware legacy exceptions:
    final StupidException ex3 = new StupidException();
    final KillMeException ex2 = new KillMeException(ex3);
    final NoGetterException ex1 = new NoGetterException(ex2);

    //Get the causes by Failure.getCause(Throwable) and check them:
    final Throwable ex1Cause = Util.getCause(ex1);
    assertEquals(null, ex1Cause);
    final Throwable ex2Cause = Util.getCause(ex2);
    assertEquals(null, ex2Cause);
    final Throwable ex3Cause = Util.getCause(ex3);
    assertEquals(null, ex3Cause);
}

@Test public void getContainedException(){
	final Throwable chain = NotextException3Chain.construct();
	
	final Throwable ex1 = Util.getContainedException(chain, NotextException3Chain.MainFailure.class);
	assertSame(NotextException3Chain.ex1, ex1);
	
	final Throwable ex2 = Util.getContainedException(chain, NotextException3Chain.MidFailure.class);
	assertSame(NotextException3Chain.ex2, ex2);
	
	final Throwable ex3 = Util.getContainedException(chain, ClassCastException.class);
	assertSame(NotextException3Chain.ex3, ex3);
	
	final Throwable notContainedExc = Util.getContainedException(chain, IndexOutOfBoundsException.class);
	assertSame(null, notContainedExc);
}

@Test public void getOriginalException(){
	final Throwable chain = NotextException3Chain.construct();
	final Throwable origin = Util.getOriginalException(chain);
	assertSame(NotextException3Chain.ex3, origin);
	
	final Throwable withoutCause = new ClassCastException("MyClass");
	assertSame(withoutCause, Util.getOriginalException(withoutCause));
}

private static class LegacyException extends Exception {
    private final Throwable _cause;
    public LegacyException(final Throwable i_cause){_cause = i_cause;}
    /**Any 'Throwable getXxx()' should be recognized as cause getter*/
    public Throwable getReason(){return _cause;}
}

private static class NoGetterException extends Exception {
    private final Throwable _cause;
    public NoGetterException(final Throwable i_cause){_cause = i_cause;}
    /**Only getXxx() should be recognized as cause getter*/
    public Throwable cause(){return _cause;}
}

private class KillMeException extends Error {
    private Exception _cause;
    public KillMeException(final Exception i_cause){_cause = i_cause;}
    public Exception why(){return _cause;}
}

private static class StupidException extends Exception {
    public static Exception getReason(){return new ArrayIndexOutOfBoundsException(-7);}
    /**getCause() with parameters should not be recognized as cause getter*/
    public Exception getCause(final String i_string){return new Exception(i_string);}
}

private class GetObjectAsCauseException extends Exception {
    /**getter returning neither Throwable nor a subclass should not be recognized as cause getter*/
    public Object getReason(){return new Exception("should not use an Object getter");}
}

/**Checks, that i_causeChain contains the causes of i_head*/
public static void checkCauseChain(final Throwable i_head, final Throwable[] i_causeChain){
  Throwable t=i_head;
  for(int i=0; i<i_causeChain.length; i++){
    t=Util.getCause(t);
    assertEquals(t, i_causeChain[i]);
  }
  assertEquals(null, Util.getCause(t));
}


}//ExceptionChainTest