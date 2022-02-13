package com.qudini.reactive.security.web.csrf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@DisplayName("CsrfVerifier")
class CsrfVerifierTest {

    private static final String HEADER_NAME = "X-Xsrf-Token";
    private static final String COOKIE_NAME = "XSRF-TOKEN";

    @Mock
    private ServerWebExchange exchange;

    private CsrfVerifier csrfService;

    private HttpHeaders headers;

    private MultiValueMap<String, HttpCookie> cookies;

    @BeforeEach
    void prepare() {
        headers = new HttpHeaders();
        cookies = new LinkedMultiValueMap<>();
        var request = mock(ServerHttpRequest.class);
        given(request.getHeaders()).willReturn(headers);
        given(request.getCookies()).willReturn(cookies);
        given(exchange.getRequest()).willReturn(request);
        csrfService = new CsrfVerifier(HEADER_NAME, COOKIE_NAME);
    }

    @Test
    @DisplayName("should fail if header is absent and cookie is absent")
    void shouldFailIfHeaderAbsentCookieAbsent() {
        var thrown = assertThrows(CsrfTokensNotFoundException.class, () -> csrfService.verify(exchange));
        assertThat(thrown.getMessage()).contains("Expected header 'X-Xsrf-Token' and cookie 'XSRF-TOKEN' to be present");
    }

    @Test
    @DisplayName("should fail if header is present but cookie is absent")
    void shouldFailIfHeaderPresentCookieAbsent() {
        addHeader("token");
        var thrown = assertThrows(CsrfTokensNotFoundException.class, () -> csrfService.verify(exchange));
        assertThat(thrown.getMessage()).contains("Expected header 'X-Xsrf-Token' and cookie 'XSRF-TOKEN' to be present");
    }

    @Test
    @DisplayName("should fail if header is absent but cookie is present")
    void shouldFailIfHeaderAbsentCookiePresent() {
        addCookie("token");
        var thrown = assertThrows(CsrfTokensNotFoundException.class, () -> csrfService.verify(exchange));
        assertThat(thrown.getMessage()).contains("Expected header 'X-Xsrf-Token' and cookie 'XSRF-TOKEN' to be present");
    }

    @Test
    @DisplayName("should fail if both header and cookie are present but not equal")
    void shouldFailIfBothPresentButNotEqual() {
        addHeader("foo");
        addCookie("bar");
        var thrown = assertThrows(CsrfTokensNotEqualException.class, () -> csrfService.verify(exchange));
        assertThat(thrown.getMessage()).contains("Expected header 'X-Xsrf-Token' and cookie 'XSRF-TOKEN' to be equal");
    }

    @Test
    @DisplayName("should pass if both header and cookie are present and equal")
    void shouldPassIfBothPresentAndEqual() {
        addHeader("token");
        addCookie("token");
        csrfService.verify(exchange);
    }

    private void addHeader(String value) {
        headers.add(HEADER_NAME, value);
    }

    private void addCookie(String value) {
        cookies.add(COOKIE_NAME, new HttpCookie(COOKIE_NAME, value));
    }

}
