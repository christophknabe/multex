package multex;

import java.util.Collection;

//History:
//2007-09-05  Knabe  Java 5: Using Object... for exception parameters.
//2006-05-11  Knabe  Added constructor with a Collection for parameters
//2006-05-04  Knabe  Added constructors with cause as in Failure
//2001-04-19  Knabe  Now its possible to redefine the checkClass-method
//2001-04-19  Knabe  getUserInformation returns the default message text pattern
//                   and the message parameters, each on a separate line
//2001-04-15  Knabe  Convenience constructors with 1..10 Object-parameters added
//2001-04-14  Knabe  Again only unnamed exception parameters
//2000-10-06  Knabe  Require default message text pattern in the exception
//2000-09-29  Knabe  Allows only named parameters
//2000-09-28  Knabe  checkClass checks public-ness of the class, too
//2000-07-31  Knabe  getMessage() formats parameter as {i}=.. rather than [i]=..
//2000-06-29  Knabe  _cause gets type Exception rather than Throwable
//2000-06-19  Knabe  printStackTrace() bei Ursachenkette ohne Ausgabe
//                   von redundanten Zeilen.
//2000-06-12  Knabe  all printStackTrace(...) variants implemented by printStackTrace(PrintWriter)
//1999-03-23  Knabe  Parametrierung wie in Java-Tutorial/i18n/format
//1999-03-12  Knabe  NEU: causingThrowable()
//1999-01-14  Knabe  erstellt

/**Business rule exception (precondition violation) with parameters and internationalizable
  message text (, and in seldom cases a cause).
  This class serves for the framework user as a base class for defining
  business rule exceptions. Objects directly of this class can be thrown, too, but such
  exceptions cannot be catched individually nor get internationalized.

  <H2>Naming convention:</H2>
  All user-defined exception classes derived from Exc should have a name
  ending in Exc. The pattern is: <PRE>
    class ErrorconditionExc extends Exc { ... }
  </PRE>

  <H2>Parameters</H2>
  All elements of the Object[] are
  considered positional parameters of this exception. The corresponding
  placeholders in the message text patterns have the syntax: {i}
  with i being the number of the parameter in the range 0..9.
  <P>
  The exception parameters can
  be substituted into the corresponding message text pattern in a desired locale
  format by class {@link MsgText}, which itself makes use of class
  java.text.MessageFormat. 
  <br>
  See the usage examples Copy, AwtCopy, SwingCopy in directory demo.
  <br/>
  Since MulTEx version 7 of 2006-05-06 you can pass many instances of Throwable as parameters.
  They will form an exception tree and are correctly reported by class {@link Msg}.

  <H2>Relation to Standards</H2>
  Class java.text.MesageFormat does much more in direction of internationalization
  than just to insert parameters into a message text pattern.
  You can e.g. specify different styles of formatting a parameter.
  E.g. formatting a parameter of type java.util.Date at position 0
  could be done by the following parameter formatting directive:
  {0,date}.

  <H2>Cause of a business rule exception</H2>
  The class Exc does since MulTEx version 7 of 2006-05-06 provide a parameter cause in its constructor.
  In the rare case, you have got a cause of a business rule exception,
  you can provide it just like to a {@link multex.Failure}.

  <P>Example</P>
  <PRE>
* //NIS-Authentication of userName:
* final String userData;
* try {
*     userData = initialContext.lookup("system/passwd/"+userName).toString();
* } catch (final NameNotFoundException ex) {
*     throw new multex.Exc("User name {0} unknown", ex, userName);
* }
  </PRE>

  <H2>Warning</H2>
  Usually you get a causal exception only when performing the modification part of an operation.
  Such causal exceptions should be captured using a {@link multex.Failure}.
  @author Christoph Knabe, TFH Berlin, 1999-2007, http://www.tfh-berlin.de/~knabe/ 
*/

