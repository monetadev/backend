package com.github.monetadev.backend.service.impl;

import com.github.monetadev.backend.dto.RegistrationDTO;
import com.github.monetadev.backend.exception.RoleNotFoundException;
import com.github.monetadev.backend.model.Role;
import com.github.monetadev.backend.model.User;
import com.github.monetadev.backend.repository.RoleRepository;
import com.github.monetadev.backend.repository.UserRepository;
import com.github.monetadev.backend.service.RegistrationService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RegistrationServiceImpl implements RegistrationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerNewUser(RegistrationDTO registrationDTO) {
        User user = new User();
        user.setUsername(registrationDTO.getUsername());
        user.setEmail(registrationDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));

        if (userRepository.count() == 0) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RoleNotFoundException("ROLE_ADMIN does not exist."));
            user.setRoles(Set.of(adminRole));
            return userRepository.save(user);
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RoleNotFoundException("ROLE_USER does not exist."));
        user.setRoles(Set.of(userRole));
        return userRepository.save(user);
    }
}
