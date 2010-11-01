package multex.demo;  //AwtFile.java

import multex.Awt;

//2005-04-14  Knabe  Central exception reporting by class MyAwtExceptionhandler
//2002-04-20  Knabe  Directory demo as subpackage of multex
//2001-04-20  Knabe  As multi-purpose utility AwtFile: copy and move
//2001-04-15  Knabe  Comments translated into english
//2000-10-26  Knabe  Using new class Awt instead of Msg for reporting exception
//2000-05-23  Knabe  Umbenannt von WinCopy nach AwtCopy wg. SwingCopy
//1999-03-24  Knabe  Exc-Parameter als Object[]
//1999-03-18  Knabe  Als Demo fuer lib.Exc-Meldungsausgabe in AWT-Oberflaeche
//1999-01-15  Knabe  Als Demo fuer lib.Exc: Ausnahme mit History und Parameter
//1998-12-14  Knabe  Umbau zu binaer Kopieren
//1998-03-10  Knabe  Erstellung als Cat

/** Copies or moves a file, see methods {@link File#copy(String,String)}
* and {@link File#move(String,String)}.
* Serves as a demonstration for reporting an exception which comes from
* a lower tier in the AWT user interface. Look as first example the
* command line file handling {@link File}.
*
* Each exception occurring in an ActionListener of the user interface is
* reported by calling <PRE><CODE>
*    multex.Awt.report(ownerFrame, exception);
*</PRE></CODE>
*
* into a pop up window including its causing exception
* The message window contains a button "Show Stack Trace",
* which will on activation report the call stack traceback, which is essential
* for searching errors in the program.
* After pressing this button the message window will contain e.g. the following
* output:
*
*<code><pre>
*File input could not be copied to output
*CAUSE: java.io.FileNotFoundException: input (Das System kann die angegebene Datei nicht finden)
*
*----------Stack Trace follows:----------
*java.io.FileNotFoundException: input (Das System kann die angegebene Datei nicht finden)
*   at java.io.FileInputStream.open(Native Method)
*   at java.io.FileInputStream.<init>(Unknown Source)
*   at File.File(File.java:94)
*WAS CAUSING:
*File$FileFailure: File {0} could not be copied to {1}
*{0}=input  {1}=output
*   at File.File(File.java:108)
*   at AwtFile$1.actionPerformed(AwtFile.java:71)
*   at java.awt.Button.processActionEvent(Unknown Source)
*   at java.awt.Button.processEvent(Unknown Source)
*   at java.awt.Component.dispatchEventImpl(Unknown Source)
*   at java.awt.Component.dispatchEvent(Unknown Source)
*   at java.awt.EventQueue.dispatchEvent(Unknown Source)
*   at java.awt.EventDispatchThread.pumpOneEvent(Unknown Source)
*   at java.awt.EventDispatchThread.pumpEvents(Unknown Source)
*   at java.awt.EventDispatchThread.run(Unknown Source)
*</pre></code>
*/

public class AwtFile implements java.awt.event.ActionListener {

  private final java.awt.Frame _frame = new java.awt.Frame(getClass().getName());
  private final int _width = 20;
  private final java.awt.TextField _fromTextField
  = new java.awt.TextField("", _width);
  private final java.awt.TextField _toTextField
  = new java.awt.TextField("", _width);
  private final java.awt.Button _copyButton = new java.awt.Button("Copy");
  private final java.awt.Button _moveButton = new java.awt.Button("Move");

  /**Effect: Creates an AWT user interface for file operations copy,move*/
  public static void main(final String[] i_args){
    new AwtFile();
  }//main

  /**Do not create objects of me!*/
  private AwtFile(){
    multex.MsgText.setInternationalization(true);
    final java.util.Calendar version = java.util.Calendar.getInstance();
    version.clear();
    version.set(2001, 03, 20);
    multex.Msg.printMessages(
      new File.StartedExc(getClass().getName(), version.getTime())
    );

    //Layout:

    _frame.setLayout(new java.awt.FlowLayout());
    _frame.add(new java.awt.Label("From:"));
    _frame.add(_fromTextField);
    _frame.add(new java.awt.Label("To:"));
    _frame.add(_toTextField);
    _frame.add(_copyButton);
    _frame.add(_moveButton);
    _frame.pack();

    //Behaviour:
    MyAwtExceptionHandler.setComponentToBeBlocked(_frame);
    Awt.setAwtExceptionHandlerClass( //One central exception handler for all user-triggered exceptions 
        new MyAwtExceptionHandler()
    );
    _copyButton.addActionListener(this);
    _moveButton.addActionListener(this);

    _frame.addWindowListener( new java.awt.event.WindowAdapter(){
      public void windowClosing(final java.awt.event.WindowEvent i_){
        System.exit(0);
      }
    });
    _frame.setVisible(true);
  }//AwtFile()

    public void actionPerformed(final java.awt.event.ActionEvent i_event){
        final Object eventSource = i_event.getSource();
        //try{
          final String from=_fromTextField.getText(), to=_toTextField.getText();
          if(eventSource==_copyButton){
            File.copy(from, to);
          }else if(eventSource==_moveButton){
            File.move(from, to);
          }else{
            throw new multex.Failure("Wrong button", null, eventSource);
          }
        /*}
          catch(final Exception ex){
            //Common handler for all ActionEvents: report any Exception,
            //now moved to class MyAwtExceptionHandler 2005-04-14 Knabe
            multex.Awt.report(_frame, ex);
            return;
          }
        //end try*/
    }//actionPerformed

}//class AwtFile