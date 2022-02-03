package com.qudini.reactive.security.support;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Set;

public final class AnonymousAuthentication implements Authentication {

    private static final long serialVersionUID = 1L;

    private static final Collection<SimpleGrantedAuthority> AUTHORITIES = Set.of();

    @Override
    public Collection<SimpleGrantedAuthority> getAuthorities() {
        return AUTHORITIES;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        return false;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new IllegalArgumentException();
    }

    @Override
    public String getName() {
        return null;
    }

}
