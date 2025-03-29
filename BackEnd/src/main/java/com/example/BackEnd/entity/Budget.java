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
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(precision = 30, scale = 0)
    BigDecimal limitAmount;

    @Column(precision = 30, scale = 0, columnDefinition = "decimal(30,0) default 0")
    BigDecimal expensedAmount;

    @Column(columnDefinition = "varchar(255) default 'VND'")
    String currencyUnit;

    @Column(columnDefinition = "varchar(255) default 'NO_RENEW'")
    String budgetType;

    @Column(nullable = true)
    String noRenewDateUnit;

    @Column(nullable = true)
    String noRenewDate;

    @Column(nullable = true)
    String renewDateUnit;

    @Column(nullable = true, columnDefinition = "TIMESTAMP")
    LocalDateTime customRenewDate;

    @Column(columnDefinition = "boolean default true")
    boolean isActive;

    @Column(columnDefinition = "boolean default false")
    boolean enableNotification;

    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    LocalDateTime createAt;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "category_id")
    Category category;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;
}