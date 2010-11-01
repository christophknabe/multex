package multex.test; //TunnelingExceptionChain

import multex.Failure;

/**
 * Chain of exceptions for test purposes.
 * 
 * In the exception chain there are some instances of multex.Failure
 * without any own information. They should be suppressed in the
 * reporting of the messages.
 * 
 * @author Christoph Knabe  2004-11-05
 */
public class TunnelingExceptionChain {

public static final String ex3ClassName = "MyClass";
public static final ClassCastException ex3 = new ClassCastException(ex3ClassName);
public static final String couldNotLoadDiagram = "Could not load diagram ";
public static final String fromFile = " from file ";
public static final String diagramName = "Bank";
public static final String fileName = "bank.dia";
public static final String ex1Object = "Diagramm";
public static final long ex1Value = 999;

/**Constructs a chain of the exceptions: 
 * <OL>
 *   <LI>multex.Failure without info</LI>
 *   <LI>multex.test.InitFailure</LI>
 *   <LI>multex.Failure with info</LI>
 *   <LI>multex.Failure without info</LI>
 *   <LI>ClassCastException</LI>
 * </OL>
*/
public static Failure construct(){
	final Failure ex2 = new multex.Failure(
        couldNotLoadDiagram + "{0}" + fromFile + "{1}", new Failure(ex3), diagramName, fileName
    );
	final multex.test.InitFailure ex1 = new multex.test.InitFailure(ex2, ex1Object, ex1Value);
	return new Failure(ex1);
}


}
