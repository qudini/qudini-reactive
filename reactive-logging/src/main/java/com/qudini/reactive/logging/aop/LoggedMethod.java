package com.qudini.reactive.logging.aop;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import static lombok.AccessLevel.NONE;

@RequiredArgsConstructor
@Getter
public final class LoggedMethod {

    private final String className;

    private final String methodName;

    private final String[] parameterNames;

    @Getter(NONE)
    private final Set<Integer> includedParameterIndexes;

    public boolean logParameter(int index) {
        return includedParameterIndexes.contains(index);
    }

}
