/*
 * Created on 14.04.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package io.github.christophknabe.multex.core;


/**
 * Used for handling otherwise uncaught RuntimeException / Error in AWT/Swing.
 * Interface for the signature of the uncaughtException method called by the
 * java.awt.EventDispatchThread.
 * 
 * @see Thread#setDefaultUncaughtExceptionHandler(Thread.UncaughtExceptionHandler)
 * @see <a href="http://www.jguru.com/faq/view.jsp?EID=427279">JGuru discussion</a>
 */
public class AwtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final java.awt.Component ownerHook;

    /**Registers the Component as to be blocked during AWT exception reporting.
     * @param ownerHook The Component to be blocked during message reporting */
    public AwtExceptionHandler(final java.awt.Component ownerHook) {
        this.ownerHook = ownerHook;
    }

    /**
     * Called by AWT/Swing when a RuntimeException / Error is propagated to the EventDispatchThread.
     * @param t The Thread on which the Throwable was thrown
     * @param e the thrown Throwable
     */
    @Override
    public void uncaughtException(final Thread t, final Throwable e) {
        Awt.report(ownerHook, e);
    }

}