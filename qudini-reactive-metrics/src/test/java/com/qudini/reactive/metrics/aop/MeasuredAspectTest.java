package com.qudini.reactive.metrics.aop;

import com.qudini.reactive.metrics.Measured;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("MeasuredAspect")
class MeasuredAspectTest {

    private MeterRegistry registry;

    @BeforeEach
    void prepare() {
        registry = new SimpleMeterRegistry();
    }

    @Test
    @DisplayName("should measure a method returning a successful mono")
    void measureSuccessfulMono() {
        class Example {
            @Measured("test_metric_name")
            Mono<Integer> example() {
                return Mono.just(42);
            }
        }
        var proxy = proxy(new Example());
        var returnedValue = proxy.example().block();
        assertThat(returnedValue).isEqualTo(42);
        checkTimer(
                "test_metric_name",
                Tag.of("class_name", "com.qudini.reactive.metrics.aop.MeasuredAspectTest$1Example"),
                Tag.of("method_name", "example"),
                Tag.of("status", "success")
        );
    }

    @Test
    @DisplayName("should measure a method annotated with a custom annotation returning a mono")
    void measureCustomMono() {
        class Example {
            @CustomMeasured
            Mono<Integer> example() {
                return Mono.just(42);
            }
        }
        var proxy = proxy(new Example());
        var returnedValue = proxy.example().block();
        assertThat(returnedValue).isEqualTo(42);
        checkTimer(
                "test_custom_metric_name",
                Tag.of("class_name", "com.qudini.reactive.metrics.aop.MeasuredAspectTest$2Example"),
                Tag.of("method_name", "example"),
                Tag.of("status", "success")
        );
    }

    @Test
    @DisplayName("should measure a method returning an erroneous mono")
    void measureErroneousMono() {
        var expectedException = new IllegalStateException();
        class Example {
            @Measured("test_metric_name")
            Mono<Integer> example() {
                return Mono.error(expectedException);
            }
        }
        var proxy = proxy(new Example());
        var thrownException = assertThrows(
                Exception.class,
                () -> proxy.example().block()
        );
        assertThat(thrownException).isEqualTo(expectedException);
        checkTimer(
                "test_metric_name",
                Tag.of("class_name", "com.qudini.reactive.metrics.aop.MeasuredAspectTest$3Example"),
                Tag.of("method_name", "example"),
                Tag.of("status", "error")
        );
    }

    @Test
    @DisplayName("should measure a method returning a successful flux")
    void measureSuccessfulFlux() {
        class Example {
            @Measured("test_metric_name")
            Flux<Integer> example() {
                return Flux.just(42);
            }
        }
        var proxy = proxy(new Example());
        var returnedValue = proxy.example().blockLast();
        assertThat(returnedValue).isEqualTo(42);
        checkTimer(
                "test_metric_name",
                Tag.of("class_name", "com.qudini.reactive.metrics.aop.MeasuredAspectTest$4Example"),
                Tag.of("method_name", "example"),
                Tag.of("status", "success")
        );
    }

    @Test
    @DisplayName("should measure a method annotated with a custom annotation returning a flux")
    void measureCustomFlux() {
        class Example {
            @CustomMeasured
            Flux<Integer> example() {
                return Flux.just(42);
            }
        }
        var proxy = proxy(new Example());
        var returnedValue = proxy.example().blockLast();
        assertThat(returnedValue).isEqualTo(42);
        checkTimer(
                "test_custom_metric_name",
                Tag.of("class_name", "com.qudini.reactive.metrics.aop.MeasuredAspectTest$5Example"),
                Tag.of("method_name", "example"),
                Tag.of("status", "success")
        );
    }

    @Test
    @DisplayName("should measure a method returning an erroneous flux")
    void measureErroneousFlux() {
        var expectedException = new IllegalStateException();
        class Example {
            @Measured("test_metric_name")
            Flux<Integer> example() {
                return Flux.error(expectedException);
            }
        }
        var proxy = proxy(new Example());
        var thrownException = assertThrows(
                Exception.class,
                () -> proxy.example().blockLast()
        );
        assertThat(thrownException).isEqualTo(expectedException);
        checkTimer(
                "test_metric_name",
                Tag.of("class_name", "com.qudini.reactive.metrics.aop.MeasuredAspectTest$6Example"),
                Tag.of("method_name", "example"),
                Tag.of("status", "error")
        );
    }

    @Test
    @DisplayName("should measure a method with a synchronous return type")
    void measureSuccessfulSyncReturn() {
        class Example {
            @Measured("test_metric_name")
            int example() {
                return 42;
            }
        }
        var proxy = proxy(new Example());
        var returnedValue = proxy.example();
        assertThat(returnedValue).isEqualTo(42);
        checkTimer(
                "test_metric_name",
                Tag.of("class_name", "com.qudini.reactive.metrics.aop.MeasuredAspectTest$7Example"),
                Tag.of("method_name", "example"),
                Tag.of("status", "success")
        );
    }

    @Test
    @DisplayName("should measure a method annotated with a custom annotation with a synchronous return type")
    void measureCustomSyncReturn() {
        class Example {
            @CustomMeasured
            int example() {
                return 42;
            }
        }
        var proxy = proxy(new Example());
        var returnedValue = proxy.example();
        assertThat(returnedValue).isEqualTo(42);
        checkTimer(
                "test_custom_metric_name",
                Tag.of("class_name", "com.qudini.reactive.metrics.aop.MeasuredAspectTest$8Example"),
                Tag.of("method_name", "example"),
                Tag.of("status", "success")
        );
    }

    @Test
    @DisplayName("should measure a method with a synchronous return type throwing an exception")
    void measureErroneousSyncReturn() {
        var expectedException = new IllegalStateException();
        class Example {
            @Measured("test_metric_name")
            int example() {
                throw expectedException;
            }
        }
        var proxy = proxy(new Example());
        var thrownException = assertThrows(
                Exception.class,
                proxy::example
        );
        assertThat(thrownException).isEqualTo(expectedException);
        checkTimer(
                "test_metric_name",
                Tag.of("class_name", "com.qudini.reactive.metrics.aop.MeasuredAspectTest$9Example"),
                Tag.of("method_name", "example"),
                Tag.of("status", "error")
        );
    }

    @Test
    @DisplayName("should measure a method without a return type")
    void measureSyncVoid() {
        class Example {
            @Measured("test_metric_name")
            void example() {
            }
        }
        var proxy = proxy(new Example());
        proxy.example();
        checkTimer(
                "test_metric_name",
                Tag.of("class_name", "com.qudini.reactive.metrics.aop.MeasuredAspectTest$10Example"),
                Tag.of("method_name", "example"),
                Tag.of("status", "success")
        );
    }

    @Target(METHOD)
    @Retention(RUNTIME)
    @Measured("test_custom_metric_name")
    public @interface CustomMeasured {
    }

    private <T> T proxy(T target) {
        var factory = new AspectJProxyFactory(target);
        var aspect = new MeasuredAspect(registry);
        factory.addAspect(aspect);
        return factory.getProxy();
    }

    private void checkTimer(String name, Tag... tags) {
        var meters = registry.getMeters();
        assertThat(meters).hasSize(1);
        var meter = meters.get(0);
        assertThat(meter).isInstanceOf(Timer.class);
        var timer = (Timer) meter;
        var timerId = timer.getId();
        assertThat(timerId.getName()).isEqualTo(name);
        assertThat(timerId.getTags()).containsExactlyInAnyOrder(tags);
    }

}
