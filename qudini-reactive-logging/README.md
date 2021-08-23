# qudini-reactive-logging

Fixes logging in a reactive stream, by handling the MDC inside the reactive context.

## Installation

```xml
<dependencies>
    <dependency>
        <groupId>com.qudini</groupId>
        <artifactId>qudini-reactive-logging</artifactId>
    </dependency>
    <!-- Depending on your monitoring systems: -->
    <dependency>
        <groupId>com.newrelic.agent.java</groupId>
        <artifactId>newrelic-api</artifactId>
    </dependency>
    <dependency>
        <groupId>io.sentry</groupId>
        <artifactId>sentry</artifactId>
    </dependency>
</dependencies>
```

## Configuration

You can leave the defaults, everything will just work out of the box. You can also reconfigure it to match your requirements, as explained in the following sections.

### Log4j 2

By default, a custom Log4j 2 configuration will be used, with:

- an asynchronous root logger,
- the log level set from the environment variable `LOG_LEVEL`, defaulting to `INFO`,
- a console appender targeting `SYSTEM_OUT`, using a custom JSON layout,
- a custom appender that pushes errors to third-party error trackers.

If you want to use the default configuration Spring provides, update your `application.yml` with the following (might be useful for development environment):

```yaml
logging:
  config: default
```

If you want to use another custom configuration of yours, use:

```yaml
logging:
  config: classpath:your-log4j2-config.xml
```

### JSON structured logging

The JSON layout will produce logs according to the following format:

```json
{
  "env": "<environment>",
  "build_version": "<build version>",
  "timestamp": "<UTC instant ISO formatted>",
  "level": "<logging level>",
  "thread": "<thread name>",
  "logger": "<logger name>",
  "message": "<logged message>",
  "stacktrace": "<error stacktrace if any>",
  "<mdc key 1>": "<mdc value 1>",
  "<mdc key 2>": "<mdc value 2>",
  "...": "..."
}
```

