package com.breadhardit.travelagencykata;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class CreationalPatternExercices {
    public abstract static class Movement {
        protected final String id;
        protected final long amount;
        protected final String description;

        private Movement(String id, long amount, String description) {
            if (amount <= 0) {
                throw new IllegalArgumentException("Movement amount must be positive");
            }
            this.id = id;
            this.amount = amount;
            this.description = description;
        }

        abstract void apply(Account account);

        abstract void reverse(Account account);
    }

    private static final class Deposit extends Movement {
        private Deposit(String id, long amount, String description) {
            super(id, amount, description);
        }

        @Override
        void apply(Account account) {
            account.credit(amount);
        }

        @Override
        void reverse(Account account) {
            account.debit(amount);
        }
    }

    private static final class Withdrawal extends Movement {
        private Withdrawal(String id, long amount, String description) {
            super(id, amount, description);
        }

        @Override
        void apply(Account account) {
            account.debit(amount);
        }

        @Override
        void reverse(Account account) {
            account.credit(amount);
        }
    }

    private static final class Transfer extends Movement {
        private final Account destination;

        private Transfer(String id, long amount, String description, Account destination) {
            super(id, amount, description);
            this.destination = destination;
        }

        @Override
        void apply(Account account) {
            account.debit(amount);
            destination.credit(amount);
        }

        @Override
        void reverse(Account account) {
            destination.debit(amount);
            account.credit(amount);
        }
    }

    private static final class Annulment extends Movement {
        private final Movement originalMovement;

        private Annulment(String id, String description, Movement originalMovement) {
            super(id, originalMovement.amount, description);
            this.originalMovement = originalMovement;
        }

        @Override
        void apply(Account account) {
            originalMovement.reverse(account);
        }

        @Override
        void reverse(Account account) {
            originalMovement.apply(account);
        }
    }

    public static final class MovementFactory {
        private MovementFactory() {
        }

        public static Movement deposit(String id, long amount, String description) {
            return new Deposit(id, amount, description);
        }

        public static Movement withdrawal(String id, long amount, String description) {
            return new Withdrawal(id, amount, description);
        }

        public static Movement transfer(String id, long amount, String description, Account destination) {
            return new Transfer(id, amount, description, destination);
        }

        public static Movement annulment(String id, String description, Movement originalMovement) {
            return new Annulment(id, description, originalMovement);
        }
    }

    @Getter
    public static final class Account {
        private final String id;
        private final Map<String, Movement> movements = new HashMap<>();
        private long balance;

        public Account(String id) {
            this.id = id;
        }

        public void addMovement(Movement movement) {
            if (movements.containsKey(movement.id)) {
                throw new IllegalArgumentException("Movement already applied: " + movement.id);
            }
            movement.apply(this);
            movements.put(movement.id, movement);
            log.info("Movement {} ({}): current balance is {}", movement.id, movement.description, balance);
        }

        private void credit(long amount) {
            balance += amount;
        }

        private void debit(long amount) {
            if (balance < amount) {
                throw new IllegalStateException("Insufficient funds in account " + id);
            }
            balance -= amount;
        }
    }

    @Test
    void factoryCreatesAllMovementTypesAndAppliesTheirEffects() {
        Account source = new Account(UUID.randomUUID().toString());
        Account destination = new Account(UUID.randomUUID().toString());

        source.addMovement(MovementFactory.deposit("deposit", 1000, "Income"));
        Movement transfer = MovementFactory.transfer("transfer", 300, "Transfer", destination);
        source.addMovement(transfer);
        source.addMovement(MovementFactory.withdrawal("withdrawal", 100, "Expenses"));
        source.addMovement(MovementFactory.annulment("annulment", "Cancel transfer", transfer));

        Assertions.assertEquals(900, source.getBalance());
        Assertions.assertEquals(0, destination.getBalance());
    }
}
