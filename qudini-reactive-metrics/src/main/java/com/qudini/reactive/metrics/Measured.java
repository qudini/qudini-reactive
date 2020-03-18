package com.qudini.reactive.metrics;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(METHOD)
@Retention(RUNTIME)
public @interface Measured {

    String value();

    String description() default "histogram";

    boolean publishPercentileHistogram() default true;

    long minimumExpectedValueInNanos() default 5 * 1000;

    long maximumExpectedValueInNanos() default 10 * 1000 * 1000;

}
