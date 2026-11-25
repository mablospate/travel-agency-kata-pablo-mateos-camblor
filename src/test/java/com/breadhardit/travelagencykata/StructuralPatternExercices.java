package com.breadhardit.travelagencykata;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

@Slf4j
public class StructuralPatternExercices {
    public interface MenuItem {
        long getCalories();
    }

    @Value
    public static class Food implements MenuItem {
        String name;
        Long caloriesPer100g;
        Long weight;

        @Override
        public long getCalories() {
            return caloriesPer100g * weight / 100;
        }
    }

    @Value
    public static class Dish implements MenuItem {
        String name;
        List<Food> foodList;

        @Override
        public long getCalories() {
            return foodList.stream().mapToLong(Food::getCalories).sum();
        }
    }

    public long getCalories(List<? extends MenuItem> menu) {
        return menu.stream().mapToLong(MenuItem::getCalories).sum();
    }

    @Test
    void menuCaloriesIncludeDishesAndIndividualFood() {
        Food potato = new Food("POTATO", 80L, 300L);
        Food bread = new Food("BREAD", 320L, 100L);
        Food tomato = new Food("TOMATO", 85L, 50L);
        Food burger = new Food("BURGER", 340L, 120L);
        Food lettuce = new Food("LETTUCE", 16L, 120L);
        Food apple = new Food("APPLE", 34L, 180L);
        Food ketchup = new Food("KETCHUP", 180L, 15L);
        Dish completeBurger = new Dish("COMPLETE BURGER", List.of(potato, bread, burger));
        Dish greenSalad = new Dish("GREEN SALAD", List.of(lettuce, tomato));
        List<MenuItem> menu = List.of(greenSalad, completeBurger, ketchup, apple);

        long calories = getCalories(menu);

        Assertions.assertEquals(1117L, calories);
        log.info("Calories: {}", calories);
    }
}
