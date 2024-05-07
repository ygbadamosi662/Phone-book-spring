package com.example.demo.Dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgetPasswordDto
{
    @NotNull
    @NotBlank
    private String email;

    @NotNull
    @NotBlank
    private String frontUrl;
}
