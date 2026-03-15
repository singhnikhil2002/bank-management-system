package com.bank.cms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerResponse {
    private String cifNumber;
    private String customerName;
    private String dateOfBirth;
    private String aadharNumber;
    private String mobileNumber;
    private String address;
    private boolean status;

    public CustomerResponse() {

    }
}
