package com.github.monetadev.backend.service.impl;

import com.github.monetadev.backend.exception.PrivilegeNotFoundException;
import com.github.monetadev.backend.model.Privilege;
import com.github.monetadev.backend.repository.PrivilegeRepository;
import com.github.monetadev.backend.service.PrivilegeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PrivilegeServiceImpl implements PrivilegeService {
    private final PrivilegeRepository privilegeRepository;

    public PrivilegeServiceImpl(PrivilegeRepository privilegeRepository) {
        this.privilegeRepository = privilegeRepository;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<Privilege> privileges() {
        return privilegeRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Privilege findPrivilegeById(UUID id) throws PrivilegeNotFoundException {
        return privilegeRepository.findById(id)
                .orElseThrow(() -> new PrivilegeNotFoundException("Privilege not found with ID: " + id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Privilege findPrivilegeByName(String name) throws PrivilegeNotFoundException {
        return privilegeRepository.findByName(name)
                .orElseThrow(() -> new PrivilegeNotFoundException("Privilege not found with name: " + name));
    }
}
