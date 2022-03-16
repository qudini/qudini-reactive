package com.qudini.reactive.graphql.http;

import com.qudini.reactive.logging.Log;
import graphql.ExceptionWhileDataFetching;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ResponseStatusException;
import reactor.util.context.Context;
import reactor.util.context.ContextView;

import java.util.concurrent.CompletableFuture;

@Slf4j
public final class LoggingContextAwareExceptionHandler implements DataFetcherExceptionHandler {

    @Override
    public CompletableFuture<DataFetcherExceptionHandlerResult> handleException(DataFetcherExceptionHandlerParameters handlerParameters) {
        var exception = handlerParameters.getException();
        var sourceLocation = handlerParameters.getSourceLocation();
        var path = handlerParameters.getPath();
        var error = new ExceptionWhileDataFetching(path, exception, sourceLocation);
        ContextView context = handlerParameters.getDataFetchingEnvironment().getGraphQlContext().getOrDefault(Context.class, Context.empty());
        if (exception instanceof ResponseStatusException && ((ResponseStatusException) exception).getStatus().is4xxClientError()) {
            Log.withContext(context, () -> log.warn(error.getMessage(), exception));
        } else {
            Log.withContext(context, () -> log.error(error.getMessage(), exception));
        }
        var result = DataFetcherExceptionHandlerResult.newResult().error(error).build();
        return CompletableFuture.completedFuture(result);
    }

}
