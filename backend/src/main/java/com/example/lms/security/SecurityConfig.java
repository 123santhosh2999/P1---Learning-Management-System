package com.example.lms.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.example.lms.security.JwtAuthFilter;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

  private final JwtAuthFilter jwtAuthFilter;

  public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
    this.jwtAuthFilter = jwtAuthFilter;
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .cors(Customizer.withDefaults())
      .formLogin(form -> form.disable())
      .httpBasic(basic -> basic.disable())
      .logout(logout -> logout.disable())
      .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(auth -> auth
        .dispatcherTypeMatchers(DispatcherType.ERROR, DispatcherType.FORWARD).permitAll()
        .requestMatchers("/", "/error").permitAll()
        .requestMatchers("/health").permitAll()
        .requestMatchers("/api/docs/**", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/v3/api-docs.yaml", "/webjars/**").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
        .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()
        .requestMatchers("/api/me/**").authenticated()
        .requestMatchers("/api/admin/**").hasRole("ADMIN")
        .requestMatchers("/api/instructor/**").hasAnyRole("INSTRUCTOR", "ADMIN")
        .requestMatchers("/api/courses/**").hasRole("STUDENT")
        .requestMatchers("/api/lessons/**").hasRole("STUDENT")
        .anyRequest().authenticated()
      )
      .exceptionHandling(ex -> ex
        .authenticationEntryPoint((request, response, authException) -> {
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          response.setContentType(MediaType.APPLICATION_JSON_VALUE);
          response.getWriter().write("{\"message\":\"Unauthorized\"}");
        })
        .accessDeniedHandler((request, response, accessDeniedException) -> {
          response.setStatus(HttpServletResponse.SC_FORBIDDEN);
          response.setContentType(MediaType.APPLICATION_JSON_VALUE);
          response.getWriter().write("{\"message\":\"Forbidden\"}");
        })
      )
      .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
