package com.breadhardit.travelagencykata;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class BehavioralPatternExercices {
    /* EXERCISE 1
        Travels has an origin and a destination. Travels have some restrictions:
        - Travels with origin and destination in the same country require only Identity Document
        - Travels with origin and destination in schengen space, requires Passport
        - Travels with origin or destination out of schengen space, requires Visa
     */

    @Test
    // When customer buy a new Travel we have to scan the proper documentation
    public void travelAgency() {
        List<Travel> travels = List.of(
                travelFactory.createTravel("PYRAMIDS TOUR", "Spain", "EGYPT"),
                travelFactory.createTravel("LISBOA TOUR", "Spain", "Portugal"),
                travelFactory.createTravel("LISBOA TOUR", "Portugal", "Portugal")
        );
        for (Travel travel : travels) {
            travel.scanningStrategy.scan();
        }
    }

    @Test
    @SneakyThrows
    public void companyTest() {
        EmployeesRepository employeesRepository = new EmployeesRepository();
        GreetingsNotificator greetingsNotificator = new GreetingsNotificator(employeesRepository);
        new Thread(() -> greetingsNotificator.applyNotifications()).start();
        Thread.sleep(200);
        employeesRepository.addEmployee(Employee.builder().id("1").name("Pepe").email("pepe@pepemail.com").build());
        Thread.sleep(200);
        employeesRepository.addEmployee(Employee.builder().id("2").name("Juan").email("pepe@pepemail.com").build());
    }

    interface ScanningStrategy {
        void scan();
    }

    @Data
    public static class Travel {
        String id;
        String name;
        String origin;
        String destination;
        ScanningStrategy scanningStrategy;

        public Travel(String id, String name, String origin, String destination, ScanningStrategy scanningStrategy) {
            this.id = id;
            this.name = name;
            this.origin = origin;
            this.destination = destination;
            this.scanningStrategy = scanningStrategy;
        }
    }

    public static class SameCountryScan implements ScanningStrategy {
        public void scan() {
            log.info("Applying DNI...");
        }
    }

    public static class SchengenSpaceScan implements ScanningStrategy {
        public void scan() {
            log.info("Applying Passport");
        }
    }

    public static class VisaRequiredScan implements ScanningStrategy {
        public void scan() {
            log.info("Applying visa...");
        }
    }
    // Refactor code using the proper structural pattern

    public static class travelFactory {
        public static Travel createTravel(String name, String origin, String destination) {
            final List<String> SCHENGEN_COUNTRIES = List.of("Spain", "France", "Iceland", "Italy", "Portugal");
            String id = UUID.randomUUID().toString();
            ScanningStrategy scanningStrategy;
            if (origin.equals(destination)) {
                return new Travel(id, "PYRAMIDS TOUR", "Spain", "EGYPT", new SameCountryScan());
            } else if (SCHENGEN_COUNTRIES.contains(origin) && SCHENGEN_COUNTRIES.contains(destination)) {
                return new Travel(id, "PYRAMIDS TOUR", "Spain", "EGYPT", new SchengenSpaceScan());
            } else {
                return new Travel(id, "PYRAMIDS TOUR", "Spain", "EGYPT", new VisaRequiredScan());
            }
        }
    }

    /*
     * When a new employee is enrolled, company sends a greetings e-mail.
     * A notification service is querying the database every second looking for new employees to notify
     */
    @Builder
    @Data
    public static class Employee {
        final String id;
        final String name;
        final String email;
        @Builder.Default
        Boolean greetingDone = Boolean.FALSE;
    }

    public static class EmployeesRepository {
        private static final ConcurrentHashMap<String, Employee> EMPLOYEES = new ConcurrentHashMap<>();

        public void addEmployee(Employee employee) {
            EMPLOYEES.put(employee.getId(), employee);
            GreetingsNotificator.notify(employee);
        }
    }

    @Value
    @AllArgsConstructor
    public static class GreetingsNotificator {
        public static void notify(Employee employee){
            log.info("Notifying {}", employee);
            employee.setGreetingDone(Boolean.TRUE);
        }
    }
    // Use the proper behavioral pattern to avoid the continuous querying to database

}
