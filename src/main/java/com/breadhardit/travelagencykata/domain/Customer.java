package com.breadhardit.travelagencykata.domain;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class Customer {
    String id;
    String name;
    String surnames;
    LocalDate birthDate;
    String passportNumber;
    LocalDate enrollmentDate;
    Boolean active;

    public Customer(String id, String name, String surnames, LocalDate birthDate, String passportNumber, LocalDate enrollmentDate, Boolean active) {
        this.id = id;
        this.name = name;
        this.surnames = surnames;
        this.birthDate = birthDate;
        this.passportNumber = passportNumber;
        this.enrollmentDate = enrollmentDate;
        this.active = active;
    }
}
