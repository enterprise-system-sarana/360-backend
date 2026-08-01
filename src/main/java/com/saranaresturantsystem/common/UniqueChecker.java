package com.saranaresturantsystem.common;

import com.saranaresturantsystem.execption.DuplicateResourceException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
@Component
public class UniqueChecker {

    public <T> void verify(JpaRepository<T, ?> repo, T entity, String fieldName, Object newValue) {

        // Get all fields to ignore (everything except the field we're checking)
        List<String> allFields = Arrays.stream(entity.getClass().getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toList());

        List<String> ignoredFields = allFields.stream()
                .filter(f -> !f.equals(fieldName))
                .collect(Collectors.toList());

        // Temporarily set the new value on entity for Example query
        Object originalValue = getFieldValue(entity, fieldName);
        setFieldValue(entity, fieldName, newValue);

        try {
            ExampleMatcher matcher = ExampleMatcher.matching()
                    .withIgnoreNullValues()
                    .withIgnorePaths(ignoredFields.toArray(new String[0]))
                    .withMatcher(fieldName, ExampleMatcher.GenericPropertyMatchers.exact());

            Object entityId = getEntityId(entity);
            boolean duplicated = repo.findAll(Example.of(entity, matcher)).stream()
                    .anyMatch(found -> !Objects.equals(getEntityId(found), entityId));

            if (duplicated) {
                throw new DuplicateResourceException(
                        fieldName + " '" + newValue + "' is already exits"
                );
            }
        } finally {
            // Always restore original value after check
            setFieldValue(entity, fieldName, originalValue);
        }
    }

    private Object getEntityId(Object target) {
        if (target == null) return null;
        Class<?> current = target.getClass();
        while (current != null) {
            try {
                Field idField = current.getDeclaredField("id");
                idField.setAccessible(true);
                return idField.get(target);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            } catch (IllegalAccessException ignored) {
                return null;
            }
        }
        return null;
    }

    private Object getFieldValue(Object target, String fieldName) {
        Class<?> current = target.getClass();
        while (current != null) {
            try {
                Field field = current.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(target);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            } catch (IllegalAccessException ignored) {
                return null;
            }
        }
        return null;
    }

    private void setFieldValue(Object target, String fieldName, Object value) {
        Class<?> current = target.getClass();
        while (current != null) {
            try {
                Field field = current.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(target, value);
                return;
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            } catch (IllegalAccessException ignored) {
                return;
            }
        }
    }
}