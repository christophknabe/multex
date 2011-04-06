package multex;

/**
 * Utilities for MulTEx.
 *
 * Usually methods of this class should not be called by framework client code.
 * Each exception (chain) should be reported by methods of classes
 * {@link multex.Msg} or {@link multex.Awt}, or {@link multex.Swing}.
 * <BR/>
 * But if the format of the mentioned reporting classes is not approriate
 * for your purposes, then you can create your own reporting methods by
 * the help of this MulTEx utility class.
 *
 * @author Christoph Knabe
 * @since MulTEx 6 on 2005-01-13
 */
public class Util {

    /**Returns a new copy of the array or null, if null was provided
     * @since MulTEx 6 on 2005-01-13
     * */
    public static Object[] clone(final Object[] i_array){
        if(i_array==null){return null;}
        final Object[] result = new Object[i_array.length];
        System.arraycopy(i_array, 0, result, 0, i_array.length);
        return result;
    }

    /**Sets a CauseGetter to be used by method {@link #getCause(Throwable)}
    in order to get the cause of a Throwable object.
    Default is {@link ReflectionCauseGetter}
    */
    public final static void setCauseGetter(final CauseGetter i_causeGetter){
        _causeGetter = i_causeGetter;
    }
    
    /** Result: The directly causing Throwables of i_throwable,
    * if there are some, otherwise null. This method is better than
    * Throwable.getCause(Throwable) in following legacy exception chaining,
    * as it uses the {@link ReflectionCauseGetter}
    * by default. You can replace the cause getter by method {@link #setCauseGetter}.
    * The cause getter is used only for getting a cause, which is returned by 
    * a method of the Throwable. All MulTEx parameters of this Throwable, which are themselves
    * instances of Throwable, are returned, too.
    */
    public final static Throwable[] getCauses(final Throwable i_throwable){
    	//Count the causes:
    	int count = 0;
    	final Throwable cause = _causeGetter.getCause(i_throwable);
    	if(cause!=null){count++;}
    	final Object[] params = _getParams(i_throwable);
		count += _countThrowables(params);
    	
    	//Return the causes, or null:
    	if(count<=0){return null;}
    	final Throwable[] result = new Throwable[count];
    	int causeIndex=0;
    	if(cause!=null){result[causeIndex] = cause; causeIndex++;}
		if(params!=null){ //Count instances of Throwable in params
			for(int i=0; i<params.length; i++){
				final Object param = params[i];
				if(param instanceof Throwable){
					final Throwable throwable = (Throwable)param;
					result[causeIndex] = throwable; causeIndex++;
				}
			}
		}
        return result;
    }

    /**Returns the MulTEx parameters of the Throwable, if it is a {@link MultexException} and has parameters, otherwise null.*/
	private static Object[] _getParams(final Throwable i_throwable) {
		if(i_throwable instanceof MultexException){
    		final MultexException multexException = (MultexException)i_throwable;
    		return multexException.getParameters();
    	}
		return null;
	}

	/**
	 * @param params
     * @return
	 */
	private static int _countThrowables(final Object[] params) {
		int result = 0;
		if(params!=null){ //Count instances of Throwable in params
			for(int i=0; i<params.length; i++){
				final Object param = params[i];
				if(param instanceof Throwable){result++;}
			}
		}
		return result;
	}
    
    /** Gets the maximum recursion depth as specified in method {@link #setMaxRecursionDepth(int)}.
     * @since MulTEx 8.3 as of 2011-03-25 */
    public static final int getMaxRecursionDepth(){
        return _maxRecursionDepth;
    }
        
