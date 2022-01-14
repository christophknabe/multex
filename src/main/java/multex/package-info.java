/** MulTEx - the Multi-Tier Exception Handling Framework.
<p>MulTEx is a simple, but powerful framework for organizing exceptions
and messages in a multi-tier Java software system.
</p>
<p>It offers the key features:</p>
<ul>
  <li>Causal chains/trees as a means to capture low-level error information</li>
  <li>Redundancy-free stack traces and message chains in the case of indirectly caused exceptions</li>
  <li>Internationalized message texts and parameters for exceptions</li>
  <li>Services for reporting an exception chain/tree onto streams and dialogs</li>
  <li>A standard way for writing method bodies with regard to exceptions</li>
  <li>A collection of utilities for centralized exception reporting</li>
</ul>
<p>For introductory information you should first read the technical paper or the user's guide.</p>

<h2>Naming conventions</h2>
<p>The following naming conventions are used throughout the package <code>multex</code>.</p>
<ul>
  <li>i_name for an in-parameter of a method, i.e. either a parameter of a primitive
      type or a parameter of a reference type, which will not be modified
      by the invoked method.
  </li>
  <li>o_name for an out-parameter of a method, i.e. a parameter of a reference type,
      where the referred object will be completely initialized by the method,
      ignoring all its previous content.
  </li>
  <li>io_name for an in/out-parameter of a method, i.e. a parameter of a reference type,
      where the referred object will be modified by the method,
      possibly using its previous content.
  </li>
  <li>_name for attributes or methods, which should not be used from
    outside the class or its subclasses.
  </li>
</ul>

<h2>Plans</h2>
The plans are ordered by priority (most important as No. 1)
<ol>
  <li>2002-04-20 Add buttons for printing or copying the contents of the
    message window of multex.Awt; place the buttons before the message TextArea.
  </li>
  <li>2005-01-13 Include wrapper exceptions for some exceptions, which do not show
    all their diagnostic info by toString(). Maybe on reflection basis.
    I think SQLException is a candidate for this, as its attributes
    SQLState, and vendorCode are not included in its toString().
  </li>
  <li>2005-01-13 Include private attributes of type Throwable into the
    {@link multex.ReflectionCauseGetter} in order to get the cause for
    an exception, which does not implement the getCause() of JDK 1.4.
    Is there still any such exception?
  </li>
</ol>
<a name="versionHistory"></a>
<H2>Version history</H2><UL>

  <li>8.4 of 2022-01-07: Compiled and runs on Java 8. Artifact vendor renamed to io.github.christophknabe.
  Test coverage report by JaCoCo instead of Cobertura. 
  </li>
  <li>8.3 of 2011-11-08: Compiled and runs on Java 6. 
    Class {@link multex.Util} has a new method {@link multex.Util#setMaxRecursionDepth(int)}.
    The lower level utility method {@link multex.Util#getCause(Throwable)}
    automatically uses the value configured by {@link multex.Util#setMaxRecursionDepth(int)}.
    The limitation of the recursion depth became necessary with the <code>org.apache.derby.client.am.SqlException</code>,
    whose method <code>getSQLException</code> wraps the <code>SqlException</code> into a <code>SQLException</code>.
  </li>
  <li>8.2 of 2009-10-22: {@link multex.tool.ExceptionMessagesDoclet} always uses encoding ISO-8859-1 for appending
    message text patterns to the -out file, as Java .properties files always have to be encoded this way.
  </li>
  <li>8.1 of 2008-12-08: {@link multex.MultexUtil#create(Class, Object...)} accepts exception class without cause or parameters.
  </li>
  <li>8.0 of 2008-01-09: New german paper about Central Exception Reporting.
    <br>Class {@link multex.Exc} now unchecked.
    <br>Method {@link multex.Exc#throwMe(Object...)} in class <code>Exc</code>
    <br>Method {@link multex.Util#getOriginalException(Throwable)} called with a <b>null</b> argument returns <b>null</b> instead of throwing a <code>NullPointerException</code>
  </li>
  <LI>7.3 of 2007-09-18:
    Compatible to previous version 7.2, but requires Java 5. Using the new features of Java 5, where helpful.
    <br>Static factory method for parameterized exceptions: {@link multex.MultexUtil#create(Class, Object...)}.
    This allows a much simpler declaration of a parameterized exception, as you no longer have to declare a constructor in it.
    <br>The constructors for the parameterized exception classes {@link multex.Exc}, {@link multex.Failure}, and {@link multex.AssertionFailure},
    and the method {@link multex.Assertion#check} are now using an <code>Object...</code> parameter, instead of being overloaded with
    variants with 0 to 9 message parameters.
    <br>Added in User's Guide: How to declare and throw parameterized exceptions, how to write method bodies encapsulating exceptions from a lower layer.
    <br>The {@link multex.tool.ExceptionMessagesDoclet} now correctly converts also a multi-line main javadoc comment of each exception
    into the format for .properties files. It adds a backslash at the end of each line to be continued.
  </LI>
  <LI>7.2 of 2007-09-04:
    Optimized {@link multex.ReflectionCauseGetter}.
    Does not use reflection on {@link multex.MultexException}s.
    <br>This version is the last usable with Java 1.4.
    <br>This version is the first produced by Maven (2).
  </LI>
  <LI>7.1 of 2006-11-04
    Added {@link multex.tool.ExceptionMessagesDoclet}.
    You can use this doclet to extract the main comment of each Throwable into a .properties file as message text.
    <br>
    Added constructor {@link multex.Failure#Failure(String)} without a cause.
  </LI>
  <LI>7 of 2006-05-11: Added parameter cause in constructors of class {@link multex.Exc};
    Added method getCause() in interface {@link multex.MultexException}.
    <br>Added list exception facility: All parameters of a {@link multex.MultexException}, which are of type java.lang.Throwable,
      are reported by {@link multex.Msg#printMessages}, thus resulting in a causal exception tree instead of an exception chain.
    <br>{@link multex.Msg#printStackTrace} prints thus a redundancy-free stack trace of all exceptions,
      contained in the causal exception tree.
    <br>As the tree is symbolized by an indentation by multiple <code>+</code> signs,
      the layout of printMessages and printStackTrace is no longer the same as in MulTEx 6.
  </LI>
  <LI>6c of 2006-02-16
    Added in User's Guide: How to assure centralized exception reporting in a Java Server Faces application.
  </LI>
  <LI>6b of 2005-04-26
    Added User's Guide documenting, how to introduce MulTEx.
    Main chapter: How to assure centralized exception reporting.
  </LI>
  <LI>6a of 2005-04-14
    Added method {@link multex.Awt#setAwtExceptionHandlerClass} to set a general exception handler,
    which can report any exception propagated through to the AWT EventDispatchThread.
    Uses the undocumented system property "sun.awt.exception.handler",
    which enables a single place handling for all exceptions in methods called from a
    AWT or Swing GUI. See example in class multex.demo.AwtFile.
  </LI>
  <LI>6 of 2005-01-13
      <br>Improvement: The methods in class {@link multex.Msg}
      for printing the stack trace of a Throwable chain betterly recognize
      low level causes in an exception chain. They follow the chain by the
      {@link multex.ReflectionCauseGetter} instead of leaving this to
      Throwable.printStackTrace().
      <br>Restriction: Now needs JRE &ge; 1.4, which exists already &ge; 3 years.
      <br>Incompatible: Much utility functionality moved from class
      {@link multex.Failure} to the new class {@link multex.Util}.
  </LI>
  <LI>5d of 2004-12-10:
      <br>Restriction: MulTEx now needs JRE &ge; 1.2 for execution, JDK &ge; 1.4 for testing.
      <br>Improvement: The methods in class {@link multex.Msg}
      for printing the messages of a Throwable chain,
      suppress redundant info caused by legacy exception chaining as String,
      e.g. Root cause is ..., or nested exception is ...
  </LI>
  <LI>5c of 2004-11-05:
      <br>Bugfix: The methods in class {@link multex.Msg}
      for printing the stack trace of a Throwable chain, report a null Throwable
      instead of throwing NullPointerException.
      <br>Improvement: The methods in class {@link multex.Msg}
      for printing the messages of a Throwable chain,
      a) report a null Throwable instead of printing nothing,
      b) suppress reporting an object of the exact class Failure
         without any own info. See {@link multex.Failure#Failure(Throwable)} shortly
         for the concept of a tunneling exception.
      <br>Packaging: The release is a .zip file, which contains a -class.jar file
        with all core framework classes, and a -java.jar file with the source code
        of the framework, the test suite, and the demo programs.
  </LI>
  <LI>5b of 2004-05-11:
      <br>Bugfix: Method {@link multex.Msg#printReport(StringBuffer, Throwable, ResourceBundle)}
      now passes its ResourceBundle to multex.Msg.printMessages(...).
      <br>Bug avoiding: Now {@link multex.Awt} and {@link multex.Swing} show the reporting dialog
      in the AWT event queue thread, instead of the callers thread.
      See http://java.sun.com/developer/JDCTechTips/2003/tt1208.html
      <br>Comfort: Method {@link multex.Awt#countLines(String)} is now public.
  </LI>
  <LI>5a of 2003-11-25:
      <br>Bugfix: Report any exception with message text pattern, but without
      exception parameters or Throwable.getMessage() without
      <CODE>": null"</CODE>.. Corresponding testcases added.
  </LI>
  <LI>5 of 2003-09-12:
      <br>Added class {@link multex.Swing} for reporting into a Swing dialog
          with dynamic or static internationalization.
          Interface of class {@link multex.Awt} simplified (incompatible change) and made
          uniform with Swing.
      <br>Added method {@link multex.Util#getContainedException(java.lang.Throwable,java.lang.Class)}, originally to class Failure.
      <br>Simpler dynamic internationalization using only a java.util.ResourceBundle
      <br>Takes the cause marker from the used ResourceBundle
      by key {@link multex.MsgText#causeMarker} instead of giving it as a separate
      argument (incompatible change).
      <br>
      Class MultexLocale deprecated, see there (incompatible change).
      Unnecessary parameter i_lineSeparator removed in all methods.
      See class description {@link multex.Msg} (incompatible change).
  </LI>
  <LI>4a of 2003-07-01 (unpublished) Bugfix: Failure.getOriginalException(Throwable):
            corrected endless loop.
  </LI>
  <LI>4  of 2003-05-22: With dynamic internationalization as for web servers.
      The new class multex.MultexLocale bundles a ResourceBundle for the message texts,
      the Locale for the message parameter substitution, and a cause marker text.
      The MultexLocale is electable for each exception message report.
      The line separator for low level exception reports is electable, too.
      See {@link multex.Msg#printMessages(StringBuffer,Throwable,java.util.ResourceBundle)}.
  </LI>
  <LI>3e of 2002-07-02: Compiles unchanged on JDK 1.1 up to 1.4. Uses by default ReflectionCauseGetter.
         Thus code compiled on one JDK-version should run on another version.
         Includes JUnit tests in subdirectory 'test'.
  </LI>
  <LI>3d of 2002-06-27: Will compile unchanged on JDK 1.4 and on earlier ones,
      automatically adapting itself to using Jdk1_4CauseGetter or Jdk1_1CauseGetter.
  </LI>
  <LI>3c of 2002-06-26: Corrected compilation error of classes Awt and Msg using MsgText.appendMessageChain()
  </LI>
  <LI>3b of 2002-06-25: Added java.sql.SQLWarning to JDK1_1CauseGetter and JDK1_4CauseGetter,
         Corrected package name in demo/MsgText_de.properties to 'multex.demo',
         MsgText.appendMessageChain() allows to specify a lineSeparator, useful for HTML-output,
         first version of errorPage.jsp.
  </LI>
  <LI>3a of 2002-05-21: New presentation slides: Actualized, landscape format.
  </LI>
  <LI>3 of 2002-04-29: Adaptation to JDK 1.4
    <UL>
      <LI>As JDK 1.4 finally introduced the concept of a causal chain for the
          exception base class
          <A HREF="http://java.sun.com/j2se/1.4/docs/api/java/lang/Throwable.html">Throwable</A>,
          MulTEx 3 now makes use of this feature instead of managing the causal chain itself.
      </LI>
      <LI>Thus requires JDK 1.4.</LI>
      <LI>gendocs.bat: Generation error in JDK 1.4 corrected</LI>
    </UL>
  <LI>2c of 2002-03-26:
    <UL>
      <LI>class Failure: Example message text parameters in constructor correctly
          quoted. Analoguous correction in demo class File.
      </LI>
      <LI>This is the last release destinated for JDK versions 1.1 up to 1.3
          and produced with JDK 1.3.
      </LI>
    </UL>
  </LI>
  <LI>2b of 2001-12-18:
    <UL>
      <LI>source files in package correct directory multex, achieved by
        introducing an automated release procedure.
      </LI>
    </UL>
  <LI>2a of 2001-06-28:
    <UL>
      <LI>Awt.report(...): Now accepts any AWT-Component as owner hook.
        The owner Window is determined by searching the Window which is the
        nearest parent of the hook component. Although some report-methods
        were deleted, this should be upwards compatible to version 2.
      </LI>
    </UL>
  <LI>2 of 2001-04-19:
    <UL>
      <LI>Msg.printStackTrace(...) does no longer delete the last lines,
        which indicate the place, where it has been invoked. So the compactified
        multi-tier trace contains all lines of the trace of the lowest
        exception.
      </LI>
      <LI>Again only unnamed indexed exception parameters for message text
        placeholders {0} ... {9}, but with a default text pattern in the
        exception object.
        In the stack trace is included the default text pattern (if provided).
        The parameters are each on a separate line.
      </LI>
      <LI>Added convenience constructors to multex.Exc and multex.Failure with
        0..10 parameters of type Object for filling the parameter Object[].
      </LI>
      <LI>class Failure: Method name getCause(...) instead of cause(...),
        getOriginalException(...) instead of originalException(...)
      </LI>
      <LI>All inner classes repositioned as outer classes: CauseGetter,
        Jdk1_1CauseGetter, AssertionFailure.
      </LI>
      <LI>class MethodFailure removed, Failure as non-abstract class.
        For a more comfortable ad hoc usage you can directly throw a newly
        created Failure-object, each time
        with an individual message text, but then without internationalization
        possibility.
      </LI>
      <LI>Now its possible to redefine the method checkClass() in order to
        modify or deactivate the checking of the naming conventions of the
        descendants of {@link multex.Exc} and {@link multex.Failure}.
      </LI>
      <LI>class multex.Awt: After showing the stack trace scrolls upwards in
        order to show its beginning.
      </LI>
    </UL>
  <LI>1c of 2000-02-27:
    <UL>
      <LI>Internationalization of message texts is now optional.
        You can activate it by initially calling
        <CODE>multex.MsgText.setInternationalization(true);</CODE>
      </LI>
      <LI>Failure.Jdk1_1CauseGetter now knows java.sql.SQLException</LI>
    </UL>
  <LI>1b of 2000-12-13:
    <UL>
      <LI>class multex.Awt: a) Now offers new methods reportNonmodal in order to
        report into a non-modal dialog.
        b) Now offers a new method report(Throwable),
        which reports using an anomymous, shared, hidden Frame as owner.
      </LI>
      <LI>class multex.Msg: Now offers methods for reporting a) only the message
        chain, b) only the compact stack trace, c) both in one.
      </LI>
      <LI>class multex.Failure:
        <UL>
          <LI>Enabled to encapsulate a cause of type Throwable rather than
              only Exception.
          </LI>
          <LI>cause(Throwable) returns by default for all known exceptions of
            JDK 1.1 with a causing exception this causing exception. The method
            for getting the causing exception of an exception is configurable
            by a call to Failure.setCauseGetter(...);
          </LI>
          <LI>Added function
            <CODE>Throwable originalException(Throwable i_throwable)</CODE>
            to get the original exception, which is <CODE>i_throwable</CODE>
            itself or (may be indirectly) caused it.
          </LI>
        </UL>
    </UL>
  <LI>1.1 of 2000-10-26  Reporting methods separated: Low level in class Msg, onto screen in class Awt.
  </LI>
  <LI>1.0 of 2000-10-21  Big revolution.<UL>
      <LI>New convenience class MethodFailure. No longer 0..10
        unnamed parameters in Failure-constructor.
      </LI>
      <LI>Each exception object has a default message text pattern, which is used,
        if there is no corresponding message text found in file MsgText.properties or one of its
        localized variants.
      </LI>
      <LI>New classes Assertion and Assertion.Failure</LI>
      <LI>Requires only JDK 1.1, no longer 1.2</LI>
    </UL>
  <LI>0.2 of 2000-10-08  Failure extends java.lang.RuntimeException by default.</LI>
  <LI>0.1 of 2000-09-29  Failure extends java.lang.Exception instead of
    multex.Exc. Thus it is easily switchable to extends java.lang.RuntimeException.
    Named parameters are the only parameters, that can be inserted into a
    message text. Nevertheless for easiest providing of diagnostic information
    in a Failure exception, the constructor of Failure accepts 0..10 unnamed
    parameters of type Object.
  </LI>
  <LI>0.0 of 1999..2000  Evolution of this framework. The name MulTEx was not yet used.</LI>
</UL>
@author Christoph Knabe, TFH Berlin, 1999-2000 Copyrighted, but usable by GNU LGPL.
 */
package multex;