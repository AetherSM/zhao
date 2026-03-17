package org.example.zhao.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.zhao.common.api.R;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.jwt.header:Authorization}")
    private String jwtHeader;

    @Value("${app.jwt.prefix:Bearer }")
    private String jwtPrefix;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthFilter jwtAuthFilter = new JwtAuthFilter(jwtTokenProvider, jwtHeader, jwtPrefix);

        http.csrf(csrf -> csrf.disable());
        http.cors(Customizer.withDefaults());
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.exceptionHandling(eh -> eh
                .authenticationEntryPoint((request, response, authException) -> {
                    try {
                        writeJson(response, R.unauthorized());
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    try {
                        writeJson(response, R.forbidden());
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                })
        );

        http.authorizeRequests(auth -> auth
                .antMatchers("/api/v1/user/login", "/api/v1/user/register").permitAll()
                .antMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                .antMatchers("/error").permitAll()
                // finance
                .antMatchers("/api/v1/payment/**").hasAnyRole("SUPER_ADMIN", "ORG_ADMIN", "FINANCE")
                .antMatchers("/api/v1/statistics/revenue").hasAnyRole("SUPER_ADMIN", "ORG_ADMIN", "FINANCE")
                // teacher
                .antMatchers("/api/v1/schedule/**").hasAnyRole("SUPER_ADMIN", "ORG_ADMIN", "TEACHER")
                .antMatchers("/api/v1/attendance/**").hasAnyRole("SUPER_ADMIN", "ORG_ADMIN", "TEACHER", "STUDENT")
                // course review
                .antMatchers("/api/v1/courseReview/list").permitAll()
                .antMatchers("/api/v1/courseReview/**").hasAnyRole("SUPER_ADMIN", "ORG_ADMIN", "STUDENT")
                // learning resource
                .antMatchers("/api/v1/learningResource/list").hasAnyRole("SUPER_ADMIN", "ORG_ADMIN", "TEACHER", "STUDENT")
                .antMatchers("/api/v1/learningResource/**").hasAnyRole("SUPER_ADMIN", "ORG_ADMIN", "TEACHER")
                // org admin
                .antMatchers("/api/v1/student/**").hasAnyRole("SUPER_ADMIN", "ORG_ADMIN")
                .antMatchers("/api/v1/teacher/**").hasAnyRole("SUPER_ADMIN", "ORG_ADMIN")
                .antMatchers("/api/v1/courseCategory/**").hasAnyRole("SUPER_ADMIN", "ORG_ADMIN")
                .antMatchers("/api/v1/course/**").hasAnyRole("SUPER_ADMIN", "ORG_ADMIN")
                .antMatchers("/api/v1/enroll/**").hasAnyRole("SUPER_ADMIN", "ORG_ADMIN")
                .antMatchers("/api/v1/statistics/student").hasAnyRole("SUPER_ADMIN", "ORG_ADMIN", "TEACHER")
                .antMatchers("/api/v1/statistics/course").hasAnyRole("SUPER_ADMIN", "ORG_ADMIN", "TEACHER")
                .antMatchers("/api/v1/statistics/**").hasAnyRole("SUPER_ADMIN", "ORG_ADMIN")
                .anyRequest().authenticated()
        );

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * 禁用 Spring Security 的默认 InMemory 用户（避免启动时打印 generated security password）。
     * 本项目采用 JWT，无需表单登录。
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            throw new UsernameNotFoundException("JWT-only mode");
        };
    }

    private void writeJson(HttpServletResponse response, Object body) throws Exception {
        response.setStatus(200);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}

