package com.github.monetadev.backend.service;

import com.github.monetadev.backend.dto.RegistrationDTO;
import com.github.monetadev.backend.model.User;

public interface RegistrationService {
    User registerNewUser(RegistrationDTO registrationDTO);
}
