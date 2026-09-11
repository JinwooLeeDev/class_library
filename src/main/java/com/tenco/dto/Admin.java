package com.tenco.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "password") // toString 출력할 때 password 부분을 뺌.
public class Admin {
    private int id;
    private String adminId;
    private String password;
    private String name;
}
