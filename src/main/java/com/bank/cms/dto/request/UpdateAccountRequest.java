package com.bank.cms.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
@AllArgsConstructor
@Data
public class UpdateAccountRequest {

    private String customerName;
    private Double initialBalance;
    private String emailId;
    private String mobileNumber;
    private String address;
}
