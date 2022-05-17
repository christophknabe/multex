package multex;

import java.lang.reflect.Modifier;
import java.util.Arrays;

/**This class contains static API methods, which can be used nearly as keywords.
 * <p>You can put them for easy usage into the global namespace by declaring
 * <br><code>import static multex.MultexUtil.*;</code>
 * </p>
 * @author Christoph Knabe
 * @since MulTEx 7.3 2007-09-18
 */
public class MultexUtil {
    
    
    /**Do not create objects of me!*/
    private MultexUtil(){}

    /**Creates a new, parameterized exception object. Usages e.g.
     * <pre>if(!allowed(username, file)){
     *    throw create(FileAccessRightExc.class, username, file);
     *}
     *...
     *try{ ...
     *}catch(Exception ex){
     *    throw create(UserLoginFailure.class, ex, username);
     *}
     *</pre>
     *The thrown exceptions do no longer need to have a constructor to pass the diagnostic information to the super class constructor. 
     *This is done after the creation by this method. Thus the thrown exceptions can look so simple as follows:
     *<pre>
     *public static final class FileAccessRightExc extends multex.Exc {}
     *public static final class UserLoginFailure extends multex.Failure {}
     *</pre> 
     * @param <E> The specialized exception type to be created
     * @param c A Class object for the desired exception type
     * @param io_parameters Message parameters for the exception. If the first parameter is of type Throwable, as in the example creating a UserLoginFailure, 
     *        it will be separated as the cause of the new exception. The last parameter will by this become null.
     * @return The new, parameterized exception object
     * @throws IllegalArgumentException given Class is an inner non-static class or does not have a parameterless constructor 
     * @throws RuntimeException Failure during the creation by reflection
     * @since MulTEx 7.3 at 2007-09-18
     */
    public static <E extends Exception & MultexException> E create(final Class<E> c, final Object... io_parameters) throws RuntimeException {
        final E e;
        String className = null;
        try {
            Util.checkClassIsStatic(c, null);
            className = c.getName();
            e = c.newInstance();
            Util.shiftParameter0ToCauseIfNecessary(e, io_parameters);
            e.initParameters(io_parameters);
        } catch (Exception createException) {
            throw new RuntimeException("Cannot create " + className + ", parameters: " + Arrays.toString(io_parameters), createException);
        }
        return e;
    }
    

}//class
