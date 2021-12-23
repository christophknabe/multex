package multex;  //MsgTest.java

//2003-05-08  Knabe  Modified from ExceptionChainTest
//2002-05-30  Knabe  Created from Stack_bt following example junit.samples.VectorTest

import org.junit.Test;
import multex.test.InitFailure;
import multex.test.MsgTextDeResourceBundle;
import multex.test.MsgTextEnResourceBundle;
import multex.test.MultexAssert;
import multex.test.TextException3Chain;
import multex.test.NotextException3Chain;
import multex.test.TunnelingExceptionChain;

import javax.naming.CommunicationException;

/** JUnit batch test driver for the class multex.Msg */
public class MsgTest extends MultexAssert {

  
/** Tests printing messages, if no exception is given. */
@Test public void printMessages_forNullException(){
	final Throwable ex = null;
	
	final StringBuffer buf = new StringBuffer();
	Msg.printMessages(buf, ex);
    final String result = buf.toString();
    assertEquals(
        "null Throwable provided to multex.MsgText.appendMessageTree(...)", result
    ); 	
}

/** Tests printing messages for a chain of exceptions without any associated message texts. */
@Test public void printMessages_withoutAnyText(){
	final Throwable ex1 = NotextException3Chain.construct();
	final Throwable ex2 = ex1.getCause();
	
	
	final StringBuffer buf = new StringBuffer();
	Msg.printMessages(buf, ex1);
    final String result = buf.toString();
    assertLongStringEquals(
        ex1.getClass().getName() + ": " + _lineSeparator 
        + "    {0} = '9999'" + _lineSeparator
        + "    {1} = '3.14'" + _lineSeparator
        + Util.causeIndenter + MsgText.causeMarker
        + ex2.getClass().getName() + ": " + _lineSeparator 
        + "    {0} = 'ABC'" + _lineSeparator 
        + Util.causeIndenter + Util.causeIndenter + MsgText.causeMarker
        + NotextException3Chain.ex3Message
        , result
    ); 	
}

/**Tests printing messages for a legacy chain of exceptions. 
 * Often a platform exception from the time before exception chaining (JDK 1.4)
 * contains the
 * causing exception's toString() in its own toString().
 * When reporting a chain of exceptions to the user, this repeating
 * of message parts is very boring. Thus Msg.printMessages(...) should
 * delete such redundant message parts.
*/
@Test public void printMessages_withRedundantMessageTexts(){
	final CommunicationException ex1 = new CommunicationException("explanation");
	ex1.setRootCause(
		new java.net.UnknownHostException("falsch.tfh-berlin.de")
	);
	
	final StringBuffer buf = new StringBuffer();
	Msg.printMessages(buf, ex1);
    final String result = buf.toString();
	final String causeSeparator = _lineSeparator + Util.causeIndenter + multex.MsgText.causeMarker;
	assertLongStringEquals(
		"javax.naming.CommunicationException: explanation [Root exception is ...]"
		+ causeSeparator
        + "java.net.UnknownHostException: falsch.tfh-berlin.de"
	    , result
	);	
}

/** Tests printing the default messages for the exception chain {@link Exception3Chain}. */
public void printMessages_defaultTextFromExceptionObject(){
	final Throwable ex1 = TextException3Chain.construct();
	_check_TextException3Chain_Messages(ex1);
}

/**Tests printing the default messages for the exception chain {@link TunnelingExceptionChain}.
 * The result should be the same, as for TextException3Chain, as there are
 * inserted only some multex.Failure-s without any info.
*/
public void printMessages_defaultTextFromExceptionObject_withTunneling(){
	final Throwable ex1 = TunnelingExceptionChain.construct();
	_check_TextException3Chain_Messages(ex1);
}

/** Tests printing the default messages for an Exc with a list of Throwables as parameters. */
public void printMessages_ListExc(){
	final Throwable t1 = new Failure("Ziel nicht gefunden", (Throwable)null);
	final Throwable t2 = new Exc("Kategorie {0} nicht erlaubt", "A");
	final Throwable listExc = new Exc("Verarbeitungsfehler", (Throwable)null, t1, t2);

	final StringBuffer buf = new StringBuffer();
	Msg.printMessages(buf, listExc);
	final String result = buf.toString();
	assertIsContained("Ziel nicht gefunden", result);
	assertIsContained("Kategorie A nicht erlaubt", result);
	assertIsContained("Verarbeitungsfehler", result); 
}

/**Tests that printing the default messages of an Exc with a cause initialized by Throwable.initCause 
 * equals to an equal Exc with a cause, initialized by an Exc constructor.
*/
public void printMessages_ofExcWithSystemOrMultexCause(){
	final Throwable lowExc = new NullPointerException("BBB");
	final Throwable topExc1 = new Exc("Parameter {0} of message", "AAA");
	topExc1.initCause(lowExc);
	final Throwable topExc2 = new Exc("Parameter {0} of message", lowExc, "AAA");

	final StringBuffer buf1 = new StringBuffer();
	Msg.printMessages(buf1, topExc1);
	final String result1 = buf1.toString();

	final StringBuffer buf2 = new StringBuffer("2) ");
	Msg.printMessages(buf2, topExc2);
	final String result2 = buf1.toString();

	assertLongStringEquals(
		"Parameter AAA of message" + _lineSeparator + Util.causeIndenter + MsgText.causeMarker + lowExc.toString()
		, result2
	);
	assertLongStringEquals(result1, result2);
}

private void _check_TextException3Chain_Messages(final Throwable ex1) {
	final StringBuffer buf = new StringBuffer();
	Msg.printMessages(buf, ex1);
	final String result = buf.toString();
	
	assertLongStringEquals(
	  InitFailure.cannotInitObject + TextException3Chain.ex1Object 
	  + InitFailure.withValue + TextException3Chain.ex1Value + InitFailure.dot
	  + _lineSeparator + Util.causeIndenter + MsgText.causeMarker
	  + TextException3Chain.couldNotLoadDiagram + TextException3Chain.diagramName + TextException3Chain.fromFile + TextException3Chain.fileName
	  + _lineSeparator + Util.causeIndenter + Util.causeIndenter + MsgText.causeMarker
	  + TextException3Chain.ex3.getClass().getName() + ": " + TextException3Chain.ex3ClassName
	  , result
	);
}

public void printMessages_withDynamicDeResourcebundleText(){
	final MsgTextDeResourceBundle bundle = new multex.test.MsgTextDeResourceBundle();
	_checkPrintMessagesDe(bundle);
}

public void printMessages_withStaticDeResourcebundleText(){
	final MsgTextDeResourceBundle bundle = new multex.test.MsgTextDeResourceBundle();
	MsgText.setInternationalization(bundle);
	_checkPrintMessagesDe(null);
	MsgText.setInternationalization(false);
}

private void _checkPrintMessagesDe(final MsgTextDeResourceBundle bundle) {
	final StringBuffer buf = new StringBuffer();
	final Throwable ex1 = TextException3Chain.construct();
	Msg.printMessages(buf, ex1, bundle);
	final String result = buf.toString();
	
	final String causeMarker = new MsgTextDeResourceBundle().getCauseMarker();
	assertLongStringEquals(
	  MsgTextDeResourceBundle.wertText + TextException3Chain.ex1Value 
	  + MsgTextDeResourceBundle.objektText + TextException3Chain.ex1Object 
	  + MsgTextDeResourceBundle.initText
	  + _lineSeparator + Util.causeIndenter + causeMarker
	  + TextException3Chain.couldNotLoadDiagram + TextException3Chain.diagramName + TextException3Chain.fromFile + TextException3Chain.fileName
	  + _lineSeparator + Util.causeIndenter + Util.causeIndenter + causeMarker
	  + MsgTextDeResourceBundle.classCastExceptionText + ": " + TextException3Chain.ex3ClassName
	  , result
	);	
}

public void printMessages_withDynamicEnResourcebundleText(){
	final MsgTextEnResourceBundle bundle = new multex.test.MsgTextEnResourceBundle();
	_checkPrintMessagesEn(bundle);
}

public void printMessages_withStaticEnResourcebundleText(){
	final MsgTextEnResourceBundle bundle = new multex.test.MsgTextEnResourceBundle();
	MsgText.setInternationalization(bundle);
	_checkPrintMessagesEn(null);
	MsgText.setInternationalization(false);
}

private void _checkPrintMessagesEn(final MsgTextEnResourceBundle bundle) {
	final StringBuffer buf = new StringBuffer();
	final Throwable ex1 = TextException3Chain.construct();
	Msg.printMessages(buf, ex1, bundle);
	final String result = buf.toString();
	
	final String causeMarker = new MsgTextEnResourceBundle().getCauseMarker();
	assertLongStringEquals(
	  MsgTextEnResourceBundle.wertText + TextException3Chain.ex1Value 
	  + MsgTextEnResourceBundle.objektText + TextException3Chain.ex1Object 
	  + MsgTextEnResourceBundle.initText
	  + _lineSeparator + Util.causeIndenter + causeMarker
	  + TextException3Chain.couldNotLoadDiagram + TextException3Chain.diagramName + TextException3Chain.fromFile + TextException3Chain.fileName
	  + _lineSeparator + Util.causeIndenter + Util.causeIndenter + causeMarker
	  + MsgTextEnResourceBundle.classCastExceptionText + ": " + TextException3Chain.ex3ClassName
	  , result
	);	
}

private static final String _lineSeparator = Util.lineSeparator;
  
/**Tests printing messages for a chain of exceptions without any associated message texts
*/
public void printStackTrace_forNullException(){
	final Throwable ex = null;
	
	final StringBuffer buf = new StringBuffer();
	Msg.printStackTrace(buf, ex);
    final String result = buf.toString();
    assertEquals(
        "null Throwable provided to multex.Util.appendCompactStackTrace(...)", result
    ); 	
}
  
/**Tests printing stack trace for a chain of exceptions with correct getCause()-chaining
*/
public void printStackTrace_forWellChainedException(){
	final Throwable lowExc = new NullPointerException("BBB");
	final Throwable topExc = new java.rmi.RemoteException("AAA", lowExc);
	
	final StringBuffer buf = new StringBuffer();
	Msg.printStackTrace(buf, topExc);

	//Delete the toString() of the top exception:
	final String topExcString = Util.toString(topExc);
    assertIsContained(topExcString, buf.toString()); 	
	final int topExcStart = buf.indexOf(topExcString); //JDK >= 1.4
	assertTrue(topExcStart>=0);
	buf.delete(topExcStart, topExcStart+topExcString.length());
	
    assertIsContained(Util.toString(lowExc), buf.toString()); 	
}
  
/**Tests printing stack trace for a chain of exceptions with legacy chaining
*/
public void printStackTrace_forLegacyChainedException(){
	final Exception lowExc = new NullPointerException("BBB");
	final Throwable topExc = new org.xml.sax.SAXException("AAA", lowExc);
	
	final StringBuffer buf = new StringBuffer();
	Msg.printStackTrace(buf, topExc);

	//Delete the toString() of the top exception:
	final String topExcString = Util.toString(topExc);
	final int topExcStart = buf.indexOf(topExcString); //JDK >= 1.4
	buf.delete(topExcStart, topExcStart+topExcString.length());
	
    assertIsStart(Util.causeIndenter+lowExc.toString(), buf.toString()); 	
}

/**Tests printing the stack trace for an Exc with a list of Throwables as parameters.
*/
public void printStackTrace_ListExc(){
	final Throwable t1 = new Failure("Ziel nicht gefunden", (Throwable)null);
	final Throwable t2 = new Exc("Kategorie {0} nicht erlaubt", "A");
	final Throwable listExc = new Exc("Verarbeitungsfehler", (Throwable)null, t1, t2);

	final StringBuffer buf = new StringBuffer();
	Msg.printStackTrace(buf, listExc);
	final String result = buf.toString();
	assertIsContained("Ziel nicht gefunden", result);
	assertIsContained("Kategorie {0} nicht erlaubt", result);
	assertIsContained("Verarbeitungsfehler", result); 
}


}//MsgTest