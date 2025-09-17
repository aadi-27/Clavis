package com.clavis.config;

import com.clavis.model.User;                // your entity
import com.clavis.repository.UserRepository; // your repo
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // avoid naming collision by calling entity 'appUser'
        com.clavis.model.User appUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Build Spring Security UserDetails using fully-qualified User builder to avoid import clash
        org.springframework.security.core.userdetails.User.UserBuilder builder =
                org.springframework.security.core.userdetails.User.withUsername(appUser.getUsername());

        builder.password(appUser.getPassword()); // BCrypt hash from DB
        // remove ROLE_ prefix for builder.roles(...) because .roles(...) adds ROLE_ automatically
        String roleWithoutPrefix = appUser.getRole().startsWith("ROLE_")
                ? appUser.getRole().substring("ROLE_".length())
                : appUser.getRole();

        builder.roles(roleWithoutPrefix); // e.g. "ADMIN" or "PLAYER"

        return builder.build();
    }
}
