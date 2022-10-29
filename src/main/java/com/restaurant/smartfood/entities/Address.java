package com.restaurant.smartfood.entities;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Embeddable
public class Address {

    @Column(length=85)
    @NotBlank
    private String city;

    @Column(length=85)
    @NotBlank
    private String streetName;

    @NotNull
    private Integer houseNumber;

    private Character entrance;

    private Integer apartmentNumber;
}