package com.qudini.reactive.logging.aop;

import com.qudini.reactive.logging.Logged;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultJoinPointSerialiser")
class DefaultJoinPointSerialiserTest {

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    @InjectMocks
    private DefaultJoinPointSerialiser serialiser;

    @BeforeEach
    void prepareMock() {
        given(joinPoint.getSignature()).willReturn(methodSignature);
    }

    public void methodWithoutExclusion(int foo, int bar) {
    }

    @Test
    @DisplayName("should serialise all parameters when none is excluded")
    void withoutExclusion() throws Exception {
        var method = getClass().getDeclaredMethod("methodWithoutExclusion", int.class, int.class);
        given(methodSignature.getParameterNames()).willReturn(new String[]{"foo", "bar"});
        given(joinPoint.getArgs()).willReturn(new Object[]{1, 2});
        given(methodSignature.getMethod()).willReturn(method);
        var serialisedJoinPoint = serialiser.serialise(joinPoint);
        assertThat(serialisedJoinPoint).isEqualTo("DefaultJoinPointSerialiserTest#methodWithoutExclusion(\n\tfoo: 1\n\tbar: 2\n)");
    }

    public void methodWithExclusion(@Logged.Exclude int foo, int bar) {
    }

    @Test
    @DisplayName("should only serialise parameters that are not excluded")
    void withExclusion() throws Exception {
        var method = getClass().getDeclaredMethod("methodWithExclusion", int.class, int.class);
        given(methodSignature.getParameterNames()).willReturn(new String[]{"foo", "bar"});
        given(joinPoint.getArgs()).willReturn(new Object[]{1, 2});
        given(methodSignature.getMethod()).willReturn(method);
        var serialisedJoinPoint = serialiser.serialise(joinPoint);
        assertThat(serialisedJoinPoint).isEqualTo("DefaultJoinPointSerialiserTest#methodWithExclusion(\n\tfoo: <excluded>\n\tbar: 2\n)");
    }

    public void methodWithoutParameters() {
    }

    @Test
    @DisplayName("should serialise when the method has no parameter")
    void withoutParameters() throws Exception {
        var method = getClass().getDeclaredMethod("methodWithoutParameters");
        given(methodSignature.getParameterNames()).willReturn(new String[]{});
        given(joinPoint.getArgs()).willReturn(new Object[]{});
        given(methodSignature.getMethod()).willReturn(method);
        var serialisedJoinPoint = serialiser.serialise(joinPoint);
        assertThat(serialisedJoinPoint).isEqualTo("DefaultJoinPointSerialiserTest#methodWithoutParameters()");
    }

}
