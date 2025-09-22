package com.bliutvikler.bliutvikler.user.controller;

import com.bliutvikler.bliutvikler.board.controller.BoardController;
import com.bliutvikler.bliutvikler.jwt.BlacklistedTokenService;
import com.bliutvikler.bliutvikler.jwt.JwtUtil;
import com.bliutvikler.bliutvikler.user.dto.UserInfoResponse;
import com.bliutvikler.bliutvikler.user.model.User;
import com.bliutvikler.bliutvikler.user.repository.UserRepository;
import com.bliutvikler.bliutvikler.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

    @Autowired
    private UserRepository userRepository;

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
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody User user, HttpServletResponse response) {
        try {
            // Opprette et autentiseringsobjekt basert på brukernavn og passord
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

            // Forsøk å autentisere brukeren
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            String token = jwtUtil.generateToken(user.getUsername());

            // Jwt cookie
            ResponseCookie cookie = ResponseCookie.from("token", token)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(Duration.ofDays(1))
                    .sameSite("Strict")
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            // Hvis autentiseringen lykkes, returner en suksessmelding
            // return ResponseEntity.ok("Bearer " + token);

            //return ResponseEntity.status(200).body("Login successful");
            return ResponseEntity.ok(Map.of("message", "Login successful"));
        } catch(AuthenticationException e) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid password or username"));
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

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        // fetch correct user from database to get email and roles
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<String> roleNames = user.getRoles().stream().map(role -> role.getName()).collect(Collectors.toList());

        UserInfoResponse response = new UserInfoResponse(user.getUsername(), user.getEmail(), roleNames, user.getId());
        logger.info("User info endpoint hit with user info: {}", user);
        return ResponseEntity.ok(response);
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
