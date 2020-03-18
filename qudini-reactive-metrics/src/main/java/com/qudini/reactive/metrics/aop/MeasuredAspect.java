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

import java.time.Duration;

import static java.lang.System.nanoTime;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

@Aspect
@RequiredArgsConstructor
public class MeasuredAspect {

    private final MeterRegistry registry;

    @Pointcut("@annotation(com.qudini.reactive.metrics.Measured)")
    public void isAnnotated() {
    }

    @Pointcut("execution(reactor.core.publisher.Mono *(..))")
    public void returnsMono() {
    }

    @Pointcut("execution(reactor.core.publisher.Flux *(..))")
    public void returnsFlux() {
    }

    @Around("isAnnotated() && returnsMono()")
    public Object measureMono(ProceedingJoinPoint joinPoint) throws Throwable {
        var start = nanoTime();
        return ((Mono<?>) joinPoint.proceed())
                .doOnSuccess(x -> recordSuccess(joinPoint, start))
                .doOnError(x -> recordError(joinPoint, start));
    }

    @Around("isAnnotated() && returnsFlux()")
    public Object measureFlux(ProceedingJoinPoint joinPoint) throws Throwable {
        var start = nanoTime();
        return ((Flux<?>) joinPoint.proceed())
                .doOnComplete(() -> recordSuccess(joinPoint, start))
                .doOnError(x -> recordError(joinPoint, start));
    }

    @Around("isAnnotated() && !returnsMono() && !returnsFlux()")
    public Object measureSynchronously(ProceedingJoinPoint joinPoint) throws Throwable {
        var start = nanoTime();
        try {
            var returnedValue = joinPoint.proceed();
            recordSuccess(joinPoint, start);
            return returnedValue;
        } catch (Throwable error) {
            recordError(joinPoint, start);
            throw error;
        }
    }

    private void recordSuccess(ProceedingJoinPoint joinPoint, long start) {
        record(joinPoint, start, "success");
    }

    private void recordError(ProceedingJoinPoint joinPoint, long start) {
        record(joinPoint, start, "error");
    }

    private void record(ProceedingJoinPoint joinPoint, long start, String status) {
        var nanos = nanoTime() - start;
        var signature = (MethodSignature) joinPoint.getSignature();
        var declaringType = signature.getDeclaringType();
        var measured = signature.getMethod().getAnnotation(Measured.class);
        Timer
                .builder(measured.value())
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
