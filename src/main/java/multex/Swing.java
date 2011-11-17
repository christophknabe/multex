package multex;
import java.util.ResourceBundle;

//Change history:
//2003-09-11  Knabe  Redundancies with class Awt removed
//2003-06-00  Lange  Derived from class Awt


import javax.swing.JTextArea;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JDialog;

import java.awt.EventQueue;
import javax.swing.JScrollPane;

/**Services for reporting onto Swing dialogs the messages for any exception
	with dynamic or static or no internationalization.
    The exception can be <UL>
	    <LI> with parameters (subclass of {@link Exc})
	    <LI> with cause chain and parameters (subclass of {@link Failure})
	    <LI> or any standard Java exception (other subclass of java.lang.Throwable)
    </UL>

    @author Michael Lange + Christoph Knabe, Berlin.
    Derived from class {@link Awt}. Copyright 1999-2003
*/
public class Swing {

	/**Fixed bounds of the message dialog for Elox monitor with a half height screen
	public static final Rectangle BOUNDS 
	= new Rectangle(0, 0, 655, 230);
    */
	//static message parts:

	/**The name of this class in the format package.class*/
	private static final String _className = Swing.class.getName();


	/**Reports i_throwable into a Swing dialog with static internationalization.
	     For details see {@link Awt#report(Component, Throwable, ResourceBundle)}.
	*/
	public static void report(
	  final java.awt.Component io_ownerHook, final Throwable i_throwable
	){
		report(io_ownerHook, i_throwable, (ResourceBundle)null);
	}


    /**Reports i_throwable into a Swing dialog.
     * For a detail description see the corresponding method of class Awt:
     * {@link Awt#report(Component, Throwable, ResourceBundle)}.
	    <P>
	    In an application this method should not be called directly from
	    a GUI class, but the author of each GUI class should set up a method e.g.<PRE>
	    private void _report(Throwable e){Swing.report(this,e,null);}
	    </PRE>, which will be called from each catch-clause in the event listeners
	    and adds the actual GUI class as the owner of the message dialog.
    */
    public static void report(
      final java.awt.Component io_ownerHook, final Throwable i_throwable, 
      final java.util.ResourceBundle i_resourceBundle
    ){
		final boolean modal = io_ownerHook != null;
        try{
            final java.awt.Window ownerWindow = Awt.getOwnerWindow(io_ownerHook);
            new Dialog(ownerWindow, modal, i_throwable, i_resourceBundle);
        }catch(final Throwable ex){Awt.reportNoWindow(_className,ex,i_throwable);}
        //end try
    }

	/**A message reporting dialog window*/
	private static class Dialog {

	  //attributes:
	  /**Dialog window for reporting the exception cause chain*/
	  private final JDialog _dialog;
	  private boolean _stackTraceShown = false;


	  private Dialog (
		  final java.awt.Window io_owner, final boolean i_modal, 
		  final Throwable i_throwable,  final java.util.ResourceBundle i_resourceBundle
	  ){
	    final String ownerTitle;

        if(io_owner instanceof java.awt.Frame){
          final java.awt.Frame owner = (java.awt.Frame)io_owner;
          _dialog = new JDialog(owner);
          ownerTitle = owner.getTitle();
        }else if(io_owner instanceof java.awt.Dialog){
          final java.awt.Dialog owner = (java.awt.Dialog)io_owner;
          _dialog = new JDialog(owner);
          ownerTitle = owner.getTitle();
        }else{
	      throw new AssertionFailure("Owner window {0} is not of class java.awt.Frame nor java.awt.Dialog", io_owner);
        }


	    _dialog.setTitle(Awt.titlePrefix + ownerTitle);
	    _dialog.setModal(i_modal);
	    _initialize(i_throwable, i_resourceBundle);

	    //_dialog.setVisible(true);
	    EventQueue.invokeLater(new Awt.WindowShower(_dialog));
	  }//Dialog

