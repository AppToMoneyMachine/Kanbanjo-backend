package com.bliutvikler.bliutvikler.user.controller;

import com.bliutvikler.bliutvikler.board.controller.BoardController;
import com.bliutvikler.bliutvikler.jwt.BlacklistedTokenService;
import com.bliutvikler.bliutvikler.jwt.JwtUtil;
import com.bliutvikler.bliutvikler.user.model.User;
import com.bliutvikler.bliutvikler.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/api/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(BoardController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BlacklistedTokenService blacklistedTokenService;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        try {
            // Validate the incoming user data
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                throw new IllegalArgumentException("Password cannot be null or empty");
            }

            User savedUser = userService.save(user);
            logger.info("Registered new user: {}", savedUser);
            return ResponseEntity.ok(savedUser);
        } catch (IllegalArgumentException e) {
            logger.error("Error registering new user {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            logger.error("Error registering new user {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User user) {
        try {
            // Opprette et autentiseringsobjekt basert på brukernavn og passord
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

            // Forsøk å autentisere brukeren
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            String token = jwtUtil.generateToken(user.getUsername());

            // Hvis autentiseringen lykkes, returner en suksessmelding
            return ResponseEntity.ok("Bearer " + token);
        } catch(AuthenticationException e) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

    @PreAuthorize("IsAuthorized")
    @GetMapping("/info")
    public ResponseEntity<String> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        String currentUserName = authentication.getName();
        return ResponseEntity.ok("User info endpoint only accessible for users. Current user: " + currentUserName);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(@RequestHeader("Authorization") String token) {
        try {
            if (token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                LocalDateTime expiryDate = jwtUtil.extractExpiration(jwtToken).toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDateTime();
                blacklistedTokenService.blacklistToken(jwtToken, expiryDate);
                SecurityContextHolder.clearContext();
                logger.info("User logged out and token blacklisted: {}", jwtToken);
                return ResponseEntity.ok().build();
            } else {
                logger.error("Invalid token format");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } catch (Exception e) {
            logger.error("Error logging out user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
