package com.restaurant.smartfood.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Polymorphism;
import org.hibernate.annotations.PolymorphismType;

import javax.persistence.*;
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
@Table(name = "order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq")
    @SequenceGenerator(name = "order_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @FutureOrPresent
    @JsonFormat(pattern="dd-MM-yyyy HH:mm")
    private LocalDateTime date;

    @OneToMany
    @NotNull
    private List<ItemInOrder> items;

    private String orderComment;

    @NotNull
    @Min(0)
    private Float totalPrice;

    private Float alreadyPaid;

    @NotNull
    private OrderStatus status;








}