package com.example.exam_portal.domain.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SoldCourseResponse {
    private String name;
    private Float price;

    public SoldCourseResponse(String name, Float price) {
        this.name = name;
        this.price = price;
    }
}