	  /**Adds to _dialog the necessary components and their listeners*/
	  private void _initialize(
	  	final Throwable i_throwable,      
	  	final java.util.ResourceBundle i_resourceBundle
	  ){

	    //Look:


	    final StringBuffer messageChain = new StringBuffer();
	    Msg.printMessages(messageChain, i_throwable, i_resourceBundle);
	    //TextArea for reporting the causer chain messages:
	    // empty: 1 row, 80 columns:
	    final JTextArea textArea = new JTextArea(/*1,80*/);
	    textArea.setBackground(java.awt.Color.white);
	    textArea.setForeground(java.awt.Color.black);
	    //textArea.setBounds(BOUNDS);
	    _append(textArea, messageChain.toString());
	    textArea.setEditable(false);

	    //Button "OK":
	    final JButton okButton = new JButton(" O  K ");
	    //okButton.setBackground(java.awt.Color.white); //???only for screenshot
	    okButton.requestDefaultFocus();
	    //Button "Show Stack Trace":
	    final JButton traceButton = new JButton("Show Stack Trace");
	    traceButton.requestDefaultFocus();
	    //traceButton.setBackground(java.awt.Color.white); //???only for screenshot
	    //buttonPanel:

	    traceButton.setNextFocusableComponent(okButton);
	    okButton.setNextFocusableComponent(traceButton);

	    final JPanel buttonPanel = new JPanel(new java.awt.BorderLayout());
	    //buttonPanel.setBounds(BOUNDS);
	    buttonPanel.add(okButton);
	    buttonPanel.add(traceButton, java.awt.BorderLayout.EAST);

	    final JPanel contentPanel = new JPanel(new java.awt.BorderLayout());
	    contentPanel.add(textArea, java.awt.BorderLayout.CENTER);
	    contentPanel.add(buttonPanel, java.awt.BorderLayout.SOUTH);

	    final JScrollPane scrollPane = new JScrollPane(contentPanel);
	    //scroll_pane.setBounds(BOUNDS);
		//scroll_pane.setVerticalScrollBar(scroll_pane.createVerticalScrollBar());
		//scroll_pane.setHorizontalScrollBar(scroll_pane.createHorizontalScrollBar());

		_dialog.getContentPane().add(scrollPane);

	    //_dialog.setBounds(BOUNDS);
	    //_dialog.setResizable(false);
	    //_dialog.pack();  //2004-05-11 done by Awt.WindowShower


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
	    //For test purposes 01-06-28:
	    //_printParents(okButton);
	  }//_initialize()

	  /**Free the resources associated with this dialog window*/
	  private void _dispose(){_dialog.dispose();}

	  /**Appends the stack trace of i_throwable to io_textArea
	    and disables io_traceButton
	  */
	  private void _fireTraceButton(final JButton io_traceButton,
	    final JTextArea io_textArea, final Throwable i_throwable
	  ){
	    io_traceButton.setVisible(false);
	    final StringBuffer buffer = new StringBuffer();
	    buffer.append(Util.lineSeparator);   buffer.append(Util.lineSeparator);
	    buffer.append(Msg.stackTraceFollows);
	    buffer.append(Util.lineSeparator);
	    Msg.printStackTrace(buffer, i_throwable);
	    final String stackTrace = buffer.toString();
	    _append(io_textArea, stackTrace);
	    io_textArea.insert("", 0); //should scroll back upwards to character first line
	    _dialog.pack();
	    _stackTraceShown = true;
	  }

	}//Dialog


	/**
	 * Returns a package-private, shared, invisible Frame
	 * to be the owner for Dialogs created with null owners.
	static java.awt.Frame getSharedOwnerFrame(){
	  return _sharedOwnerFrame;
	}
	 */

	/**package-private, shared, invisible Frame
	 * 
	private static final java.awt.Frame _sharedOwnerFrame = new java.awt.Frame();
*/
	/**Reports to System.err, that exception i_problem occured when
	  trying to report the exception i_reportee.

	  Replaced by this from Awt:

	private static void _reportNoWindow(
	  final Throwable i_problem, final Throwable i_reportee
	){
	  Util.printErrorLine();
	  Util.printErrorString(_className + ": THE FOLLOWING EXCEPTION OCCURED:");
      Util.printErrorLine();
	  i_problem.printStackTrace();
	  Util.printErrorString(_className + ": WHEN CONSTRUCTING THE MESSAGE WINDOW FOR EXCEPTION:");
      Util.printErrorLine();
	  i_reportee.printStackTrace();
	}
	*/

	/**Appends i_text to io_textArea and enlarges the number of rows of this
	  TextArea as necessary
	*/
	private static void _append(
	  final JTextArea io_textArea, final String i_text
	){
	  io_textArea.setRows( io_textArea.getRows() + Awt.countLines(i_text) );
	  io_textArea.append(i_text);
	}

	/**Result: Number of lines in String i_text
	  @see java.io.BufferedReader#readLine()

	private static int _countLines(final String i_text){
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
	*/

	/**User should not construct objects of this class*/
	private Swing(){}

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


}//Swing
