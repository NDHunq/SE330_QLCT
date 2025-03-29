package com.example.BackEnd.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserWallet {
    @Id
    String walletId;

    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    LocalDateTime joinDate;

    @Column(columnDefinition = "boolean default true")
    boolean isAdmin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "wallet_ref_id") // Đổi tên cột để tránh trùng
    Wallet wallet;
}