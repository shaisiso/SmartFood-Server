package com.restaurant.smartfood.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.Table;

@SuperBuilder
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table()
public class TakeAway extends Order {
}