package com.restaurant.smartfood.websocket;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TextMessageDTO {
    private String message;
    private String date;
}
