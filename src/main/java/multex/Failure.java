package multex; //Failure.java

import java.util.Collection;

//Change History:
//2007-09-05  Knabe  Java 5: Using Object... for exception parameters.
//2006-05-11  Knabe  Added constructor with a Collection for parameters
//2005-01-14  Knabe  checkClass-method made again private
//2005-01-13  Knabe  Uses Throwable.getCause() instead of its own cause, thus
//                   now depends on JRE >= 1.4.
//2003-09-10  Knabe  Added getContainedException(...)
//2003-07-01  Knabe  Corrected endless loop in getOriginalException(...)
//2002-07-02  Knabe  Will compile and run unchanged on JDK 1.4 and on JDK 1.1
//2002-06-27  Knabe  Will compile unchanged on JDK 1.4 and on JDK 1.1
//2002-03-26  Knabe  Example message text parameters in constructor correctly
//                   quoted
//2001-04-19  Knabe  Now its possible to redefine the checkClass-method
//2001-04-19  Knabe  Method names getCause(...) instead of cause(...),
//                   getOriginalException(...) instead of originalException(...)
//2001-04-15  Knabe  Convenience constructors with 1..9 Object-parameters added
//2001-04-14  Knabe  Again only unnamed exception parameters, not abstract
//2000-12-10  Knabe  A CauseGetter enables the MulTEx user to configure
//                   the exceptions, of which a cause can be got for reporting
//                   the causal chain.
//2000-12-08  Knabe  _cause has type Throwable rather than only Exception
//2000-10-25  Knabe  printCompactStackTrace renamed to appendCompactStackTrace
//2000-10-06  Knabe  Require default message text pattern in the exception
//2000-07-13  Knabe  Efficiently reimplemented: printCompactStackTrace
//2000-07-09  Knabe  Retrofitted to JDK 1.1: Uses Vector instead of List
//2000-06-29  Knabe  Class Exc split into Exc and Failure


/**Indirectly caused exception with parameters, causal chain and internationalizable message text.
  This class serves for the framework user as a base class for defining
  Failure-exceptions. Objects of this class can be thrown, too, but such
  exceptions cannot be handled individually nor get internationalized.
  Failure-exceptions have the following benefits: <UL>
    <LI> coherent usage always for unexpected failure of an operation
    <LI> reduce the amount of exceptions to specify in the throws-clause
         from many unexpected exceptions to only one
    <LI> chance to provide the failure of an operation with a message text,
         message parameters and a chain of the unexpected causing exceptions.
  </UL>
  The (unnamed, indexed) exception parameters are handled the same way as in
  class {@link Exc}.
  <P>
  Naming convention:
    All user-defined exception classes derived from Failure should have a name
    ending in Failure. The pattern is: <PRE>
      class OperationnameFailure extends Failure { ... }
    </PRE>

  <H2>Failure as unchecked exception?</H2>
  It can be bothering always to specify Failure in the throws-clause
  of any method, that calls other non-trivial methods. This holds especially,
  when you are converting an existing software system to usage of MulTEx.
  Thus Failure inherits the unchecked java.lang.RuntimeException.
  <P>
  So you can specify Failure, but you need not to do this.
  This is in my opinion not the cleanest way, but even without Failure
  you could not base on the assumption, that a method without a throws-clause
  does not throw any exception. Any method could throw any
  java.lang.RuntimeException, e.g. the IndexOutOfBoundsException.
  <P>
  If you want to strictly check Failure, then you can
  change the superclass of Failure to the checked java.lang.Exception instead of
  the unchecked java.lang.RuntimeException. Up to now I am not sure, what is the
  best way. Please report your experiences.
  @author Christoph Knabe, TFH Berlin, 1999-2007, http://www.tfh-berlin.de/~knabe/ 
*/

public class Failure extends RuntimeException implements MultexException {


