package com.restaurant.smartfood.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Polymorphism;
import org.hibernate.annotations.PolymorphismType;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
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
@JsonIdentityInfo(property = "id",generator = ObjectIdGenerators.PropertyGenerator.class)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq")
    @SequenceGenerator(name = "order_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    @FutureOrPresent
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;

    @Column(nullable = false)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime hour;

    @OneToMany(mappedBy = "order",fetch = FetchType.LAZY, cascade = {CascadeType.MERGE,CascadeType.REMOVE})
    private List<ItemInOrder> items;

    private String orderComment;

    @Column(nullable = false)
    @DecimalMin(value = "0")
    private Float originalTotalPrice;

    @Column(nullable = false)
    @DecimalMin(value = "0")
    private Float totalPriceToPay;

    @Column(nullable = false)
    @DecimalMin(value = "0")
    private Float alreadyPaid;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne
    @Valid
    private Person person;
}