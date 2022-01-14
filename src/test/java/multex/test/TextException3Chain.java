package multex.test;

/**
 * Chain of 3 exceptions for test purposes.
 * 
 * @author Christoph Knabe  2003-05-21
 */
public class TextException3Chain {

public static final String ex3ClassName = "MyClass";
public static final ClassCastException ex3 = new ClassCastException(ex3ClassName);
public static final String couldNotLoadDiagram = "Could not load diagram ";
public static final String fromFile = " from file ";
public static final String diagramName = "Bank";
public static final String fileName = "bank.dia";
public static final String ex1Object = "Diagramm";
public static final long ex1Value = 999;

/**Constructs a chain of the 3 exceptions: 
 * {@link multex.test.InitFailure}, {@link multex.Failure}, {@link ClassCastException}
 * @return the constructed exception chain
*/
public static multex.test.InitFailure construct(){
	final multex.Failure ex2 = new multex.Failure(
        couldNotLoadDiagram + "{0}" + fromFile + "{1}", ex3, diagramName, fileName
    );
	final multex.test.InitFailure ex1 = new multex.test.InitFailure(ex2, ex1Object, ex1Value);
	return ex1;
}


}
