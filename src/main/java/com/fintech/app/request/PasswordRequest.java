package com.fintech.app.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordRequest {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}

