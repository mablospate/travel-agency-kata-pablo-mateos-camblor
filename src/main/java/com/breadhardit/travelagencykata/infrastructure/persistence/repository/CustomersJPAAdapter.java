package com.breadhardit.travelagencykata.infrastructure.persistence.repository;

import com.breadhardit.travelagencykata.application.port.CustomersRepository;
import com.breadhardit.travelagencykata.domain.Customer;
import com.breadhardit.travelagencykata.infrastructure.persistence.entity.CustomerEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomersJPAAdapter implements CustomersRepository {
    private final CustomersJPARepository customersJPARepository;

    @Override
    public void saveCustomer(Customer customer) {
        customersJPARepository.save(toEntity(customer));
    }

    @Override
    public Optional<Customer> getCustomerById(String id) {
        return customersJPARepository.findById(id).map(CustomersJPAAdapter::toCustomer);
    }

    @Override
    public Optional<Customer> getCustomerByPassport(String passportNumber) {
        return customersJPARepository.findByPassportNumber(passportNumber)
                .map(CustomersJPAAdapter::toCustomer);
    }

    private static CustomerEntity toEntity(Customer customer) {
        return CustomerEntity.builder()
                .id(customer.getId())
                .name(customer.getName())
                .surnames(customer.getSurnames())
                .birthDate(customer.getBirthDate())
                .passportNumber(customer.getPassportNumber())
                .enrollmentDate(customer.getEnrollmentDate() == null
                        ? LocalDate.now()
                        : customer.getEnrollmentDate())
                .active(customer.getActive() == null ? Boolean.FALSE : customer.getActive())
                .build();
    }

    private static Customer toCustomer(CustomerEntity customerEntity) {
        return Customer.builder()
                .id(customerEntity.getId())
                .name(customerEntity.getName())
                .surnames(customerEntity.getSurnames())
                .birthDate(customerEntity.getBirthDate())
                .passportNumber(customerEntity.getPassportNumber())
                .enrollmentDate(customerEntity.getEnrollmentDate())
                .active(customerEntity.getActive())
                .build();
    }
}
