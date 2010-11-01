import junit.framework.Test;
import junit.framework.TestSuite;

/**Test suite that runs all Java platform and all MulTEx tests.
 * @author knabe 2006-04-05
 */
public class MultexAllTests {

	public static void main(final String[] args)throws ClassNotFoundException{
	    junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		final TestSuite suite = new TestSuite("Tests for platform and MulTEx");

		suite.addTest(multex.AllTests.suite());
		return suite;
	}

}
