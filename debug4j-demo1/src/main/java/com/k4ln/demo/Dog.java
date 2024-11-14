package com.k4ln.demo;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Dog {

    private String name;

    private int age;
}
