package com.mapago.config;

import com.mapago.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        final String requestUri = request.getRequestURI();

        String userId = null;
        String jwt = null;
        String secretKey = null;
        
        if (requestUri.startsWith("/admin/api")) {
            secretKey = JwtUtil.ADMIN_SECRET_KEY;
        } else if (requestUri.startsWith("/portal/api")) {
            secretKey = JwtUtil.PORTAL_SECRET_KEY;
        }

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ") && secretKey != null) {
            jwt = authorizationHeader.substring(7);
            userId = jwtUtil.extractUserId(jwt, secretKey);

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userId);

                if (jwtUtil.validateToken(jwt, userDetails.getUsername(), secretKey)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }

        chain.doFilter(request, response);
    }
}