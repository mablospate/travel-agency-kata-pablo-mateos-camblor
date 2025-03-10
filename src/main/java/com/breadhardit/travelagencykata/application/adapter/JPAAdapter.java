package com.breadhardit.travelagencykata.application.adapter;

import com.breadhardit.travelagencykata.application.port.CustomersRepository;
import com.breadhardit.travelagencykata.domain.Customer;
import com.breadhardit.travelagencykata.infrastructure.persistence.entity.CustomerEntity;
import com.breadhardit.travelagencykata.infrastructure.persistence.repository.CustomersJPARepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class JPAAdapter implements CustomersRepository {
    private CustomersJPARepository jpa;

    //public JPAAdapter(CustomersJPARepository jpa){
    //    this.jpa = jpa;
    //}
    @Override
    public void saveCustomer(Customer customer) {
        CustomerEntity adaptedCustomer = CustomerEntity.builder()
                .id(customer.getId())
                .name(customer.getName())
                .surnames(customer.getSurnames())
                .birthDate(customer.getBirthDate())
                .passportNumber(customer.getPassportNumber())
                .enrollmentDate(LocalDate.now())
                .active(true)
                .build();
        jpa.saveAndFlush(adaptedCustomer);
    }

    @Override
    public Optional<Customer> getCustomerById(String id) {
        Customer adaptedCustomer = null;
        List<CustomerEntity> customerList = jpa.findAll();
        for(CustomerEntity customer: customerList){
            if(customer.getId().equals(id)){
                adaptedCustomer = Customer.builder()
                        .id(customer.getId())
                        .name(customer.getName())
                        .surnames(customer.getSurnames())
                        .birthDate(customer.getBirthDate())
                        .passportNumber(customer.getPassportNumber())
                        .enrollmentDate(LocalDate.now())
                        .active(true)
                        .build();
            }
        }
        return adaptedCustomer == null? Optional.empty(): Optional.of(adaptedCustomer);
    }

    @Override
    public Optional<Customer> getCustomerByPassport(String id) {
        Customer adaptedCustomer = null;
        List<CustomerEntity> customerList = jpa.findAll();
        for(CustomerEntity customer: customerList){
            if(customer.getPassportNumber().equals(id)){
                adaptedCustomer = Customer.builder()
                        .id(customer.getId())
                        .name(customer.getName())
                        .surnames(customer.getSurnames())
                        .birthDate(customer.getBirthDate())
                        .passportNumber(customer.getPassportNumber())
                        .enrollmentDate(LocalDate.now())
                        .active(true)
                        .build();
            }
        }
        return adaptedCustomer == null? Optional.empty(): Optional.of(adaptedCustomer);
    }
}
