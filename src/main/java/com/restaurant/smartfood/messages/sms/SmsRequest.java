package com.restaurant.smartfood.messages.sms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@Builder
public class SmsRequest {

	@NotBlank
	@Pattern(regexp = "^[0][5][0-9]{8}",message = "phone number must be 10 digits as 05xxxxxxxx")
    private String phoneNumber; // destination

    @NotBlank
    private String message;

}
