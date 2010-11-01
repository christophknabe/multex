package multex; //Awt.java
import java.util.ResourceBundle;
import java.awt.Window;
import java.awt.EventQueue;

//Change history:
//2005-04-14  Knabe  New method registerAwtExceptionHandlerClass
//2003-09-12  Knabe  Interface simplified and unified with class Swing
//2001-06-28  Knabe  Any Component usable to determine the owner Window
//2000-12-12  Knabe  Methods report(...) modularized,
//                   new methods reportNonmodally(...)
//2000-10-25  Knabe  Extracted from class multex.Msg
//2000-07-17  Knabe  Auch report(Applet, Throwable)
//2000-07-11  Knabe  Button "Show Stack Trace" verschwindet nach Betätigung
//2000-06-28  Knabe  Zeilenanzahl der TextArea aus ihrem Inhalt ermitteln
//2000-06-21  Knabe  Auch Anzeige des Stack Trace im Meldungsfenster
//2000-05-23  Knabe  Erweitert um Dialog als owner
//1999-05-20  Knabe  Mit OK-Button zum Quittieren der Meldung per Return/Maus.
//1999-04-29  Knabe  Hintergrundfarbe weiss im Meldungsfenster
//1999-03-18  Knabe  erstellt in Klasse multex.Msg


/**Services for reporting onto AWT Dialogs the messages for any exception
	with dynamic or static or no internationalization.
    The exception can be <UL>
	    <LI> with parameters (subclass of {@link Exc})
	    <LI> with cause chain and parameters (subclass of {@link Failure})
	    <LI> or any standard Java exception (other subclass of java.lang.Throwable).
    </UL>

    For setting static internationalization see {@link MsgText}.

  @author Christoph Knabe, Berlin, Copyright 1999-2001
*/
public class Awt {

//static message parts:
/**The prefix to be appended to the name of the GUI class owning the error
  message
*/
/*package*/ static final String titlePrefix = "Error Message for ";

/**The name of this class in the format package.class*/
private static final String _className = Awt.class.getName();

/**Reports i_throwable into an error message dialog with static internationalization.
     For details see @link #report(java.awt.Component, Throwable, ResourceBundle)}.
*/
public static void report(
  final java.awt.Component io_ownerHook, final Throwable i_throwable
){
  report(io_ownerHook, i_throwable, (ResourceBundle)null);
}

/**Reports i_throwable into an AWT Dialog, detail description here.
  <BR>Firstly reports the same as {@link Msg#printMessages(StringBuffer, Throwable, ResourceBundle)}
  into a pop-up window of appropriate size.
  Accepting the message is done by <UL>
  <LI> clicking on the "close window" icon, or
  <LI> clicking on the OK-button, or
  <LI> typing &lt;RETURN&gt; on the OK-button </UL>
  Each of these will close the dialog box.
  <P>
  The Button "Show Stack Trace" will add the compactified
  stack trace including the chain of all causing exceptions
  to the message dialog box. The contents of the compact
  stack trace are described at method
  {@link Msg#printStackTrace(StringBuffer,Throwable)}.
  <P>
  <P>
  In an application this method should not be called directly from
  a GUI class, but the author of each GUI class should set up a method e.g.<PRE>
  private void _report(Throwable ex){Awt.report(this,ex,null);}
  </PRE>, which will be called from each catch-clause in the event listeners
  and adds the actual GUI class as the owner of the message dialog.

    @param io_ownerHook The owner Window of the dialog box will be determined
	    from io_ownerHook by searching the nearest parent Window. 
	    The owner window is blocked for further input 
	    until this dialog box will be closed.
	    If the owner hook is null, the dialog is created as a nonmodal dialog
	    with a shared, anonymous owner frame.

    @param i_throwable The exception to be reported along with its causal chain
    
    @param i_resourceBundle Where to take from the message texts for the exceptions
    	to be reported. If null, falls back to static or no internationalization,
    	depending on the actual state of @{link MsgText}.

    @see Msg#printReport(StringBuffer,Throwable, ResourceBundle)
*/
public static void report(
	final java.awt.Component io_ownerHook, final Throwable i_throwable,
	final java.util.ResourceBundle i_resourceBundle
){
	final boolean modal = io_ownerHook != null;
    try{
	    final java.awt.Window ownerWindow = getOwnerWindow(io_ownerHook);
	    new Dialog(ownerWindow, modal, i_throwable, i_resourceBundle);
    }catch(final Throwable ex){reportNoWindow(_className, ex,i_throwable);}
    //end try
}

/**Shows a window in the AWT event queue thread.
 * @see http://java.sun.com/developer/JDCTechTips/2003/tt1208.html
 */
/*package*/ static class WindowShower implements Runnable {
    final Window _window;
    public WindowShower(final Window io_window) {
      this._window = io_window;
    }
    public void run() {
	  _window.pack();
      _window.show();
    }
}

/**A message reporting dialog window*/
private static class Dialog {


