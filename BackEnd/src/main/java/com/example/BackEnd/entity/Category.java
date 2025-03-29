package com.example.BackEnd.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(length = 20)
    String name;

    @Column(columnDefinition = "text", nullable = true)
    String picture;

    @Column(columnDefinition = "varchar(255) default 'EXPENSE'")
    String type;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @OneToOne(mappedBy = "category")
    Budget budget;

    @OneToMany(mappedBy = "category")
    List<Transactions> transactions;
}