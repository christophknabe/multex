package io.github.christophknabe.multex.tool;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be placed on subtypes of MulTEx <code>Exc</code> and <code>Failure</code>.
 * It will be ignored or produce errors when placed on other types.
 * @since 9.0 at 2026-07-29
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TextPattern {

    /** The value of this annotation has to be a message text pattern to be formatted by class {@link java.text.MessageFormat} */
    String value();

}