public class Exc extends java.lang.RuntimeException implements MultexException { //2007-09-21 as unchecked exception MulTEx 8

    
    private static final long serialVersionUID = 7898008365464293998L;

/**The unqualified name of this class*/
public static final String className = "Exc";


private final String _defaultMessageTextPattern;

/**Positional parameters of this exception, or null if no parameters are given.*/
private /*final*/ Object[] _parameters; //can be set by constructor or method init

/**Constructs an Exc with now no diagnostic information.
 * You should additionally provide:<UL>
 * <LI>message text pattern as main Javadoc comment of this exception class</LI>
 * <LI>message text parameters by invoking the method {@link #initParameters(Object[])} on the empty Exc object</LI>
 * <LI>if necessary a cause by invoking the method {@link #initCause(Throwable)} on the result of the former</LI>
 * </UL>
 * @since MulTEx 7.3 2007-09-06
@see #Exc(String, Object...)
*/
public Exc(){
this((Object[])null, (String)null);
}

/**Constructs an Exc with only a default message text pattern.
@see #Exc(String, Object...)
*/
public Exc(final String i_defaultMessageTextPattern){
this((Object[])null, i_defaultMessageTextPattern);
}

/** Constructs an Exc with a default message text pattern
* and exception parameters as an polymorphic Object[].
* <P>Example of defining an exception with parameters: <PRE>
*  public static class AuthorizationExc extends Exc {
*    public AuthorizationExc(final String i_username, final String i_functionName){
*      super("User {0} is not allowed to use function {1}.", i_username, i_functionName);
*    }
*  }
* </PRE>

    @param i_defaultMessageTextPattern The default message text pattern
    in the syntax of java.text.MessageFormat
    or null, if message text patterns shall only be taken from a java.util.ResourceBundle
    @param i_parameters Exception parameters as an polymorphic Object[],
        which can be inserted into the message text pattern by placeholders {0} ... {9}.
        null is allowed here, if you do not want to provide exception parameters.
*/
public Exc(
  final String i_defaultMessageTextPattern, final Object... i_parameters
){
    this(Util.clone(i_parameters), i_defaultMessageTextPattern);
}

/**Convenience constructor null
  @see #Exc(String, Throwable, Object[])
*/
public Exc(
  final String i_defaultMessageTextPattern, final Throwable i_cause
){
  this((Object[])null, i_defaultMessageTextPattern, i_cause);
}

/**Constructs an Exc with a default message text pattern, a cause,
* and exception parameters as an polymorphic Object[].
* When to give a cause to an Exc is discussed in the class description.
* <P>Example of defining an Exc with default text, cause and parameters: <PRE>
*   public static class UserUnknownExc extends Exc {
*     public UserUnknownExc(
*       final Throwable i_cause,
*       final String    i_userName
*     ){
*       super("User ''{0}'' does not exist in this system.",
*         i_cause, i_userName
*       );
*     }
*   }//UserUnknownExc
* </PRE>

    @param i_defaultMessageTextPattern The default message text pattern
    in the syntax of java.text.MessageFormat
    or null, if message text pattern shall only be taken from a ResourceBundle using the class name as key.
    @param i_cause The causing Throwable object for providing the diagnostics causer chain.
                   null is allowed here, if no cause is available or necessary.
                   In most cases an Exc will not have a cause, but will be checked for by an if-condition.
    @param i_parameters Exception parameters as a polymorphic Object[],
        which can be inserted into the message text pattern by placeholders {0} ... {9}.
        null is allowed here, if you do not want to provide exception parameters.
*/
public Exc(
  final String i_defaultMessageTextPattern,
  final Throwable i_cause, final Object... i_parameters
){
    this(Util.clone(i_parameters), i_defaultMessageTextPattern, i_cause);
}


/**Convenience constructor with parameters as Collection.
 * You can use this as a List Exc, passing collected Throwables to i_parameters.
  @see #Exc(String, Throwable, Object[])
*/
public Exc(
  final String i_defaultMessageTextPattern, /*final Throwable i_cause,*/ final Collection i_parameters
){
  this(i_parameters.toArray(), i_defaultMessageTextPattern);
}

/**Does not make a clone of i_parameters*/
private Exc(
    final Object[] i_parameters, final String i_defaultMessageTextPattern
){
  _defaultMessageTextPattern
  = "".equals(i_defaultMessageTextPattern) ? null : i_defaultMessageTextPattern;
  _parameters = i_parameters;
  _checkClass();
}

/**Does not make a clone of i_parameters*/
private Exc(
    final Object[] i_parameters, final String i_defaultMessageTextPattern,
    final Throwable i_cause
){
    super(i_cause);
  _defaultMessageTextPattern
  = "".equals(i_defaultMessageTextPattern) ? null : i_defaultMessageTextPattern;
  _parameters = i_parameters;
  _checkClass();
}

public Exc initParameters(final Object... i_parameters){
    this._parameters = i_parameters;
    return this;
}


/**Throws an Exc object of the class of the method, which is directly calling this method.
 * To be used like the following pattern in self-checking exceptions:
 * <PRE>
 *class UserNotAllowedExc extends multex.Exc {
 *    void check(final User user, final File file) throws UserNotAllowedExc {
 *        if(!user.isAllowed(file)){
 *            throwMe(user, file);
 *        }
 *    }
 *}
 *</PRE>
 *Atttention: As long as Exc is a checked exception, the here mentioned throws clause is not sufficient.
 *You will have to write throws Exc.
 * @param i_parameters the parameters for the exception message
 * @since MulTEx 7.3 2007-09-18
 */
public static <E extends Exc> void throwMe(final Object... i_parameters) throws E {
    Class c = null;
    final E e;
    try {
        final Class[] callingClasses = securityManager.getCallingClasses();
        //final List<Class> callingClassesList = Arrays.asList(callingClasses);
        //System.out.println("Calling classes: " + callingClassesList);
        c = callingClasses[2];
        e = (E)c.newInstance();
        e._parameters = i_parameters;
    } catch (Exception cause) {
        throw new RuntimeException("Cannot throw " + c, cause);
    }
    throw e;
}
/**Purpose: Enabling access to a protected method of java.lang.SecurityManager.*/
private static final class MySecurityManager extends SecurityManager {
    public Class[] getCallingClasses(){
        return getClassContext();
    }
}
private static final MySecurityManager securityManager = new MySecurityManager();

public String getMessage(){return Util.getUserInformation(this);}

public boolean hasParameters(){
    return _parameters!=null && _parameters.length>0;
}

public Object[] getParameters(){return Util.clone(_parameters);}

public String getDefaultMessageTextPattern(){
  return _defaultMessageTextPattern;
}

/** Prints the chained, compact stack traces of this <code>Exc</code>
  object to the standard error stream
  <A HREF="http://java.sun.com/j2se/1.3/docs/api/java/lang/System.html#err">System.err</A>.
  @see #printStackTrace(java.io.PrintWriter)
*/
public void printStackTrace(){
    Msg.printStackTrace(this);
}

/** Prints the chained, compact stack traces of this <code>Failure</code>
  object to io_printer.
* @see #printStackTrace(java.io.PrintWriter)
*/
public void printStackTrace(final java.io.PrintStream io_printer) {
    Msg.printStackTrace(io_printer, this);
}

/** Prints the chained, compact stack traces of this <code>Failure</code>
  object to io_printer.
*/
public void printStackTrace(final java.io.PrintWriter io_printer) {
    //super.printStackTrace(io_printer);
    //Use the better MulTEX printStackTrace instead of the JRE ones:
    Msg.printStackTrace(io_printer, this);
}

/**Checks that the class of this object is OK.
  In the moment this means, that its name ends in {@link #className}
*/
private void _checkClass(){
  Util.checkClass(this, className);
}

}//Exc