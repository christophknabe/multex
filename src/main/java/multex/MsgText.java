package multex; //MsgText.java

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;



//Change history:
//2003-09-09  Knabe  MulTEx 5: Dynamic internationalization only by ResourceBundle
//                   parameter i_lineSeparator removed in all methods
//2003-05-22  Knabe  Enables dynamic internationalization. See appendMessageChain.
//2001-02-26  Knabe  Internationalization of message texts is now optional.
//2000-10-25  Knabe  appendMessageChain and appendMessageLine append to a
//                   StringBuffer rather than returning a String.
//2000-07-07  Knabe  extrahiert aus Klasse Msg


/** Services for getting the message text for exceptions.
  It handles exceptions <UL>
    <LI>with parameters an may be a cause chain (subclasses of {@link Exc})
    <LI>with cause chain and parameters (subclasses of {@link Failure})
    <LI>and standard Java exceptions (other subclasses of java.lang.Throwable)
  </UL>
  For normal message reporting you should use the services of one of the
  destination oriented reporting classes, e.g. {@link Msg}, {@link Awt}, or {@link Swing}.

  <P>MulTEx 5: Parameter i_lineSeparator removed in all methods.
  Always uses the platform default.
  See the discussion in the description of class {@link Msg}.

  @author Christoph Knabe, Berlin, Copyright 1999-2006
*/
public class MsgText {