    /** Sets the maximum depth to follow an exception chain, when calling recursively method {@link #getCause(Throwable)}.
     * Will be used by {@link Util#_appendCompactStackTraceRecursively(StringBuffer, Throwable, StackTraceElement[], int)}, 
     * {@link MsgText#_appendMessageTreeRecursively(StringBuffer, Throwable, java.util.ResourceBundle, String, int)}
     * and should be respected by everyone following an exception chain by calling method {@link #getCause(Throwable)}.
     * @param maxRecursionDepth May range from 5 to 100. Default is 10.
     * @throws IllegalArgumentException argument outside of range 5...100
     * @since 8.3 as of 2011-03-25
    */
    public static void setMaxRecursionDepth(final int maxRecursionDepth) throws IllegalArgumentException {
        if(maxRecursionDepth<5 || maxRecursionDepth>100){
            throw new IllegalArgumentException(Util.class.getName() + ".setMaxRecursionDepth allows only integers from 5 to 100");
        }
        _maxRecursionDepth = maxRecursionDepth;
    }
    
    /** @since MulTEx 8.3 as of 2011-03-25 */
    private static int _maxRecursionDepth = 10;

	/** Result: The directly causing Throwable of i_throwable,
    * if there is one, otherwise null. This method is better than
    * Throwable.getCause(Throwable) in following legacy exception chaining,
    * as it uses the {@link ReflectionCauseGetter}
    * by default. You can replace the cause getter by method {@link #setCauseGetter}.
    * Tries to avoid a cause cycle by returning null, if a direct cycle is detected.
    */
    public final static Throwable getCause(final Throwable i_throwable){
      final Throwable result = _causeGetter.getCause(i_throwable);
      if(result==null || result.getCause()==i_throwable){ 
          //result is a wrapper for i_throwable, but not its cause.
          return null;
      }
      return result;
    }

    /**Returns true, if class java.lang.Throwable has an operation getCause() in this Java Runtime Environment*/
    public static boolean jreHasExceptionChaining(){return Util._jreHasExceptionChaining;}

    /**Nullifies the redundant lines ending each of the contained stack traces.
     * Since the stack trace of an exception and its cause have much in common,
     * there is a big redundancy. From JDK 1.4 this redundancy is eliminated by
     * the standard printStackTrace(). Before JDK 1.4 the redundancy elimination
     * is the task of this method.
     * Looks up the end locations of each of the stack traces, which are separated
     * by the word contained in wasCausing. The goin downwards from each of these
     * end locations nullifies the redundant lines.
    private static void nullifyRedundantTraceTrailLines(
        final java.util.Vector io_tracesLines
    ) {
          final int[] traceEndIndices = _getTraceEndIndices(io_tracesLines);
          //_println("traceEndIndices", traceEndIndices); //debug
          for(int i=1; i<traceEndIndices.length; i++){
            _nullifyCommonTraceTail(
              io_tracesLines, traceEndIndices[i-1], traceEndIndices[i]
            );
          }
    }//nullifyRedundantTraceTrailLines
     * No longer necessary since JRE 1.4
     * */

    /**Splits i_string into internized line strings by BufferedReader.readln().
      @return The lines in the sequence of storage in i_string as a Vector of
              String-s.
    private static java.util.Vector _splitIntoLines(final String i_string){ //String_Vector
      final java.io.BufferedReader reader = new java.io.BufferedReader(
        new java.io.StringReader(i_string)
      );
      final java.util.Vector result = new java.util.Vector();  //String_Vector
      for(;;){
        String line; //Warum geht es hier nicht final?
        try{line = reader.readLine();}
          catch(java.io.IOException ex){line=null;}
        //end try
        if(line==null){break;}
        result.addElement(line.intern());
      }
      return result;
    }
*/



    /**Result: The indices of the elements of i_tracesLines, which each represent
      the last line of a trace. Each line immediately before an occurence of the
      separator String wasCausing in i_tracesLines is considered as a trace end
      line. Additionally the last line of i_tracesLines is considered as a trace
      end line.
      Require: In i_traces are several stack traces, split into lines; the traces
        are separated by the String wasCausing.
    private static int[] _getTraceEndIndices(final java.util.Vector i_tracesLines){
      final int numberOfTraces
      = _numberOfOccurences(i_tracesLines, Util.wasCausing) + 1;
      final int[] result = new int[numberOfTraces];
      final int size = i_tracesLines.size();
      int resultIndex = 0;
      for(int i=0; i<size; i++){
        if(i_tracesLines.elementAt(i) == Util.wasCausing){
          result[resultIndex++] = i-1; //line before wasCausing
        }
      }
      result[resultIndex++] = size-1; //last line
      return result;
    }
    */



