package multex; //Jdk1_4StackTraceTest.java

//2002-07-02  Knabe  Creation

import org.junit.Test;
import multex.test.MultexAssert;

/**JUnit batch test driver for Throwable.getStackTrace() and the reporting of a
 * stack trace of an exception chain by class multex.Failure.
  Compilable and useful only from JDK 1.4.
*/
public class Jdk1_4StackTraceTest extends MultexAssert {


    private static final int _baseLineNumber = 15; //Must equal to this line number!!! 
	private NullPointerException _createNullPointerException() {
		return new NullPointerException("BBB"); //Leave at this location!!!
	}
	private final Exception _lowExc = _createNullPointerException(); //Leave at this location!!!
	private final Throwable _topExc = new org.xml.sax.SAXException("AAA", _lowExc); //Leave at this location!!!

    private static final int _npeLineNumber = _baseLineNumber + 2;
    private static final int _lowExcLineNumber = _npeLineNumber + 2;
    private static final int _topExcLineNumber = _lowExcLineNumber + 1;
	
	/** Tests getting the stack trace of a low and a top exception and prints them. */
	public void _testGetStackTrace(){
		_printStackTraceWithoutExceptionInfo("\nStack trace of top exception is:", _topExc);
		_printStackTraceWithoutExceptionInfo("\nStack trace of low exception is:", _lowExc);
		_logMsgPrintStackTrace();
	}
	
	/** Logs the stack trace of _topExc. */
	private void _logMsgPrintStackTrace() {
		System.err.println("\nMsg.printStackTrace(_topExc) prints:");
		Msg.printStackTrace(_topExc);		
	}
	
	@Test public void appendIrredundantTraceLines(){
		final StackTraceElement[] lowTrace = _lowExc.getStackTrace();
		final StackTraceElement[] topTrace = _topExc.getStackTrace();
		final StringBuffer buf = new StringBuffer();
		
		Util.appendIrredundantTraceLines(buf, lowTrace, topTrace);
		assertEquals(
			"\tat multex.Jdk1_4StackTraceTest._createNullPointerException(Jdk1_4StackTraceTest.java:" + _npeLineNumber + ")"
			+ Util.lineSeparator
			+ "\tat multex.Jdk1_4StackTraceTest.<init>(Jdk1_4StackTraceTest.java:" + _lowExcLineNumber + ")"
			+ Util.lineSeparator
			,
			buf.toString()
		);
	}
	
	@Test public void msgPrintStackTrace(){
		final StringBuffer buf = new StringBuffer();
		Msg.printStackTrace(buf, _lowExc);
		final String lowExcTraceStart = "java.lang.NullPointerException: BBB" + Util.lineSeparator
			+ "\tat multex.Jdk1_4StackTraceTest._createNullPointerException(Jdk1_4StackTraceTest.java:" + _npeLineNumber + ")"
			+ Util.lineSeparator
			+ "\tat multex.Jdk1_4StackTraceTest.<init>(Jdk1_4StackTraceTest.java:" + _lowExcLineNumber + ")"
			+ Util.lineSeparator
		;
		assertIsStart(
			lowExcTraceStart + "\tat "
			, buf.toString()
		);
		
		buf.setLength(0);
		Msg.printStackTrace(buf, _topExc);
		System.out.println("multex.Jdk1_4StackTraceTest.msgPrintStackTrace:");
		System.out.println(buf);
		assertIsStart( ""
			+ Util.causeIndenter
			+ lowExcTraceStart 
			+ Util.wasCausing + Util.lineSeparator
			+ "org.xml.sax.SAXException: AAA" + Util.lineSeparator + "java.lang.NullPointerException: BBB" + Util.lineSeparator
			+ "\tat multex.Jdk1_4StackTraceTest.<init>(Jdk1_4StackTraceTest.java:" + _topExcLineNumber + ")"
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