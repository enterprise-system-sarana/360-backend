package com.saranaresturantsystem.common;

import com.saranaresturantsystem.execption.DuplicateResourceException;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Locale;

@Component
public class UniqueChecker {

    public <T> void verify(
            JpaRepository<T, ?> repository,
            T entity,
            String fieldName,
            Object value
    ) {

        if (!(repository instanceof JpaSpecificationExecutor<?>)) {
            throw new IllegalArgumentException("Repository must extend JpaSpecificationExecutor");
        }

        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        if (fieldName == null || fieldName.isBlank()) {
            throw new IllegalArgumentException("Field name cannot be null or blank");
        }


        if (value == null) {
            return;
        }

        @SuppressWarnings("unchecked") JpaSpecificationExecutor<T> specificationExecutor = (JpaSpecificationExecutor<T>) repository;

        Object currentId = getEntityId(entity);

        Specification<T> specification = (root, query, criteriaBuilder) -> {

            Path<?> fieldPath = root.get(fieldName);

            Predicate fieldPredicate;
            if (value instanceof String stringValue) {

                @SuppressWarnings("unchecked") Path<String> stringPath = (Path<String>) fieldPath;

                fieldPredicate = criteriaBuilder.equal(criteriaBuilder.lower(stringPath), stringValue.toLowerCase(Locale.ROOT));

            } else {
                fieldPredicate = criteriaBuilder.equal(fieldPath, value);
            }
                    if (currentId == null) {
                        return fieldPredicate;
                    }

            Predicate idPredicate = criteriaBuilder.notEqual(root.get("id"), currentId);

            return criteriaBuilder.and(fieldPredicate, idPredicate);
        };

        boolean exists = specificationExecutor.exists(specification);

        if (exists) {

            throw new DuplicateResourceException(fieldName + " '" + value + "' already exists");
        }
    }


    private Object getEntityId(Object target) {

        if (target == null) {
            return null;
        }

        Class<?> currentClass = target.getClass();

        while (currentClass != null) {

            try {

                Field idField = currentClass.getDeclaredField("id");

                idField.setAccessible(true);

                return idField.get(target);

            } catch (NoSuchFieldException e) {

                /*
                 * ID may be declared in BaseEntity.
                 */
                currentClass = currentClass.getSuperclass();

            } catch (IllegalAccessException e) {

                throw new IllegalStateException("Cannot access entity id", e);
            }
        }

        throw new IllegalArgumentException("Entity must have an 'id' field");
    }
}