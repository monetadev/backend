package com.github.monetadev.backend.model;

import jakarta.persistence.*;

import java.util.Set;
import java.util.UUID;

@Entity
public class Privilege {
    @Id
    @Column(name = "privilege_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name")
    private String name;

    @ManyToMany(mappedBy = "privileges")
    private Set<Role> roles;
}