    private static final long serialVersionUID = -4151651514573488565L;

private final String _defaultMessageTextPattern;

/**Positional parameters of this exception, or null if no parameters are given.
 * Should not be modified after initialization.
 */
private /*final*/ Object[] _parameters;

/**The unqualified name of this class*/
public static final String className = "Failure";

/**Default constructor. Not for use by client packages!*/
protected Failure(){
    //this((Object[])null, (String)null, (Throwable)null);
    //If we once set cause to null, we cannot later use initCause(Throwable) to give a better cause.
    //Thus no longer delegating to parameterized constructor. Knabe 2007-09-13
    
    _defaultMessageTextPattern = null;
    _parameters = null;
    _checkClass();
}

/**Usable as <A HREF="http://www.c2.com/cgi/wiki?ExceptionTunneling">Tunneling Exception</A>.
 * When reporting the message chain, instances of multex.Failure
 * without any own information, will be suppressed by method
 * {@link Msg#printMessages(StringBuffer,Throwable,ResourceBundle)} and
 * the similar convenience methods.
 *
 * This is a convenience constructor with only a cause, but
 * null for both i_defaultMessageTextPattern, and i_parameters.
 *
  @see #Failure(String, Throwable, Object[])
*/
public Failure(final Throwable i_cause){
  this((Object[])null, (String)null, i_cause);
}

/**Constructs a Failure with only a default message text pattern.
 * 
  @see #Failure(String, Throwable, Object...])
  @since MulTEx 7.1 at 2006-11-04
*/
public Failure(
  final String i_defaultMessageTextPattern
){
  this((Object[])null, i_defaultMessageTextPattern, (Throwable)null);
}

/**Constructs a Failure with a default message text pattern, and a cause, but without parameters.
  @see #Failure(String, Throwable, Object...)
*/
public Failure(
  final String i_defaultMessageTextPattern, final Throwable i_cause
){
  this((Object[])null, i_defaultMessageTextPattern, i_cause);
}

/**Convenience constructor with parameters as Collection.
 * You can use this as a List Failure, passing collected Throwables to i_parameters.
  @since MulTEx 7
  @see #Failure(String, Throwable, Object[])
*/
public Failure(
  final String i_defaultMessageTextPattern, /*final Throwable i_cause,*/ final Collection i_parameters
){
  this(i_parameters.toArray(), i_defaultMessageTextPattern, (Throwable)null);
}

/**Constructs a Failure-exception, giving complete diagnostic information.
* <P>Example of defining an exception with default text, cause and parameters: <PRE>
*   public static class CopyFailure extends Failure {
*     public CopyFailure(
*       final Throwable i_cause,
*       final String    i_inFileName,
*       final String    i_outFileName
*     ){
*       super("File ''{0}'' could not be copied to ''{1}''",
*         i_cause, i_inFileName, i_outFileName
*       );
*     }
*   }//CopyFailure
* </PRE>

    @param i_defaultMessageTextPattern The default message text pattern
    in the syntax of java.text.MessageFormat
    or null, if message text pattern shall only be taken from a ResourceBundle using the class name as key.
    @param i_cause The causing Throwable object for providing the diagnostics causer chain.
                   null is allowed here, if no cause is available or necessary.
    @param i_parameters Exception parameters as a polymorphic Object[],
        which can be inserted into the message text pattern by placeholders {0} ... {9}.
        null is allowed here, if you do not want to provide exception parameters.
*/
public Failure(
  final String i_defaultMessageTextPattern,
  final Throwable i_cause, final Object... i_parameters
){
    this(Util.clone(i_parameters), i_defaultMessageTextPattern, i_cause);
}

/**Does not make a clone of i_parameters*/
private Failure(
    final Object[] i_parameters,
    final String i_defaultMessageTextPattern,
    final Throwable i_cause
){
    super(i_cause);
    _defaultMessageTextPattern
    = "".equals(i_defaultMessageTextPattern) ? null : i_defaultMessageTextPattern;
    _parameters = i_parameters;
    _checkClass();
}

public Failure initParameters(final Object... i_parameters){
    this._parameters = i_parameters;
    return this;
}

public String getDefaultMessageTextPattern(){
  return _defaultMessageTextPattern;
}

public boolean hasParameters(){
    return _parameters!=null && _parameters.length>0;
}

public Object[] getParameters(){return Util.clone(_parameters);}

public String getMessage(){return Util.getUserInformation(this);}

/** Prints the chained, compact stack traces of this <code>Failure</code>
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
    //Use the better MulTEx printStackTrace instead of the JRE ones:
    Msg.printStackTrace(io_printer, this);
}


/**Checks that the class of this object is OK.
  In the moment this means, that its name ends in {@link #className}
*/
private void _checkClass(){
  Util.checkClass(this, className);
}


}//Failure