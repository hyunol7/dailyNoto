package com.daily.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.daily.entity.User;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	 private PrincipalDetailsService principalDetailsService;

    @Autowired
    public SecurityConfig(PrincipalDetailsService principalDetailsService) {
        this.principalDetailsService = principalDetailsService;
    }

    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .requestMatchers("/diary/detail/**").authenticated()  // '/diary/detail/' 경로는 인증된 사용자만 접근 가능
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                .anyRequest().permitAll()
            .and()
            .formLogin()
                .loginPage("/login")  // 사용자 정의 로그인 페이지
                .permitAll()
            .and()
            .logout()
                .permitAll();
            http
        .authorizeRequests()
        .requestMatchers("/upload/**").permitAll()  // '/upload/**' 경로는 인증 없이 접근 가능
        .anyRequest().authenticated()  // 그 외의 경로는 인증 필요
    .and()
    .formLogin()
        .loginPage("/login")
        .permitAll()
    .and()
    .logout()
        .permitAll();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()  // CSRF 보호 비활성화
            .authorizeRequests()
                .requestMatchers("/diary/detail/**").authenticated()  // '/diary/detail/' 경로는 인증된 사용자만 접근 가능
                .requestMatchers("/upload/**").permitAll()  // '/upload/**' 경로는 인증 없이 접근 가능
                .requestMatchers("/security-login/admin/**").hasAuthority(User.UserRole.ADMIN.name())  // 관리자 권한 필요
                .anyRequest().permitAll()
            .and()
            .formLogin()
                .usernameParameter("loginId")
                .passwordParameter("password")
                .loginPage("/login")
                .defaultSuccessUrl("/main", true)
                .failureUrl("/login/login")
                .permitAll()
            .and()
            .logout()
                .logoutSuccessUrl("/main")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll();

        return http.build();
    }

    
    

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
