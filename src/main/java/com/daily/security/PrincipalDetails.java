package com.daily.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.daily.entity.User;

import java.util.Collection;
import java.util.Collections;

public class PrincipalDetails implements UserDetails {

    private final User user;

    public PrincipalDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한을 반환합니다. 여기서는 간단한 역할을 추가하고 있습니다.
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();  // username 반환
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;  // 계정 만료 여부를 확인할 로직을 추가할 수 있습니다.
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;  // 계정 잠금 여부를 확인할 로직을 추가할 수 있습니다.
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // 비밀번호 만료 여부를 확인할 로직을 추가할 수 있습니다.
    }

    @Override
    public boolean isEnabled() {
        return true;  // 계정 활성화 여부를 확인할 로직을 추가할 수 있습니다.
    }

    // 사용자에 대한 getter 메서드 추가
    public User getUser() {
        return user;
    }
    
    public String getNickname() {
        return user.getNickname();
    }
    
    public String getLoginId() {
        return user.getLoginId();  // Ensure this is correctly fetched
    }

    
    public String getRole() {
        return user.getRole().name();
    }
}
