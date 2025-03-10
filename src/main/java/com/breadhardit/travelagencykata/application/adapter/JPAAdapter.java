package com.breadhardit.travelagencykata.application.adapter;

import com.breadhardit.travelagencykata.application.port.CustomersRepository;
import com.breadhardit.travelagencykata.domain.Customer;
import com.breadhardit.travelagencykata.infrastructure.persistence.entity.CustomerEntity;
import com.breadhardit.travelagencykata.infrastructure.persistence.repository.CustomersJPARepository;
import org.springframework.data.domain.Example;

import java.util.List;
import java.util.Optional;

public class JPAAdapter implements CustomersRepository {
    private CustomersJPARepository jpa;

    public JPAAdapter(CustomersJPARepository jpa){
        this.jpa = jpa;
    }
    @Override
    public void saveCustomer(Customer customer) {
        CustomerEntity adaptedCustomer = new CustomerEntity(customer.getId(),customer.getName(), customer.getSurnames(),
                customer.getBirthDate(), customer.getPassportNumber(), customer.getEnrollmentDate(), customer.getActive());
        jpa.saveAndFlush(adaptedCustomer);
    }

    @Override
    public Optional<Customer> getCustomerById(String id) {
        try{
            CustomerEntity customer = jpa.getReferenceById(id);
            Customer adaptedCustomer = new Customer(customer.getId(), customer.getName(), customer.getSurnames(),
                    customer.getBirthDate(), customer.getPassportNumber(), customer.getEnrollmentDate(), customer.getActive());
            return Optional.of(adaptedCustomer);
        }catch (Exception ex){
            return Optional.empty();
        }
    }

    @Override
    public Optional<Customer> getCustomerByPassport(String id) {
        Customer adaptedCustomer = null;
        List<CustomerEntity> customerList = jpa.findAll();
        for(CustomerEntity customer: customerList){
            if(customer.getPassportNumber().equals(id)){
                adaptedCustomer = new Customer(customer.getId(), customer.getName(), customer.getSurnames(),
                        customer.getBirthDate(), customer.getPassportNumber(), customer.getEnrollmentDate(), customer.getActive());
            }
        }
        return adaptedCustomer == null? Optional.empty(): Optional.of(adaptedCustomer);
    }
}
