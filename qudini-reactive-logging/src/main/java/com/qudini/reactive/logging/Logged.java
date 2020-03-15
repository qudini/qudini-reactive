package com.qudini.reactive.logging;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>Annotate a type or a method with `@Logged` to make it logged when starting, and if an error occurs.</p>
 * <p>Only public methods returning either a mono or a flux will be matched.</p>
 * <p>Example:
 * <pre>{@literal
 * public class YourClass {
 *
 *     @Logged
 *     public Mono<String> yourMethod(String foobar, @Logged.Exclude String email) {
 *         return ...;
 *     }
 *
 * }
 * }</pre>
 * </p>
 * <p>Logged message:
 * <pre>{@literal
 * YourClass#yourMethod(
 * 	foobar: "the value the method received"
 * 	email:  <excluded>
 * )
 * }</pre>
 * </p>
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
public @interface Logged {

    /**
     * Excludes a parameter from the logged message, see {@link Logged}.
     */
    @Target(PARAMETER)
    @Retention(RUNTIME)
    @interface Exclude {
    }

}