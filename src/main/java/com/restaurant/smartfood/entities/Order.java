package com.restaurant.smartfood.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Polymorphism;
import org.hibernate.annotations.PolymorphismType;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Polymorphism(type = PolymorphismType.EXPLICIT)
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq")
    @SequenceGenerator(name = "order_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(nullable = false)
    @FutureOrPresent
    @JsonFormat(pattern="dd-MM-yyyy HH:mm")
    private LocalDateTime date;

    @OneToMany
    @NotNull
    @Column(nullable = false)
    private List<ItemInOrder> items;

    private String orderComment;

    @NotNull
    @Column(nullable = false)
    @DecimalMin(value = "0",inclusive = false)
    private Float totalPrice;

    @Column(nullable = false)
    @DecimalMin(value = "0")
    private Float alreadyPaid;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

}