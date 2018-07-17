package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.model.Role;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.Month;

import static ru.javawebinar.topjava.UserTestData.USER_ID;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {
    public static int meal_id = START_SEQ;

    public static Meal createTestMeal(int id, LocalDateTime dateTime, String description, int calories) {
        return new Meal(id, dateTime, description, calories);
    }

    public static void assertMatch(Meal actual, Meal expected) {
    }
}
