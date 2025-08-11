package com.example.exam_portal.domain.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SoldCourseResponse {
    private Long id;
    private String name;
    private Float price;

    public SoldCourseResponse(Long id, String name, Float price) {
        this.id=id;
        this.name = name;
        this.price = price;
    }
}
