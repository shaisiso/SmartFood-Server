package com.restaurant.smartfood.utility;

import com.restaurant.smartfood.entities.ItemInOrder;
import com.restaurant.smartfood.entities.MenuItem;
import lombok.*;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ItemInOrderResponse {

    private Long id;

    private Long orderId;

    private MenuItem item;

    private String itemComment;

    private Float price;

    public static ItemInOrderResponse buildItemInOrderResponse(ItemInOrder itemInOrder){
        return ItemInOrderResponse.builder()
                .id(itemInOrder.getId())
                .item(itemInOrder.getItem())
                .orderId(itemInOrder.getOrder().getId())
                .itemComment(itemInOrder.getItemComment())
                .price(itemInOrder.getPrice())
                .build();
    }
}
