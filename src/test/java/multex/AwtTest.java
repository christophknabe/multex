package multex;  //AwtTest.java

import org.junit.Test;
import org.junit.Assert;

//2011-03-25  Knabe  Migrated to JUnit 4.5
//2004-05-11  Knabe  Aus Vorlage MsgTest

/** JUnit batch test driver for the class multex.Awt */
public class AwtTest extends Assert {
    
    
/** Tests counting the lines in a String. */
@Test public void countLines(){
	_testCountLines(0, "");
	_testCountLines(1, "a");
	_testCountLines(1, "abc" + _lineSeparator);
	_testCountLines(2, "abc" + _lineSeparator + "def");
	_testCountLines(2, "abc" + "\r\n" + "def");
	_testCountLines(2, "abc" + "\r" + "def");
	_testCountLines(2, "abc" + "\n" + "def");
}

private void _testCountLines(
	final int i_expectedNumberOfLines, final String i_string
){
	assertEquals(i_expectedNumberOfLines, Awt.countLines(i_string));
}

private static final String _lineSeparator = Util.lineSeparator;

private static class _ExcHdlOK implements AwtExceptionHandler {
    public _ExcHdlOK(){}
    public void handle(Throwable t){}
} 

private static class _ExcHdlWithoutDefaultConstructor implements AwtExceptionHandler {
    public _ExcHdlWithoutDefaultConstructor(String s){}
    public void handle(Throwable t){}
} 

@Test public void registerAwtExceptionHandlerClass(){
    try{
        Awt.setAwtExceptionHandlerClass(null);
        fail("Failure expected");
    } catch ( Failure expected ){}

    try{
        Awt.setAwtExceptionHandlerClass(new _ExcHdlWithoutDefaultConstructor("s"));
        fail("Failure expected");
    } catch ( Failure expected ){}
    
    //Successfull registration:
    Awt.setAwtExceptionHandlerClass(new _ExcHdlOK());
    final String expected = System.getProperty("sun.awt.exception.handler");
    assertEquals(expected, _ExcHdlOK.class.getName());
}

}//AwtTest