package io.github.christophknabe.multex.core;  //AwtTest.java

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

private static class _ExcHdlWithoutDefaultConstructor extends AwtExceptionHandler {
    public _ExcHdlWithoutDefaultConstructor(String s){
        super(null);
    }
    public void handle(Throwable throwable){}
}

}//AwtTest