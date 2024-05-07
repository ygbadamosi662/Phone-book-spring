package com.example.demo.Dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDto
{
    @NotNull
    @NotBlank
    @Email(message = "Enter a valid email, eg: example6_@gmail.com",
            regexp = "[a-z]{2,30}@[a-z]{2,10}\\.[a-z]{2,10}")
    private String email;

    @NotNull
    @NotBlank
    private String password;
}
