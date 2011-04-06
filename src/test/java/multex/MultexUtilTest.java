package multex;

import java.io.File;
import org.junit.Test;
import org.junit.Assert;
import static multex.MultexUtil.create;


public class MultexUtilTest extends Assert {


    @Test public void createExcWithoutParams(){
        final MultexException e = create(FileNotFoundExc.class);
        assertTrue(e instanceof FileNotFoundExc);
        final Object[] parameters = e.getParameters();
        assertEquals(0, parameters.length);
    }
    
    @Test public void createExcWithParams(){
        final File dataDirectory = new File("/data");
        final MultexException e = create(FileNotFoundExc.class, "data.log", dataDirectory);
        assertTrue(e instanceof FileNotFoundExc);
        final Object[] parameters = e.getParameters();
        assertEquals("data.log", parameters[0]);
        assertSame(dataDirectory, parameters[1]);
    }

    @Test public void createExcWithCause(){
        final Exception cause = new Exc("cause");
        final File dataDirectory = new File("/data");
        final MultexException e = create(FileNotFoundExc.class, cause, "data.log", dataDirectory);
        assertTrue(e instanceof FileNotFoundExc);
        assertSame(cause, e.getCause());
        final Object[] parameters = e.getParameters();
        assertEquals("data.log", parameters[0]);
        assertSame(dataDirectory, parameters[1]);
    }
    
    @Test public void createFailureWithCause(){
        final Exception cause = new Exc("cause");
        final File dataDirectory = new File("/data");
        final MultexException e = create(FileOpenFailure.class, cause, "data.log", dataDirectory);
        assertTrue(e instanceof FileOpenFailure);
        assertSame(cause, e.getCause());
        final Object[] parameters = e.getParameters();
        assertEquals("data.log", parameters[0]);
        assertSame(dataDirectory, parameters[1]);
    }
    
    @Test public void throwCreate(){
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
