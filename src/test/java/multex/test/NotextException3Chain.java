package multex.test;

/**
 * Chain of 3 exceptions for test purposes.
 * 
 * 2003-07-01 Knabe Exceptions made public
 * 
 * @author Christoph Knabe  2003-05-21
 */
public class NotextException3Chain {

public	static class MainFailure extends multex.Failure {
	MainFailure(final Throwable i_cause, final long i_long, final double i_double){
		super(null, i_cause, new Long(i_long), new Double(i_double));
	}
}
	
public	static class MidFailure extends multex.Failure {
	MidFailure(
		final Throwable i_cause, final String i_string
	){super("", i_cause, i_string);}
}

public static final String ex3Param = "MyClass";
public static final Throwable ex3 = new ClassCastException(ex3Param);
public static final String ex3Name = ex3.getClass().getName();
public static final String ex3Message = ex3Name + ": " + ex3Param;
public static final MidFailure ex2 = new MidFailure(ex3, "ABC");
public static final String ex2Name = ex2.getClass().getName();
public static	final MainFailure ex1 = new MainFailure(ex2, 9999, 3.14);
public static final String ex1Name = ex1.getClass().getName();


/**Constructs a chain of the 3 exceptions: 
 * {@link multex.test.NotextException3Chain.MainFailure}, 
 * {@link multex.test.NotextException3Chain.MidFailure}, 
 * ClassCastException.
 * @return the constructed exception chain
*/
public static Throwable construct(){
	return ex1;
}


}
