package multex.test;


/**
 * @author Christoph Knabe 2003-05-21
 */
public class InitFailure extends multex.Failure {

	public static final String cannotInitObject = "Cannot initialize object ";
	public static final String withValue = " with value ";
	public static final String dot = ".";

	public InitFailure(
	    final Throwable i_cause, final String i_object, final long i_value
	){
		super(cannotInitObject + "{0}" + withValue + "{1}" + dot, 
		    i_cause, i_object, new Long(i_value)
		);
	}
	

}
