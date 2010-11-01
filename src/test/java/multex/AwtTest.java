package multex;  //AwtTest.java

//2004-05-11  Knabe  Aus Vorlage MsgTest

/**JUnit batch test driver for the class multex.Awt.
*/
public class AwtTest extends junit.framework.TestCase {


//Verwaltungsoperationen:

public AwtTest(String name) {
  super(name);
}
public static void main (String[] args) {
  junit.textui.TestRunner.run(suite());
}
public static junit.framework.Test suite() {
  final junit.framework.TestSuite result
  = new junit.framework.TestSuite(AwtTest.class);
  return result;
}

//Einzelne Testschritte:

/**Tests counting the lines in a String
*/
public void testCountLines(){
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

public static void testRegisterAwtExceptionHandlerClass(){
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