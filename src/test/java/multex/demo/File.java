package multex.demo;  //File.java

//Change history:
//2002-04-20  Knabe  Option for standard stack trace output addded for JRE 1.4
//2002-04-20  Knabe  Directory demo as subpackage of multex
//2002-03-26  Knabe  Default message text of CopyFailure with quoted parameters
//2001-04-20  Knabe  As multi-purpose utility: copy and move
//2001-04-14  Knabe  Again only unnamed exception parameters
//2000-12-13  Knabe  Use new method Msg.printReport(...)
//2000-09-29  Knabe  Nur noch benannte Ausnahmeparameter
//2000-07-24  Knabe  Test von benannten Ausnahmeparametern
//2000-07-04  Knabe  CopyFailure beerbt multex.Failure statt Exc
//1999-03-24  Knabe  Exc-Parameter als Object[]
//1999-01-15  Knabe  Als Demo fuer Exc: Ausnahme mit History und Parameter
//1998-12-14  Knabe  Umbau zu binaer Kopieren
//1998-03-10  Knabe  Erstellung als Cat

/** Copies or moves a file, see methods main() and _usageExit()
  This program serves as a demonstration for the redefinition of an exception
  coming from a lower tier into a Failure-exception of the own abstraction level
  without suppressing the diagnostic information of the causing exception.
  Here the method copy() redefines each IOException into CopyFailure.
  CopyFailure extends Failure and passes the causing exception via the
  constructor to Failure. So it is possible in main() catching any Exception to
  report the complete diagnostic information both of the causing IOException
  and of the abstracted CopyFailure.

  Additionally CopyFailure has two parameters:
  <P>{0}=Name of the input file, {1}=Name of the output file

  <P>Example Dialog with JRE 1.4:
<code><pre>
*  C:\>java multex.demo.File -c eingabe ausgabe
*  File 'input' could not be copied to 'output'
*  CAUSE: java.io.FileNotFoundException: input (The system can't find the indicated file)
*  -----Stack trace follows-----
*  java.io.FileNotFoundException: input (The system can't find the indicated file)
*          at java.io.FileInputStream.open(Native Method)
*          at java.io.FileInputStream.<init>(FileInputStream.java:68)
*          at File.copy(File.java:140)
*  WAS CAUSING:
*  multex.demo.File$CopyFailure: File ''{0}'' could not be copied to ''{1}''
*      {0} = 'input'
*      {1} = 'output'
*          at File.copy(File.java:154)
*          at File.main(File.java:83)
*          at ...
*  C:\>
</pre></code>

<P>The shorter way to capture low level diagnostic information is used
  in method move(inFileName, outFileName).
  @see #move(String, String)
*/

public class File {

  private static final String _option = "-";
  private static final String _copy = "c";
  private static final String _move = "m";
  private static final String _standard = "s";
  private static final String _optionCopy = _option+_copy;
  private static final String _optionMove = _option+_move;
  private static final String _className = File.class.getName();

  /**Prints a usage message and then exits with error code 1*/
  private static void _exitWithUsage(){
    System.err.println(
      "Usage: java "+_className+"  "+_option+"{"+_copy+"|"+_move+"}["+_standard+"] inFileName outFileName\n"
      + "Options:\n"
      + "   "+_optionCopy+"   Copies file inFileName to file outFileName\n"
      + "   "+_optionMove+"   Moves file inFileName to file outFileName (copy, delete)\n"
      + "   "+_optionCopy+_standard+"  Standard: Same as "+_optionCopy+", but will report standard stack trace\n"
      + "   "+_optionMove+_standard+"  Standard: Same as "+_optionMove+", but will report standard stack trace\n"
    );
    System.exit(1);
  }

  /**Effect: Copies or moves file i_args[1] in binary mode to file i_args[2].*/
  public static void main(final String[] i_args){
    if(i_args.length!=3){_exitWithUsage();}
    final String option=i_args[0], inFileName=i_args[1], outFileName=i_args[2];
    try{
      if(option.startsWith(_optionCopy)){
        copy(inFileName, outFileName);
      }else if(option.startsWith(_optionMove)){
        move(inFileName, outFileName);
      }else{
        _exitWithUsage();
      }
    } catch(Exception ex){
        if(option.endsWith(_standard)){
          ex.printStackTrace();
        }else{
          multex.Msg.printReport(ex);
        }
      }//catch
    //end try
  }//main

  /**Indicates, that this test driver has been started. If your environment
    is set to language "de", then the message text from the german message
    text file MsgText_de.properties will be taken instead of this default
    message text defined here.*/
  public static class StartedExc extends multex.Exc {
    public StartedExc(final String i_driver, final java.util.Date i_date){
      super("File handling driver {0}, version of {1,date,full} was started",
        i_driver, i_date
      );
    }
  }

  /**Indicates failure of a file copy operation.
    This way of individually declaring Failure-exceptions is more writing,
    but you get exceptions <UL>
      <LI>whith internationalizable message text
      <LI>which are individually handable
      <LI>with better documented parameters
    </UL>
    The other way you can see in method {@link #move(String, String)}
  */
  public static class CopyFailure extends multex.Failure {
    public CopyFailure(
      final Exception i_cause,
      final String    i_inFileName,
      final String    i_outFileName
    ){
      super("File ''{0}'' could not be copied to ''{1}''",i_cause,
        i_inFileName, i_outFileName
      );
    }
  }//CopyFailure

  /**Effect: Copies the file i_inFileName in binary mode to file i_outFileName.
  */
  public static void copy(
    final String i_inFileName,
    final String i_outFileName
  ) throws CopyFailure
  {
    try{
      final java.io.FileInputStream inStream
      = new java.io.FileInputStream(i_inFileName);
      final java.io.FileOutputStream outStream
      = new java.io.FileOutputStream(i_outFileName);
      final byte[] buf = new byte[512];

      for(;;){
        final int len = inStream.read(buf);
        if(len<=0){break;}
        outStream.write(buf,0,len);
      }
      inStream.close();
      outStream.close();
    } catch(final Exception ex){
      throw new CopyFailure(ex, i_inFileName, i_outFileName);
    }
    //end try
  }//copy

  /**Effect: Moves the file i_inFileName to file i_outFileName. This method
    firstly copies the input file in binary mode and then deletes it.
    @throws multex.Failure if its is not possible to move file i_inFileName
      to file i_outFileName; This way of directly throwing a multex.Failure
      exception is less writing; You can nevertheless provide an individual
      message text with parameterization; Internationalization is not possible
      this way.
    @see CopyFailure
  */
  public static void move(
    final String i_inFileName,
    final String i_outFileName
  ) throws multex.Failure
  {
    try{
      copy(i_inFileName, i_outFileName);
      _delete(i_inFileName);
    } catch(final Exception ex){
        throw new multex.Failure(
          "Cannot move file {0} to file {1}", ex, i_inFileName, i_outFileName
        );
      }
    //end try
  }//move

  /**Effect: Deletes the file i_fileName.*/
  private static void _delete(
    final String i_fileName
  )
  {
    final java.io.File file = new java.io.File(i_fileName);
    file.delete();
  }//_delete


  /**Do not create objects of me!*/
  private File(){}

}//File