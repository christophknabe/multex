package multex;

//Jdk1_4ExceptionChainTest.java

//2002-07-02  Knabe  Creation

/**JUnit batch test driver for the exception chaining facilities of JDK 1.4.
  Compilable and useful only on JDK 1.4.
*/
public class Jdk1_4ExceptionChainTest extends junit.framework.TestCase {


//Verwaltungsoperationen:

public Jdk1_4ExceptionChainTest(final String name) {
  super(name);
}
public static void main (final String[] args) {
  junit.textui.TestRunner.run(suite());
}
public static junit.framework.Test suite() {
  return new junit.framework.TestSuite(Jdk1_4ExceptionChainTest.class);
}

//Einzelne Testschritte:


/**Test exceptions, which provide a cause by the standard means of JDK 1.4,
  i.e. by the getCause() method of Throwable
*/
public void testJdk1_4Exceptions(){
    //Construct a chain of the legacy exceptions giving them a cause by initCause(Throwable):
    final IllegalAccessException ex5 = new IllegalAccessException("root exc") ;
    //throwable.initCause(Throwable) will compile only on JDK 1.4:
    final NoSuchFieldException ex4 = new NoSuchFieldException(); ex4.initCause(ex5);
    final InterruptedException ex3 = new InterruptedException(); ex3.initCause(ex4);
    final NullPointerException ex2 = new NullPointerException(); ex2.initCause(ex3);
    final OutOfMemoryError ex1 = new OutOfMemoryError(); ex1.initCause(ex2);

    //Now check the chain:
    ExceptionChainTest.checkCauseChain(ex1, new Throwable[]{ex2,ex3,ex4,ex5});
}


}//Jdk1_4ExceptionChainTest