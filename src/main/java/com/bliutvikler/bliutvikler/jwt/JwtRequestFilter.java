package com.bliutvikler.bliutvikler.jwt;

import com.bliutvikler.bliutvikler.user.service.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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
        // final String requestTokenHeader = request.getHeader("Authorization");

        // No JWT validation for registering, login or logout
        String requestURI = request.getRequestURI();
        if (requestURI.equals("/api/user/register") || requestURI.equals("/api/user/login") || requestURI.equals("/api/user/logout")) {
            logger.info("No JWT validation for URI: {}", requestURI);
            chain.doFilter(request, response);
            return;
        }

        String username = null;
        String jwtToken = extractTokenFromHeaderOrCookie(request);

        if (jwtToken != null) {
            try {
                username = jwtUtil.extractUsername(jwtToken);
            } catch (IllegalArgumentException e) {
                logger.error("Unable to get JWT token", e);
            } catch (ExpiredJwtException e) {
                logger.error("JWT token has expired", e);
            }
        } else {
            logger.warn("No JWT token found in header or cookie");
        }

        // Validate token
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // check if token is blacklisted
            if (blacklistedTokenService.isTokenBlacklisted(jwtToken)) {
                logger.warn("JWT token is blacklisted"); // unauthenticated
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 in response to client
                return;
            } else if (jwtUtil.validateToken(jwtToken, userDetails.getUsername())) {
                // if valid token
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // set authentication in security context
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        logger.info("Request URI: {}", request.getRequestURI());

        chain.doFilter(request, response);
    }
    private String extractTokenFromHeaderOrCookie(HttpServletRequest request) {
        // Try Authorization header first
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        // Then try cookies
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    String value = cookie.getValue();
                    if (value != null && value.startsWith("Bearer")) {
                        return value.substring(7);
                    }
                    return value;
                }
            }
        }

        logger.warn("No JWT token found in request header or cookie");
        return null;
    }
}
