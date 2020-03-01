package com.qudini.reactive.logging.aop;

import org.aspectj.lang.JoinPoint;

public interface JoinPointSerialiser {

    String serialise(JoinPoint joinPoint);

}
