package ru.javawebinar.topjava.repository.mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.Util;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@Repository
public class InMemoryMealRepositoryImpl implements MealRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryMealRepositoryImpl.class);

    // Map  userId -> (mealId-> meal)
    private Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.MEALS.forEach(meal -> save(meal, USER_ID));

        save(new Meal(LocalDateTime.of(2018, Month.JUNE, 1, 9, 0), "Админ завтрак", 510), ADMIN_ID);
        save(new Meal(LocalDateTime.of(2018, Month.JUNE, 1, 14, 0), "Админ обед", 1325), ADMIN_ID);
        save(new Meal(LocalDateTime.of(2018, Month.JUNE, 1, 19, 0), "Админ ужин", 600), ADMIN_ID);

        save(new Meal(LocalDateTime.of(2018, Month.JUNE, 2, 8, 0), "Админ завтрак", 300), ADMIN_ID);
        save(new Meal(LocalDateTime.of(2018, Month.JUNE, 2, 13, 0), "Админ обед", 800), ADMIN_ID);
        save(new Meal(LocalDateTime.of(2018, Month.JUNE, 2, 18, 0), "Админ ужин", 100), ADMIN_ID);

        save(new Meal(LocalDateTime.of(2018, Month.JUNE, 1, 8, 0), "Пользователь завтрак", 400), USER_ID);
        save(new Meal(LocalDateTime.of(2018, Month.JUNE, 1, 14, 0), "Пользователь обед", 500), USER_ID);
        save(new Meal(LocalDateTime.of(2018, Month.JUNE, 1, 18, 0), "Пользователь ужин", 900), USER_ID);

        save(new Meal(LocalDateTime.of(2018, Month.JUNE, 1, 10, 0), "Пользователь завтрак", 150), USER_ID);
        save(new Meal(LocalDateTime.of(2018, Month.JUNE, 1, 15, 0), "Пользователь обед", 1200), USER_ID);
        save(new Meal(LocalDateTime.of(2018, Month.JUNE, 1, 20, 0), "Пользователь ужин", 900), USER_ID);
    }


    @Override
    public Meal save(Meal meal, int userId) {
        Objects.requireNonNull(meal);
        Map<Integer, Meal> meals = repository.computeIfAbsent(userId, ConcurrentHashMap::new);
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            meals.put(meal.getId(), meal);
            return meal;
        }
        return meals.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @PostConstruct
    public void postConstruct() {
        log.info("+++ PostConstruct");
    }

    @PreDestroy
    public void preDestroy() {
        log.info("+++ PreDestroy");
    }

    @Override
    public boolean delete(int id, int userId) {
        Map<Integer, Meal> meals = repository.get(userId);
        return meals != null && meals.remove(id) != null;
    }

    @Override
    public Meal get(int id, int userId) {
        Map<Integer, Meal> meals = repository.get(userId);
        return meals == null ? null : meals.get(id);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return getAllFiltered(userId, meal -> true);
    }

    @Override
    public List<Meal> getBetween(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        Objects.requireNonNull(startDateTime);
        Objects.requireNonNull(endDateTime);
        return getAllFiltered(userId, meal -> Util.isBetween(meal.getDateTime(), startDateTime, endDateTime));
    }

    private List<Meal> getAllFiltered(int userId, Predicate<Meal> filter) {
        Map<Integer, Meal> meals = repository.get(userId);
        return CollectionUtils.isEmpty(meals) ? Collections.emptyList() :
                meals.values().stream()
                        .filter(filter)
                        .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                        .collect(Collectors.toList());
    }
}