    /**Result: The number, how often i_object occurs in i_vector,
      measured by the identity comparison
    private static int _numberOfOccurences
    (final java.util.Vector i_vector, Object i_object){
      final int size = i_vector.size();
      int result = 0;
      for(int i=0; i<size; i++){
        if(i_vector.elementAt(i) == i_object){result++;}
      }
      return result;
    }
    */



    /**Nullifies redundant subsequent entries in io_vector.
      Nullifies all entries in io_vector
      starting from i_nullifyEndIndex inclusively downwards,
      which are identically contained in the same
      sequence as entries starting from i_compareEndIndex inclusively downwards.
      The lowest such redundant entry will not be nullified.
    private static void _nullifyCommonTraceTail(
      final java.util.Vector io_vector,
      final int i_nullifyEndIndex, final int i_compareEndIndex
    ){
      for(int ni=i_nullifyEndIndex, ci=i_compareEndIndex; ni>=0;ni--,ci--){
        if(io_vector.elementAt(ni)!=io_vector.elementAt(ci)){break;}
        io_vector.setElementAt(null, ni); //leaves no redundant lines
      }
    }//_nullifyCommonTraceTail
    */


    /**Returns the upmost exception in i_throwableChain, which is of class
    i_expectedThrowableClass, helpwise null.
    Usage e.g. for a FieldValueExc, which is thrown
    from the business logic layer to the user interface layer, wrapped some
    times on this way, but should trigger a uniform reaction on the user
    interface layer (Marking the named form field as erroneous, and issuing
    its error message).
    */
    public static Throwable getContainedException_Old(
        final Throwable i_throwableChain, final Class i_expectedThrowableClass
    ){
        for(Throwable result = i_throwableChain;;){
            if(result==null){return result;}
            if(i_expectedThrowableClass.isInstance(result)){return result;}
            final Throwable cause = Util.getCause(result);
            result = cause;
        }
    }
    
    /**Returns the upmost exception in i_throwableChain, which is of class
    i_expectedThrowableClass, helpwise null.
    Usage e.g. for a FieldValueExc, which is thrown
    from the business logic layer to the user interface layer, wrapped some
    times on this way, but should trigger a uniform reaction on the user
    interface layer (Marking the named form field as erroneous, and issuing
    its error message).
    @return {@link #INFINITE_EXCEPTION_CHAIN} if the exception chain to be followed is longer than {@link #getMaxRecursionDepth()}.
      @see #setMaxRecursionDepth(int)
    */
    public static Throwable getContainedException(
        final Throwable i_throwableChain, final Class i_expectedThrowableClass
    ){
        Throwable result = i_throwableChain;
        for(int i=1; i<=_maxRecursionDepth; i++){
            if(result==null){return result;}
            if(i_expectedThrowableClass.isInstance(result)){return result;}
            final Throwable cause = Util.getCause(result);
            result = cause;
        }
        return INFINITE_EXCEPTION_CHAIN;
    }
    
    /**This exception indicates, that there is probably a cycle in an exception chain.*/
    public static final class InfiniteExceptionChain extends Exception {
        @Override public String getMessage() {
            return "Probably cycle in exception chain. Exceeding maximum causal recursion depth of " + _maxRecursionDepth;
        }
    }
    /**Reusable instance*/
    public static final InfiniteExceptionChain INFINITE_EXCEPTION_CHAIN = new InfiniteExceptionChain();

    /**Result: The deepest Throwable object, which indirectly caused the
      program to throw i_throwable, if there is one, otherwise i_throwable
      itself. If i_throwable is null, the result will be null, too.
    @return {@link #INFINITE_EXCEPTION_CHAIN} if the exception chain to be followed is longer than {@link #getMaxRecursionDepth()}.
      @see #getCause(Throwable)
      @see #setMaxRecursionDepth(int)
    */
    public final static Throwable getOriginalException(final Throwable i_throwable){
        if(i_throwable==null){return null;}
        Throwable result = i_throwable;
        for(int i=1; i<=_maxRecursionDepth; i++){
            final Throwable cause = Util.getCause(result);
            if(cause==null){return result;}
            result = cause;
        }
        return INFINITE_EXCEPTION_CHAIN;
    }
    
