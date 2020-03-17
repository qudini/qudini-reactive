package com.qudini.reactive.sqs.listener;

import com.qudini.reactive.logging.Log;
import com.qudini.reactive.logging.ReactiveLoggingContextCreator;
import com.qudini.reactive.sqs.SqsListener;
import com.qudini.reactive.sqs.message.SqsMessageChecker;
import com.qudini.reactive.utils.MoreTuples;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;

import javax.annotation.PreDestroy;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static com.qudini.reactive.utils.MoreTuples.onBoth;
import static com.qudini.reactive.utils.MoreTuples.onLeftWhen;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toUnmodifiableMap;

@Slf4j
public final class DefaultSqsListeners implements SqsListeners {

    private final Map<String, SqsListener<?>> listeners;

    private final SqsAsyncClient sqsClient;

    private final SqsMessageChecker sqsMessageChecker;

    private final ReactiveLoggingContextCreator reactiveLoggingContextCreator;

    private final Flux<Void> flux;

    private Disposable disposable;

    public DefaultSqsListeners(Collection<SqsListener<?>> listeners, SqsAsyncClient sqsClient, SqsMessageChecker sqsMessageChecker, ReactiveLoggingContextCreator reactiveLoggingContextCreator) {
        this.listeners = listeners.stream().collect(toUnmodifiableMap(SqsListener::getQueueName, identity()));
        this.sqsClient = sqsClient;
        this.sqsMessageChecker = sqsMessageChecker;
        this.reactiveLoggingContextCreator = reactiveLoggingContextCreator;
        this.flux = prepare();
    }

    @Override
    public synchronized void start() {
        if (disposable == null) {
            this.disposable = flux.subscribe();
            log.info("SQS listeners started");
        } else {
            throw new IllegalStateException("SQS listeners already started");
        }
    }

    private Flux<Void> prepare() {
        return Mono
                .just(listeners)
                .flatMapIterable(Map::entrySet)
                .map(MoreTuples::fromEntry)
                .flatMap(onLeftWhen(this::getQueueUrl))
                .flatMap(onBoth(this::startPolling));
    }

    private Mono<String> getQueueUrl(String queueName) {
        return Mono
                .fromFuture(() -> {
                    var getQueueUrlRequest = GetQueueUrlRequest.builder()
                            .queueName(queueName)
                            .build();
                    return sqsClient.getQueueUrl(getQueueUrlRequest);
                })
                .map(GetQueueUrlResponse::queueUrl)
                .doOnEach(Log.onError(error -> log.error("Unable to get the URL of the queue named {}, long polling will not start", queueName, error)))
                .onErrorResume(error -> Mono.empty());
    }

    private Flux<Void> startPolling(String queueUrl, SqsListener<?> listener) {
        return sqsMessageChecker
                .checkForMessages(queueUrl, listener)
                .doOnEach(Log.onError(error -> log.error("An error occurred while checking for messages for queue {}, long polling will keep going", queueUrl, error)))
                .onErrorResume(error -> Mono.empty())
                .subscriberContext(context -> context.putAll(reactiveLoggingContextCreator.create(Optional.empty(), Map.of())))
                .repeat();
    }

    @PreDestroy
    @Override
    public synchronized void stop() {
        if (null != disposable && !disposable.isDisposed()) {
            disposable.dispose();
            log.info("SQS listeners stopped");
        } else {
            log.info("SQS listeners already stopped");
        }
        disposable = null;
    }

}