The properties `env` and `build_version` will be populated thanks to `com.qudini.reactive.utils.metadata.MetadataService` once available ([see the defaults and how to override them](https://github.com/qudini/qudini-reactive/tree/master/qudini-reactive-utils)). 

### Correlation id

By default, a `WebFilter` is registered to prepare the [Mapped Diagnostic Context](http://www.slf4j.org/manual.html#mdc):

- it tries to extract a correlation id from the incoming request headers (or generates one if none is found),
- then extracts additional logging context properties from the request,
- and finally populates the reactive subscriber context.

#### Request header name

The default request header name that will be looked for is [AWS `X-Amzn-Trace-Id`](https://docs.aws.amazon.com/elasticloadbalancing/latest/application/load-balancer-request-tracing.html), but this can be overridden:

```yaml
logging:
  correlation-id:
    header-name: X-Your-Header-Name
```

If no matching request header is found, a correlation id will be generated instead.

#### Correlation id generation

The default generator mimics AWS algorithm by generating a correlation id following the format:

```
<optional-prefix><version>-<time>-<id>
```

You can specify the optional prefix:

```yaml
logging:
  correlation-id:
    prefix: YourAppName=
```

You can also use a custom generator by registering a component implementing `com.qudini.reactive.logging.correlation.CorrelationIdGenerator`.

#### Forwarding the correlation id

If you need to forward the correlation id to outgoing requests, you can inject `CorrelationIdForwarder`:

```java
var request = WebClient.create().post();
return correlationIdForwarder
    .forwardOn(request)
    .flatMap(WebClient.RequestHeadersSpec::exchange);
```

The default implementation will write a header named according to the one used to extract the correlation id from the incoming request.

You can provide your own implementation by registering a component implementing `com.qudini.reactive.logging.web.CorrelationIdForwarder`.

### Additional logging context properties

By default, no additional logging context will be extracted from the incoming request.
 
You can register a component implementing `com.qudini.reactive.logging.web.LoggingContextExtractor` if you need more domain-specific properties to be available in the MDC (you'll have access to the incoming HTTP request).

### Third-party error trackers

If the JDK of a supported third-party error tracker is found in the classpath, logs at level `ERROR` or above will be pushed to them.

#### NewRelic

If `com.newrelic.api.agent.NewRelic` is found in the classpath, errors will be pushed to [NewRelic](https://newrelic.com/) via `NewRelic.noticeError(errorOrMessage, params)`.

#### Sentry

If `io.sentry.Sentry` is found in the classpath, errors will be pushed to [Sentry](https://sentry.io/) via `Sentry.captureEvent(event)`.

Sentry's `environment` and `release` will be populated thanks to `com.qudini.reactive.utils.metadata.MetadataService` ([see the defaults and how to override them](https://github.com/qudini/qudini-reactive/tree/master/qudini-reactive-utils)). 

### Reactive context creation

By default, the reactive context will be populated with a `Map<String, String>` mapped to the key `"LOGGING_MDC"`, that will used to populate the MDC when logging.

Inside the MDC, the correlation id will be mapped to a key named `"correlation_id"`. 

If you kept the default Log4J 2 configuration, you will then have the correlation id available in the logs via the JSON property `correlation_id`, plus any other property you may have added via your implementation of `LoggingContextExtractor`.

You can change this behaviour by registering a component implementing `com.qudini.reactive.logging.ReactiveLoggingContextCreator`.

Injecting `ReactiveLoggingContextCreator` can be useful to populate the reactive context manually, when a reactive stream isn't started by an HTTP request (e.g. a CRON job).

## Usage

### com.qudini.reactive.logging.Log

Makes the MDC available when logging.

#### Starting

```java
Mono<Integer> example() {
    return Log.then(() -> {
        log.debug("foobar");
        return 42;
    });
}

Mono<Integer> example() {
    return Log.thenFuture(() -> {
        log.debug("foobar");
        return CompletableFuture.completedFuture(42);
    });
}

Mono<Integer> example() {
    return Log.thenMono(() -> {
        log.debug("foobar");
        return Mono.just(42);
    });
}

Flux<Integer> example() {
    return Log.thenIterable(() -> {
        log.debug("foobar");
        return List.of(42);
    });
}

Flux<Integer> example() {
    return Log.thenFlux(() -> {
        log.debug("foobar");
        return Flux.fromStream(Stream.of(42));
    });
}
```

#### Mapping

```java
Mono<Integer> example(Mono<String> mono) {
    return mono.flatMap(Log.then(s -> {
        log.debug("s:{}", s);
        return 42;
    }));
}

Mono<Integer> example(Mono<String> mono) {
    return mono.flatMap(Log.thenMono(s -> {
        log.debug("s:{}", s);
        return Mono.just(42);
    }));
}

Flux<Integer> example(Mono<String> mono) {
    return mono.flatMapMany(Log.thenIterable(s -> {
        log.debug("s:{}", s);
        return List.of(42);
    }));
}

Flux<Integer> example(Mono<String> mono) {
    return mono.flatMapMany(Log.thenFlux(s -> {
        log.debug("s:{}", s);
        return Flux.fromStream(Stream.of(42));
    }));
}
```

#### Consuming

```java
Mono<String> example(Mono<String> mono) {
    return mono.doOnEach(Log.onNext(s -> log.debug("s:{}", s)));
}

Mono<String> example(Mono<String> mono) {
    return mono.doOnEach(Log.onError(e -> log.debug("An error occurred", e)));
}

Mono<String> example(Mono<String> mono) {
    return mono.doOnEach(Log.onError(YourException.class, e -> log.debug("Your exception occurred", e)));
}

Mono<String> example(Mono<String> mono) {
    return mono.doOnEach(Log.onComplete(() -> log.debug("Completed")));
}

Mono<String> example(Mono<String> mono) {
    return mono.doOnEach(Log.on(Signal::isOnSubscribe, (value, error) -> log.debug("subscribed with value:{} error:{}", value, error)));
}
```

#### Getting the correlation id

```java
Mono<Integer> example(Context context) {
    return Log
            .getCorrelationId()
            .defaultIfEmpty("N/A")
            .doOnNext(correlationId -> store(correlationId))
            .then(Mono.just(42));
}
```

#### Synchronous

```java
void example(Context context) {
    Log.withContext(context, () -> log.debug("foobar"));
}

int example(Context context) {
    return Log.withContext(context, () -> {
        log.debug("foobar");
        return 42;
    });
}
```

### com.qudini.reactive.logging.Logged

Annotate a method with `@Logged` to make it logged when starting, and if an error occurs.

Parameters will be logged too, but you can exclude them by annotating them with `@Logged.Exclude`.

#### Example

```java
@Component
public class YourClass {

    @Logged
    public Mono<String> yourMethod(String foobar, @Logged.Exclude String email) {
        return ...;
    }

}
```

Logged message:

```
YourClass#yourMethod(
	foobar: "the value the method received"
	email:  <excluded>
)
```