    public final static Throwable getOriginalException_Old(final Throwable i_throwable){
        if(i_throwable==null){return null;}
        for(Throwable result = i_throwable;;){
            final Throwable cause = Util.getCause(result);
            if(cause==null){return result;}
            result = cause;
        }
    }

    /**For low level reporting of an exception. Preferably use this toString(), than
     * the JDK-provided throwable.toString().
     * The JDK reports in the format
     *   package.subpackage.ClassName: throwable.getMessage(),
     * which is suitably redefined for any {@link MultexException}, too.
     * <P>But some legacy-chained exceptions like org.xml.sax.SAXException suppress the
     * class name of this exception and instead of it return the toString()
     * of the causing exception.
     * As to obtain all exception class names in a causal chain,
     * we use our own toString(), which includes
     * firstly a mere copy of Throwable.toString().and then the toString() of the reported
     * exception.</P>
     */
    public static String toString(
        final Throwable i_throwable
    ) {
        final String throwableString = i_throwable.toString();
        if(i_throwable instanceof MultexException){return throwableString;}
    
        //Other exception:
        final String className = i_throwable.getClass().getName();
        final String detailMessage = i_throwable.getMessage();
        if(throwableString.indexOf(className)>=0){return throwableString;}
        //className missing in throwableString
        return className + (detailMessage==null ? "" : ": " + detailMessage)
            + "; Caused by: " + throwableString
        ;
    }

    /**Prints the given string to System.err.
     * This can be used as the emergency exit, if MulTEx is not capable to report an exception.
     */
    /*package*/ static void printErrorString(final String i_string){
        System.err.print(i_string);
    }

    /**Prints a newline to System.err.
     * This can be used as the emergency exit, if MulTEx is not capable to report an exception.
     */
    /*package*/ static void printErrorLine(){
        System.err.println();
    }
    
    /**Appends the stack trace of i_throwable and all its chained cause exceptions
      to io_buffer. All redundant location lines therein are suppressed.
      Usually you should not directly call this method, but a printStackTrace-method in
      class Msg.
      @see Msg#printStackTrace(StringBuffer,Throwable,String)
    private static void appendCompactStackTrace(
        final StringBuffer io_destination, final Throwable i_throwable,
        final String i_lineSeparator //TODO remove this parameter
    ){
        if(i_throwable==null){
            io_destination.append("null Throwable provided to ")
            .append(_className).append(".appendCompactStackTrace(...)");
            return;
        }

        //prepare out writer:
        final java.io.StringWriter sw = new java.io.StringWriter();
        final java.io.PrintWriter out = new java.io.PrintWriter(sw, true);

        //collect stack traces onto out writer:
        i_throwable.printStackTrace(out);

        final String tracesString = sw.toString();
        //printErrorLine("tracesString:");printErrorLine(tracesString);//debug

    //old version in MulTEx 4:
    //  final java.util.Vector tracesLines //String_Vector
    //  = _splitIntoLines(tracesString);
    //
    //  if(!_jreHasExceptionChaining){ //on JDK 1.1 ... 1.3
    //      //standard printStackTrace() left redundant lines in stack traces
    //      //of causing exceptions!
    //      nullifyRedundantTraceTrailLines(tracesLines);
    //  }
    //  _printLines(io_destination, tracesLines, i_lineSeparator);


        //More efficient from MulTEx 5:
        if(_jreHasExceptionChaining){ //from JDK 1.4:
            //Standard stack trace is already redundancy-free.
            //TODO; Unfortunately it is not complete, as it forgets legacy causes
            io_destination.append(tracesString);
        }else{  //on JDK 1.1 ... 1.3:
            //Standard printStackTrace() left redundant lines in stack traces
            //of causing exceptions!
            final java.util.Vector tracesLines //String_Vector
            = _splitIntoLines(tracesString);
            nullifyRedundantTraceTrailLines(tracesLines);
            _printLines(io_destination, tracesLines, i_lineSeparator);
        }

    }//appendCompactStackTrace
    //No longer necessary since JRE 1.4
    */

