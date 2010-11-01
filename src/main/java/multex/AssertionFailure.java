package multex; //AssertionFailure.java

//History:
//2007-09-05  Knabe  Java 5: Using Object... for exception parameters.
//2001-04-19  Knabe  Now as standalone class AssertionFailure
//2000-09-29  Knabe  Creation as inner class of Assertion

/**Indicates that an assertion failed. An assertion should always be true.
  Any failing of an assertion thus has to be considered as a programming error.
  You should use assertions for indicating the violation of invariants or
  postconditions. For indicating the violation of a precondition you should
  better use an individually named subclass of {@link multex.Exc}.
*/
public class AssertionFailure extends multex.Failure {

    
    private static final long serialVersionUID = 8638198842468540210L;

/**Creates an AssertionFailure object. If all diagnostic parameters of the
    assertion are Object-s, i.e. of non-primitive types, it is more
    convenient and still efficient to call {@link Assertion#check(boolean, String, Object...)}.
    @param i_name The condition, which failed, as a String
    @param i_parameters Additional diagnostic parameters, e.g. values of variables in the assertion condition
  */
  public AssertionFailure(final String i_name, final Object... i_parameters)
  {
    super("Failure of assertion " + i_name,
      (Throwable)null, i_parameters
    );
  }

  /**Creates an AssertionFailure object without parameters.
    @see #AssertionFailure(String, Object...)
  */
  public AssertionFailure(final String i_name){this(i_name,(Object[])null);}

  
}//AssertionFailure
