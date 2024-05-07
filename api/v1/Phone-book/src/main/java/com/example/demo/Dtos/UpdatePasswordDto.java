package com.example.demo.Dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordDto
{
    @NotNull
    @NotBlank
    private String token;

    @NotNull
    @NotBlank
    private String new_password;
}
