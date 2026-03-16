package com.bank.cms.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateCustomerRequest {
    private String customerName;
    private String dateOfBirth;
    private String aadharNumber;
    private String mobileNumber;
    private String address;
}
