package com.restaurant.smartfood.entities;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Min;
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

    @NotBlank(message = "City must not be blank")
    @Column(length=85)
    private String city;

    @NotBlank(message = "Street must not be blank")
    @Column(length=85)
    private String streetName;

    @NotNull(message = "House number must not be null")
    @Min(0)
    private Integer houseNumber;


    private Character entrance;
    @Min(0)
    private Integer apartmentNumber;
}