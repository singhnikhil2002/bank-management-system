package com.bank.cms.service.impl;

import com.bank.cms.dto.request.CreateCustomerRequest;
import com.bank.cms.entity.Customer;
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

    private String generateCifNumber() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return now.format(dtf);
    }
}
