package com.github.monetadev.backend.repository;

import com.github.monetadev.backend.model.Privilege;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, UUID> {
    @NotNull List<Privilege> findAll();
    Optional<Privilege> findByName(String name);
}
