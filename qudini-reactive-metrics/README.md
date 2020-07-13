# qudini-reactive-metrics

Provides an easy integration of custom metrics, as well as health checks following the Kubernetes kubelet design. 

## Installation

```xml
<dependencies>
    <dependency>
        <groupId>com.qudini</groupId>
        <artifactId>qudini-reactive-metrics</artifactId>
        <version>${qudini-reactive.version}</version>
    </dependency>
    <!-- Depending on your monitoring system: -->
    <dependency>
        <groupId>io.micrometer</groupId>
        <artifactId>micrometer-registry-prometheus</artifactId>
    </dependency>
</dependencies>
```

## Configuration

You can leave the defaults, everything will just work out of the box. You can also reconfigure it to match your requirements, as explained in the following sections.

### Build info

A `Gauge` meter named `${prefix}_build_info` will be registered to expose the build name and version via tags. You can specify gauge name prefix:

```yaml
metrics:
  build-info:
    gauge-name-prefix: yourprefix
```

By default, those two values will be read from the JAR file's manifest, respectively `Implementation-Title` and `Implementation-Version`.

You can override this behaviour by registering a component implementing `com.qudini.reactive.metrics.buildinfo.BuildInfoService`.

### Probes

A new `liveness` endpoint will be registered, simply returning 200 OK. It will be mapped to `/liveness` on the main server port.

You can also expose it via Spring Boot Actuator.

Below is our recommended configuration of Spring Boot Actuator, especially if orchestrated by Kubernetes:

```yaml
server:
  # the main "public" server port
  port: ${SERVICE_APP_PORT:8080}

management:
  server:
    # use a different "private" port for Spring Boot Actuator:
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
        # expose liveness, added by qudini-reactive-metrics,
        # expose health (aka readiness),
        # expose the monitoring system you're using (Prometheus here):
        include: liveness,health,prometheus
      path-mapping:
        # remap /health to /readiness
        health: readiness
        # remap /prometheus to /metrics
        prometheus: metrics
        # liveness already mapped to /liveness
```

If run locally, this will allow having the following endpoints available:

- On the public port:

    - `http://localhost:8080/`: your main app
    - `http://localhost:8080/liveness`: your public liveness probe

- On the private port, exposed by Spring Boot Actuator:

    - `http://localhost:8081/liveness`: your liveness probe
    - `http://localhost:8081/readiness`: your readiness probe
    - `http://localhost:8081/metrics`: your metrics, ready to be scraped

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
@Measured("yourapp_duration", publishPercentileHistogram = false)
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
