package com.qudini.reactive.logging.aop;

import com.qudini.reactive.logging.Logged;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public final class DefaultJoinPointSerialiser implements JoinPointSerialiser {

    private static final Map<UniqueMethodSignature, LoggedMethod> CACHE = new ConcurrentHashMap<>();

    @Override
    public String serialise(JoinPoint joinPoint) {

        var loggedMethod = getLoggedMethod(joinPoint);
        var parameterNames = loggedMethod.getParameterNames();
        var parameterValues = joinPoint.getArgs();

        var output = new StringBuilder()
                .append(loggedMethod.getClassName())
                .append("#")
                .append(loggedMethod.getMethodName())
                .append("(");
        for (int i = 0, n = parameterNames.length; i < n; i++) {
            if (i == 0) {
                output.append("\n");
            }
            var parameterName = parameterNames[i];
            var parameterValue = parameterValues[i];
            output
                    .append("\t")
                    .append(parameterName)
                    .append(": ")
                    .append(loggedMethod.logParameter(i) ? parameterValue : "<excluded>")
                    .append("\n");
        }

        return output.append(")").toString();
    }

    private LoggedMethod getLoggedMethod(JoinPoint joinPoint) {
        var methodSignature = (MethodSignature) joinPoint.getSignature();
        var uniqueMethodSignature = new UniqueMethodSignature(methodSignature);
        return CACHE.computeIfAbsent(uniqueMethodSignature, this::buildLoggedMethod);
    }

    private LoggedMethod buildLoggedMethod(UniqueMethodSignature uniqueMethodSignature) {
        var methodSignature = uniqueMethodSignature.getMethodSignature();
        var method = methodSignature.getMethod();
        var className = method.getDeclaringClass().getSimpleName();
        var methodName = method.getName();
        var parameterNames = methodSignature.getParameterNames();
        var includedParameterIndexes = getIncludedParameterIndexes(method);
        return new LoggedMethod(className, methodName, parameterNames, includedParameterIndexes);
    }

    private Set<Integer> getIncludedParameterIndexes(Method method) {
        var parameterAnnotations = method.getParameterAnnotations();
        var includedParameterIndexes = new HashSet<Integer>();
        for (int i = 0, n = parameterAnnotations.length; i < n; i++) {
            var annotations = parameterAnnotations[i];
            var log = Stream.of(annotations).map(Annotation::annotationType).noneMatch(Logged.Exclude.class::equals);
            if (log) {
                includedParameterIndexes.add(i);
            }
        }
        return includedParameterIndexes;
    }

}
