package multex.demo;

//ChainedExceptionJUnitTestDemo.java

//2007-06-29  Knabe  Renamed to ChainedExceptionJUnitTestDemo, so that its name does not end with Test.
//2002-07-07  Knabe  Creation

import org.junit.Test;
import org.junit.Assert;

/**Main program in order to look, how an unexpected exception with
  causal chain is reported by the JUnit Eclipse plugin, respectively as an application.
  This does not belong to the test suite for MulTEx and will not be executed by 'mvn test'.
  When executing it in the Eclipse test runner, this test will always fail.
*/
public class ChainedExceptionJUnitDemo {


    public static void main (String[] args) {
      final ChainedExceptionJUnitDemo obj = new ChainedExceptionJUnitDemo();
      obj.provokeJdk1_4Exceptions();
    }


/**Test exceptions, which provide a cause by the standard means of JDK 1.4,
  i.e. by the getCause() method of Throwable
*/
@Test public void provokeJdk1_4Exceptions(){
    //Construct a chain of exceptions and throw it:
    multex.demo.File.copy("a", "b");
}


}//ChainedExceptionJUnitTestDemo