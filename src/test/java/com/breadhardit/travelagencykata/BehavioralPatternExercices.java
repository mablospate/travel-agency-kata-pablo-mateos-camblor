package com.breadhardit.travelagencykata;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Slf4j
public class BehavioralPatternExercices {
    @Data
    public static class Travel {
        private static final List<String> SCHENGEN_COUNTRIES =
                List.of("Spain", "France", "Iceland", "Italy", "Portugal");

        private final String id;
        private final String name;
        private final String origin;
        private final String destination;

        public boolean isDomestic() {
            return origin.equalsIgnoreCase(destination);
        }

        public boolean isWithinSchengen() {
            return SCHENGEN_COUNTRIES.stream().anyMatch(origin::equalsIgnoreCase)
                    && SCHENGEN_COUNTRIES.stream().anyMatch(destination::equalsIgnoreCase);
        }
    }

    private interface DocumentScanningStrategy {
        boolean supports(Travel travel);

        void scan();
    }

    private static final class IdentityDocumentStrategy implements DocumentScanningStrategy {
        @Override
        public boolean supports(Travel travel) {
            return travel.isDomestic();
        }

        @Override
        public void scan() {
            scanDNI();
        }
    }

    private static final class PassportStrategy implements DocumentScanningStrategy {
        @Override
        public boolean supports(Travel travel) {
            return travel.isWithinSchengen();
        }

        @Override
        public void scan() {
            scanPassport();
        }
    }

    private static final class VisaStrategy implements DocumentScanningStrategy {
        @Override
        public boolean supports(Travel travel) {
            return !travel.isDomestic() && !travel.isWithinSchengen();
        }

        @Override
        public void scan() {
            scanVisa();
        }
    }

    private static final class TravelDocumentProcessor {
        private final List<DocumentScanningStrategy> strategies = List.of(
                new IdentityDocumentStrategy(),
                new PassportStrategy(),
                new VisaStrategy()
        );

        void scanRequiredDocument(Travel travel) {
            strategies.stream()
                    .filter(strategy -> strategy.supports(travel))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No document strategy for travel"))
                    .scan();
        }
    }

    private static void scanVisa() {
        log.info("Applying visa...");
    }

    private static void scanDNI() {
        log.info("Applying DNI...");
    }

    private static void scanPassport() {
        log.info("Applying passport...");
    }

    @Test
    void travelAgencyScansTheDocumentRequiredForEachTravel() {
        TravelDocumentProcessor processor = new TravelDocumentProcessor();
        List<Travel> travels = List.of(
                new Travel(UUID.randomUUID().toString(), "PYRAMIDS TOUR", "Spain", "Egypt"),
                new Travel(UUID.randomUUID().toString(), "LISBOA TOUR", "Spain", "Portugal"),
                new Travel(UUID.randomUUID().toString(), "LOCAL TOUR", "Portugal", "Portugal")
        );

        travels.forEach(processor::scanRequiredDocument);
    }

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
        private final ConcurrentHashMap<String, Employee> employees = new ConcurrentHashMap<>();
        private final CopyOnWriteArrayList<Consumer<Employee>> observers = new CopyOnWriteArrayList<>();

        public void subscribe(Consumer<Employee> observer) {
            observers.add(observer);
        }

        public void addEmployee(Employee employee) {
            employees.put(employee.getId(), employee);
            observers.forEach(observer -> observer.accept(employee));
        }
    }

    public static class GreetingsNotificator implements Consumer<Employee> {
        public GreetingsNotificator(EmployeesRepository employeesRepository) {
            employeesRepository.subscribe(this);
        }

        @Override
        public void accept(Employee employee) {
            if (!employee.getGreetingDone()) {
                log.info("Notifying {}", employee);
                employee.setGreetingDone(Boolean.TRUE);
            }
        }
    }

    @Test
    void employeeIsNotifiedWhenAddedWithoutPolling() {
        EmployeesRepository employeesRepository = new EmployeesRepository();
        new GreetingsNotificator(employeesRepository);
        Employee employee = Employee.builder().id("1").name("Pepe").email("pepe@example.com").build();

        employeesRepository.addEmployee(employee);

        Assertions.assertTrue(employee.getGreetingDone());
    }
}
