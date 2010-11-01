package multex; //Jdk1_4StackTraceTest.java


//2002-07-02  Knabe  Creation

/**JUnit batch test driver for Throwable.getStackTrace() and the reporting of a
 * stack trace of an exception chain by class multex.Failure.
  Compilable and useful only from JDK 1.4.
*/
public class Jdk1_4StackTraceTest extends junit.framework.TestCase {


	private NullPointerException _createNullPointerException() {
		return new NullPointerException("BBB"); //Leave at line 14!
	}
	private final Exception _lowExc = _createNullPointerException(); //Leave at line 16!
	private final Throwable _topExc = new org.xml.sax.SAXException("AAA", _lowExc); //Leave at line 17!

	//Verwaltungsoperationen:
	
	public Jdk1_4StackTraceTest(final String name) {
	  super(name);
	}
	public static void main (final String[] i_args) {
	  junit.textui.TestRunner.run(suite());
	}
	public static junit.framework.Test suite() {
	  return new junit.framework.TestSuite(Jdk1_4StackTraceTest.class);
	}
	
	//Einzelne Testschritte:
	
	
	/**Tests getting the stack trace of a low and a top exception and prints them.
	*/
	public void _testGetStackTrace(){
		_printStackTraceWithoutExceptionInfo("\nStack trace of top exception is:", _topExc);
		_printStackTraceWithoutExceptionInfo("\nStack trace of low exception is:", _lowExc);
		_logMsgPrintStackTrace();
	}
	
	/**Logs the stack trace of _topExc.*/
	private void _logMsgPrintStackTrace() {
		System.err.println("\nMsg.printStackTrace(_topExc) prints:");
		Msg.printStackTrace(_topExc);		
	}
	
	public void testAppendIrredundantTraceLines(){
		final StackTraceElement[] lowTrace = _lowExc.getStackTrace();
		final StackTraceElement[] topTrace = _topExc.getStackTrace();
		final StringBuffer buf = new StringBuffer();
		
		Util.appendIrredundantTraceLines(buf, lowTrace, topTrace);
		assertEquals(
			"\tat multex.Jdk1_4StackTraceTest._createNullPointerException(Jdk1_4StackTraceTest.java:14)"
			+ Util.lineSeparator
			+ "\tat multex.Jdk1_4StackTraceTest.<init>(Jdk1_4StackTraceTest.java:16)"
			+ Util.lineSeparator
			,
			buf.toString()
		);
	}
	
	public void testMsgPrintStackTrace(){
		final StringBuffer buf = new StringBuffer();
		Msg.printStackTrace(buf, _lowExc);
		final String lowExcTraceStart = "java.lang.NullPointerException: BBB" + Util.lineSeparator
			+ "\tat multex.Jdk1_4StackTraceTest._createNullPointerException(Jdk1_4StackTraceTest.java:14)"
			+ Util.lineSeparator
			+ "\tat multex.Jdk1_4StackTraceTest.<init>(Jdk1_4StackTraceTest.java:16)"
			+ Util.lineSeparator
		;
		multex.test.TestUtil.assertIsStart(
			lowExcTraceStart + "\tat "
			, buf.toString()
		);
		
		buf.setLength(0);
		Msg.printStackTrace(buf, _topExc);
		multex.test.TestUtil.assertIsStart( ""
			+ Util.causeIndenter
			+ lowExcTraceStart 
			+ Util.wasCausing + Util.lineSeparator
			+ "org.xml.sax.SAXException: AAA; Caused by: java.lang.NullPointerException: BBB" + Util.lineSeparator
			+ "\tat multex.Jdk1_4StackTraceTest.<init>(Jdk1_4StackTraceTest.java:17)"
			+ Util.lineSeparator + "\tat "
			, buf.toString()
		);
	}
	
	private void _printStackTraceWithoutExceptionInfo(
		final String i_header,
		final Throwable ex2
	){
		final StackTraceElement[] ex2Trace = ex2.getStackTrace();
		
		System.err.println(i_header);
		for(int i=0; i<ex2Trace.length; i++){
		    System.err.println(ex2Trace[i]);
		}
	}


}//Jdk1_4StackTraceTest