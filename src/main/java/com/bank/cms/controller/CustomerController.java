package com.bank.cms.controller;

import com.bank.cms.api.response.ApiResponse;
import com.bank.cms.dto.request.CreateCustomerRequest;
import com.bank.cms.dto.response.CustomerResponse;
import com.bank.cms.entity.Customer;
import com.bank.cms.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customer")
@Tag(name= "Customer Service", description="API is related to Customer Management")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService){
        this.customerService = customerService;
    }

    @Operation(summary = "Create a new customer")
    @PostMapping("/createCustomer")
    public ApiResponse<CustomerResponse> createCustomer(@Valid @RequestBody CreateCustomerRequest request){
        // request send service layer and response received
        Customer customer = customerService.createCustomer(request);

        // response created
        CustomerResponse response = new CustomerResponse();
        response.setCifNumber(customer.getCifNumber());
        response.setCustomerName(customer.getCustomerName());
        response.setAddress(customer.getAddress());
        response.setDateOfBirth(customer.getDateOfBirth());
        response.setMobileNumber(customer.getMobileNumber());
        response.setAadharNumber(customer.getAadharNumber());
        response.setStatus(customer.getStatus());

        // response send to user
        return new ApiResponse<>(
                "Success",
                "Customer created sucessfully",
                response
        );
    }
}
