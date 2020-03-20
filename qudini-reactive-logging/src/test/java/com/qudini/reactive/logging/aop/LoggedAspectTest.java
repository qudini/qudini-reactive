package com.qudini.reactive.logging.aop;

import com.qudini.reactive.logging.Logged;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoggedAspect")
class LoggedAspectTest {

    @Mock
    private JoinPointSerialiser joinPointSerialiser;

    @Mock
    private Logger logger;

    @Test
    @DisplayName("should log a method returning a successful mono")
    void logMonoSuccess() {
        class Example {
            @Logged
            Mono<Integer> example() {
                return Mono.just(42);
            }
        }
        var proxy = proxy(new Example());
        given(joinPointSerialiser.serialise(any())).willReturn("foobar");
        var returnedValue = proxy.example().block();
        assertThat(returnedValue).isEqualTo(42);
        verify(logger, times(1)).info("foobar");
        verify(logger, never()).error(any(), (Object[]) any());
    }

    @Test
    @DisplayName("should log a method returning an erroneous mono")
    void logMonoError() {
        var expectedException = new IllegalStateException();
        class Example {
            @Logged
            Mono<Integer> example() {
                return Mono.error(expectedException);
            }
        }
        var proxy = proxy(new Example());
        given(joinPointSerialiser.serialise(any())).willReturn("foobar");
        var thrownException = assertThrows(
                Exception.class,
                () -> proxy.example().block()
        );
        assertThat(thrownException).isEqualTo(expectedException);
        verify(logger, times(1)).info("foobar");
        verify(logger, times(1)).error(
                "{}#{} failed",
                "com.qudini.reactive.logging.aop.LoggedAspectTest$2Example",
                "example",
                expectedException
        );
    }

    @Test
    @DisplayName("should log a method returning a successful flux")
    void logFluxSuccess() {
        class Example {
            @Logged
            Flux<Integer> example() {
                return Flux.just(42);
            }
        }
        var proxy = proxy(new Example());
        given(joinPointSerialiser.serialise(any())).willReturn("foobar");
        var returnedValue = proxy.example().blockLast();
        assertThat(returnedValue).isEqualTo(42);
        verify(logger, times(1)).info("foobar");
        verify(logger, never()).error(any(), (Object[]) any());
    }

    @Test
    @DisplayName("should log a method returning an erroneous flux")
    void logFluxError() {
        var expectedException = new IllegalStateException();
        class Example {
            @Logged
            Flux<Integer> example() {
                return Flux.error(expectedException);
            }
        }
        var proxy = proxy(new Example());
        given(joinPointSerialiser.serialise(any())).willReturn("foobar");
        var thrownException = assertThrows(
                Exception.class,
                () -> proxy.example().blockLast()
        );
        assertThat(thrownException).isEqualTo(expectedException);
        verify(logger, times(1)).info("foobar");
        verify(logger, times(1)).error(
                "{}#{} failed",
                "com.qudini.reactive.logging.aop.LoggedAspectTest$4Example",
                "example",
                expectedException
        );
    }

    @Test
    @DisplayName("should log a method with a synchronous return type")
    void logSyncSuccess() {
        class Example {
            @Logged
            int example() {
                return 42;
            }
        }
        var proxy = proxy(new Example());
        given(joinPointSerialiser.serialise(any())).willReturn("foobar");
        var returnedValue = proxy.example();
        assertThat(returnedValue).isEqualTo(42);
        verify(logger, times(1)).info("foobar");
        verify(logger, never()).error(any(), (Object[]) any());
    }

    @Test
    @DisplayName("should log a method with a synchronous return type throwing an exception")
    void logSyncError() {
        var expectedException = new IllegalStateException();
        class Example {
            @Logged
            int example() {
                throw expectedException;
            }
        }
        var proxy = proxy(new Example());
        given(joinPointSerialiser.serialise(any())).willReturn("foobar");
        var thrownException = assertThrows(
                Exception.class,
                proxy::example
        );
        assertThat(thrownException).isEqualTo(expectedException);
        verify(logger, times(1)).info("foobar");
        verify(logger, times(1)).error(
                "{}#{} failed",
                "com.qudini.reactive.logging.aop.LoggedAspectTest$6Example",
                "example",
                expectedException
        );
    }

    @Test
    @DisplayName("should log a method without a return type")
    void logSyncVoid() {
        class Example {
            @Logged
            void example() {
            }
        }
        var proxy = proxy(new Example());
        given(joinPointSerialiser.serialise(any())).willReturn("foobar");
        proxy.example();
        verify(logger, times(1)).info("foobar");
        verify(logger, never()).error(any(), (Object[]) any());
    }

    private <T> T proxy(T target) {
        var factory = new AspectJProxyFactory(target);
        var aspect = new LoggedAspect(joinPointSerialiser, x -> logger);
        factory.addAspect(aspect);
        return factory.getProxy();
    }

}
