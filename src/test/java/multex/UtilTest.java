package multex;  //UtilTest.java

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import org.junit.Test;
import org.xml.sax.SAXException;

import multex.test.MultexAssert;


/**JUnit batch test driver for the class multex.Msg. @author Christoph Knabe*/
public class UtilTest extends MultexAssert {


    //Testfixtures:
    private static final int _baseLineNumber = 18; //Must be the same as the line it stands on!!!
	private final Exc       t1  = new Exc("Kategorie nicht erlaubt");      //Leave at this line!
	private final Throwable t21 = new FileNotFoundException("kasse.dat");  //Leave at line 22
	private final Failure t2    = new Failure("Ziel nicht gefunden", t21); //Leave at line 23
	private final Exc listExc   = new Exc("Verarbeitungsfehler", t1, t2);  //Leave at line 24
    private static final int _t1LineNumber = _baseLineNumber + 1;
    private static final int _t21LineNumber = _t1LineNumber + 1;
    private static final int _t2LineNumber = _t21LineNumber + 1;
    private static final int _listExcLineNumber = _t2LineNumber + 1;
		  
	/** Tests cloning an object array or null. */
	@Test public void cloneObjectArray(){
		final Integer entry1 = new Integer(1);
		final Character entry2 = new Character('2');
	    final String entry3 = "333";
	    final Object[] original = {entry1, entry2, entry3};
	    final Object[] expected = {entry1, entry2, entry3};
	    assertEquals(expected, original);
		
		final Object[] clone = Util.clone(original);
	    assertEquals(expected, clone);
	    assertTrue(original!=clone);
	    for(int i=0; i<original.length; i++){
	    	original[i] = "X" + i;
	    }
	    assertEquals(expected, clone);
	    assertTrue(!expected[0].equals(original[0]));
	}
	
	/** Tests getting all causes (including those stored as parameters of a {@link MultexException}). */
	@Test public void getCauses(){
		final Throwable lowExc1 = new Exc("lowExc1");
		final Throwable lowExc2 = new NullPointerException("lowExc2");
		final Throwable topExc  = new Failure("topExc", lowExc1, "par0", lowExc2, "par2");
		final Throwable[] expectedCauses = new Throwable[]{lowExc1, lowExc2}; 
		final Throwable[] actualCauses = Util.getCauses(topExc);
	    assertEquals(expectedCauses, actualCauses);
	}
    
    /** Tests getting the lowest cause of an exception chain by the cause getter. */
    @Test public void getOriginalException(){
        final Throwable t3 = new Failure("topExc", t2);
        final Throwable result = Util.getOriginalException(t3);
        assertSame(t21, result);
        
        final Throwable nullResult = Util.getOriginalException(null);
        assertNull(nullResult);
    }

	@Test public void toString_withoutCause(){
		//In the case without cause the standard String representation is
		//   package.subpackage.Class: detailMessage
		{
			final Throwable throwable = new Throwable("AAA");
			final String result = Util.toString(throwable);
			assertEquals("java.lang.Throwable: AAA", result);
			assertEquals(throwable.toString(), result);
		}
		//The same holds for a SAXException without causing exception:
		{
			final SAXException saxException = new SAXException("BBB");
			final String result = Util.toString(saxException);
			assertEquals("org.xml.sax.SAXException: BBB", result);
			assertEquals(saxException.toString(), result);
		}
	}

	static class ExceptionWithoutClassName extends Exception {
		
		public ExceptionWithoutClassName(final String message) {
			super(message);
		}
		// Here the class name is missing in toString:
		@Override public String toString() {
			return getMessage();
		}
		
	}
	
	static class ExceptionWithoutMessage extends Exception {
		
		public ExceptionWithoutMessage(final String message) {
			super(message);
		}
		// Here the message is missing in toString:
		@Override public String toString() {
			return getClass().getName();
		}
		
	}

