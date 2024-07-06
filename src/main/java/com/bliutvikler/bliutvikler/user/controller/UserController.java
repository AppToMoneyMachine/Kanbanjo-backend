package com.bliutvikler.bliutvikler.user.controller;

import com.bliutvikler.bliutvikler.user.model.User;
import com.bliutvikler.bliutvikler.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        userService.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User user) {
        try {
            // Opprette et autentiseringsobjekt basert på brukernavn og passord
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

            // Forsøk å autentisere brukeren
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // Hvis autentiseringen lykkes, returner en suksessmelding
            return ResponseEntity.ok("User successfully logged in");
        } catch(AuthenticationException e) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

}
