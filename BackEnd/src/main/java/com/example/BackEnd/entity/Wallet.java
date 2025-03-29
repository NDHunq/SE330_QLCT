package com.example.BackEnd.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(columnDefinition = "varchar(255) default 'Default wallet'")
    String name;

    @Column(precision = 30, scale = 0, columnDefinition = "decimal(30,0) default 0")
    BigDecimal amount;

    @Column(columnDefinition = "varchar(255) default 'VND'")
    String currencyUnit;

    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    LocalDateTime createAt;

    @UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    LocalDateTime updateAt;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    List<UserWallet> userWallets;

    @OneToMany(mappedBy = "wallet")
    List<Transactions> transactions;

    @OneToMany(mappedBy = "targetWallet")
    List<Transactions> transactionsTransferIn;
}