	@Test public void toStringWithCause_shouldIncludeOwnClassNameAndMessage(){
		final IllegalArgumentException cause = new IllegalArgumentException("99");
		final String causeString = Util.toString(cause);
		assertEquals("java.lang.IllegalArgumentException: 99", causeString);
		assertEquals(cause.toString(), causeString);
		
		// SAXException includes the cause string into its own string representation.
		// We do not want that, as we have the causal chain stringized in a better format by Msg.printMessages.
		final SAXException saxException = new SAXException("(<BBB>)", cause);
		{
			// Here the undesired behavior is documented:
			final String saxExceptionString = "org.xml.sax.SAXException: (<BBB>)\njava.lang.IllegalArgumentException: 99";
			assertEquals(
				saxExceptionString,
				saxException.toString()
			);
		}

		{
			final ExceptionWithoutClassName exceptionWithoutClassName = new ExceptionWithoutClassName("(<BBB>)");
			// Here we test that Util.toString starts with its own class name and contains its own message:
			final String result = Util.toString(exceptionWithoutClassName);
			assertIsStart(exceptionWithoutClassName.getClass().getName(), result);
			assertIsContained("(<BBB>)", result);
		}
		{
			final ExceptionWithoutMessage exceptionWithoutMessage = new ExceptionWithoutMessage("(<BBB>)");
			// Here we test that Util.toString starts with its own class name and contains its own message:
			final String result = Util.toString(exceptionWithoutMessage);
			assertIsStart(exceptionWithoutMessage.getClass().getName(), result);
			assertIsContained("(<BBB>)", result);
		}
	}
	
	/** Tests printing the stack trace for an Exc with a list of Throwables as parameters. */
	@Test public void appendCompactStackTrace_ListExc(){
		final StringBuffer buf = new StringBuffer();
		Util.appendCompactStackTrace(buf, listExc);
		final String result = buf.toString();
		assertIsContained("kasse.dat", result);
		assertIsContained("Ziel nicht gefunden", result);
		assertIsContained("Kategorie nicht erlaubt", result);
		assertIsContained("Verarbeitungsfehler", result); 
		final String expected =
			"+" + t1.getClass().getName() + ": " + t1.getDefaultMessageTextPattern() + Util.lineSeparator
			+ "\tat multex.UtilTest.<init>(UtilTest.java:" + _t1LineNumber + ")" + Util.lineSeparator
			+ "++" + t21.getClass().getName() + ": " + t21.getMessage() + Util.lineSeparator
			+ "\tat multex.UtilTest.<init>(UtilTest.java:" + _t21LineNumber + ")" + Util.lineSeparator
			+ Util.wasCausing + Util.lineSeparator
			+ "+" + t2.getClass().getName() + ": " + t2.getDefaultMessageTextPattern() + Util.lineSeparator
			+ "\tat multex.UtilTest.<init>(UtilTest.java:" + _t2LineNumber + ")" + Util.lineSeparator
			+ Util.wasCausing + Util.lineSeparator
			+ listExc.getClass().getName() + ": " + listExc.getDefaultMessageTextPattern() + Util.lineSeparator
			+ "\tat multex.UtilTest.<init>(UtilTest.java:" + _listExcLineNumber + ")" + Util.lineSeparator
			+ "\tat sun.reflect."			
		;
		assertIsStart(expected, result);
	}

    /** Tests appending only the irredundant trace elements. */
    @Test public void failingAppendIrredundantTraceLines(){
        //io_destination is null:
        try {
            Util.appendIrredundantTraceLines(null, null, null);
            fail("AssertionError expected");
        } catch ( AssertionError expected ){}
        
        //i_reporteeElements is null:
        final StringBuffer io_destination = new StringBuffer();
        Util.appendIrredundantTraceLines(io_destination, null, null);
        assertEquals("multex.Util: No stack trace elements to append.", io_destination.toString());
    }

    /** Tests appending only the irredundant trace elements. */
    @Test public void succeedingAppendIrredundantTraceLinesOnJRE5(){
        final StringBuffer io_destination = new StringBuffer();
        final StackTraceElement[] i_reporteeElements = {
                new StackTraceElement("Ca","ma","Fa",1),
                new StackTraceElement("Cb","mb","Fb",2),
                new StackTraceElement("Cc","mc","Fc",3),
                new StackTraceElement("Cd","md","Fd",4),
        };
        final StackTraceElement[] i_causeeElements = {
                new StackTraceElement("Cc","mc","Fc",3),
                new StackTraceElement("Cd","md","Fd",4),
        };
        Util.appendIrredundantTraceLines(io_destination, i_reporteeElements, i_causeeElements);
        assertEquals("\tat Ca.ma(Fa:1)"+Util.lineSeparator+"\tat Cb.mb(Fb:2)"+Util.lineSeparator, io_destination.toString());
    }
    