    /*package*/ static void appendCompactStackTrace(
        final StringBuffer io_destination, final Throwable i_throwable
    ){
        if(i_throwable==null){
            io_destination.append("null Throwable provided to ")
            .append(_className).append(".appendCompactStackTrace(...)");
            return;
        }
        _appendCompactStackTraceRecursively(
            io_destination, i_throwable, new StackTraceElement[0], 0
        );
    }//appendCompactStackTrace



    /**Appends the stack trace of i_reportee and all its chained cause exceptions
      to io_destination. All redundant location lines therein are suppressed.
      Usually you should not directly call this method, but a printStackTrace-method in
      class Msg.
     * @param level TODO

      @see Msg#printStackTrace(StringBuffer,Throwable,String)
      TODO: Should rely on JDK >= 1.4 and work with Throwable.getStackTrace()
            Will replace appendCompactStackTrace
    */
    /*package*/ static void _appendCompactStackTraceRecursively(
        final StringBuffer io_destination,
        final Throwable i_reportee,
        final StackTraceElement[] i_causeeElements, 
		final int level
    ){
        if(level >= _maxRecursionDepth){
            io_destination.append("... SEVERE: Exceeding maximum causal recursion depth of ");
            io_destination.append(_maxRecursionDepth);
            io_destination.append(".");
            io_destination.append(Util.lineSeparator);
            return;
        }
        final Throwable directCause = Util.getCause(i_reportee);
        final StackTraceElement[] reporteeElements = i_reportee.getStackTrace();

        //Recurse for causes:
        int causesCount = 0;
        //Recurse for direct cause:
        if(directCause!=null){
        	causesCount++;
            _appendCompactStackTraceRecursively(io_destination, directCause, reporteeElements, level+1);
        }
        //Recurse for Throwable parameters as causes: 
        recurseParameters: {
			if(!(i_reportee instanceof MultexException)){break recurseParameters;}
			final MultexException multexException = (MultexException)i_reportee;
			final Object[] params = multexException.getParameters();
			if(null==params){break recurseParameters;}
			for(int i=0; i<params.length; i++){
			    final Object param = params[i];
			    if(param instanceof Throwable){
			    	causesCount++;
			        final Throwable cause = (Throwable)param;
			        _appendCompactStackTraceRecursively(io_destination, cause, reporteeElements, level+1);
			    }
			}//for
		}//recurseParameters

        if(causesCount>0){
			io_destination.append(Util.wasCausing);
			io_destination.append(Util.lineSeparator);
		}
        appendCauseIndentation(io_destination, level);
        /*toString() is sometimes strangely redefined! E.g. at a SAXException with
         * a cause the toString() returns the toString() of its cause, so it does
         * not contain its own class name. Thus:
         */
        final String reporteeString = Util.toString(i_reportee);
        io_destination.append(reporteeString);
        io_destination.append(Util.lineSeparator);

        appendIrredundantTraceLines(
            io_destination,
            reporteeElements,
            i_causeeElements
        );
    }//appendCompactStackTrace



