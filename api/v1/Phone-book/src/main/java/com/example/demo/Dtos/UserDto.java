package com.example.demo.Dtos;

import com.example.demo.Enums.Gender;
import com.example.demo.Enums.Role;
import com.example.demo.Enums.Status;
import com.example.demo.Models.User;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto
{
    @Size(min=3,max=25)
    @NotNull
    @NotBlank
    private String fname;

    @Size(min=3,max=25)
    @NotNull
    @NotBlank
    private String lname;

    @Size(min = 8,max = 15)
    @NotBlank
    @NotNull
    private String password;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @Size(min=11)
    @Email(message = "Must follow this pattern 'dfshghf@hdfgf.com'")
    @NotNull
    private String email;

    @NotNull
    @NotBlank
    @Size(min=10,max=10)
    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    public User getUser ()
    {
        User user = new User();
        user.setEmail(this.email);
        user.setFname(this.fname);
        user.setLname(this.lname);
        user.setRole(this.role);
        user.setPassword(this.password);
        user.setGender(this.gender);
        user.setPhone(this.phone);
        user.setStatus(this.status);

        return user;
    }
}
