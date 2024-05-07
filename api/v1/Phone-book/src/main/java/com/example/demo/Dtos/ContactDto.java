package com.example.demo.Dtos;

import com.example.demo.Enums.*;
import com.example.demo.Models.Contact;
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
public class ContactDto
{
    @Size(min=3,max=25)
    @NotNull
    @NotBlank
    private String fullname;

    @Size(min=3,max=25)
    private String aka;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private Type type = Type.OTHER;

    @Enumerated(EnumType.STRING)
    private Relationship relation = Relationship.OTHER;

    @Size(min=11)
    @Email(message = "Must follow this pattern 'dfshghf@hdfgf.com'")
    private String email;

    @NotNull
    @NotBlank
    @Size(min=10,max=10)
    private String phone;

    public Contact getContact (User user)
    {
        Contact contact = new Contact();
        contact.setAka(this.aka);
        contact.setFullname(this.fullname);
        contact.setGender(this.gender);
        contact.setEmail(this.email);
        contact.setType(this.type);
        contact.setPhone(this.phone);
        contact.setRelation(this.relation);
        contact.setUser(user);

        return contact;
    }
}