    /**Appends the head element sequence of i_reporteeElements,
     * each as a line, which is not contained in i_causeeElements.
     * E.g. if i_reporteeElements is [a,b,c] and i_causeeElements is [b,c],
     * then only a will be appended as a line to io_destination.
     * Note: non-private for testability.
     * @param io_destination Where to append, must not be null.
     * @param i_reporteeElements The elements which have to be appended, without those contained in i_causeeElements, too. 
     *          If null, a warning will be appended.
     * @param i_causeeElements The elements which will suppress appending equal elements of i_reporteeElements.
     *          If null, the complete array i_reporteeElements will be appended.
     * */
    /*package*/ static void appendIrredundantTraceLines(
        final StringBuffer io_destination,
        final StackTraceElement[] i_reporteeElements,
        final StackTraceElement[] i_causeeElements
    ){
        if(io_destination==null){
            throw new AssertionError("io_destination != null");
        }
        if(i_reporteeElements==null){
            io_destination.append(_className);
            io_destination.append(": No stack trace elements to append.");
            return;
        }        

        //Count down to first different stack trace element:
        final int lastSignificantIndex;
        {
            int ri = i_reporteeElements.length - 1;
            if (i_causeeElements != null) {
                for (int ci = i_causeeElements.length - 1; ri >= 0 && ci >= 0; ri--, ci--) {
                    final StackTraceElement re = i_reporteeElements[ri];
                    if (re == null || !re.equals(i_causeeElements[ci])) {
                        break;
                    }
                }
            }
            lastSignificantIndex = ri;
        }        

        //Now append all different elements as String:
        for(int i=0; i<=lastSignificantIndex; i++){
            io_destination.append("\tat ");
            io_destination.append(i_reporteeElements[i].toString());
            io_destination.append(Util.lineSeparator);
        }
    }

    /**Returns all user-provided information contained in the exception object.
    * Serves for {@link MultexException#getMessage}
    */
    /*package*/ static String getUserInformation(final MultexException i_exception) 
    {
        final String text = i_exception.getDefaultMessageTextPattern();
        //TODO Can we do it without creating a copy of the parameters[]? Knabe 07-09-12
        final Object[] parameters = i_exception.getParameters();
        final boolean hasText = text!=null;
        if(!hasText && parameters==null){return null;}

        final StringBuffer result = new StringBuffer();
        if(hasText){result.append(text);}
        if((parameters!=null) && parameters.length>0){
            final int lastIndex = parameters.length-1;
            for(int i=0; i<=lastIndex; i++){
                final Object parameter = parameters[i];
                //Do not report a Throwable as parameter. It is the task of Msg.printMessages to do that!
                if(parameter instanceof Throwable){continue;}
                //Suppress ending null parameter. Probably moveParameter0ToCauseIfNecessary was applied. 2007-09-12
                if(parameter==null && i==lastIndex){continue;}
                result.append(Util.lineSeparator);
                result.append("    ");
		        appendParam(result, Integer.toString(i), parameter);
            }//for
        }//if
        return result.toString();
    }

    /**Appends to io_buf the external representation "{i_name} = 'i_value'"
    */
    /*package*/ static void appendParam(
      final StringBuffer io_buf, final String i_name, final Object i_value
    ){
      io_buf.append("{");   io_buf.append(i_name);   io_buf.append("} = '");
      io_buf.append(i_value);   io_buf.append("'");
    }

    /**Reports to System.err, if the class of this exception object
    does not satisfy the conditions: <UL>
      <LI> end with i_suffix
    </UL>
    This checking should better occur statically before running a program
    and not dynamically each time an exception object is created.
    */
    /*package*/ static void checkClass(final Exception i_exception, final String i_suffix){
      //check, that class name ends with i_suffix:
      final Class exceptionClass = i_exception.getClass();
      final String exceptionName = exceptionClass.getName();
      if(!exceptionName.endsWith(i_suffix)){
        printErrorString("The name of subclass ");
        printErrorString(exceptionName);
        printErrorString(" of ");
        printErrorString(i_suffix);
        printErrorString(" should end in ");
        printErrorString(i_suffix);
        printErrorString("!");
        printErrorLine();
      }
    }

    /**Moves parameter 0 to the cause of the exception, if the parameter 0 is a Throwable.
     * @param <E> The type of a MulTEx exception
     * @param io_multexException the exception object
     * @param io_parameters its parameters. May be empty. If moved, the last parameter becomes null.
     */
    /*package*/ static <E extends Exception & MultexException> void shiftParameter0ToCauseIfNecessary(final E io_multexException, final Object[] io_parameters) {
        if(io_parameters.length==0){return;}
        final Object parameter0 = io_parameters[0];
        if(parameter0 instanceof Throwable){
            //Use parameter0 as the cause of this MultexException, setting the last parameter to null
            final Throwable cause = (Throwable)parameter0;
            io_multexException.initCause(cause);
            Util._shiftParametersLeftLeavingNull(io_parameters);
            //e.initParameters(io_parameters);
        }
    }

