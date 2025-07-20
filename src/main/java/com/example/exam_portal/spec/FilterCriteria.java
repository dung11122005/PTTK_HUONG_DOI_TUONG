package com.example.exam_portal.spec;

public class FilterCriteria {
    private String key;
    private String operation;
    private Object value;

    public FilterCriteria(String key, String operation, Object value) {
        this.key = key;
        this.operation = operation;
        this.value = value;
    }

    public String getKey() { return key; }
    public String getOperation() { return operation; }
    public Object getValue() { return value; }
}
