package com.qudini.reactive.logging;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>Annotate a type or a method with <code>@Logged</code> to make it logged when starting, and if an error occurs.</p>
 * <p>Only public methods returning either a mono or a flux will be matched.</p>
 * <p>Example:</p>
 * <pre><code>
 * public class YourClass {
 *
 *     &#64;Logged
 *     public Mono&lt;String&gt; yourMethod(String foobar, &#64;Logged.Exclude String email) {
 *         return ...;
 *     }
 *
 * }
 * </code></pre>
 * <p>Logged message:</p>
 * <pre>{@literal
 * YourClass#yourMethod(
 * 	foobar: "the value the method received"
 * 	email:  <excluded>
 * )
 * }</pre>
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
public @interface Logged {

    /**
     * <p>Excludes a parameter from the logged message, see {@link Logged}.</p>
     */
    @Target(PARAMETER)
    @Retention(RUNTIME)
    @interface Exclude {
    }

}