  /**Creates a dialog window for reporting i_throwable with owner Window io_owner
    and modality i_modal.
    If io_owner is neither a Frame nor a Dialog, the shared hidden Frame of
    multex.Awt will be used as owner window.
    This Dialog will get a title using the title of io_owner with a prefix
    indicating, that this Dialog is an error message.
    @see #report(java.awt.Frame,Throwable)
  */
  private Dialog (
		  final java.awt.Window io_owner, final boolean i_modal, 
		  final Throwable i_throwable,  final java.util.ResourceBundle i_resourceBundle
  ){
    final String ownerTitle;
    if(io_owner instanceof java.awt.Frame){
		final java.awt.Frame owner = (java.awt.Frame)io_owner;
		_dialog = new java.awt.Dialog(owner);
		ownerTitle = owner.getTitle();
    }else if(io_owner instanceof java.awt.Dialog){
		final java.awt.Dialog owner = (java.awt.Dialog)io_owner;
		_dialog = new java.awt.Dialog(owner);
		ownerTitle = owner.getTitle();
    }else{ //must not occur
	      throw new AssertionFailure("Owner window {0} is not of class java.awt.Frame nor java.awt.Dialog", io_owner);
    }
    _dialog.setTitle(titlePrefix + ownerTitle);
    _dialog.setModal(i_modal);
    _initialize(i_throwable);
    
    //_dialog.setVisible(true);
    EventQueue.invokeLater(new WindowShower(_dialog));
  }//Dialog

  /**Adds to _dialog the necessary components and their listeners*/
  private void _initialize(final Throwable i_throwable){

    //Look:

    //Button "OK":
    final java.awt.Button okButton
    = new java.awt.Button(" O  K ");
    //okButton.setBackground(java.awt.Color.white); //???nur für FensterFoto
    //Button "Show Stack Trace":
    final java.awt.Button traceButton
    = new java.awt.Button("Show Stack Trace");
    //traceButton.setBackground(java.awt.Color.white); //???nur für FensterFoto
    //buttonPanel:
    final java.awt.Panel buttonPanel
    = new java.awt.Panel(new java.awt.BorderLayout());
    buttonPanel.add(okButton);
    buttonPanel.add(traceButton, java.awt.BorderLayout.EAST);
    _dialog.add(buttonPanel, java.awt.BorderLayout.SOUTH);

    //TextArea for reporting the causer chain messages:
    // empty: 1 row, 80 columns:
    final java.awt.TextArea textArea = new java.awt.TextArea(1,80);
    final StringBuffer messageChain = new StringBuffer();
    Msg.printMessages(messageChain, i_throwable, (java.util.ResourceBundle)null);
    textArea.setBackground(java.awt.Color.white);
    textArea.setForeground(java.awt.Color.black);
    _append(textArea, messageChain.toString());
    textArea.setEditable(false);
    _dialog.add(textArea, java.awt.BorderLayout.CENTER);

    //_dialog.pack();  //Moved to WindowShower 2004-05-11

    //Behaviour:

    okButton.addKeyListener( new java.awt.event.KeyAdapter(){
      public void keyTyped(java.awt.event.KeyEvent ev){
        if(ev.getKeyChar()=='\n'){_dispose();}
      }
    });

    okButton.addActionListener( new java.awt.event.ActionListener(){
      public void actionPerformed(final java.awt.event.ActionEvent i_){
        _dispose();
      }
    });

    traceButton.addKeyListener( new java.awt.event.KeyAdapter(){
      public void keyTyped(java.awt.event.KeyEvent ev){
        if(ev.getKeyChar()=='\n'){
          _fireTraceButton(traceButton, textArea, i_throwable);
        }
      }
    });

    traceButton.addActionListener( new java.awt.event.ActionListener(){
      public void actionPerformed(final java.awt.event.ActionEvent i_){
        _fireTraceButton(traceButton, textArea, i_throwable);
      }
    });

    _dialog.addWindowListener( new java.awt.event.WindowAdapter(){
      public void windowClosing(final java.awt.event.WindowEvent i_){
        _dispose();
      }
    });

    final java.awt.Toolkit toolkit = _dialog.getToolkit();
    toolkit.beep();

    //Testweise eingefügt 01-06-28:
    //_printParents(okButton);
  }//_initialize()
        
