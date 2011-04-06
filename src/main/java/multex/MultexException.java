package multex; //MultexException.java

//2006-05-16  Knabe  A Parameter can be an instance of Throwable
//2006-05-11  Knabe  Added method getCause(), as Exc can have a cause now, too.
//2001-04-14  Knabe  No longer using public non-static attributes as parameters
//2000-10-06  Knabe  Creation

/**Marks an exception as to be handled especially by MsgText. A MultexException
  has indexed parameters as a polymorphic Object[], which can be inserted into
  the message text pattern by directives {0} to {9}. General purpose subclasses should provide a
  constructor with a parameter of type Object... with up to 10 elements, as well as a convenience constructor
  with a Collection of Throwable objects for the parameters.
  See <A HREF="http://java.sun.com/j2se/1.3/docs/api/java/text/MessageFormat.html">
  *java.text.MessageFormat</A>.
*/

public interface MultexException {


/**Returns the default message text pattern for this exception object or null.
  Inserting the exception parameters into the message text pattern is
  done by class {@link MsgText}.
  Locale specific message text patterns are handled by MsgText, too.
*/
String getDefaultMessageTextPattern();

/**Returns the cause given to this exception, or null.
 * In the case of a list exception (that is an exception, which has Throwable-objects as parameters),
 * it does not return any of these parameters. They have to be retrieved by {@link #getParameters()}.
 * @return The direct main cause of this exception.
  Overrides the same method of
  <A HREF="http://java.sun.com/j2se/1.5.0/docs/api/java/lang/Throwable.html#getCause()">java.lang.Throwable</A>
 */
Throwable getCause();

/**Returns true, if the exception parameter array exists and has at least one
 * element.*/
boolean hasParameters();

/**Returns a copy of the positional exception parameter array, or null if the exception does not have parameters.*/
Object[] getParameters();

/**
  Returns all user-provided information contained in the exception object
  in an internal, but human readable format. The information are the default
  message text pattern and
  the exception parameters of this object or null if none of these is provided.
  E.g.:
  <PRE>
  *Unable to create file {0} in directory {1}
  *    {0}='myfile.dat'
  *    {1}='C:\temp\data'
  </PRE>
  Overrides the same method of
  <A HREF="http://java.sun.com/j2se/1.3/docs/api/java/lang/Throwable.html">java.lang.Throwable</A>
*/
public String getMessage();

/**Initializes the parameters array of this exception.
 * Normally to be called only by the MulTEx framework.
 * @param i_parameters the parameters to be used for initialization
 * @return the exception object, of which the parameters have been initialized.
 */
MultexException initParameters(final Object... i_parameters);


}