# qudini-reactive-metrics

Provides an easy integration of custom metrics, as well as health checks following the Kubernetes kubelet design. 

## Installation

```xml
<dependencies>
    <dependency>
        <groupId>com.qudini</groupId>
        <artifactId>qudini-reactive-metrics</artifactId>
    </dependency>
    <!-- Depending on your monitoring systems: -->
    <dependency>
        <groupId>io.micrometer</groupId>
        <artifactId>micrometer-registry-prometheus</artifactId>
    </dependency>
</dependencies>
```

## Configuration

You can leave the defaults, everything will just work out of the box. You can also reconfigure it to match your requirements, as explained in the following sections.

### Build info

A `Gauge` meter named `${prefix}_build_info` will be registered to expose the build name and version via tags. You can specify gauge name prefix, defaulted to `app`:

```yaml
metrics:
  build-info:
    gauge-name-prefix: yourprefix
```

These two values will be read from `com.qudini.reactive.utils.metadata.MetadataService` ([see the defaults and how to override them](https://github.com/qudini/qudini-reactive/tree/master/qudini-reactive-utils)).

### Probes

A new `liveness` HTTP endpoint will be registered, mapped to `GET /liveness` on the main server port, simply returning 200 OK with an empty body.

The `readiness` and `metrics` endpoints should be exposed via Spring Boot Actuator on a separate management port though, as they could expose sensitive data.

Below is our recommended configuration of Spring Boot Actuator, especially if orchestrated by Kubernetes:

```yaml
server:
  # the main "public" server port
  port: ${SERVICE_APP_PORT:8080}

management:
  server:
    # use a separate "private" management port for Spring Boot Actuator:
    port: ${SERVICE_MNG_PORT:8081}
  endpoint:
    health:
      # make the health (aka readiness) probe display all details:
      show-details: always
  endpoints:
    web:
      # change the base path to "/":
      base-path: /
      exposure:
        # expose health (aka readiness),
        # expose the monitoring system you're using (e.g. Prometheus):
        include: health,prometheus
      path-mapping:
        # remap /health to /readiness
        health: readiness
        # remap /prometheus to /metrics
        prometheus: metrics
```

This will allow having the following endpoints available:

- On the public port:

    - `http://localhost:8080/`: your main app
    - `http://localhost:8080/liveness`: your public liveness probe

- On the private port (exposed by Spring Boot Actuator):

    - `http://localhost:8081/readiness`: your private readiness probe
    - `http://localhost:8081/metrics`: your private metrics, ready to be scraped

### Security

If you're using Spring Security, you may want to have custom rules for the probes. If should be the case, you can use `com.qudini.reactive.metrics.security.ProbesMatcher` to match them, for example:

```java
@EnableWebFluxSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange()
                .matchers(ProbesMatcher.probes()).permitAll()
                .anyExchange().authenticated()
                .and().build();
    }

}
```

By default, the paths `/liveness`, `/readiness` and `/metrics` will be allowed. If you used other ones, you can specify
them via `com.qudini.reactive.metrics.security.ProbesPaths` when creating the `ProbesMatcher`:

```java
ProbesPaths paths = ProbesPaths
    .builder()
    .liveness("/your-liveness")
    .readiness("/your-readiness")
    .metrics("/your-metrics")
    .build();
ProbesMatcher matcher = ProbesMatcher.probes(paths);
```

## Usage

### com.qudini.reactive.metrics.Measured 

Annotating a method with `@Measured` will make it available in the metrics endpoint via a `Timer` meter.

Three tags will be automatically added:

- `class_name` valued with the name of the class,
- `method_name` valued with the name of the method,
- `status`: either `success` or `error` depending on whether the method ended in error.

#### Example

```java
@Component
public class YourClass {

    @Measured("yourapp_duration")
    public Mono<String> yourMethod() {
        return ...;
    }

}
```

Prometheus output:

```text
# HELP yourapp_duration_seconds histogram
# TYPE yourapp_duration_seconds histogram
yourapp_duration_seconds_bucket{class_name="your.package.to.YourClass",method_name="yourMethod",status="success",le="0.005",} 4.0
yourapp_duration_seconds_bucket{class_name="your.package.to.YourClass",method_name="yourMethod",status="success",le="0.005592405",} 4.0
...
yourapp_duration_seconds_bucket{class_name="your.package.to.YourClass",method_name="yourMethod",status="success",le="5.0",} 4.0
yourapp_duration_seconds_bucket{class_name="your.package.to.YourClass",method_name="yourMethod",status="success",le="+Inf",} 4.0
yourapp_duration_seconds_count{class_name="your.package.to.YourClass",method_name="yourMethod",status="success",} 4.0
yourapp_duration_seconds_sum{class_name="your.package.to.YourClass",method_name="yourMethod",status="success",} 0.004546575
# HELP yourapp_duration_seconds_max histogram
# TYPE yourapp_duration_seconds_max gauge
yourapp_duration_seconds_max{class_name="your.package.to.YourClass",method_name="yourMethod",status="success",} 0.004407308
```

#### Diagrams

You can easily build RED diagrams with PromQL out of the above metrics.

##### Rate (per minute)

```text
sum(rate(yourapp_duration_seconds_count{class_name="your.package.to.YourClass",method_name="yourMethod"}[1m])*60 or 0*up)
```

##### Error rate (per minute)

```text
sum(rate(yourapp_duration_seconds_count{class_name="your.package.to.YourClass",method_name="yourMethod",status="error"}[1m])*60 or 0*up)
```

##### 50% percentile (aka average)

```text
histogram_quantile(0.50, sum(rate(yourapp_duration_seconds_bucket{class_name="your.package.to.YourClass",method_name="yourMethod"}[1m])) by (le))
```

##### 95% percentile (match your SLOs/SLAs)

```text
histogram_quantile(0.95, sum(rate(yourapp_duration_seconds_bucket{class_name="your.package.to.YourClass",method_name="yourMethod"}[1m])) by (le))
```

#### Customising the timer

As you can see above, histograms are published by default, with a minimum expected value of 5ms and a maximum expected value of 5s.

You can override those defaults via the annotation attributes:

```java
@Measured(
    value = "yourapp_duration",
    // below are the defaults you can override:
    description = "histogram",
    publishPercentileHistogram = true,
    minimumExpectedValueInMillis = 5,
    maximumExpectedValueInMillis = 5 * 1000
)
```

#### Using custom annotations

You can use custom annotations if you're feeling you're repeating yourself:

```java
@Target(METHOD)
@Retention(RUNTIME)
@Measured(value = "yourapp_duration", publishPercentileHistogram = false)
public @interface YourAppMeasured {
}
```

Then:

```java
@Component
public class YourClass {

    @YourAppMeasured
    public Mono<String> yourMethod() {
        return ...;
    }

}
```
