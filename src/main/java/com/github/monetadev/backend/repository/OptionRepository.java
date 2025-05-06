package com.github.monetadev.backend.repository;

import com.github.monetadev.backend.model.Option;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OptionRepository extends JpaRepository<Option, UUID> {
}
