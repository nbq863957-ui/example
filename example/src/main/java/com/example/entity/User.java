package com.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "role", nullable = false, length = 50)
    private String role;

    @Column(name = "identitycode", nullable = false, length = 50)
    private String identityCode;

    public User(String username, String password, String role, String identityCode) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.identityCode = identityCode;
    }
}
