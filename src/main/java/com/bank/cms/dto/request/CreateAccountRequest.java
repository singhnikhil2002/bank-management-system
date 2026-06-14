package com.bank.cms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateAccountRequest {

    @NotBlank
    private String customerName;

    @NotNull
    private String cifNumber;

    @NotNull
    @Min(0)
    private Double initialBalance;
    private String emailId;
    private String mobileNumber;
    private String address;

}
