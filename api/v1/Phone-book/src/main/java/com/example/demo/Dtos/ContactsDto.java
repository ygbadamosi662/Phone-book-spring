package com.example.demo.Dtos;

import com.example.demo.Enums.Gender;
import com.example.demo.Enums.Relationship;
import com.example.demo.Enums.Type;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactsDto
{
    private boolean count = false;

    @Size(min=3,max=25)
    private String fullname;

    @Size(min=3,max=25)
    private String aka;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Enumerated(EnumType.STRING)
    private Relationship relation;

    @Email(message = "Enter a valid email, eg: example6_@gmail.com",
            regexp = "[a-z]{2,30}@[a-z]{2,10}\\.[a-z]{2,10}")
    private String email;

    private int lastHours = 0;

    private int page = 1;

    private int size = 3;

    public boolean getCount() {
        return this.count;
    }
}