  /**Free the resources associated with this dialog window*/
  private void _dispose(){_dialog.dispose();}

  /**Appends the stack trace of i_throwable to io_textArea
    and disables io_traceButton
  */
  private void _fireTraceButton(final java.awt.Button io_traceButton,
    final java.awt.TextArea io_textArea, final Throwable i_throwable
  ){
    io_traceButton.setVisible(false);
    final StringBuffer buffer = new StringBuffer();
    buffer.append(Util.lineSeparator);   buffer.append(Util.lineSeparator);
    buffer.append(Msg.stackTraceFollows);
    buffer.append(Util.lineSeparator);
    Msg.printStackTrace(buffer, i_throwable);
    final String stackTrace = buffer.toString();
    _append(io_textArea, stackTrace);
    io_textArea.insert("", 0); //scrolls back upwards to character first line
    _dialog.pack();
    _stackTraceShown = true;
  }

  //attributes:

  /**Dialog window for reporting the exception cause chain*/
  private final java.awt.Dialog _dialog;

  private boolean _stackTraceShown = false;

}//Dialog


/**Returns the owner window (which should be a Frame or Dialog),
  to wich i_component belongs to.
  Returns the nearest java.awt.Window, received by repeated invocation of
  java.awt.Component.getParent().
  Returns a shared owner Frame, if no such java.awt.Window is found,
  but issues an error message to System.err.
  @param i_component The component, for which the owner window is searched.
  	If null, returns a shared owner Frame without error message.
*/
/*package*/ static java.awt.Window getOwnerWindow(final java.awt.Component i_component){
    if(i_component==null){return _sharedOwnerFrame;}
    for(java.awt.Component c=i_component; c!=null; c=c.getParent()){
      if(c instanceof java.awt.Window){
        return (java.awt.Window)c;
      }
    }
    Util.printErrorString(_className);
    Util.printErrorString(": NO OWNER Window FOUND FOR Component ");
    Util.printErrorString(i_component.toString());
    Util.printErrorLine();
    return _sharedOwnerFrame;
}//_getOwnerWindow

/**
 * Returns a package-private, shared, invisible Frame
 * to be the owner for Dialogs created with null owners.

private static java.awt.Frame getSharedOwnerFrame(){
  return _sharedOwnerFrame;
}
*/

/**package-private, shared, invisible Frame*/
private static final java.awt.Frame _sharedOwnerFrame = new java.awt.Frame();

/**Reports to System.err, that in class i_className occured the exception i_problem
 *  when trying to report the exception i_reportee.
*/
/*package*/ static void reportNoWindow(
  final String i_className,
  final Throwable i_problem, final Throwable i_reportee
){
    Util.printErrorLine();
    Util.printErrorString(_className);
    Util.printErrorString(": THE FOLLOWING EXCEPTION OCCURED:");
    Util.printErrorLine();
    i_problem.printStackTrace();
    Util.printErrorString(_className);
    Util.printErrorString(": WHEN CONSTRUCTING THE MESSAGE WINDOW FOR EXCEPTION:");
    Util.printErrorLine();
    i_reportee.printStackTrace();
}

/**Appends i_text to io_textArea and enlarges the number of rows of this
  TextArea as necessary
*/
private static void _append(
  final java.awt.TextArea io_textArea, final String i_text
){
  io_textArea.setRows( io_textArea.getRows() + countLines(i_text) );
  io_textArea.append(i_text);
}

/**Result: Number of lines in String i_text.
 * Useful for sizing the text area in a message window.
  @see java.io.BufferedReader#readLine()
*/
public static int countLines(final String i_text){
  final java.io.BufferedReader br
  = new java.io.BufferedReader(new java.io.StringReader(i_text));
  for(int result=0;;result++){
    String line; //final
    try{line = br.readLine();}
      catch(final java.io.IOException ex){line = null;}
    //end try
    if(line==null){return result;}
  }
}//_countLines

/**Registers the class of the passed object as the default exception handler
 * for AWT and Swing. The object itself is used only for type safety of the method
 * handle(Throwable), but is ignored in other aspects.
 * 
 * <P>Quoted below is the documentation of the private method handleException of
 * java.awt.EventDispatchThread. It's not a long-term solution but it does work.
 * </P>
 * Handles an exception thrown in the event-dispatch thread.
 *
 * <p> If the system property "sun.awt.exception.handler" is defined, then
 * when this method is invoked it will attempt to do the following:
 *
 * <ol>
 * <li> Load the class named by the value of that property, using the
 *      current thread's context class loader,
 * <li> Instantiate that class using its zero-argument constructor,
 * <li> Find the resulting handler object's <tt>public void handle</tt>
 *      method, which should take a single argument of type
 *      <tt>Throwable</tt>, and
 * <li> Invoke the handler's <tt>handle</tt> method, passing it the
 *      <tt>thrown</tt> argument that was passed to this method.
 * </ol>
 *
 * If any of the first three steps fail then this method will return
 * <tt>false</tt> and all following invocations of this method will return
 * <tt>false</tt> immediately.  An exception thrown by the handler object's
 * <tt>handle</tt> will be caught, and will cause this method to return
 * <tt>false</tt>.  If the handler's <tt>handle</tt> method is successfully
 * invoked, then this method will return <tt>true</tt>.  This method will
 * never throw any sort of exception.
 *
 * <p> <i>Note:</i> This method is a temporary hack to work around the
 * absence of a real API that provides the ability to replace the
 * event-dispatch thread.  The magic "sun.awt.exception.handler" property
 * <i>will be removed</i> in a future release.
 * 
 * @param i_exceptionHandler Any instance of the class, which shall be used as global AWT/Swing AwtExceptionHandler 
 * @throws Failure i_exceptionHandler is null, or its class has no default constructor, or it is not instantiable via its default constructor or the necessary System property is not settable.
 *         It seems, too, that the class must be public, and must be a static class (if inner).
 *         The diagnostics of Class.newInstance() are not very clear. 
 * 
 * @see <a href="http://www.jguru.com/faq/view.jsp?EID=427279">JGuru discussion</a>
 *
 */
public static void setAwtExceptionHandlerClass(final AwtExceptionHandler i_exceptionHandler)
throws Failure
{
    try {
		final Class exceptionHandlerClass = i_exceptionHandler.getClass();
		exceptionHandlerClass.newInstance();
		//If the execution was successful up to here, then the class of 
		// i_exceptionHandler fulfills the conditions required by class 
		// java.awt.EventDispatchThread, i.e. it offers:
		// - a default constructor, and
		// - a public method void handle(Throwable).
		System.setProperty("sun.awt.exception.handler", exceptionHandlerClass.getName());
	} catch (final Exception e) {
		throw new Failure("Failure registering class of {0} as global AWT/Swing exception handler.", e, i_exceptionHandler);
	}
}

/**User should not construct objects of this class*/
private Awt(){}

