# qudini-reactive-logging

Helps logging in a reactive context.

## Installation

```xml
<dependencies>
    <dependency>
        <groupId>com.qudini</groupId>
        <artifactId>qudini-reactive-logging</artifactId>
        <version>${qudini-reactive.version}</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
        <exclusions>
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-logging</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-aop</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-log4j2</artifactId>
    </dependency>
</dependencies>
```

## Usage

You can leave the defaults, everything will just work out of the box. You can also reconfigure it to match your requirements, as explained in the following sections.

### JSON structured logging

By default, a custom Log4j 2 configuration will be used, with:

- an asynchronous root logger,
- the log level set from the environment variable `LOG_LEVEL`, defaulting to `INFO`,
- a console appender targeting `SYSTEM_OUT`,
- a custom JSON layout.

The JSON layout will produce logs according to the following format:

```json
{
  "timestamp": "<UTC instant ISO formatted>",
  "log_level": "<log level>",
  "thread": "<thread name>",
  "logger_name": "<logging name>",
  "message": "<logged message>",
  "stacktrace": "<error stacktrace if any>",
  "<mdc key 1>": "<mdc value 1>",
  "<mdc key 2>": "<mdc value 2>",
  "...": "..."
}
```

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

### Correlation id

By default, a `WebFilter` is registered to prepare the [Mapped Diagnostic Context](http://www.slf4j.org/manual.html#mdc):

- it tries to extract a correlation id from the incoming request headers (or generates one if none is found),
- then extracts additional logging context properties from the request,
- and finally populates the reactive subscriber context. 

#### Request header name

The default request header name that will be looked for is AWS [`X-Amzn-Trace-Id`](https://docs.aws.amazon.com/elasticloadbalancing/latest/application/load-balancer-request-tracing.html), but this can be overridden:

```yaml
logging:
  correlation-id:
    header-name: X-My-Header-Name
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
    prefix: MyAppName=
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

By default, no additional logging context will be extracted from the incoming request, but you can implement `com.qudini.reactive.logging.web.LoggingContextExtractor` if you need more domain-specific properties to be available.

### Reactive context creation

By default, the reactive context will be populated with a `Map<String, String>` mapped to the key `"LOGGING_MDC"`, that will used to populate the MDC when logging.

Inside the MDC, the correlation id will be mapped to a key named `"correlation_id"`. 

If you kept the default Log4J 2 configuration, you will then have the correlation id available in the logs via the JSON property `correlation_id`.  

You can change this behaviour by registering a component implementing `com.qudini.reactive.logging.ReactiveLoggingContextCreator`.

Injecting `ReactiveLoggingContextCreator` can be useful to populate the reactive context manually, when a stream isn't started by an HTTP request (e.g. a CRON job).

### Log


