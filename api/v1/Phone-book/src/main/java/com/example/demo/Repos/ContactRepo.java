package com.example.demo.Repos;

import com.example.demo.Models.Contact;
import com.example.demo.Models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ContactRepo extends JpaRepository<Contact,Long>
{
    boolean existsByPhoneAndUser(String phone, User user);

    boolean existsByFullnameAndUser(String phone, User user);

    boolean existsByIdAndUser(long id, User user);

    long count(Specification<Contact> spec);

    Page<Contact> findAll(Specification<Contact> spec, Pageable pageable);

    List<Contact> findAll(Specification<Contact> spec);
}
