package org.emerald.comicapi.config;

import org.emerald.comicapi.model.data.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    AuthenticationSuccessHandler authenticationSuccessHandler;
    @Autowired
    AuthenticationFailureHandler authenticationFailureHandler;
    @Autowired
    AuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    LogoutSuccessHandler logoutSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin().loginProcessingUrl("/api/users/login")
            .usernameParameter("username").passwordParameter("password")
            .successHandler(authenticationSuccessHandler)
            .failureHandler(authenticationFailureHandler)
            .and()
            .logout().logoutRequestMatcher(new AntPathRequestMatcher("/api/users/logout", "POST"))
            .logoutSuccessHandler(logoutSuccessHandler)
            .deleteCookies("SESSION", "remember-me")
            .and()
            .rememberMe()
            .and()
            .authorizeRequests()
            .antMatchers("/api/users/register").permitAll()
            .antMatchers("/api/users/admin-creation").permitAll()
            .antMatchers(HttpMethod.DELETE).hasRole("ADMIN")
            .antMatchers(HttpMethod.GET).permitAll()
            .anyRequest().authenticated()
            .and()
            .httpBasic().disable()
            .csrf().disable()
            .cors()
            .and()
            .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration config = new CorsConfiguration();
        List<String> allowedOrigins = Collections.singletonList("*");
        List<String> allowedMethods = Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE");
        List<String> allowedHeaders = Arrays.asList("Authorization", "Cache-Control", "Content-Type");
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(allowedMethods);
        config.setAllowCredentials(true);
        config.setAllowedHeaders(allowedHeaders);

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
