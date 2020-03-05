package com.qudini.reactive.logging.aop;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

import static lombok.AccessLevel.NONE;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, doNotUseGetters = true)
public final class UniqueMethodSignature {

    private final MethodSignature methodSignature;

    @Getter(NONE)
    @EqualsAndHashCode.Include
    private final Method method;

    public UniqueMethodSignature(MethodSignature methodSignature) {
        this.methodSignature = methodSignature;
        this.method = methodSignature.getMethod();
    }

}
