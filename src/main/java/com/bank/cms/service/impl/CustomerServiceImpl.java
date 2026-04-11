package com.bank.cms.service.impl;

import com.bank.cms.dto.request.CreateCustomerRequest;
import com.bank.cms.dto.request.UpdateCustomerRequest;
import com.bank.cms.dto.response.CustomerResponse;
import com.bank.cms.entity.Customer;
import com.bank.cms.exception.ResourceNotFoundException;
import com.bank.cms.repository.AccountRepository;
import com.bank.cms.repository.CustomerRepository;
import com.bank.cms.service.CustomerService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class CustomerServiceImpl implements CustomerService {

    public final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository){
        this.customerRepository = customerRepository;
    }

    @Override // request received from controller
    public Customer createCustomer(CreateCustomerRequest request){

        // request created for customer repository
        Customer customer = new Customer();
        customer.setCifNumber(generateCifNumber());
        customer.setCustomerName(request.getCustomerName());
        customer.setMobileNumber(request.getMobileNumber());
        customer.setAddress(request.getAddress());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setAadharNumber(request.getAadharNumber());
        customer.setStatus(true);

        // send the customer created object to repository layer.
        // repository layer will insert this into db
        return customerRepository.save(customer);
    }

    @Override // request received from controller
    public Customer findCustomerByCifNumber(String cifNumber){

        // request send to repository layer
        Customer customer = customerRepository.findCustomerByCifNumber(cifNumber).orElseThrow(() ->
                new ResourceNotFoundException("Customer not found with this number : " + cifNumber)
            );

        return customer;
    }

    @Override
    public Customer findCustomerByName(String customerName){

        Customer customer = customerRepository.findByCustomerName(customerName).orElseThrow(() ->
                new ResourceNotFoundException("Customer not found with this name : " + customerName));
        return customer;
    }

    @Override
    public CustomerResponse amendByCifNumber(UpdateCustomerRequest request, String cifNumber){

        Customer customer = customerRepository.findCustomerByCifNumber(cifNumber).orElseThrow(()->
                    new ResourceNotFoundException("Customer not found with this cif :" + cifNumber)
                );

        // request is created
        // this will go db via repository
        customer.setCustomerName(request.getCustomerName());
        customer.setMobileNumber(request.getMobileNumber());
        customer.setAddress(request.getAddress());
        customer.setAadharNumber(request.getAadharNumber());
        customer.setDateOfBirth(request.getDateOfBirth());

        // request saved in db
        customerRepository.save(customer);

        return mapToAccount(customer);
    }

    public CustomerResponse mapToAccount(Customer customer){
        CustomerResponse response = new CustomerResponse();

        response.setCifNumber(customer.getCifNumber());
        response.setCustomerName(customer.getCustomerName());
        response.setAddress(customer.getAddress());
        response.setMobileNumber(customer.getMobileNumber());
        response.setDateOfBirth(customer.getDateOfBirth());
        response.setAadharNumber(customer.getAadharNumber());
        response.setStatus(customer.getStatus());

        return response;
    }

    private String generateCifNumber() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return now.format(dtf);
    }
}
