package multex; //Msg.java

import java.util.ResourceBundle;

//Change history:
//2004-05-11  Knabe  Bugfix: Method multex.Msg.printReport(StringBuffer, Throwable, ResourceBundle)
//                   now passes its ResourceBundle to multex.Msg.printMessages(...).
//2003-09-10  Knabe  Unnecesary parameter i_lineSeparator removed. See class description.
//2003-05-28  Knabe  Dynamic internationalization in printMessages
//2001-04-12  Knabe  Static preallocation of a shared DummyHereException object
//2000-12-13  Knabe  New methods get/print-Report report the message chain
//                   and the stack trace
//2000-12-13  Knabe  report-Methods renamed to get/print-Messages
//2000-10-25  Knabe  Reporting onto screen relocated into class multex.Awt
//2000-07-07  Knabe  Meldungstext holen+formatieren verlagert in Klasse MsgText
//2000-07-04  Knabe  report mit Varianten für Print-Writer/Stream
//1999-03-23  Knabe  Parametrierung wie in Java-Tutorial/i18n/format
//1999-03-18  Knabe  erstellt

/** Services for low-level reporting of any exception.
    <BR>The destinations can be of types OutputStream, Writer, String, and StringBuffer.
    <BR>The exception can be <UL>
        <LI> with parameters (subclass of {@link Exc})
        <LI> with cause chain and parameters (subclass of {@link Failure})
        <LI> or any standard Java exception (other subclass of java.lang.Throwable),
             which can from JDK 1.4 have a causal chain, too.
    </UL>
    <BR>The output can be of the type<UL>
        <LI>Messages: The message for an exception and eventually each of its causes
            in the causal chain.
        <LI>StackTrace: The stack traces of the reported exception and each of its
            causes in the causal chain. All redundant at-lines are removed.
        <LI>Report: A combination of both.
    </UL>
    <BR>You can use no or static internationalization by {@link MsgText#setInternationalization},
    or dynamic internationalization by passing the corresponding java.util.ResourceBundle
    at each invocation of a get- or print-method.

  <P>MulTEx 5: Parameter i_lineSeparator removed from all methods.
  If you miss this possibility for creating HTML output with &lt;BR&gt;
  as line separator, then betterly consider enclosing the output with
  &lt;PRE&gt;&lt;FONT FACE="Times"&gt; ... &lt;/FONT&gt;&lt;/PRE&gt;
  This is a much cheaper way to separate the lines of a message chain or
  a stack trace.

  @author Christoph Knabe, Berlin, Copyright 1999-2003
*/
public class Msg {


//static message parts:

/**The name of this class in the format package.class*/
private static final String _className = new Msg().getClass().getName();
private static final String _beep = "\u0007";


/**Reports i_throwable and its chained causing exceptions to System.err
  @see #printReport(StringBuffer,Throwable)
*/
public static void printReport(final Throwable i_throwable){
  printReport(System.err, i_throwable);
  System.err.print(_beep);
}//printReport

/**Reports i_throwable and its chained causing exceptions to io_destination
  @see #printReport(StringBuffer,Throwable)
*/
public static void printReport(
  final java.io.PrintStream io_destination, final Throwable i_throwable
){
  io_destination.println(getReport(i_throwable));
  _flushPrint(io_destination, i_throwable, "REPORT");
}//printReport

/**Reports i_throwable and its chained causing exceptions to io_destination
  @see #printReport(StringBuffer,Throwable,ResourceBundle)
*/
public static void printReport(
  final java.io.PrintWriter io_destination, final Throwable i_throwable
){
  io_destination.println(getReport(i_throwable));
  _flushPrint(io_destination, i_throwable, "REPORT");
}//printReport

/**Reports i_throwable and its chained causing exceptions to io_destination
  @see #printReport(StringBuffer,Throwable,ResourceBundle)
*/
public static void printReport(
  final StringBuffer io_destination, final Throwable i_throwable
){
	printReport(io_destination, i_throwable, (ResourceBundle)null);
}

/**Reports i_throwable and its chained causing exceptions to io_destination,
  detail description here.
  Reports the message chain of i_throwable by
  {@link #printMessages(StringBuffer,Throwable,ResourceBundle)}
  and the stack trace of i_throwable by
  {@link #printStackTrace(StringBuffer,Throwable)}.

  @param io_destination   Where to append the message chain, must not be null.

  @param i_throwable      The exception, which has to be reported along with its causal chain, must not be null.

  @param i_resourceBundle Where the messsage text patterns and the Locale are taken from.
		May be null. See at {@link #printMessages(StringBuffer,Throwable,ResourceBundle)}.
*/
public static void printReport(
	final StringBuffer io_destination,
	final Throwable i_throwable,
	final ResourceBundle i_resourceBundle
) {
	  printMessages(io_destination, i_throwable, i_resourceBundle);
	  io_destination.append(Util.lineSeparator);
	  io_destination.append(stackTraceFollows);
	  io_destination.append(Util.lineSeparator);
	  printStackTrace(io_destination, i_throwable);
}//printReport

/**The String used to separate the message chain of an exception from
  the stack trace of it.
*/
public static String stackTraceFollows
= "----------Stack Trace follows:----------";

/**Returns the report of i_throwable and its
  chained causing exceptions.
  @see #printReport(StringBuffer,Throwable)
*/
public static String getReport(final Throwable i_throwable){
  final StringBuffer result = new StringBuffer();
  printReport(result, i_throwable);
  return result.toString();
}//getReport

/**Reports the message texts of i_throwable and its
  chained causing exceptions to System.err
  @see #printMessages(StringBuffer,Throwable)
*/
public static void printMessages(final Throwable i_throwable){
  printMessages(System.err, i_throwable);
}//printMessages

/**Reports the message texts of i_throwable and its
  chained causing exceptions to io_destination
  @see #printMessages(StringBuffer,Throwable)
*/
public static void printMessages(
  final java.io.PrintStream io_destination, final Throwable i_throwable
){
  printMessages(new java.io.PrintWriter(io_destination, true), i_throwable);
}//printMessages

/**Reports the message texts of i_throwable and its
  chained causing exceptions to io_destination
  @see #printMessages(StringBuffer,Throwable)
*/
public static void printMessages(
  final java.io.PrintWriter io_destination, final Throwable i_throwable
){
  io_destination.print(_beep);
  final StringBuffer text = new StringBuffer();
  printMessages(text, i_throwable);
  io_destination.println(text);
  _flushPrint(io_destination, i_throwable, "MESSAGES");
}//printMessages

/**Finishes printing by flushing and checks its success.*/
private static void _flushPrint(
  final java.io.PrintWriter io_destination, final Throwable i_throwable,
  final String i_what
){
  io_destination.flush();
  checkPrintError(io_destination.checkError(), i_throwable, i_what);
}

/**Finishes printing by flushing and checks its success.*/
private static void _flushPrint(
  final java.io.PrintStream io_destination, final Throwable i_throwable,
  final String i_what
){
  io_destination.flush();
  checkPrintError(io_destination.checkError(), i_throwable, i_what);
}

/**If i_error is true,
 * reports an error message and the stack trace of i_throwable onto System.err.
 * */
private static void checkPrintError(
	final boolean  i_error,
	final Throwable i_throwable,
	final String    i_what)
{
	  if(i_error){
        Util.printErrorString(_beep);
        Util.printErrorLine();
	    Util.printErrorString(_className);
	    Util.printErrorString(": COULD NOT PRINT ");
	    Util.printErrorString(i_what);
	    Util.printErrorString(" FOR:");
        Util.printErrorLine();
	    i_throwable.printStackTrace();
	  }
}

/**Returns the message texts of i_throwable and its
  chained causing exceptions.
  @see #printMessages(StringBuffer,Throwable)
*/
public static String getMessages(final Throwable i_throwable){
  final StringBuffer result = new StringBuffer();
  MsgText.appendMessageTree(result, i_throwable, (ResourceBundle)null);
  return result.toString();
}//getMessages

/**Appends the message texts of i_throwable and its
  chained causing exceptions to io_destination using static internationalization.
  @see #printMessages(StringBuffer,Throwable,ResourceBundle)
*/
public static void printMessages(
  final StringBuffer io_destination, final Throwable i_throwable
){
  printMessages(io_destination, i_throwable, (ResourceBundle)null);
}//printMessages



/**Prints the message texts of i_throwable and its
  chained causing exceptions, detail description here.
  Reports the parameterized textual message of i_throwable,
  or if the message pattern is not available, the name and parameters of i_throwable.
  Reports the same for each in the chain of the eventually existing causing
  Throwable exceptions.

  The concept of chaining causing exceptions is described in the class
  {@link Failure}.
  <P>
  Note MulTEx 5: Parameter i_lineSeparator removed. Always uses the platform
  default. See discussion in the description of class {@link Msg}.

  @param io_destination   Where to append the message chain, must not be null.
  @param i_throwable      The exception, which has to be reported along with its causal chain, must not be null.
  @param i_resourceBundle Where the messsage text patterns are taken from. The Locale
            is taken from this i_resourceBundle, too.
            If null, falls back to static or no internationalization,
    		depending on the actual state of @{link MsgText}.

  @since MulTEx 5 (2003-09-10) with dynamic internationalization, but without line separator.
*/
public static void printMessages(
  final StringBuffer  io_destination,
  final Throwable      i_throwable,
  final ResourceBundle i_resourceBundle
){
	MsgText.appendMessageTree( io_destination, i_throwable, i_resourceBundle );
}

/**Prints the compactified stack trace of i_throwable and its
  chained causing Throwable exceptions to System.err.
  @see #printStackTrace(StringBuffer,Throwable)
  */
public static void printStackTrace(final Throwable i_throwable){
  printStackTrace(System.err, i_throwable);
}//printStackTrace

/**Prints the compactified stack trace of i_throwable and its
  chained causing Throwable exceptions to io_destination.
  @see #printStackTrace(StringBuffer,Throwable)
  */
public static void printStackTrace(
  final java.io.PrintStream io_destination, final Throwable i_throwable
){
  printStackTrace(new java.io.PrintWriter(io_destination, true), i_throwable);
}//printStackTrace

/**Prints the compactified stack trace of i_throwable and its
  chained causing Throwable exceptions to io_destination.
  @see #printStackTrace(StringBuffer,Throwable)
*/
public static void printStackTrace(
  final java.io.PrintWriter io_destination, final Throwable i_throwable
){
  io_destination.print(getStackTrace(i_throwable));
  io_destination.flush();
}//printStackTrace

/**Returns the compactified stack trace of i_throwable and its
  chained causing Throwable exceptions.
  @see #printStackTrace(StringBuffer,Throwable)
*/
public static String getStackTrace(final Throwable i_throwable){
  final StringBuffer result = new StringBuffer();
  Util.appendCompactStackTrace(result, i_throwable);
  return result.toString();
}//getStackTrace

/**Prints the compactified stack trace of i_throwable and all its
  chained causing Throwable exceptions appending it to io_destination,
  detail description here.
  All redundant location lines in the stack traces therein are suppressed.

  Note MulTEx 5: Parameter i_lineSeparator removed. Always uses the platform
  default. See discussion in the description of class {@link Msg}.

  @param io_destination Where to append the compactified stack trace.
  @param i_throwable The exception to report, possibly containing a chain of
    nested cause exceptions.
*/
public static void printStackTrace(
  final StringBuffer io_destination, final Throwable i_throwable
){
    Util.appendCompactStackTrace(io_destination, i_throwable);
}

/**User should not construct objects of this class*/
private Msg(){}

}//Msg