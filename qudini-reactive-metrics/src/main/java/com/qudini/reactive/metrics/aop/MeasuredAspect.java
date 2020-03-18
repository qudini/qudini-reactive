package com.qudini.reactive.metrics.aop;

import com.qudini.reactive.metrics.Measured;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.lang.System.nanoTime;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

@Aspect
@RequiredArgsConstructor
public class MeasuredAspect {

    private static final Map<Method, Measured> CACHE = new ConcurrentHashMap<>();

    private final MeterRegistry registry;

    @Pointcut("@annotation(com.qudini.reactive.metrics.Measured)")
    public void isAnnotatedWithStandardAnnotation() {
    }

    @Pointcut("execution(@(@com.qudini.reactive.metrics.Measured *) * *(..))")
    public void isAnnotatedWithCustomAnnotation() {
    }

    @Pointcut("execution(reactor.core.publisher.Mono *(..))")
    public void returnsMono() {
    }

    @Pointcut("execution(reactor.core.publisher.Flux *(..))")
    public void returnsFlux() {
    }

    @Around("isAnnotatedWithStandardAnnotation() && returnsMono()")
    public Object measureMonoWithStandardAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {
        return measureMono(joinPoint, getStandardAnnotation(joinPoint));
    }

    @Around("isAnnotatedWithStandardAnnotation() && returnsFlux()")
    public Object measureFluxWithStandardAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {
        return measureFlux(joinPoint, getStandardAnnotation(joinPoint));
    }

    @Around("isAnnotatedWithStandardAnnotation() && !returnsMono() && !returnsFlux()")
    public Object measureSynchronouslyWithStandardAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {
        return measureSynchronously(joinPoint, getStandardAnnotation(joinPoint));
    }

    @Around("isAnnotatedWithCustomAnnotation() && returnsMono()")
    public Object measureMonoWithCustomAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {
        return measureMono(joinPoint, getCustomAnnotation(joinPoint));
    }

    @Around("isAnnotatedWithCustomAnnotation() && returnsFlux()")
    public Object measureFluxWithCustomAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {
        return measureFlux(joinPoint, getCustomAnnotation(joinPoint));
    }

    @Around("isAnnotatedWithCustomAnnotation() && !returnsMono() && !returnsFlux()")
    public Object measureSynchronouslyWithCustomAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {
        return measureSynchronously(joinPoint, getCustomAnnotation(joinPoint));
    }

    private Measured getStandardAnnotation(ProceedingJoinPoint joinPoint) {
        return getAnnotation(joinPoint, this::findStandardAnnotation);
    }

    private Measured getCustomAnnotation(ProceedingJoinPoint joinPoint) {
        return getAnnotation(joinPoint, this::findCustomAnnotation);
    }

    private Measured getAnnotation(ProceedingJoinPoint joinPoint, Function<Method, Measured> finder) {
        var signature = (MethodSignature) joinPoint.getSignature();
        return CACHE.computeIfAbsent(signature.getMethod(), finder);
    }

    private Measured findStandardAnnotation(Method method) {
        return method.getAnnotation(Measured.class);
    }

    private Measured findCustomAnnotation(Method method) {
        return Stream
                .of(method.getAnnotations())
                .map(Annotation::annotationType)
                .map(annotation -> annotation.getAnnotation(Measured.class))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private Object measureMono(ProceedingJoinPoint joinPoint, Measured measured) throws Throwable {
        var start = nanoTime();
        return ((Mono<?>) joinPoint.proceed())
                .doOnSuccess(x -> recordSuccess(joinPoint, measured, start))
                .doOnError(x -> recordError(joinPoint, measured, start));
    }

    private Object measureFlux(ProceedingJoinPoint joinPoint, Measured measured) throws Throwable {
        var start = nanoTime();
        return ((Flux<?>) joinPoint.proceed())
                .doOnComplete(() -> recordSuccess(joinPoint, measured, start))
                .doOnError(x -> recordError(joinPoint, measured, start));
    }

    private Object measureSynchronously(ProceedingJoinPoint joinPoint, Measured measured) throws Throwable {
        var start = nanoTime();
        try {
            var returnedValue = joinPoint.proceed();
            recordSuccess(joinPoint, measured, start);
            return returnedValue;
        } catch (Throwable error) {
            recordError(joinPoint, measured, start);
            throw error;
        }
    }

    private void recordSuccess(ProceedingJoinPoint joinPoint, Measured measured, long start) {
        record(joinPoint, measured, start, "success");
    }

    private void recordError(ProceedingJoinPoint joinPoint, Measured measured, long start) {
        record(joinPoint, measured, start, "error");
    }

    private void record(ProceedingJoinPoint joinPoint, Measured measured, long start, String status) {
        var nanos = nanoTime() - start;
        var signature = (MethodSignature) joinPoint.getSignature();
        var declaringType = signature.getDeclaringType();
        Timer
                .builder(measured.value())
                .description(measured.description())
                .tag("class_name", declaringType.getName())
                .tag("method_name", signature.getName())
                .tag("status", status)
                .publishPercentileHistogram(measured.publishPercentileHistogram())
                .minimumExpectedValue(Duration.ofNanos(measured.minimumExpectedValueInNanos()))
                .maximumExpectedValue(Duration.ofNanos(measured.maximumExpectedValueInNanos()))
                .register(registry)
                .record(nanos, NANOSECONDS);
    }

}
