package com.breadhardit.travelagencykata;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class CreationalPatternExercices {
    /*
     * Banking accounts has movements. And each movement can be a deposit or withdrawal
     * After a few months operating, we need to create new types of movements:
     *   - TRANSFER: It's a withdrawal, bet we need the destination account number
     *   - ANNULMENT: It cancels a movement, so, we need the original movement
     */
    public static abstract class Movement {
        String id;
        Long amount;
        String description;

        public Movement(Long amount, String description){
            this.id = UUID.randomUUID().toString();
            this.amount = amount;
            this.description = description;
        }

        public String getId() {
            return id;
        }

        public Long getAmount() {
            return amount;
        }

        public String getDescription() {
            return description;
        }
    }

    public static class Deposit extends Movement{

        public Deposit(Long amount, String description) {
            super(amount, description);
        }
    }

    public static class Withdrawal extends Movement{

        public Withdrawal(Long amount, String description) {
            super(amount, description);
        }

        public Long getAmount() {
            return -super.getAmount();
        }
    }

    @Data
    @RequiredArgsConstructor
    public static class Account {
        public static final ConcurrentHashMap<String,Movement> MOVEMENTS = new ConcurrentHashMap<>();
        final String id;
        Long balance = 0L;
        public void addMovement(Movement movement) {
            MOVEMENTS.put(movement.getId(),movement);
            balance += movement.getAmount();
            log.info("Current balance: {}",balance);
        }
    }
    @Test
    public void test() {
        Account account = new Account(UUID.randomUUID().toString());
        account.addMovement(new Deposit(1000L, "INGRESO"));
        account.addMovement(new Withdrawal(10L, "GASTOS VARIOS"));
    }
    /* TODO
        Made the refactor to create new movement types, and avoid scalability issues applying the proper creational pattern.
        Remember, our code MUST follow SOLID Principles, so refactor the classes you need to accomplish it
     */


}
