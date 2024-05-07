package com.example.demo.Dtos.ResponseDtos;

import com.example.demo.Enums.Gender;
import com.example.demo.Enums.Relationship;
import com.example.demo.Enums.Type;
import com.example.demo.Models.Contact;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
public class ContactResponseDto
{
    private long id;

    private String fullname;

    private String aka;

    private String email;

    private String phone;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Enumerated(EnumType.STRING)
    private Relationship relation;

    private LocalDateTime created_on;

    private LocalDateTime updated_on;


    public ContactResponseDto (Contact contact)
    {
        this.id = contact.getId();
        this.aka = contact.getAka();
        this.email = contact.getEmail();
        this.phone = contact.getPhone();
        this.fullname = contact.getFullname();
        this.relation = contact.getRelation();
        this.type = contact.getType();
        this.gender = contact.getGender();
        this.created_on = contact.getCreatedOn();
        this.updated_on = contact.getUpdatedOn();
    }
}