    /**Shifts each element of the array one position closer to 0. The last element becomes null.
     * @param io_parameters may be null. Then nothing is done.
     * */
    private static void _shiftParametersLeftLeavingNull(Object[] io_parameters) {
        final int length = io_parameters.length;
        if(length<1){
            return;
        }
        System.arraycopy(io_parameters, 1, io_parameters, 0, length-1);
        io_parameters[length-1] = null;
    }

    /*Commented out 2003-05-07: obsolete
    *
    /**Shortens the Vector io_tracesLines by setting the element at index
      i_fromIndex to the abbreviation symbol "at ..." and nullifying all
      elements after this.
    * /
    private static void _shortenLastTrace(
      final java.util.Vector io_tracesLines, final int i_fromIndex
    ){
      io_tracesLines.setElementAt("\tat ...", i_fromIndex);
      final int size = io_tracesLines.size();
      for(int i=i_fromIndex+1; i<size; i++){
        io_tracesLines.setElementAt(null, i);
      }
    }
    */

    /**Appends the non-null elements of i_lines each as a line to io_buffer,
     * each followed by i_lineSeparator
    private static void _printLines(
      final StringBuffer    io_buffer,
      final java.util.Vector i_lines,
      final String           i_lineSeparator
    ){
      final int size = i_lines.size();
      for(int i=0; i<size; i++){
        final Object line = i_lines.elementAt(i);
        if(line!=null){io_buffer.append(line);io_buffer.append(i_lineSeparator);}
      }
    }
    //No longer necessary from JRE 1.4, 2005-01-13
    **/

    /**Appends a cause indentation marking.
	 * @param io_destination where to append
	 * @param i_level How often to repeat the character
	 * @see #causeIndenter The character to append repeatedly
	 */
	/*package*/ static void appendCauseIndentation(final StringBuffer io_destination, final int i_level) {
		//append cause indentation:
		for(int i=0; i<i_level; i++){
		    io_destination.append(causeIndenter);
		}
	}




	/**Getter for the cause of an exception, by default the ReflectionCauseGetter*/
    private static CauseGetter _causeGetter = new ReflectionCauseGetter();

    /**The version number of the Java Runtime Environment, on which we are running*/
    private static final String  _jreVersion = System.getProperty("java.version");

    /**The minimum version of the JRE, on which this MulTEx version can be used.*/
    private static final String _minimumJreVersion = "1.4";

    /**Is true, if the JRE-version number is >= 1.4*/
    private static final boolean _jreHasExceptionChaining
    = _jreVersion.compareTo(_minimumJreVersion) >= 0;

    /**Returns the line separator for the actual platform.
      This is the line separator for the platform the Java virtual machine is
      running on. It is the value of the system property 'line.separator'.
	*/
	public static final String lineSeparator
	= System.getProperty("line.separator");

    /**The string used to delimit the stack trace part of a causing exception from
      the following stack trace of the caused exception.
    */
    public static final String wasCausing = "WAS CAUSING:";

	/**The char used to indent one level of causal chain when reporting the messages or stack trace of an exception chain.
	 * @since MulTEx 6d 2006-05-16
	 */
	public static final char causeIndenter = '+';
	
	/**The demonstration of action ''{0}'' failed by fault of person {1}.
	 * We will criticize him.*/ 
	public static final class MyDemoExc extends Exc {

		/**Constructor.*/
		public MyDemoExc(final String i_action, final String i_person) {
			super(null, i_action, i_person);
		}
		
	}


    private static final String _className = Util.class.getName();


    static {
        if(!Util._jreHasExceptionChaining){
            throw new RuntimeException("This version of MulTEx needs a Java Runtime Environment >= " + _minimumJreVersion + ", but runs on " + Util._jreVersion);
        }
    }


}//class
