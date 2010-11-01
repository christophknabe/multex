package multex;

import static multex.MultexUtil.create;

//ExceptionTreeTest.java

//2003-05-??  Knabe  Umbau zu ExceptionTreeTest
//2003-05-??  Knabe  Umbau zu ExceptionChainTest
//2002-05-30  Knabe  Umbau von Stack_bt gem‰ﬂ Beispiel junit.samples.VectorTest

/**JUnit batch test driver for the exception treeing facilities of MulTEx.*/
public class ExceptionTreeTest extends junit.framework.TestCase {

    
    //Verwaltungsoperationen:
    
    public ExceptionTreeTest(final String name) {
      super(name);
    }
    public static void main (final String[] args) {
      junit.textui.TestRunner.run(suite());
    }
    public static junit.framework.Test suite() {
      final junit.framework.TestSuite result
      = new junit.framework.TestSuite(ExceptionTreeTest.class);
      return result;
    }
    
    //Einzelne Testschritte:
    
    public void testExceptionTree(){
        final SingleCopyFailure exc1 = create(SingleCopyFailure.class, new IllegalArgumentException(), "alpha.dat", "/tmp/");
        final SingleCopyFailure exc2 = create(SingleCopyFailure.class, new NullPointerException(), "beta.dat", "/tmp/");
        final CopyNotAllowedExc exc3 = create(CopyNotAllowedExc.class, "gamma.dat", "knabe");
        final MultiCopyFailure excTree = new MultiCopyFailure();
        excTree.initParameters(new Object[]{exc1, exc2, exc3});
        assertNull(excTree.getCause());
        final StringBuffer buf = new StringBuffer();
        Msg.printMessages(buf, excTree);
        final String expected = "multex.ExceptionTreeTest$MultiCopyFailure: " + _lineSeparator
            + "+CAUSE: multex.ExceptionTreeTest$SingleCopyFailure: " + _lineSeparator
            + "    {0} = 'alpha.dat'" + _lineSeparator
            + "    {1} = '/tmp/'" + _lineSeparator
            + "++CAUSE: java.lang.IllegalArgumentException" + _lineSeparator
            + "+CAUSE: multex.ExceptionTreeTest$SingleCopyFailure: " + _lineSeparator
            + "    {0} = 'beta.dat'" + _lineSeparator
            + "    {1} = '/tmp/'" + _lineSeparator
            + "++CAUSE: java.lang.NullPointerException" + _lineSeparator
            + "+CAUSE: multex.ExceptionTreeTest$CopyNotAllowedExc: " + _lineSeparator
            + "    {0} = 'gamma.dat'" + _lineSeparator
            + "    {1} = 'knabe'"
            ;
        multex.test.TestUtil.assertLongStringEquals(expected, buf.toString());
    };
    
    /**The copying of the data file failed in {0} cases.*/
    public static final class MultiCopyFailure extends Failure {}
    /**Failure copying file {0} to directory {1}*/
    public static final class SingleCopyFailure extends Failure {}
    /**Copying file {0} is not allowed for user {1}.*/
    public static final class CopyNotAllowedExc extends Exc {}
    
    private static final String _lineSeparator = Util.lineSeparator;
    

}//ExceptionTreeTest