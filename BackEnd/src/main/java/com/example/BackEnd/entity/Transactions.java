package com.example.BackEnd.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Transactions {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(precision = 30, scale = 0)
    BigDecimal amount;

    @Column(columnDefinition = "text", nullable = true)
    String notes;

    @Column(columnDefinition = "text", nullable = true)
    String picture;

    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    LocalDateTime transactionDate;

    @Column(columnDefinition = "varchar(255) default 'EXPENSE'")
    String transactionType;

    @Column(columnDefinition = "varchar(255) default 'VND'")
    String currencyUnit;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    Wallet wallet;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "target_wallet_id")
    Wallet targetWallet;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;
}