    /**Prints i_component and the chain of its parents for test purposes.
    private static void _printParents(final java.awt.Component i_component){
      Util.printErrorLine("Component: ");
      for(java.awt.Component c=i_component;; c=c.getParent()){
        Util.printErrorLine(c);
        Util.printErrorLine("getParent(): ");
        if(c==null){break;}
      }
    }//_printParents
    */


}//Awt

/* Knabe 1999-03-18
Unerklaerlicherweise muss zuerst der traceButton, dann die textArea in den
Dialog eingefuegt werden. Bei der umgekehrten Reihenfolge tritt in der
folgenden Situation:
    nach Ausloesen der Ausname FigurNichtErzeugbar: Rechtspfeil
    und dann Druecken des Buttons <Print Stack Trace>
    und dann Klicken des <Fenster zu>-Kreuzes
die folgende, unmotivierte Ausnahme auf:
    Exception occurred during event dispatching:
    java.lang.NullPointerException: invalid peer
        at java.awt.Component.removeNotify(Component.java:2523)
        at java.awt.TextComponent.removeNotify(TextComponent.java:106)
        at java.awt.Container.removeNotify(Container.java:1147)
        at java.awt.Window.dispose(Window.java:177)
        at lib.Awt$1.windowClosing(Awt.java:63)
*/