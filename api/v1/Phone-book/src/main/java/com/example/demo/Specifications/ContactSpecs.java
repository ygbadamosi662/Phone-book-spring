package com.example.demo.Specifications;

import com.example.demo.Enums.Gender;
import com.example.demo.Enums.Relationship;
import com.example.demo.Enums.Type;
import com.example.demo.Models.Contact;
import com.example.demo.Models.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

public class ContactSpecs
{
    public static Specification<Contact> fullnameEquals(String fullname) {
        return (root, query, cb) -> cb.equal(root.get("fullname"), fullname);
    }

    public static Specification<Contact> akaEquals(String aka) {
        return (root, query, cb) -> cb.equal(root.get("aka"), aka);
    }

    public static Specification<Contact> genderEquals(Gender gender) {
        return (root, query, cb) -> cb.equal(root.get("gender"), gender);
    }

    public static Specification<Contact> typeEquals(Type type) {
        return (root, query, cb) -> cb.equal(root.get("type"), type);
    }

    public static Specification<Contact> relationEquals(Relationship relations) {
        return (root, query, cb) -> cb.equal(root.get("relations"), relations);
    }

    public static Specification<Contact> userEquals(User user) {
        return (root, query, cb) -> cb.equal(root.get("user"), user);
    }

    public static Specification<Contact> createdBefore(LocalDateTime before) {
        return (root, query, cb) -> cb.lessThan(root.get("createdOn"), before);
    }

    public static Specification<Contact> searchContacts(String search) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(search)) {
                return null;
            }
            String pattern = "%" + search + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("fullname")), pattern.toLowerCase()),
                    cb.like(cb.lower(root.get("aka")), pattern.toLowerCase()),
                    cb.like(cb.lower(root.get("phone")), pattern.toLowerCase()),
                    cb.like(cb.lower(root.get("email")), pattern.toLowerCase())
            );
        };
    }
}
