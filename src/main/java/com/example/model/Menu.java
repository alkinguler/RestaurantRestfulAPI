package com.example.model;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "menu")
public class Menu {
    @Id
    @Column(name = "id",nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(name = "day",nullable = false)
    private String Day;
}
