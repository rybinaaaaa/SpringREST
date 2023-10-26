package com.rybina.firstRestAPI.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class PersonDTO {
    @NotEmpty(message = "name should nit be empty")
    @Size(min = 2, max = 5, message = "name should be between 2 and 5 letters")
    private String name;

    @Min(value = 0, message = "age should be greater than zero")
    private int age;

    @Email
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
