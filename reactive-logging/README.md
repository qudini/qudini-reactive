# Logging utilities

## Usage

If no `X-Amzn-Trace-Id` header is found in the incoming request, one be be generated based on the `spring.application.name` property, for example:

```
spring:
    application:
        name: TasksService
```

If not specified, `UnknownIssuer` will be used.

---

If you implement `com.qudini.services.core.logging.mdc.MerchantIdExtractor` and make it available as a Spring bean, then the returned merchant id will be available in the logs too. The default implementation is a no-op.

---

To log from the different Mono/Flux stages, use `com.qudini.services.core.logging.com.qudini.reactive.logging.Log`, for example:

```
Mono.just(1)
    .doOnEach(com.qudini.reactive.logging.Log.onSubscribe(() -> log.info("subscribe")))
    .doOnEach(com.qudini.reactive.logging.Log.onNext(value -> log.info("next {}", value)))
    .doOnEach(com.qudini.reactive.logging.Log.onError(throwable -> log.error("error", throwable)))
    .doOnEach(com.qudini.reactive.logging.Log.onComplete(() -> log.info("complete")))
```

This will take care of populating the MDC with the correlation id and the optional merchant id for you.

You can also log when the reactive stream starts with:

```
com.qudini.reactive.logging.Log.onStart(() -> log.info("start"))
    .then(...)
```

---

You can also annotate a Spring bean or its methods with `@com.qudini.services.core.logging.Logged`.

If the method is `public` and returns either a `Mono` or a `Flux`, then it will automatically be logged `onStart` and `onError`.

If you don't want some parameters to be logged (for example if it can hold PII), then annotate it with `@com.qudini.services.core.logging.Logged.Exclude`.

---

When making an HTTP call to another service, use `com.qudini.services.core.logging.CorrelationId#forward` to forward the correlation id, for example:

```
Mono
    .just(webClient.get().uri(uri))
    .flatMap(CorrelationId::forward)
    .flatMap(WebClient.RequestHeadersSpec::exchange)
```

---

If you have a reactive stream that isn't linked to an HTTP context, you can still provide tracing information:

```
@Inject
private final MdcPopulator mdcPopulator;

public Mono<String> foobar() {
    return Mono
        .just("foobar")
        ...
        .subscriberContext(mdcPopulator.createContext(Optional.of(merchantId)));
}
```

A correlation id will be generated and stored in the MDC context map so that usual logging functions can still be used.

---

The default configuration in file log4j2.xml is automatically loaded.
If you want to use the default Spring log configuration add the code below to `application.yaml` file.

```
logging:
  config: spring-default
  level:
    com:
      qudini: DEBUG
```
