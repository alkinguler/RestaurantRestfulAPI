package com.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "item")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long Id;
    @OneToOne()
    @JoinColumn(name = "item_type_id")
    private ItemType ItemType;

    @Column(name = "name")
    private String Name;

    @Column(name = "price")
    private Integer Price;

    @Column(name = "description")
    private String Description;

}
