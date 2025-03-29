package com.example.BackEnd.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(length = 20, unique = true)
    String username;

    @Column(length = 15, unique = true)
    String phoneNumber;

    @Column(length = 100)
    String password;

    @Column(columnDefinition = "text", nullable = true)
    String deviceToken;

    @Column(columnDefinition = "varchar(255) default 'VND'")
    String currencyUnit;

    @OneToMany(mappedBy = "user")
    List<UserWallet> userWallets;

    @OneToMany(mappedBy = "user")
    List<Transactions> transactions;

    @OneToMany(mappedBy = "user")
    List<Category> categories;

    @OneToMany(mappedBy = "user")
    List<Budget> budgets;
}