    /**Tests appending only the irredundant trace elements.
     * @throws IOException {@link BufferedReader#readLine() failed}
     */
    @Test public void succeedingAppendIrredundantTraceLinesOnJRE1_4() throws IOException {
        final String traceString = captureStandardStackTrace(testException);

        //Test with i_causeeElements is null; must append the complete testTrace:
        final StringBuffer io_destination = new StringBuffer();
        io_destination.append("java.lang.Exception");
        io_destination.append(Util.lineSeparator);
        Util.appendIrredundantTraceLines(io_destination, testTrace, null);
        assertEquals(traceString, io_destination.toString());
        
        //Test with only 3 irredundant lines:
        io_destination.setLength(0);
        final StackTraceElement[] i_causeeElements = stripFirstThreeElements(testTrace);
        Util.appendIrredundantTraceLines(io_destination, testTrace, i_causeeElements);
        //Extract lines 1-3 from standard stack trace:
        final BufferedReader r = new BufferedReader(new StringReader(traceString));
        r.readLine(); //Consume the exception name
        final String l1 = r.readLine();
        final String l2 = r.readLine();
        final String l3 = r.readLine();
        r.close();
        final String expected = l1+Util.lineSeparator+l2+Util.lineSeparator+l3+Util.lineSeparator;
        assertEquals(expected, io_destination.toString());
    }
    
    static class MyStaticExc extends Exc {}
    static class MyStaticFailure extends Failure {}
    
    @Test public void checkClass_permittedCases() {
    	//check direct objects of Exc respectively Failure:
    	Util.checkClass(t1, "Exc");
    	final Failure failure = new Failure(t1);
    	Util.checkClass(failure, "Failure");
    	//Check objects of static inner subclasses or implementation classes of Exc, Failure, MultexException
    	final MyStaticExc myExc = new MyStaticExc();
    	Util.checkClass(myExc, "Exc");
    	final MyStaticFailure myFailure = new MyStaticFailure();
    	Util.checkClass(myFailure, "Failure");
    }
    
    public class MyNonstaticExc extends Exc {}
    public class MyNonstaticFailure extends Failure {}
    
    @Test public void exceptionConstructor_failsIfNonstaticInner() {
    	try{
        	new MyNonstaticExc();
    		fail("IllegalArgumentException expected");
    	}catch(final IllegalArgumentException expected) {}
    	try{
        	new MyNonstaticFailure();
    		fail("IllegalArgumentException expected");
    	}catch(final IllegalArgumentException expected) {}
    }
    
    @Test public void checkRunsOnJreVersionOrLater() {
    	Util.checkRunsOnJreVersionOrLater(Util.buildJreVersion);
    	try {
    		Util.checkRunsOnJreVersionOrLater("9");
    		fail("RuntimeException expected");
    	}catch(RuntimeException expected) {}
    	try {
    		Util.checkRunsOnJreVersionOrLater("11");
    		fail("RuntimeException expected");
    	}catch(RuntimeException expected) {}
    }
    
    @Test public void compareVersions() {
		//Test examples taken from https://www.baeldung.com/java-comparing-versions
    	{
			final int result = Util.compareVersions("1.0.1", "1.1.2");
			assertTrue(Integer.toString(result), result < 0);
		}
		assertTrue(Util.compareVersions("1.0.1", "1.10") < 0);
        assertTrue(Util.compareVersions("1.1.2", "1.0.1") > 0);
        assertTrue(Util.compareVersions("1.1.2", "1.2.0") < 0);
        assertEquals(0, Util.compareVersions("1.3.0", "1.3"));
        //Own Java version examples
    	assertTrue(Util.compareVersions("1.8", "10") < 0);
    	assertTrue(Util.compareVersions("10", "10.1") < 0);      
    }

    private StackTraceElement[] stripFirstThreeElements(final StackTraceElement[] trace) {
        final StackTraceElement[] result = new StackTraceElement[trace.length-3];
        System.arraycopy(trace, 3, result, 0, result.length);
        return result;
    }

    /**Returns the standard stack trace of the Throwable, as if it were printed by printStackTrace(), as a String.
     * @param throwable The Throwable, whose stack trace has to be printed.
     */
    private String captureStandardStackTrace(final Throwable throwable) {
        //prepare out writer:
        final java.io.StringWriter sw = new java.io.StringWriter();
        final java.io.PrintWriter out = new java.io.PrintWriter(sw, true);
        //collect stack traces onto out writer:
        throwable.printStackTrace(out);
        return sw.toString();
    }    

    private static final Exception testException = new Exception();
    private static final StackTraceElement[] testTrace = testException.getStackTrace();
    

}//UtilTest