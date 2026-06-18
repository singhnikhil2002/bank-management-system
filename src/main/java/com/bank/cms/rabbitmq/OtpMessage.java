package com.bank.cms.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OtpMessage {
    private String mobileNumber;
    private String email;
    private String otp;
    private String purpose;     // LOGIN, TRANSACTION, PASSWORD_RESET
}