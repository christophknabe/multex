package multex;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * TestSuite that runs all MulTEx tests
 *
 */
public class AllTests {


	public static void main(final String[] args)throws ClassNotFoundException{
	    junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite(){
	    final TestSuite suite = new TestSuite("All MulTEx tests");
	    suite.addTestSuite(UtilTest.class);
	    suite.addTestSuite(ReflectionCauseGetterTest.class);
	    suite.addTestSuite(MultexExceptionTest.class);
	    suite.addTestSuite(AwtTest.class);
	    suite.addTestSuite(ExceptionChainTest.class);
	    suite.addTestSuite(MsgTextTest.class);
	    suite.addTestSuite(MsgTest.class);
	    suite.addTestSuite(Jdk1_4ExceptionChainTest.class);
        suite.addTestSuite(Jdk1_4StackTraceTest.class);
        suite.addTestSuite(MultexUtilTest.class);
        suite.addTestSuite(ExceptionTreeTest.class);
	    return suite;
	}


}