package com.ua.config.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.ua.persistence.entity.user.User;
import com.ua.persistence.repository.user.UserRepository;

import java.util.HashSet;
import java.util.Set;

@Service
public class DefaultUserDetailsService implements UserDetailsService {

    private final UserRepository<User> userRepository;

    public DefaultUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("invalid username or password"));

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(user.getRoleType().name()));
        return convertCustomUserToSpringUser(user, authorities);
    }

    private org.springframework.security.core.userdetails.User convertCustomUserToSpringUser(
            User user, Set<GrantedAuthority> authorities) {
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getEnabled(),
                true,
                true,
                true,
                authorities
        );
    }
}
