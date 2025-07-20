package com.example.exam_portal.spec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.domain.Specification; // với Spring Boot 3+

import jakarta.persistence.criteria.Predicate;

public class SpecificationBuilder<T> {
    public Specification<T> buildFromParams(Map<String, String> params) {
        List<FilterCriteria> filters = new ArrayList<>();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            String[] parts = entry.getKey().split("\\.");
            if (parts.length == 2) {
                filters.add(new FilterCriteria(parts[0], parts[1], entry.getValue()));
            }
        }

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (FilterCriteria criteria : filters) {
                String key = criteria.getKey();
                String op = criteria.getOperation();
                Object value = criteria.getValue();

                switch (op) {
                    case "equals":
                        predicates.add(cb.equal(root.get(key), value));
                        break;
                    case "contains":
                        predicates.add(cb.like(cb.lower(root.get(key)), "%" + value.toString().toLowerCase() + "%"));
                        break;
                    case "gte":
                        predicates.add(cb.greaterThanOrEqualTo(root.get(key), value.toString()));
                        break;
                    case "lte":
                        predicates.add(cb.lessThanOrEqualTo(root.get(key), value.toString()));
                        break;
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
