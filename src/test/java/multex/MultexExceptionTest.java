package multex;  //MultexExceptionTest.java

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import multex.test.MultexAssert;;


//2005-01-12  Knabe  Aus Vorlage MsgTest

/** JUnit batch test driver for the class implementing MultexException */
public class MultexExceptionTest extends MultexAssert {


private final String _messageTextPattern = "Here is a problem";
private final Object[] _parameters = new Object[]{new Integer(1), new Character('2'), "333"};
private final MultexException _exc = new Exc(_messageTextPattern, _parameters);
private final MultexException _failure = new Failure(_messageTextPattern, null, _parameters);

private final Object[] _parametersCopy = Util.clone(_parameters);  

/**Tests regetting the exception infos*/
@Test public void getInfos(){
    final Exc emptyExc = new Exc(_messageTextPattern);
    assertEquals(_messageTextPattern, emptyExc.getDefaultMessageTextPattern());
    assertNull(emptyExc.getParameters());
    assertNull(emptyExc.getCause());

    final Exception cause = new Exception("Cause");

    final Exc causedExc = new Exc(_messageTextPattern, cause);
    assertEquals(_messageTextPattern, causedExc.getDefaultMessageTextPattern());
    assertNull(causedExc.getParameters());
    assertSame(cause, causedExc.getCause());
    
    final Failure causedFailure = new Failure(_messageTextPattern, cause);
    assertEquals(_messageTextPattern, causedFailure.getDefaultMessageTextPattern());
    assertNull(causedFailure.getParameters());
    assertSame(cause, causedFailure.getCause());
    
    final Failure emptyFailure = new Failure(_messageTextPattern);
    assertEquals(_messageTextPattern, emptyFailure.getDefaultMessageTextPattern());
    assertNull(emptyFailure.getParameters());
    assertNull(emptyFailure.getCause());
    
	assertEquals(_messageTextPattern, _exc.getDefaultMessageTextPattern());
	assertEquals(_parametersCopy, _exc.getParameters());
	assertEquals(_messageTextPattern, _failure.getDefaultMessageTextPattern());
	assertEquals(_parametersCopy, _failure.getParameters());
}

static class MyExc extends Exc {
    static void check(String name, Object value) throws Exc {
        throwMe(name, value);
    }
}
/**Tests that the method Exc.throwMe throws an object of the directly calling class.
 * As long as Exc is a checked exception, throwMe will be necessary to specify throws Exc.
 */
@Test public void throwMe() throws Exc {
    try {
        MyExc.check("age", 65);
        fail("MyExc expected");
    } catch ( MyExc expected) { //MyExc expected
    }
}
  
/**Tests that the Exc constructor clones its parameter[].*/
@Test public void excContructor_makesCopyOfParameters(){
	final Object[] parameters = Util.clone(_parameters);
	final MultexException multexException = new Exc(_messageTextPattern, parameters);
	_testContructor_makesCopyOfParameters(parameters, multexException);
}
  
/**Tests that the Failure constructor clones its parameter[].
*/
@Test public void failureContructor_makesCopyOfParameters(){
	final Object[] parameters = Util.clone(_parameters);
	final MultexException multexException = new Failure(_messageTextPattern, null, parameters);
	_testContructor_makesCopyOfParameters(parameters, multexException);
}

private void _testContructor_makesCopyOfParameters(
	final Object[] io_parameters, 
	final MultexException i_exception
) /*throws AssertionFailedError*/ {
	for(int i=0; i<io_parameters.length; i++){
		io_parameters[i] = "param " + i;
	}
	assertEquals(_parametersCopy, i_exception.getParameters());
}
  
/**Tests regetting the parameters of an Exc
*/
@Test public void excGetParameters_isCopy(){
	_testGetParameters_isCopy(_exc);
}
  
/**Tests regetting the parameters of a Failure
*/
@Test public void failureGetParameters_isCopy(){
	_testGetParameters_isCopy(_failure);
}

private void _testGetParameters_isCopy(final MultexException i_exception) /*throws AssertionFailedError*/ {
	final Object[] parameters = i_exception.getParameters();
	for(int i=0; i<parameters.length; i++){
		parameters[i] = "param " + i;
	}
	assertEquals(_parametersCopy, i_exception.getParameters());
}
  
/* Class under test for void Failure(String, Throwable, List) */
@Test public void constructorWithParametersAsList() {
	//final Throwable cause = new Throwable("cause");
	final String p0e = new String("p0");
	final Exc p1e = new Exc("p1");
	final Integer p2e = new Integer(2);
	final List<Object> pe = new ArrayList<Object>();
	pe.add(p0e);
	pe.add(p1e);
	pe.add(p2e);
	final String message = "MESSAGE";

	final Failure f = new Failure(message, /*cause,*/ pe);
	final Exc e = new Exc(message, /*cause,*/ pe);
	
	_checkContent(f, message, pe);
	_checkContent(e, message, pe);
}

/**
 * @param me
 * @param message
 * @param pe
 */
private void _checkContent(final MultexException me, final String message, final List pe) {
	assertEquals(message, me.getDefaultMessageTextPattern());
	//assertEquals(cause, me.getCause());
	final Object[] pa = me.getParameters();
	assertEquals(pe.size(), pa.length);
	for(int i=0; i<pa.length; i++){
		assertEquals(pe.get(i), pa[i]);
	}
}

/**Tests that printing stack trace for a Failure with cause using failure.printStackTrace(...)
 * uses the MulTEx printStackTrace(...)
*/
@Test public void printStackTrace_ofFailure(){
	final Throwable lowExc = new NullPointerException("BBB");
	final Throwable topExc = new Failure("MSG", lowExc, "AAA");
    _testPrintStackTrace_ofMultexException(topExc);
}

/**Tests that printing stack trace for an Exc with a cause initialized by Throwable.initCause using exc.printStackTrace(...)
 * uses the MulTEx printStackTrace(...)
*/
@Test public void printStackTrace_ofExcSystemCause(){
	final Throwable lowExc = new NullPointerException("BBB");
	final Throwable topExc = new Exc("MSG", "AAA");
	topExc.initCause(lowExc);
    _testPrintStackTrace_ofMultexException(topExc);
}

/**Tests that printing stack trace for an Exc with a cause, initialized by an Exc constructor, using exc.printStackTrace(...)
 * uses the MulTEx printStackTrace(...)
*/
@Test public void printStackTrace_ofExcMultexCause(){
	final Throwable lowExc = new NullPointerException("BBB");
	final Throwable topExc = new Exc("MSG", lowExc, "AAA");
    _testPrintStackTrace_ofMultexException(topExc);
}

private void _testPrintStackTrace_ofMultexException(final Throwable i_topExc) {
    final StringBuffer buf = new StringBuffer();
    Msg.printStackTrace(buf, i_topExc);
    
    final StringWriter topExcStringWriter = new StringWriter(buf.length());
    final PrintWriter topExcPrintWriter = new PrintWriter(topExcStringWriter);
    i_topExc.printStackTrace(topExcPrintWriter);
    assertLongStringEquals(buf.toString(), topExcStringWriter.toString());
}


}//MultexExceptionTest