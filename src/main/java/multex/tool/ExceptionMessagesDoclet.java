package multex.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import multex.Exc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;

/**This doclet collects the exception message text pattern of each concrete Throwable-subclass.
 * Scans all Java sources, takes the javadoc main comment text of each concrete subclass of Throwable, and appends all of them to the
 * file given by the Doclet option <code>-out</code>. This file is lateron usable as a resource bundle for internationalizing
 * exception message texts.
 * Each Throwable message text pattern line will be in the format
 * <BR/>
 * <I>fully qualified internal class name</I>=<I>message text pattern</I>
 * <BR/>
 * for example
 * <BR/>
 * <CODE>net.sourceforge.banking.lg.Account$CreditLimitExc=The amount of {0} euros is not available on this account.</CODE>
 * <P><B>Multiple line text</B>: If the main javadoc comment consists of more than one line, a backslash is appended to each line,
 * except the last one. So the convention of the .properties files continuation lines is achieved.
 * As Javadoc removes leading white space on continuation lines, and java.util.Properties.load removes trailing white space,
 * two subsequent lines in the Javadoc comment will be collapsed into one logical line without even a space between them.
 * <BR/>
 * So in order to make a long exception message legible, you should break inside of a word or append \u0020 or \t to each physical line.
 * </P>
 * <P><B>Character Encoding</B>: The encoding used to interpret characters in the source files can be set by the Javadoc option -encoding.
 *   In ANT use the attribute Encoding="...".<BR/>
 *   The encoding used to encode characters when writing them to the -out file, is always ISO-8859-1, 
 *   as this is the only encoding usable for .properties files, see documentation of java.util.Properties.load(InputStream).
 * <BR/>
 * So in order to make a long exception message legible, you should break inside of a word or append \u0020 or \t to each physical line.
 * </P>
 **/
public class ExceptionMessagesDoclet {


    private static final String OUT_OPTION = "-out";

    /**Qualified class name of java.lang.Object*/
    private static final String JAVA_LANG_OBJECT = Object.class.getName();

    /**Qualified class name of java.lang.Throwable*/
    private static final String JAVA_LANG_THROWABLE = Throwable.class.getName();

    /**The destination, where to write the exception message texts.*/
    private java.io.PrintWriter out;

    /**Check for doclet-added options.
     * Allowed options:
     * <ul>
     *   <li><b>-out filename</b> : Indicates where to append the message lines.
     * </ul>
     * @param option the name of the option including the minus character, can be "-out" or "-outencoding".
     * @return number of arguments on the command line for an option including the option name itself. Zero return means option not known. Negative value means error occurred.
     */
    public static int optionLength(final String option){
        if(OUT_OPTION.equals(option)){return 2;}
        return 0;
    }

    /**Entry point to execute this Doclet. It will be invoked by the Javadoc tool.*/
    public static boolean start(final RootDoc i_rootDoc) throws Exception {
        new ExceptionMessagesDoclet()._execute(i_rootDoc);
        return true;
    }

