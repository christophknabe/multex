package multex.demo; //MyAwtExceptionHandler.java

import multex.Awt;
import multex.AwtExceptionHandler;

/**General exception handler for reporting exceptions during AWT event dispatching.
 * 
 * @author Christoph Knabe 2005-05-14
 */
public class MyAwtExceptionHandler implements AwtExceptionHandler {

	private static java.awt.Component _component;

    /**Registers the Component as to be blocked during AWT exception reporting*/
    public static void setComponentToBeBlocked(final java.awt.Component io_component){
        _component = io_component;
    }
    
    /* (non-Javadoc)
	 * @see multex.AwtExceptionHandler#handle(java.lang.Throwable)
	 */
	public void handle(final Throwable i_throwable) {
        Awt.report(_component, i_throwable);
	}

}
