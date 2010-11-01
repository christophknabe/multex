package multex.demo;

//ChainedExceptionJUnitTestDemo.java

//2007-06-29  Knabe  Renamed to ChainedExceptionJUnitTestDemo, so that its name does not end with Test.
//2002-07-07  Knabe  Creation

/**JUnit batch test driver in order to look, how an unexpected exception with
  causal chain is reported by JUnit. This does not belong to the test suite for MulTEx.
*/
public class ChainedExceptionJUnitTestDemo extends junit.framework.TestCase {


//Verwaltungsoperationen:

public ChainedExceptionJUnitTestDemo(String name) {
  super(name);
}
public static void main (String[] args) {
  junit.textui.TestRunner.run(suite());
}
public static junit.framework.Test suite() {
  return new junit.framework.TestSuite(ChainedExceptionJUnitTestDemo.class);
}

//Einzelne Testschritte:


/**Test exceptions, which provide a cause by the standard means of JDK 1.4,
  i.e. by the getCause() method of Throwable
*/
public void testJdk1_4Exceptions(){
    //Construct a chain of exceptions and throw it:
    multex.demo.File.copy("a", "b");
}


}//ChainedExceptionJUnitTestDemo