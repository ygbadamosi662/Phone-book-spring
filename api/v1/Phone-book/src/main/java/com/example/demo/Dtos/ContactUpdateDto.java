package com.example.demo.Dtos;

import com.example.demo.Enums.Gender;
import com.example.demo.Enums.Relationship;
import com.example.demo.Enums.Type;
import com.example.demo.Models.Contact;
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
public class ContactUpdateDto
{
    @NotNull
    private long id;

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

    @Size(min=10,max=10)
    private String phone;

    public Contact updateContact (Contact contact)
    {
        if(this.aka == null && this.fullname == null &&
                this.gender == null && this.email == null && this.type == null &&
                this.phone == null && this.relation == null) {
            return null;

        }

        if(this.aka != null) {
            contact.setAka(this.aka);
        }

        if(this.fullname != null) {
            contact.setFullname(this.fullname);
        }

        if(this.gender != null) {
            contact.setGender(this.gender);
        }

        if(this.email != null) {
            contact.setEmail(this.email);
        }

        if(this.type != null) {
            contact.setType(this.type);
        }

        if(this.phone != null) {
            contact.setPhone(this.phone);
        }

        if(this.relation != null) {
            contact.setRelation(this.relation);
        }

        return contact;
    }
}
