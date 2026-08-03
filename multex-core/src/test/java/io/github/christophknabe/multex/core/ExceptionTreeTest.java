package io.github.christophknabe.multex.core; //ExceptionTreeTest.java

import static io.github.christophknabe.multex.core.MultexUtil.create;
import io.github.christophknabe.multex.core.test.MultexAssert;
import org.junit.Test;


//2003-05-??  Knabe  Modified to ExceptionTreeTest
//2003-05-??  Knabe  Modified to ExceptionChainTest
//2002-05-30  Knabe  Created from Stack_bt following example junit.samples.VectorTest

/** JUnit batch test driver for the exception treeing facilities of MulTEx. */
public class ExceptionTreeTest extends MultexAssert {

    
    //Einzelne Testschritte:
    
    @Test public void testExceptionTree(){
        final SingleCopyFailure exc1 = create(SingleCopyFailure.class, new IllegalArgumentException(), "alpha.dat", "/tmp/");
        final SingleCopyFailure exc2 = create(SingleCopyFailure.class, new NullPointerException(), "beta.dat", "/tmp/");
        final CopyNotAllowedExc exc3 = create(CopyNotAllowedExc.class, "gamma.dat", "knabe");
        final MultiCopyFailure excTree = new MultiCopyFailure();
        excTree.initParameters(new Object[]{exc1, exc2, exc3});
        assertNull(excTree.getCause());
        final StringBuffer buf = new StringBuffer();
        Msg.printMessages(buf, excTree);
        final String expected = "io.github.christophknabe.multex.core.ExceptionTreeTest$MultiCopyFailure: " + _lineSeparator
            + "+CAUSE: io.github.christophknabe.multex.core.ExceptionTreeTest$SingleCopyFailure: " + _lineSeparator
            + "    {0} = 'alpha.dat'" + _lineSeparator
            + "    {1} = '/tmp/'" + _lineSeparator
            + "++CAUSE: java.lang.IllegalArgumentException" + _lineSeparator
            + "+CAUSE: io.github.christophknabe.multex.core.ExceptionTreeTest$SingleCopyFailure: " + _lineSeparator
            + "    {0} = 'beta.dat'" + _lineSeparator
            + "    {1} = '/tmp/'" + _lineSeparator
            + "++CAUSE: java.lang.NullPointerException" + _lineSeparator
            + "+CAUSE: io.github.christophknabe.multex.core.ExceptionTreeTest$CopyNotAllowedExc: " + _lineSeparator
            + "    {0} = 'gamma.dat'" + _lineSeparator
            + "    {1} = 'knabe'"
            ;
        //assertLongStringEquals(expected, buf.toString());
        assertEquals(expected, buf.toString());
    };
    
    /**The copying of the data file failed in {0} cases.*/
    public static final class MultiCopyFailure extends Failure {}
    /**Failure copying file {0} to directory {1}*/
    public static final class SingleCopyFailure extends Failure {}
    /**Copying file {0} is not allowed for user {1}.*/
    public static final class CopyNotAllowedExc extends Exc {}
    
    private static final String _lineSeparator = Util.lineSeparator;
    

}//ExceptionTreeTest