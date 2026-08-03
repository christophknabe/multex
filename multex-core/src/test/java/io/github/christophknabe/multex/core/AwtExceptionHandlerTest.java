package io.github.christophknabe.multex.core;

import org.junit.Test;

/**
 * @since 9.0 at 2026-07-28
 */
public final class AwtExceptionHandlerTest {

    @Test public void registerAwtExceptionHandler(){
        final Thread.UncaughtExceptionHandler exceptionHandler = new AwtExceptionHandler(null);
        Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);
    }

}