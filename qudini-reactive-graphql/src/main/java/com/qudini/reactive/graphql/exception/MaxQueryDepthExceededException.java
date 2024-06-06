package com.qudini.reactive.graphql.exception;

public class MaxQueryDepthExceededException extends RuntimeException {

    public MaxQueryDepthExceededException(String message) {
        super(message);
    }
}
