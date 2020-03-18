package com.qudini.reactive.logging.aop;

import com.qudini.reactive.logging.Log;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.LoggerFactory;

@Aspect
@RequiredArgsConstructor
public class LoggedAspect {

    private final JoinPointSerialiser joinPointSerialiser;

    @Pointcut("@annotation(com.qudini.reactive.logging.Logged)")
    public void isAnnotated() {
    }

    @Pointcut("execution(reactor.core.publisher.Mono *(..))")
    public void returnsMono() {
    }

    @Pointcut("execution(reactor.core.publisher.Flux *(..))")
    public void returnsFlux() {
    }

    @Around("isAnnotated() && returnsMono()")
    public Object logMono(ProceedingJoinPoint joinPoint) {
        return Log
                .thenMono(() -> logAndProceed(joinPoint))
                .doOnEach(Log.onError(error -> logError(error, joinPoint)));
    }

    @Around("isAnnotated() && returnsFlux()")
    public Object logFlux(ProceedingJoinPoint joinPoint) {
        return Log
                .thenFlux(() -> logAndProceed(joinPoint))
                .doOnEach(Log.onError(error -> logError(error, joinPoint)));
    }

    @Around("isAnnotated() && !returnsMono() && !returnsFlux()")
    public Object logSynchronously(ProceedingJoinPoint joinPoint) {
        try {
            return logAndProceed(joinPoint);
        } catch (Throwable error) {
            logError(error, joinPoint);
            throw error;
        }
    }

    @SneakyThrows
    private <T> T logAndProceed(ProceedingJoinPoint joinPoint) {
        var serialisedJoinPoint = joinPointSerialiser.serialise(joinPoint);
        var signature = (MethodSignature) joinPoint.getSignature();
        var declaringType = signature.getDeclaringType();
        var logger = LoggerFactory.getLogger(declaringType);
        logger.info(serialisedJoinPoint);
        return (T) joinPoint.proceed();
    }

    private void logError(Throwable error, ProceedingJoinPoint joinPoint) {
        var signature = (MethodSignature) joinPoint.getSignature();
        var declaringType = signature.getDeclaringType();
        var logger = LoggerFactory.getLogger(declaringType);
        logger.error("{}#{} failed", declaringType.getName(), signature.getName(), error);
    }

}
