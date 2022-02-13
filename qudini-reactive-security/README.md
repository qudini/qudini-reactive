# qudini-reactive-security

Security-related utilities.

## Installation

```xml

<dependencies>
    <dependency>
        <groupId>com.qudini</groupId>
        <artifactId>qudini-reactive-security</artifactId>
    </dependency>
</dependencies>
```

## Configuration

This library will enable WebFlux
security (`org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity`) as well as the reactive
method security (`org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity`).

If authenticated, the principal's name (from `java.security.Principal.getName`) will be made available in the logging
MDC under the key `principal`.

### SecurityWebFilterChain

By default, the `SecurityWebFilterChain` will be configured as follows:

```java
.securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
        .csrf().disable()
        .requestCache().disable()
        .logout().disable()
        .addFilterAt(new AuthenticatingFilter(authenticationServices),SecurityWebFiltersOrder.AUTHENTICATION)
        .addFilterBefore(new AccessDeniedExceptionHandlingFilter(),SecurityWebFiltersOrder.AUTHORIZATION)
```

You can provide your custom `SecurityWebFilterChain` if needed, but we recommend keeping
an `com.qudini.reactive.security.web.AccessDeniedExceptionHandlingFilter` before `SecurityWebFiltersOrder.AUTHORIZATION`
so that `AccessDeniedException`s are successfully converted
into [403 FORBIDDEN](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/403)
or [401 UNAUTHORIZED](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/401) depending on whether the request is
authenticated or not.

### AuthenticatingFilter

`com.qudini.reactive.security.web.AuthenticatingFilter` is a lightweight authenticating filter, arguably easier to grasp
than the
usual [`org.springframework.security.authentication.AuthenticationManager`](https://spring.io/guides/topicals/spring-security-architecture)
from Spring Security, although less powerful and featured.

It works by providing implementations of `com.qudini.reactive.security.web.AuthenticationService`, which, given
a `ServerWebExchange`, return an `Authentication` that will be directly used in the reactive security context, so that
your usual authorisation checks can be run (e.g. with `@PreAuthorize`).

#### Example

```java

@Service
@RequiredArgsConstructor
public class MyHeaderBasedUserAuthenticationService implements AuthenticationService<MyUserAuthentication> {

    private final MyUserService myUserService;

    @Override
    public Mono<MyUserAuthentication> authenticate(ServerWebExchange exchange) {
        return myUserService
                .findUser(exchange.getRequest().getHeaders())
                .map(MyUserAuthentication::new);
    }

}
```

```java

@RequiredArgsConstructor
public final class MyUserAuthentication implements Authentication {

    private final MyUser myUser;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return myUser.getAuthorities();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return myUser;
    }

    @Override
    public Object getPrincipal() {
        return myUser.getUsername();
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new IllegalStateException();
    }

    @Override
    public String getName() {
        return myUser.getUsername();
    }

}
```

You can provide multiple authentication services: they will all be tested against the incoming requests.

If successful, the returned authentication will be made available via the
usual `org.springframework.security.core.context.ReactiveSecurityContextHolder.getContext`.

### CSRF

You can inject `com.qudini.reactive.security.web.csrf.CsrfVerifier` into your cookie-based authentication services to
protect against CSRF attacks, for example:

```java

@Service
@RequiredArgsConstructor
public class MyCookieBasedUserAuthenticationService implements AuthenticationService<MyUserAuthentication> {

    private final MyUserService myUserService;
    private final CsrfVerifier csrfVerifier;

    @Override
    public Mono<MyUserAuthentication> authenticate(ServerWebExchange exchange) {
        return myUserService
                .findUser(exchange.getRequest().getCookies)
                .doOnNext(x -> csrfVerifier.verify(exchange))
                .map(MyUserAuthentication::new);
    }

}
```

It works by ensuring the request brings the same value in both a header and a cookie, as explained
by [OWASP](https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html#double-submit-cookie)
.

The header that will be checked by default is `X-Xsrf-Token`, but can be modified via the `csrf.header-name` property.

The cookie that will be checked by default is `XSRF-TOKEN`, but can be modified via the `csrf.cookie-name` property.

### GrantedAuthorityDefaults

A bean of type `org.springframework.security.config.core.GrantedAuthorityDefaults` will be registered by default to
remove the `ROLE_` default prefix from the role names, but you can modify this behaviour by registering your own.
