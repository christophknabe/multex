package multex; //CauseGetter.java

//History:
//2001-04-19  Knabe  Moved here from inner class Failure.CauseGetter

/**Describes, how to get the causing exception of a caused Throwable, which is
  not a subclass of {@link Failure}
*/
public interface CauseGetter {
  /**Returns the causing Throwable object of an i_throwable other than Failure,
    or null if not known
  */
  Throwable getCause(Throwable i_throwable);
}
