package org.example.zhao.security;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final String header;
    private final String prefix;

    public JwtAuthFilter(JwtTokenProvider jwtTokenProvider, String header, String prefix) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.header = header;
        this.prefix = prefix;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // header 为空会触发 Tomcat NPE（getHeader(null)），这里做兜底
        if (!StringUtils.hasText(header)) {
            filterChain.doFilter(request, response);
            return;
        }
        String auth = request.getHeader(header);
        if (auth == null || !StringUtils.hasText(auth) || !auth.startsWith(prefix)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String token = auth.substring(prefix.length());
        try {
            Claims claims = jwtTokenProvider.parse(token);
            String username = claims.getSubject();
            Long uid = claims.get("uid", Long.class);
            List<String> roles = claims.get("roles", List.class);
            if (roles == null) roles = Collections.emptyList();

            LoginUser principal = new LoginUser(uid, username, roles);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    principal,
                    null,
                    roles.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r)).collect(Collectors.toList())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            log.debug("JWT parse failed: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }
}

