package com.qudini.reactive.metrics;

import io.micrometer.core.instrument.Timer;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>Annotating a method with <code>@Measured</code> will make it available
 * in the Spring Boot Actuator metrics endpoint via a {@link Timer} meter.</p>
 * <p>Three tags will be automatically added:</p>
 * <ul>
 * <li><code>class_name</code> valued with the name of the class,</li>
 * <li><code>method_name</code> valued with the name of the method,</li>
 * <li><code>status</code>: either <code>success</code> or <code>error</code> depending on whether the method ended in error.</li>
 * </ul>
 * <p>Example:</p>
 * <pre><code>
 * &#64;Component
 * public class YourClass {
 *
 *     &#64;Measured("yourapp_duration")
 *     public Mono&lt;String&gt; yourMethod() {
 *         return ...;
 *     }
 *
 * }
 * </code></pre>
 * <p>By default, histograms are published, with a minimum expected value of 5ms and a maximum expected value of 5s.</p>
 * <p>This can be overridden via attributes:</p>
 * <pre><code>
 * &#64;Measured(
 *     value = "yourapp_duration",
 *     // below are the defaults you can override:
 *     description = "histogram",
 *     publishPercentileHistogram = true,
 *     minimumExpectedValueInMillis = 5,
 *     maximumExpectedValueInMillis = 5 * 1000
 * )
 * </code></pre>
 * <p>You can use custom annotations if you're feeling you're repeating yourself:</p>
 * <pre><code>
 * &#64;Target(METHOD)
 * &#64;Retention(RUNTIME)
 * &#64;Measured("yourapp_duration", publishPercentileHistogram = false)
 * public &#64;interface YourAppMeasured {
 * }
 * </code></pre>
 * <p>Then:</p>
 * <pre><code>
 * &#64;Component
 * public class YourClass {
 *
 *     &#64;YourAppMeasured
 *     public Mono&lt;String&gt; yourMethod() {
 *         return ...;
 *     }
 *
 * }
 * </code></pre>
 */
@Target({METHOD, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface Measured {

    String value();

    String description() default "histogram";

    boolean publishPercentileHistogram() default true;

    long minimumExpectedValueInMillis() default 5;

    long maximumExpectedValueInMillis() default 5 * 1000;

}
