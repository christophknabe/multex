package io.github.christophknabe.multex.core.test;

import io.github.christophknabe.multex.core.Failure;

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
 * {@link InitFailure}, {@link Failure}, {@link ClassCastException}
 * @return the constructed exception chain
*/
public static InitFailure construct(){
	final Failure ex2 = new Failure(
        couldNotLoadDiagram + "{0}" + fromFile + "{1}", ex3, diagramName, fileName
    );
	final InitFailure ex1 = new InitFailure(ex2, ex1Object, ex1Value);
	return ex1;
}


}
