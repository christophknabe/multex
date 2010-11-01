package multex; //Assertion.java

//History:
//2007-09-05  Knabe  Java 5: Using Object... for exception parameters.
//2001-04-19  Knabe  Added parameterized check methods,
//                   AssertionFailure as outer class.
//2000-09-29  Knabe  Creation

/**Tool for checking unexpected programming errors*/
public class Assertion {

    
/**Do not create objects of me!*/
private Assertion(){ /*Do not create objects of me!*/ }


/**Checks, that an assertion condition holds.
  <H3>Note:</H3>
  If you want to give diagnostic parameters which have to be created newly,
  e.g. to encapsulate a value of a primitive type, then you should yourself
  write the code e.g.<PRE>
*   if(!condition){
*     throw new AssertionFailure(name, stringPar1, new Integer(intPar2));
*   }
  </PRE>
  This should not be committed to a check-Method, because creating a new
  capsule object for each parameter of a primitive type involves to much
  overhead in non-error cases, too.
  @param i_condition The condition, which must be true.
  @param i_name The condition, which failed, as a String.
  @param i_parameters Additional diagnostic parameters, e.g. values of variables in the assertion condition
  @throws AssertionFailure The condition did not hold
*/
public static void check(
  final boolean i_condition, final String i_name, Object... i_parameters
)throws AssertionFailure {
  if(!i_condition){
    throw new AssertionFailure(i_name, i_parameters);
  }
}//check

/**Convenience check method without parameters.
  @see #check(boolean, String, Object...)
*/
public static void check(
  final boolean i_condition, final String i_name
)throws AssertionFailure {
  check(i_condition, i_name, (Object[])null);
}//check


}//Assertion