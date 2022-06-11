package com.fintech.app.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String accountNumber;
    private String bvn;
    private String password;
    private String confirmPassword;
}
