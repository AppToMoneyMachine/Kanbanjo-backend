package com.bliutvikler.bliutvikler.jwt;

import com.bliutvikler.bliutvikler.user.service.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);


    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BlacklistedTokenService blacklistedTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        // No JWT validation for registering, login or logout
        String requestURI = request.getRequestURI();
        if (requestURI.equals("/api/user/register") || requestURI.equals("/api/user/login") || requestURI.equals("/api/user/logout")) {
            logger.info("No JWT validation for URI: {}", requestURI); // Debug statement
            chain.doFilter(request, response);
            return;
        }

        // JWT-tokenen er i form av "Bearer token". Fjern "Bearer" og få tokenen.
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwtToken);
            } catch (IllegalArgumentException e) {
                System.out.println("Unable to get JWT token");
            } catch ( ExpiredJwtException e) {
                System.out.println("JWT token has expired");
            }
        } else {
            logger.error("JWT Token does not begin with Bearer String");
        }

        // Validate token
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // check if token is blacklisted
            if (blacklistedTokenService.isTokenBlacklisted(jwtToken)) {
                logger.error("JWT token is blacklisted");
            } else if (jwtUtil.validateToken(jwtToken, userDetails.getUsername())) {
                // if valid token
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // set authentication in security context
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        chain.doFilter(request, response);
    }
}
