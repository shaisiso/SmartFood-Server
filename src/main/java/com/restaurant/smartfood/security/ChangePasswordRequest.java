package com.restaurant.smartfood.security;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ChangePasswordRequest {
    @NotNull(message = "User id must not be null")
    private Long userId;
    @NotBlank(message = "Old password must not be null")
    @Size(min = 6,message = "Password needs to be at least 6 characters")
    private String oldPassword;
    @NotBlank (message = "New password must not be null")
    @Size(min = 6,message = "Password needs to be at least 6 characters")
    private String newPassword;
}
