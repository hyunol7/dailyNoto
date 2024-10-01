package com.daily.security;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomAuthenticationToken  implements Authentication {
	private final UserDetails userDetails;
    private final Object credentials;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomAuthenticationToken(UserDetails userDetails, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        this.userDetails = userDetails;
        this.credentials = credentials;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getDetails() {
        return userDetails;
    }

    @Override
    public Object getPrincipal() {
        return userDetails;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        // No-op
    }

    @Override
    public String getName() {
        return userDetails.getUsername();
    }
}