    /**Appends all exception message text patterns to the file named by option -out in encoding ISO-8859-1,
     * as this is the standard encoding for Java .properties files, 
     * see java.util.Properties.load(InputStream inStream).
     * @param rootDoc Comprising all classes to visit.
     * @throws Exception An error occured inside of this doclet.
     */
    private void _execute(final RootDoc rootDoc) throws Exception {
        final String outFileName = _getOutputFilename(rootDoc.options());
        final File outFile = new File(outFileName).getAbsoluteFile();
        //final FileWriter fileWriter = new FileWriter(outFile, /*append*/true);
        if(outFile.isDirectory()){
            throw new Exception("Output file " + outFile + " is a directory");
        }
        if(!outFile.canWrite()){
            throw new Exception("Cannot write to output file " + outFile);
        }
        final FileOutputStream fileOutputStream = new FileOutputStream(outFile, /*append*/true);
        final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "ISO-8859-1");
        //this.out = new PrintWriter(fileWriter);
        this.out = new PrintWriter(outputStreamWriter);
        _visitAll(rootDoc);
        this.out.close();
        System.out.println();
        System.out.println(getClass().getName() + ": All exception message text patterns have been appended to file " + outFile);
    }

    /**Gets the value of the doclet option -out.
     * This will be used as the name of the file to append the messages to.
     * @param options Each element of it is an array with it's first element being the option's name, the following elements being option values.
     * @return the filename where to add exception text property definitions
     */
    private String _getOutputFilename(final String[][] options) {
        for(int i=0; i<options.length; i++){
            final String[] option = options[i];
            if(OUT_OPTION.equals(option[0])){
                return option[1];
            }
        }
        throw new OutputFilenameMissingExc();
    }
    public static class OutputFilenameMissingExc extends Exc {
        private static final long serialVersionUID = -4465866427805576812L;
        public OutputFilenameMissingExc() {
            super("Missing name of output file, where to append the extracted exception text property definitions.\nUse Javadoc doclet option " + OUT_OPTION + " to indicate it!");
        }
    }

    /**Visits all classes contained in rootDoc. Visits the nested classes, too.*/
    private void _visitAll(final RootDoc rootDoc) throws Exception {
        final ClassDoc[] classDocs = rootDoc.classes();
        for (int i = 0; i < classDocs.length; ++i) {
            final ClassDoc classDoc = classDocs[i];
            //System.out.println("Visiting class " + classDoc);
            _visitClass(classDoc);
        }
    }

    /**Vists the class in classDoc.
     * @throws TooNestedThrowableException
     * @throws IOException */
    private void _visitClass(final ClassDoc classDoc) throws TooNestedThrowableException, IOException{
        _printMessageTextPatternOfThrowable(classDoc);
        /*Rekursion anscheinend unnötig!
        final ClassDoc[] innerClasses = classDoc.innerClasses();
        for(int i = 0; i < innerClasses.length; ++i){
            final ClassDoc innerClassDoc = innerClasses[i];
            _visitClass(innerClassDoc);
        }
        */
    }

    /**Prints the message text pattern for the class, if it is a concrete subclass of Throwable.
     * The main commentText (without tags) of the class is written in the Java property file format to {@link #out}.
     *
     * @param classDoc representing the class to be checked.
     * @throws TooNestedThrowableException
     * @throws IOException
     */
    private void _printMessageTextPatternOfThrowable(final ClassDoc classDoc) throws TooNestedThrowableException, IOException {
        if(classDoc.isAbstract() || classDoc.isOrdinaryClass()){return;}
        //Now classdoc is a concrete interface, annotation type, enum, exception, or error
        //out.println("class: " + classDoc);
        if(!_isSubclassOfThrowable(classDoc)){return;}
        final String commentText = classDoc.commentText();
        if(commentText.length()<10){
            System.err.println("Too short (<10ch) message text \"" + commentText + "\" for Throwable " + classDoc);
            return;
        }
        final String key = _internalClassName(classDoc);
        this.out.print(key);
        this.out.print(" = ");
        _printMultilineTextForPropertiesFile(commentText);
    }

    /**Prints the multi-line text suitable for usage as value in a .properties file to this.out.
     * Each new-line in the text is printed as the sequence backslash, new-line.
     * At the end a simple new-line is printed.
     * @param multilineText The text to be printed in .properties multi-line format.
     * @throws IOException
     */
    private void _printMultilineTextForPropertiesFile(final String multilineText) throws IOException {
        final BufferedReader br = new BufferedReader(new StringReader(multilineText));
        for(boolean isContinuationLine=false;;){
            final String line = br.readLine();
            if(null==line){break;}
            if(isContinuationLine){this.out.println('\\');}
            isContinuationLine = true;
            this.out.print(line);
        }
        this.out.println();
    }

    /**Throwable class {canonicalName} has more than one nesting level. Handling not yet implemented.*/
    public static final class TooNestedThrowableException extends Exception {
        private static final long serialVersionUID = 2388470231622088395L;

        public TooNestedThrowableException(final String canonicalName){
            super("Throwable class " + canonicalName + " has more than one nesting level. Handling not yet implemented.");
        }
    }

    /**Returns the internal class name of the class.
     * @param classDoc the class documentation, from which to get the internal name.
     * @return the class name in the format of java.lang.Class.getName(),
     *         that means nested class identifiers are separated by a dollar sign ($),
     *         rather than by a period (.) from the identifier of the containing class.
     * @throws TooNestedThrowableException The internal class name would have more than one dollar character.
     */
    private String _internalClassName(final ClassDoc classDoc) throws TooNestedThrowableException {
        final String canonicalName = classDoc.toString();
        final ClassDoc containingClass = classDoc.containingClass();
        if(null==containingClass){
            //canonicalName does not contain a dollar ($) character.
            return canonicalName;
        }
        if(null!=containingClass.containingClass()){
            throw new TooNestedThrowableException(canonicalName);
        }
        final int indexOfLastDollar = canonicalName.lastIndexOf('.');
        final char[] characters = canonicalName.toCharArray();
        characters[indexOfLastDollar] = '$';
        final String result = new String(characters);
        return result;
    }

    private boolean _isSubclassOfThrowable(final ClassDoc classDoc){
        final ClassDoc superClass = classDoc.superclass();
        final String superClassName = superClass.toString();
        //out.println("super: " + superClass);
        if(JAVA_LANG_THROWABLE.equals(superClassName)){return true;}
        if(JAVA_LANG_OBJECT.equals(superClassName)){return false;}
        return _isSubclassOfThrowable(superClass);
    }


}