	private static final String _className = MsgText.class.getName();

/**The name of the ResourceBundle used on static default internationalization.
    @see #setInternationalization(boolean)
*/
public  static final String resourceBundleName = "MsgText";

/**The String used by default to introduce the message of an exception,
 * which was causing the exception justly reported.
 * You can customize this by defining a cause marker in the localization
 * ResourceBundle under the key {@link #causeMarkerKey}.
 * */
public  static final String causeMarker = "CAUSE: ";

/**The key used to get the cause marker from a ResourceBundle.
 * It is the fully qualified name of the field {@link #causeMarker},
 * In the .properties file for a localization you should define it e.g.:
 * <PRE>multex.MsgText.causeMarker = Cause: </PRE>
 * */
public  static final String causeMarkerKey = _className + ".causeMarker";

/**If true, activates the static default internationalization of message texts.
    Further calls to
  {@link #appendMessageLine} or {@link #appendMessageTree} will deliver the
  localized message text for an exception instead of the default message
  text contained in the MultexException-object.
  The localized message text will be taken from the resource bundle
  with the name {@link #resourceBundleName}.

  If false, deactivates static internationalization. The Message text for a
  MultexException will be taken directly from the exception object itself.

  @since MulTEx 1c
*/
public static synchronized void setInternationalization(final boolean i_internationalization){
    _internationalization = i_internationalization;
    if(_internationalization){
        _resourceBundle = _tryLoadMessages();
    }else{
        _resourceBundle = null;
    }
}


/**Activates a specific static internationalization of message texts.
 * Further calls to
  {@link #appendMessageLine} or {@link #appendMessageTree} will deliver the
  localized message text for an exception instead of the default message
  text contained in the MultexException-object.

  @param i_resourceBundle How to localize exception message texts

  @throws IllegalArgumentException If null is provided as i_locale

  @since MulTEx 4

  @see #setInternationalization(boolean)
*/
public static synchronized void setInternationalization(final ResourceBundle i_resourceBundle)
throws IllegalArgumentException
{
    if(i_resourceBundle==null){throw new IllegalArgumentException("ResourceBundle must not be null");}
    _internationalization = true;
    _resourceBundle = i_resourceBundle;
}

/**Returns the actual ResourceBundle used for static internationalization
 * or null, if no one is provided.*/
public static ResourceBundle getResourceBundle(){return _resourceBundle;}


/**Appends the message for i_throwable along with its causal chain.
    Preferably use {@link Msg#printMessages(StringBuffer,Throwable,ResourceBundle)} instead.
    @param io_destination where to append, not null.
    @param i_throwable the exception to be reported, not null.
    @param i_resourceBundle the bundle to use for
*/
protected static void appendMessageTree(
    final StringBuffer             io_destination,
    final Throwable                 i_throwable,
    final ResourceBundle            i_resourceBundle
){
    final ResourceBundle resourceBundle = i_resourceBundle!=null ? i_resourceBundle : getResourceBundle();
    final String causeMarker = _getCauseMarker(resourceBundle)
    ;
    if(null==i_throwable){
        io_destination.append("null Throwable provided to ")
        .append(_className).append(".appendMessageTree(...)");
        return;
    }
    //Throwable-Meldungen eintragen:
    int level = 0;
    //before it was: _appendMessageChainRecursively
    _appendMessageTreeRecursively(io_destination, i_throwable, resourceBundle, causeMarker, level);
}

/**Appends the message for i_throwable along with its causing exceptions.
 * @param io_destination not null
 * @param i_throwable not null
 * @param i_resourceBundle may be null
 * @param i_causeMarker not null
 * @param i_level distance from root of exception tree (=0 means exception to be reported by user call, >0 means exception found as cause or parameter of a higher exception)
 */
private static void _appendMessageTreeRecursively(
    final StringBuffer io_destination, final Throwable i_throwable, final ResourceBundle i_resourceBundle,
    final String i_causeMarker, final int i_level
){
    final Throwable directCause = Util.getCause(i_throwable);
    //Tests, if i_throwable should be reported, i.e. is not a tunnelling exception
    final boolean isTunnelling;
    if(i_throwable instanceof Failure){
        final Failure failure = (Failure)i_throwable;
        if( failure.getDefaultMessageTextPattern()==null  && !failure.hasParameters() ){ //does not have own info
            //final Throwable secondCause = Util.getCause(i_throwable);
            isTunnelling = directCause!=null;
        }else{
            isTunnelling = false;
        }
    }else{
        isTunnelling = false;
    }

    if(!isTunnelling) {
        _appendCauseLine(io_destination, i_throwable, i_resourceBundle, i_causeMarker, i_level);
    }
    if(directCause!=null){
        _appendMessageTreeRecursively(
            io_destination, directCause,    i_resourceBundle, i_causeMarker
            , i_level + (isTunnelling ? 0 : 1)
        );
    }

    if(!(i_throwable instanceof MultexException)){return;}
    final MultexException multexException = (MultexException)i_throwable;
    final Object[] params = multexException.getParameters();
    if(null==params){return;}
    for(int i=0; i<params.length; i++){
        final Object param = params[i];
        if(param instanceof Throwable){
            final Throwable cause = (Throwable)param;
            _appendMessageTreeRecursively(io_destination,   cause,  i_resourceBundle, i_causeMarker, i_level+1);
        }
    }//for
}

/**Appends the message for one exception (usually one line), prefixed by a cause level indentation, and if level>0 a causeMarker.
 * @param io_destination where to append
 * @param i_throwable which exception (without its causes) to be reported
 * @param resourceBundle to get message text for the exception from 
 * @param causeMarker String to introduce a causing exception
 * @param level How many levels to indent this line
 */
private static void _appendCauseLine(final StringBuffer io_destination, final Throwable i_throwable, final ResourceBundle resourceBundle, final String causeMarker, final int level) {
    if(level>0){
        io_destination.append(Util.lineSeparator);
        Util.appendCauseIndentation(io_destination, level);
        io_destination.append(causeMarker);
    }
    _appendMessageLineDeletingPreviousOccurence(
        io_destination, i_throwable,    resourceBundle
    );
}


/**Appends the usually one-line message text for the exception
  i_throwable with inserted parameters to io_buffer.
  This variant deletes one previous occurence of the message line
  in io_destination.
*/
private static void _appendMessageLineDeletingPreviousOccurence(
    final StringBuffer io_destination,
    final Throwable t,
    final ResourceBundle resourceBundle
){
    final int previousLength = io_destination.length();

    //appendMessageLine(io_destination, t, resourceBundle);
    //final String lastLine = io_destination.substring(previousLength); //substring since JDK 1.2
    final String lastLine = appendMessageLine(io_destination, t, resourceBundle);

    if(lastLine!=null){
        //platform exception without message text pattern, may be redundant in message chain
        final int firstOccurenceIndex = io_destination.indexOf(lastLine);
        if(firstOccurenceIndex<previousLength){ //There is a previous occurence of lastLine
            //replace it by "...":
            io_destination.replace(firstOccurenceIndex,firstOccurenceIndex+lastLine.length(),"..."); //replace since JDK 1.2
        }
    }
}


/**Appends the usually one-line message text for the exception
  i_throwable with inserted parameters to io_buffer.

  The message text pattern for the exception is taken from the mostly localized
  variant of the properties file "MsgText.properties" or helpwise from the
  default message text pattern in the exception object itself. A local message
  text pattern for the german language (de) in its swiss (CH) variant would be
  taken from file "MsgText_de_CH.properties", see the Java Tutorial
  <A HREF="http://java.sun.com/docs/books/tutorial/i18n/format/messageFormat.html">
  chapter on internationalization</A> at february 1999.
  <P>
  This file must contain definition lines of the form: <PRE>
     packageName.ExceptionName = message text pattern
  </PRE>or<PRE>
     packageName.ClassName$InnerExceptionName = message text pattern
  </PRE>with parameter substitutors in the message text pattern, e.g.:<PRE>
     CopyNP$CopyFailure = The file "{0}" could not be copied to "{1}"
  </PRE>
*/
public static void appendMessageLine(final StringBuffer io_destination, final Throwable i_throwable){
    appendMessageLine(io_destination, i_throwable, getResourceBundle());
}

/**Appends the usually one-line message text for the exception
 * i_throwable with inserted parameters to io_buffer, localizable.
 * <P>The message line format is produced according to the following case list:
 * <UL>
 *   <LI>MulTEx exceptions:
 *     <UL>
 *       <LI>textPattern & parameters: textPattern with parameters inserted
 *       <LI>textPattern, no parameters: textPattern unchanged
 *       <LI>no textPattern, with parameters: ClassName: getMessage()
 *       <LI>neither textPattern nor parameters: ClassName
 *     </UL>
 *   <LI>Standard Java exceptions:
  *     <UL>
 *       <LI>textPattern & detailMessage: textPattern: getMessage()
 *       <LI>textPattern, no detailMessage: textPattern unchanged
 *       <LI>no textPattern, with detailMessage: ClassName: getMessage()
 *       <LI>neither textPattern nor detailMessage: ClassName
 *     </UL>
 * </UL>
 *
 *
 * @param i_resourceBundle Source for the message text pattern for reporting i_throwable.
 *          The text pattern is taken with the class name of i_throwable
 *          as key from i_resourceBundle.
 *          Controls the formatting of the message parameters, too, by containing a Locale.
 *          If null, falls back to no internationalization.
 * @return The appended line, if it was created by i_throwable.toString()
 *          If not null, i_throwable had neither a text pattern, nor message parameters.
 *          Useful only for efficiently deleting redundant info in legacy string chain of platform exceptions.
 * @see #appendMessageLine(StringBuffer, Throwable) for details of parameter substitution
 * */
public static String appendMessageLine(
    final StringBuffer io_destination,
    final Throwable i_throwable,
    final ResourceBundle i_resourceBundle
) {
    //final ResourceBundle i_resourceBundle = i_resourceBundle==null ? getResourceBundle() : i_resourceBundle;
      try {
//      final String textPattern = getMessageTextPattern(i_throwable, resourceBundle);
//      final Object[] parameters = _getParameters(i_throwable);
//      if(parameters==null){ //no insertable exception parameters
//        io_destination.append(textPattern);
//      }else{ //with exception parameters:
//        final Locale locale = resourceBundle==null ? null : resourceBundle.getLocale();
//        format(io_destination, textPattern, parameters, locale);
//      }
//      if(!(i_throwable instanceof MultexException)){
//        //other Java exceptions may have detailMessage:
//        final String detailMessage = i_throwable.getMessage();
//        if(detailMessage!=null && detailMessage.length()!=0){
//          //Append standard Java detailMessage, separated by a colon
//          io_destination.append(": ");
//          io_destination.append(detailMessage);
//        }
//      }

        final String textPattern = getMessageTextPattern(i_throwable, i_resourceBundle);
        final Object[] parameters = _getParameters(i_throwable);
        if(textPattern!=null && parameters!=null){ //full regular MulTEx case
            //Insert parameters into text pattern:
            final Locale locale = i_resourceBundle==null ? null : i_resourceBundle.getLocale();
            format(io_destination, textPattern, parameters, locale);
        }else if(textPattern!=null && i_throwable instanceof MultexException){
            //MulTEx case without insertable exception parameters
            io_destination.append(textPattern);
        }else if(textPattern!=null){ //a standard exception with text from resource bundle
            io_destination.append(textPattern);
            final String detailMessage = i_throwable.getMessage();
            if(detailMessage!=null){
                io_destination.append(": ");
                io_destination.append(detailMessage);
            }
        }else{ //no text pattern available
            final String result = Util.toString(i_throwable);
            io_destination.append(result);
            return result;
        }
        return null;
      } catch(final Exception ex){
            Util.printErrorLine();
            Util.printErrorString(_className);
            Util.printErrorString(": WHEN PREPARING MESSAGE LINE OCCURRED EXCEPTION:");
            Util.printErrorLine();
            ex.printStackTrace();
            //io_destination.append(Failure.lineSeparator);
            final String result = Util.toString(i_throwable);
            io_destination.append(result);
            return result;
        }
      //end try
}//appendMessageLine

/**Formats i_textPattern inserting the formatted elements of i_parameters
 * instead of the placeholders {0} to {9}.
 * This is a convenience method packing services of {@link java.text.MessageFormat}.
 * @param io_destination Where the result is appended to.
 * @param i_locale Used for localized formatting of i_parameters. If null, uses the default Locale.
 * */
/*package*/ static void format(
    final StringBuffer io_destination,
    final String i_textPattern,
    final Object[] i_parameters,
    final Locale i_locale
) {
      final MessageFormat format = new MessageFormat(i_textPattern);
      if(i_locale!=null){format.setLocale(i_locale);}
      format.format(i_parameters, io_destination, null);
}

/**Gets a message text pattern for the exception i_throwable.
  The following sources are tried one after another with the class name
  of i_throwable as message text key: <OL>
    <LI>The language and country specific MsgText properties file,
        e.g. MsgText_en_UK.properties
    <LI>The language specific MsgText properties file,
        e.g. MsgText_en.properties
    <LI>The default MsgText properties file: MsgText.properties
    <LI>The default message text pattern from i_throwable, if it is a
        MultexException and not null or an empty String.
    <LI>null in all other cases.
  </OL>
*/
/*package*/ static String getMessageTextPattern(final Throwable i_throwable){
    return getMessageTextPattern(i_throwable, _resourceBundle);
}


/**Gets a message text pattern for the exception i_throwable.
  The following sources are tried one after another with the class name
  of i_throwable as message text key: <OL>
    <LI>The ResourceBundle i_resourceBundle,
    <LI>The default message text pattern from i_throwable, if it is a
        MultexException,
    <LI>null otherwise
  </OL>
*/
/*package*/ static String getMessageTextPattern(
    final Throwable i_throwable, final ResourceBundle i_resourceBundle
) {
    final String className = i_throwable.getClass().getName();
    final String defaultPattern = i_throwable instanceof MultexException ?
        ((MultexException)i_throwable).getDefaultMessageTextPattern() : null
    ;
    return _getResource(i_resourceBundle, className, defaultPattern);
}//_getMessageTextPattern

/**Returns the value for the resource with name i_key, helpwise i_defaultValue*/
private static String _getResource(
    final ResourceBundle i_resourceBundle,
    final String i_key,
    final String i_defaultValue)
{
    if(i_resourceBundle==null){return i_defaultValue;}
    try{
        return i_resourceBundle.getString(i_key);
    }catch(final java.util.MissingResourceException ex){
      return i_defaultValue;
    }
    //try
}//_getResource

/**Returns the parameters contained in i_throwable or null*/
private static Object[] _getParameters(final Throwable i_throwable){
  if(!(i_throwable instanceof MultexException)){return null;}
  return ((MultexException)i_throwable).getParameters();
}

/*debug
private static void _println(final String i_name, final Object[] i_array){
  System.err.print(i_name + ": ");
  for(int i=0; i<i_array.length; i++){
    System.err.print(i_array[i] + "  ");
  }
  System.err.println();
}
*/


/**The maximum number of index parameters, as allowed by
  java.text.MessageFormat
*/
private static final int _maxNumberOfIndexParams = 10;


//Message text access:

private static boolean        _internationalization = false;
private static ResourceBundle  _resourceBundle = null;
private static boolean        _loadMessagesTried = false;

/**If _internationalization is on, tries once to load the ResourceBundle
  resourceBundleName as message text file and returns it.
  If the loading fails, this method will return null,
  and will do nothing on further invocations.
*/
private static java.util.ResourceBundle _tryLoadMessages(){
  if(_loadMessagesTried){return null;}
  try{
    _loadMessagesTried = true;
    return java.util.ResourceBundle.getBundle(resourceBundleName);
  } catch(final java.util.MissingResourceException ex){ //resource not found
      Util.printErrorString(ex.toString());
      Util.printErrorLine();
      Util.printErrorString(_className);
      Util.printErrorString(": Unable to load exception message texts from the ResourceBundle");
      Util.printErrorLine();
      Util.printErrorString(resourceBundleName);
      Util.printErrorLine();
      return null;
    } catch(final Exception ex){ //not loadable
      ex.printStackTrace();
      return null;
    }
  //end try
}//_tryLoadMessages

private static String _getCauseMarker(final ResourceBundle i_resourceBundle){
    return _getResource(i_resourceBundle, causeMarkerKey, causeMarker);
}

/**Do not create objects of me!*/
private MsgText(){}

}//MsgText
