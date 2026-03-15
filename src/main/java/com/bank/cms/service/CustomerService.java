package com.bank.cms.service;

import com.bank.cms.dto.request.CreateCustomerRequest;
import com.bank.cms.entity.Customer;
import org.springframework.stereotype.Service;

public interface CustomerService {

    Customer createCustomer(CreateCustomerRequest request);
}
