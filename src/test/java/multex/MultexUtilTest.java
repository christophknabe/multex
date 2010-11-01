package multex;

import static multex.MultexUtil.create;

import java.io.File;

import junit.framework.TestCase;

public class MultexUtilTest extends TestCase {

    public MultexUtilTest(String name) {
        super(name);
    }

    public void testCreateExcWithoutParams(){
        final MultexException e = create(FileNotFoundExc.class);
        assertTrue(e instanceof FileNotFoundExc);
        final Object[] parameters = e.getParameters();
        assertEquals(0, parameters.length);
    }
    
    public void testCreate(){
        final File dataDirectory = new File("/data");
        final MultexException e = create(FileNotFoundExc.class, "data.log", dataDirectory);
        assertTrue(e instanceof FileNotFoundExc);
        final Object[] parameters = e.getParameters();
        assertEquals("data.log", parameters[0]);
        assertSame(dataDirectory, parameters[1]);
    }

    public void testCreateExcWithCause(){
        final Exception cause = new Exc("cause");
        final File dataDirectory = new File("/data");
        final MultexException e = create(FileNotFoundExc.class, cause, "data.log", dataDirectory);
        assertTrue(e instanceof FileNotFoundExc);
        assertSame(cause, e.getCause());
        final Object[] parameters = e.getParameters();
        assertEquals("data.log", parameters[0]);
        assertSame(dataDirectory, parameters[1]);
    }
    
    public void testCreateFailureWithCause(){
        final Exception cause = new Exc("cause");
        final File dataDirectory = new File("/data");
        final MultexException e = create(FileOpenFailure.class, cause, "data.log", dataDirectory);
        assertTrue(e instanceof FileOpenFailure);
        assertSame(cause, e.getCause());
        final Object[] parameters = e.getParameters();
        assertEquals("data.log", parameters[0]);
        assertSame(dataDirectory, parameters[1]);
    }
    
    public void testThrowCreate(){
        final File dataDirectory = new File("/data");
        try {
            throw create(FileNotFoundExc.class, "data.log", dataDirectory);
        } catch (FileNotFoundExc e) {
            final Object[] parameters = e.getParameters();
            assertEquals("data.log", parameters[0]);
            assertSame(dataDirectory, parameters[1]);
        }
    }
    
    /**File {0} not found in directory {1}*/
    static final class FileNotFoundExc extends Exc {}
    /**Failure opening file {0} in directory {1}*/
    static final class FileOpenFailure extends Failure {}
    
    

}
