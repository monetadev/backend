package com.github.monetadev.backend.repository;

import com.github.monetadev.backend.model.User;
import com.github.monetadev.backend.model.UserLogin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.UUID;

@Repository
public interface UserLoginRepository extends JpaRepository<UserLogin, UUID> {
    Page<UserLogin> findAllByUser(User user, Pageable pageable);
    Page<UserLogin> findAllByUserAndLoginDateTimeBetween(User user, OffsetDateTime start, OffsetDateTime end, Pageable pageable);
}
