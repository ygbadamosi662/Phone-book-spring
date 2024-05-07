package com.example.demo.Models;

import com.example.demo.Enums.Gender;
import com.example.demo.Enums.Relationship;
import com.example.demo.Enums.Role;
import com.example.demo.Enums.Type;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Contact
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column( name="contact_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    private String fullname;

    @Nullable
    private String aka;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private Relationship relation;

    @Column(unique = true)
    private String phone;

    private String email;

    @Enumerated(EnumType.STRING)
    private Type type;

    @CreationTimestamp
    private LocalDateTime createdOn;

    @UpdateTimestamp
    private LocalDateTime updatedOn;
}
