package multex;  //MsgTextTest.java

import java.util.Locale;
import java.util.ResourceBundle;
import org.junit.Test;
import org.junit.Assert;

import multex.test.MsgTextResourceBundle;
import multex.test.MsgTextDeResourceBundle;
import multex.test.NotextException3Chain;
import multex.test.NotextNoparamExc;

import org.xml.sax.SAXException;

//2003-05-08  Knabe  Modified from MsgTest
//2002-05-30  Knabe  Created from Stack_bt following example junit.samples.VectorTest

/** JUnit batch test driver for the class multex.MsgText */
public class MsgTextTest extends Assert {


private static final Class _myClass = MsgTextTest.class;

private static final String    _ex2Par = "MyClass";
private static final Exception _ex2    = new ClassCastException(_ex2Par);
private static final String    _ex2Name = "java.lang.ClassCastException";
private static final Exception _ex1 = new multex.test.InitFailure(_ex2, "OOO", 9999);
private static final String    _ex1Name = "multex.test.InitFailure";
private static final String ursachenMarkierer = "CAUSE: ";

@Test public void getMessageTextPattern_withoutAnyTexts(){
	final Throwable ex1 = NotextException3Chain.construct();

	final String result1 = MsgText.getMessageTextPattern(ex1);
	assertEquals(null, result1);
	
	final String result2 = MsgText.getMessageTextPattern(ex1.getCause());
	assertEquals(null, result2);
	
	final String result3 = MsgText.getMessageTextPattern(ex1.getCause().getCause());
	assertEquals(null, result3);
}

@Test public void getMessageTextPattern_withResourceBundle(){
	final ResourceBundle germanBundle = new multex.test.MsgTextDeResourceBundle();
	assertEquals("de", germanBundle.getLocale().toString());
	
	final String result1 = MsgText.getMessageTextPattern(_ex2, germanBundle);
	assertEquals(multex.test.MsgTextDeResourceBundle.classCastExceptionText, result1);
	
	final String result2 = MsgText.getMessageTextPattern(_ex1, germanBundle);
	assertEquals(MsgTextDeResourceBundle.initFailureText, result2);
}

@Test public void appendMessageLineWithoutAnyText(){
	final Throwable ex1 = NotextException3Chain.construct();

	final StringBuffer result = new StringBuffer();
	MsgText.appendMessageLine(result, ex1, (ResourceBundle)null);
	assertEquals(
	    ex1.getClass().getName() + ": " + Util.lineSeparator
	    + "    {0} = '9999'" + Util.lineSeparator
	    + "    {1} = '3.14'"
	    , result.toString()
	);
	
	final Throwable ex2 = ex1.getCause();
	result.setLength(0);
	MsgText.appendMessageLine(result, ex2, (ResourceBundle)null);
	assertEquals(
	    ex2.getClass().getName() + ": " + Util.lineSeparator
	    + "    {0} = 'ABC'"
	    , result.toString()
	);
	
	final Throwable ex3 = ex2.getCause();
	result.setLength(0);
	MsgText.appendMessageLine(result, ex3, (ResourceBundle)null);
	assertEquals(
	    ex3.getClass().getName() + ": MyClass"
	    , result.toString()
	);
}

@Test public void appendMessageLineWithDefaultLocale(){
	final ResourceBundle unspecBundle = new multex.test.MsgTextResourceBundle();
    //System.out.println(getClass().getName() + ".testAppendMessageLineWithDefaultLocale: default Locale is: " + unspecBundle.getLocale());
	final StringBuffer result2 = new StringBuffer();
	MsgText.appendMessageLine(result2, _ex2, unspecBundle);
	assertEquals(
	    multex.test.MsgTextResourceBundle.classCastExceptionText
	    + ": " + _ex2Par, 
	    result2.toString()
	);

}

@Test public void appendMessageLine_WithClassCastException_WithNeitherTextNorDetailMessage(){
	final StringBuffer result = new StringBuffer();
	final Throwable ex = new ClassCastException();
	MsgText.appendMessageLine(result, ex, null);
	assertEquals(
	    "java.lang.ClassCastException",
	    result.toString()
	);
}

@Test public void appendMessageLine_WithMultexException_WithNeitherTextNorDetailMessage(){
	final StringBuffer result = new StringBuffer();
	final Throwable ex = new NotextNoparamExc();
	MsgText.appendMessageLine(result, ex, null);
	assertEquals(
	    NotextNoparamExc.class.getName(),
	    result.toString()
	);
}

@Test public void appendMessageLineWithClassCastExceptionWithoutDetailMessage(){
	final StringBuffer result = new StringBuffer();
	final Throwable ex = new ClassCastException();
	final ResourceBundle bundle = new multex.test.MsgTextResourceBundle();
	MsgText.appendMessageLine(result, ex, bundle);
	assertEquals(
	    MsgTextResourceBundle.classCastExceptionText,
	    result.toString()
	);
}

@Test public void appendMessageLineWithExcAndNeitherMultexNorThrowableParameters(){
	final String expected = "Das Ger�t ist zur Zeit belegt";
	final StringBuffer result = new StringBuffer();
	final Throwable ex = new Exc(expected);
	final ResourceBundle unspecBundle = new multex.test.MsgTextResourceBundle();
	MsgText.appendMessageLine(result, ex, unspecBundle);
	assertEquals(
	    expected,
	    result.toString()
	);
}

@Test public void toStringSimple(){
	//In the case without cause the standard String representation is
	//   package.subpackage.Class: detailMessage
	final Throwable throwable = new Throwable("AAA");
	final String throwableString = Util.toString(throwable);
	assertEquals("java.lang.Throwable: AAA", throwableString);
	assertEquals(throwable.toString(), throwableString);

	//The same holds for a SAXException without causing exception:
	final SAXException saxException = new SAXException("BBB");
	final String saxExceptionString = Util.toString(saxException);
	assertEquals("org.xml.sax.SAXException: BBB", saxExceptionString);
	assertEquals(saxException.toString(), saxExceptionString);
}

@Test public void toStringWithCause(){
	final IllegalArgumentException illegalArgumentException = new IllegalArgumentException("99");
	final String illegalArgumentExceptionString = Util.toString(illegalArgumentException);
	assertEquals("java.lang.IllegalArgumentException: 99", illegalArgumentExceptionString);
	assertEquals(illegalArgumentException.toString(), illegalArgumentExceptionString);

	
	//If SAXException has a causing exception, it unfortunately suppresses
	//its own class name and detailMessage.  This strange behaviour is documented here:
	final SAXException saxException = new SAXException("BBB", illegalArgumentException);
	assertEquals(
		"java.lang.IllegalArgumentException: 99",
		saxException.toString()
	);

	//Thus as a rule they should be 
	//included in the toString(), if no already by itself:
	final String saxExceptionString = Util.toString(saxException);
	assertEquals("org.xml.sax.SAXException: BBB; Caused by: java.lang.IllegalArgumentException: 99", saxExceptionString);
}

@Test public void formatWithLocales(){
	final StringBuffer result = new StringBuffer();
	final Float number = new Float(9999.99);
	final Object[] parameters = new Object[]{number};
	
	MsgText.format(result, "+{0}$", parameters, Locale.ENGLISH);
	assertEquals( "+9,999.99$", result.toString() );

	result.setLength(0);
	MsgText.format(result, "+{0}$", parameters, Locale.GERMAN);
	assertEquals( "+9.999,99$", result.toString() );

}

@Test public void appendMessageLineWithLocales(){
	final ResourceBundle englishBundle = new multex.test.MsgTextEnResourceBundle();
    //System.out.println(getClass().getName() + ".testAppendMessageLineWithLocales: english Locale is: " + englishBundle.getLocale());
	
	final StringBuffer result = new StringBuffer();
	MsgText.appendMessageLine(result, _ex1, englishBundle);
	assertEquals( //english numeric formatting with comma as 1000-separator:
	    "Man kann mit Wert 9,999 nicht Objekt OOO initialisieren", 
	    result.toString()
	);

	result.setLength(0);
	MsgText.appendMessageLine(result, _ex1, new multex.test.MsgTextDeResourceBundle());
	assertEquals( //german numeric formatting with period as 1000-separator:
	    "Man kann mit Wert 9.999 nicht Objekt OOO initialisieren", 
	    result.toString()
	);

}


}//MsgTextTest