package com.inwi.KpiDashboardAuth.dtos;


import lombok.Data;

@Data
public class RegisterUserDto {
    private String email;
    private String password;
    //private String fullName;
    private String firstName;
    private String lastName;
    
}