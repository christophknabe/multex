/*
 * Created on 14.04.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package multex;


/**
 * Used for handling otherwise uncaught RuntimeException / Error in AWT/Swing.
 * Interface for the signature of the handle-method called by the
 * private method handleException of java.awt.EventDispatchThread.
 * 
 * @see Awt#setAwtExceptionHandlerClass(AwtExceptionHandler)
 * @see <a href="http://www.jguru.com/faq/view.jsp?EID=427279">JGuru discussion</a>
 */
public interface AwtExceptionHandler {
    /**
     * Called by AWT/Swing when a RuntimeException / Error is propagated to the EventDispatchThread.
     * @param i_throwable the thrown Throwable
     */
    public void handle(final Throwable i_throwable);
}