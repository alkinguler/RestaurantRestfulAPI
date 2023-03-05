package com.example.model;

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
@Table(name = "order_item")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column( name = "id")
    private Long Id;
    @Column( name = "order_id")
    private Long OrderId;
    @Column( name = "name_id")
    private Long ItemId;

    @Column( name = "quantity")
    private Integer Quantity;
}
