package multex; //ReflectionCauseGetter.java

import java.lang.reflect.Modifier;

//History:
//2002-07-01  Knabe  Developped from Jdk1_1CauseGetter
//2002-06-24  Knabe  Re-added java.sql.SQLException, added java.sql.SQLWarning
//2002-04-29  Knabe  Simpler Approach with built-in causes in JDK 1.4
//2001-04-19  Knabe  Moved here from inner class Failure.Jdk1_1CauseGetter

/**Describes, how to get the causing exception of any non-MulTEx-exception
  in any JDK supporting reflection.
  Reflection was introduced in JDK 1.1.
  This is now the default CauseGetter used by class {@link multex.Util} to get the cause
  of a Throwable.
  @see multex.Util#setCauseGetter(CauseGetter)
*/
public class ReflectionCauseGetter implements CauseGetter {
    
        
  /**Returns the cause of i_throwable.
    Firstly tries the method getCause(), which is the standard cause getter from
    JDK 1.4.
    Then uses reflection in order to try to get a causing Throwable.
    Then tries all public parameterless getter methods, which return a Throwable.
    TODO: Then should try all member fields of Throwable or a subtype.
    If any of such trials succeeeds in getting a Throwable object, which is not the
    same as i_throwable, then returns it.
    If none succeeds, returns null.
    @return The exception, which caused i_throwable to be created, or null if none is found.
  */
  public Throwable getCause(final Throwable i_throwable){

	//Use the standard JRE 1.4 and MulTEx getCause():
	{
	    final Throwable result = i_throwable.getCause();
	    if(
                result!=null //standard getCause() returned result, thus do not go into reflection!
                || i_throwable instanceof MultexException //A MultexException will not have legacy exception chaining
        ){
            return result;
        }
	}

    //Start using reflection:
    final Class throwableClass = i_throwable.getClass();

    //Check for public get-methods returning a Throwable:
    try{
      final java.lang.reflect.Method[] methods
      = throwableClass.getMethods();
      for(int i=0; i<methods.length; i++){
        final java.lang.reflect.Method method = methods[i];
        final String methodName = method.getName();
        final int modifiers = method.getModifiers();
        final Class[] parameterTypes = method.getParameterTypes();
        if(methodName.startsWith("get") && !methodName.equals("getCause")
          && Modifier.isPublic(modifiers)
          && !Modifier.isStatic(modifiers) && parameterTypes.length==0
        ){
          //looks like a getter
          final Throwable result = _getCause(i_throwable, method);
          if(result!=null){return result;}
        }
      }
    }catch(final Exception ex){if(_log){ex.printStackTrace();}}

    return null;
  }//getCause
  
  /**Gets the cause of i_throwable using i_getCauseMethod.
    @return The cause of i_throwable, if the result of successfully calling
      i_getCauseMethod on i_throwable is of type Throwable, otherwise null.
  */
  private static Throwable _getCause(
    final Throwable i_throwable, final java.lang.reflect.Method i_getCauseMethod
  ){
    try{
        final Class returnType = i_getCauseMethod.getReturnType();
        if(!Throwable.class.isAssignableFrom(returnType)){return null;}
        //Here returnType is a Throwable
        if(!i_getCauseMethod.isAccessible()){i_getCauseMethod.setAccessible(true);}
        final Throwable result = (Throwable)i_getCauseMethod.invoke(i_throwable, null);
        if(result==i_throwable){return null;} //identical exception object cannot be a cause
        return result;
    }catch(Exception ex){if(_log){ex.printStackTrace();}}
    return null;
  }//_getCause

  /**Enables logging of exceptions occuring when trying to get a cause of an
    exception. Set it to false for production usage, but to true for development of MulTEx.
  */
  private static final boolean _log = true;

  
}//ReflectionCauseGetter
