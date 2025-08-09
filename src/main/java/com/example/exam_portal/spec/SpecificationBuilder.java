package com.example.exam_portal.spec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.domain.Specification; // với Spring Boot 3+

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

public class SpecificationBuilder<T> {
    public Specification<T> buildFromParams(Map<String, String> params) {
        List<FilterCriteria> filters = new ArrayList<>();
        
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
        
            String[] parts = key.split("\\.");
            if (parts.length == 2) {
                // Ví dụ: "student.fullName"
                filters.add(new FilterCriteria(key, "contains", value));
            } else {
                filters.add(new FilterCriteria(key, "contains", value));
            }
        }
    
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (FilterCriteria criteria : filters) {
                String key = criteria.getKey();
                String op = criteria.getOperation();
                Object value = criteria.getValue();

                Path<?> path;
                if (key.contains(".")) {
                    String[] nested = key.split("\\.");
                    path = root.join(nested[0]).get(nested[1]);
                } else {
                    path = root.get(key);
                }

                switch (op) {
                    case "equals":
                        predicates.add(cb.equal(path, value));
                        break;
                    case "contains":
                        predicates.add(cb.like(cb.lower(path.as(String.class)),
                                               "%" + value.toString().toLowerCase() + "%"));
                        break;
                    case "gte":
                        predicates.add(cb.greaterThanOrEqualTo(path.as(String.class), value.toString()));
                        break;
                    case "lte":
                        predicates.add(cb.lessThanOrEqualTo(path.as(String.class), value.toString()));
                        break;
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

