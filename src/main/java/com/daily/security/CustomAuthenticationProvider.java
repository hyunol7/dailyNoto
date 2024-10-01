package com.daily.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CustomAuthenticationProvider  implements AuthenticationProvider{
	  @Autowired
	    private UserDetailsService userDetailsService;
	  private PasswordEncoder passwordEncoder;

	  @Override
	  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
	      String username = authentication.getName();  // 더 명확하게 username으로 명명
	      String password = (String) authentication.getCredentials();

	      UserDetails userDetails = userDetailsService.loadUserByUsername(username);

	      // UserDetails가 null인 경우는 loadUserByUsername에서 이미 UsernameNotFoundException을 던지므로 null 체크는 필요 없음
	      if (!passwordEncoder.matches(password, userDetails.getPassword())) {
	          throw new BadCredentialsException("Invalid username or password");
	      }

	      return new CustomAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	  }


	    @Override
	    public boolean supports(Class<?> authentication) {
	        return CustomAuthenticationToken.class.isAssignableFrom(authentication);
	    }
}
