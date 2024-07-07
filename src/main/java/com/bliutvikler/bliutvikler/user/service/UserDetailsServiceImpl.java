package com.bliutvikler.bliutvikler.user.service;

import com.bliutvikler.bliutvikler.user.model.User;
import com.bliutvikler.bliutvikler.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
         User user = userRepository.findByUsername(username);
         if (user == null) {
            throw new UsernameNotFoundException("User not found");
         }
        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> {
                    logger.info("Role found {}", role.getName());
                    return new SimpleGrantedAuthority(role.getName());

                })
                .collect(Collectors.toSet());

        logger.info("User '{}' logged in with roles: {}", user.getUsername(), authorities);

         return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
     }
}
