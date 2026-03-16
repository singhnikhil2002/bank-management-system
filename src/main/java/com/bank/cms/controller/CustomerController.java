package com.bank.cms.controller;

import com.bank.cms.api.response.ApiResponse;
import com.bank.cms.dto.request.CreateCustomerRequest;
import com.bank.cms.dto.request.UpdateCustomerRequest;
import com.bank.cms.dto.response.CustomerResponse;
import com.bank.cms.entity.Customer;
import com.bank.cms.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "Get customer by CIF Number")
    @GetMapping("{cifNumber}")
    public ApiResponse<CustomerResponse> getCustomerByCifNumber(@PathVariable String cifNumber){

        // request sent to service layer
        Customer customer = customerService.findCustomerByCifNumber(cifNumber);

        // We will create response obj to return to user
        CustomerResponse response = new CustomerResponse();
        response.setCifNumber(customer.getCifNumber());
        response.setCustomerName(customer.getCustomerName());
        response.setAddress(customer.getAddress());
        response.setMobileNumber(customer.getMobileNumber());
        response.setDateOfBirth(customer.getDateOfBirth());
        response.setAadharNumber(customer.getAadharNumber());
        response.setStatus(customer.getStatus());

        return new ApiResponse<>(
                "Success",
                "Customer information fetched",
                response
        );
    }

    @Operation(summary = "Update existing customer")
    @PutMapping("/updateCustomer/{cifNumber}")
    public ApiResponse<CustomerResponse> updateCustomer(@Valid @RequestBody UpdateCustomerRequest request, @PathVariable String cifNumber){
        // request is send to service layer
        CustomerResponse response = customerService.amendByCifNumber(request,cifNumber);

        response.setCifNumber(cifNumber);
        response.setCustomerName(request.getCustomerName());
        response.setMobileNumber(request.getMobileNumber());
        response.setDateOfBirth(request.getDateOfBirth());
        response.setAddress(request.getAddress());
        response.setAadharNumber(request.getAadharNumber());
        response.setStatus(true);

        // response returned to user
        return new ApiResponse<>(
                "Success",
                "Updated Successfully",
                response
        );
    }
}
