package com.ovansa.task_api.config;

import com.ovansa.task_api.domain.CustomUserDetails;
import com.ovansa.task_api.service.CustomUserDetailsService;
import com.ovansa.task_api.service.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final ApplicationContext applicationContext;
    private final JwtTokenService jwtTokenService;

    @Override
    protected void doFilterInternal (HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader ("Authorization");
        String token = null;
        String email = null;

        if (authHeader != null && authHeader.startsWith ("Bearer ")) {
            token = authHeader.substring (7);
            email = jwtTokenService.extractUsername (token);
        }

        if (email != null && SecurityContextHolder.getContext ().getAuthentication () == null) {
            UserDetails userDetails = applicationContext.getBean(CustomUserDetailsService.class)
                    .loadUserByUsername(email);

            if (jwtTokenService.validateToken (token, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken (userDetails, null, userDetails.getAuthorities ());
                authenticationToken.setDetails (new WebAuthenticationDetailsSource ().buildDetails (request));
                SecurityContextHolder.getContext ().setAuthentication (authenticationToken);
                request.setAttribute ("userId", ((CustomUserDetails)userDetails).getId());
            };
        }

        filterChain.doFilter (request, response);
    }
}
