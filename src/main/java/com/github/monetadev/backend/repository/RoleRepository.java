package com.github.monetadev.backend.repository;

import com.github.monetadev.backend.model.Role;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    @NotNull Page<Role> findAll(@NotNull Pageable pageable);
    Optional<Role> findByName(String name);
}
