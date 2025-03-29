package com.example.BackEnd.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "role_id")
    Role role;

    @Column(length = 30)
    String email;

    @Column(length = 20)
    String password;

    @Column(length = 30)
    String fullname;

    @Column(length = 150)
    String address;

    @Column(length = 15)
    String phoneNumber;

    @Column
    LocalDate birthday;

    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    Date createAt;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    Date updateAt;
}