package com.tenco.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Student {
    private int id;
    private String name;
    private String studentId;
}
