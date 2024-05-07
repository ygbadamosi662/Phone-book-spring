package com.example.demo.Controllers;

import com.example.demo.Dtos.*;
import com.example.demo.Dtos.ResponseDtos.ContactResponseDto;
import com.example.demo.Dtos.ResponseDtos.UserResponseDto;
import com.example.demo.Enums.Role;
import com.example.demo.Enums.Status;
import com.example.demo.Models.Contact;
import com.example.demo.Models.JwtBlacklist;
import com.example.demo.Models.User;
import com.example.demo.Repos.ContactRepo;
import com.example.demo.Repos.JwtBlacklistRepo;
import com.example.demo.Repos.UserRepo;
import com.example.demo.Services.EmailService;
import com.example.demo.Services.JwtService;
import com.example.demo.Specifications.ContactSpecs;
import com.example.demo.Specifications.UserSpecs;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController
{
    private final EmailService emailService;

    private final JwtService jwtService;

    @Autowired
    private final UserRepo userRepo;

    private final AuthenticationManager authenticationManager;

    @Autowired
    private final ContactRepo contactRepo;


    @PostMapping("/super/role/update")
    public ResponseEntity<?> updateRole(@Valid @RequestBody UpdateRoleDto dto, HttpServletRequest req)
    {
        try {
            String jwt = jwtService.setJwt(req);
            User authUser = jwtService.getUser(jwt);
            Optional<User> chk = userRepo.findByEmail(dto.getEmail());
            if(chk.isPresent() == false) {
                return new ResponseEntity<>("User does not exist", HttpStatus.BAD_REQUEST);
            }
            User user = chk.get();
            if(authUser.getRole().equals(Role.SUPER_ADMIN) == false) {
                return new ResponseEntity<>("Invalid Credentials", HttpStatus.BAD_REQUEST);
            }
            if(user.getRole().equals(dto.getRole())) {
                String msg = "User is already "+ (user.getRole().equals(Role.SUPER_ADMIN) ?
                        "a " : "an ")+user.getRole().name();
                return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
            }
            user.setRole(dto.getRole());
            user = userRepo.save(user);
            String body = "Your account role have been updated to "+user.getRole().name();
            emailService.sendEmail(user.getEmail(), "Role updated", body);

            UserResponseDto resDto = new UserResponseDto(user);

            return ResponseEntity.ok(resDto);
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Its not you, its us", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/contact/contacts")
    public ResponseEntity<?> getContacts(@Valid @RequestBody ContactsDto dto)
    {
        try {
            Specification<Contact> spec = Specification.where(null);

            boolean all = true;

            if (dto.getFullname() != null) {
                spec = spec.and(ContactSpecs.fullnameEquals(dto.getFullname()));
                all = false;
            }
            if(dto.getAka() != null) {
                spec = spec.and(ContactSpecs.akaEquals(dto.getAka()));
                if(all == true) {
                    all = false;
                }
            }
            if(dto.getGender() != null) {
                spec = spec.and(ContactSpecs.genderEquals(dto.getGender()));
                if(all == true) {
                    all = false;
                }
            }
            if(dto.getType() != null) {
                spec = spec.and(ContactSpecs.typeEquals(dto.getType()));
                if(all == true) {
                    all = false;
                }
            }
            if(dto.getRelation() != null) {
                spec = spec.and(ContactSpecs.relationEquals(dto.getRelation()));
                if(all == true) {
                    all = false;
                }
            }
            if(dto.getLastHours() == 0) {
                LocalDateTime before = LocalDateTime.now();
                before = before.minusHours(dto.getLastHours());
                spec = spec.and(ContactSpecs.createdBefore(before));
                if(all == true) {
                    all = false;
                }
            }

            if(dto.getCount()) {
                long count = contactRepo.count(spec);
                return ResponseEntity.ok(count);
            } else {
                Pageable pageRequest = PageRequest.of(dto.getPage() - 1,
                        dto.getSize(), Sort.by("createdOn").descending());
                Page<Contact> contactsPage = null;
                if(all) {
                    contactsPage = contactRepo.findAll(pageRequest);
                } else {
                    contactsPage = contactRepo.findAll(spec, pageRequest);
                }
                List<Contact> contacts = contactsPage.getContent();

                List<ContactResponseDto> contactDtos = new ArrayList<>();

                contacts
                        .stream()
                        .map((contact) -> {
                            ContactResponseDto conRes = new ContactResponseDto(contact);
                            contactDtos.add(conRes);
                            return contact;
                        })
                        .collect(Collectors.toList());;

                Map<String, Object> resDto = new HashMap<>();
                resDto.put("contacts", contactDtos);
                resDto.put("total", contactsPage.getTotalElements());
                resDto.put("totalPages", contactsPage.getTotalPages());
                resDto.put("haveNextPage", contactsPage.hasNext());
                resDto.put("havePrevPage", contactsPage.hasPrevious());
                resDto.put("currentPage", dto.getPage());
                resDto.put("size", contactsPage.getSize());

                return ResponseEntity.ok(resDto);
            }

        } catch(Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Its not you, its us", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/contact/search")
    public ResponseEntity<?> searchContacts(@Valid @RequestParam String search)
    {
        try {
            Specification<Contact> spec = Specification.where(null);
            spec = spec.and(ContactSpecs.searchContacts(search));

            List<Contact> contacts = contactRepo.findAll(spec);

            List<ContactResponseDto> contactDtos = new ArrayList<>();

            contacts
                    .stream()
                    .map((contact) -> {
                        ContactResponseDto conRes = new ContactResponseDto(contact);
                        contactDtos.add(conRes);
                        return contact;
                    })
                    .collect(Collectors.toList());;

            return ResponseEntity.ok(contactDtos);

        } catch(Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Its not you, its us", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/users")
    public ResponseEntity<?> getUsers(@Valid @RequestBody UsersDto dto)
    {
        try {
            Specification<User> spec = Specification.where(null);

            boolean all = true;

            if (dto.getFname() != null) {
                spec = spec.and(UserSpecs.fnameEquals(dto.getFname()));
                all = false;
            }
            if(dto.getLname() != null) {
                spec = spec.and(UserSpecs.lnameEquals(dto.getLname()));
                if(all == true) {
                    all = false;
                }
            }
            if(dto.getGender() != null) {
                spec = spec.and(UserSpecs.genderEquals(dto.getGender()));
                if(all == true) {
                    all = false;
                }
            }
            if(dto.getStatus() != null) {
                spec = spec.and(UserSpecs.statusEquals(dto.getStatus()));
                if(all == true) {
                    all = false;
                }
            }
            if(dto.getRole() != null) {
                spec = spec.and(UserSpecs.roleEquals(dto.getRole()));
                if(all == true) {
                    all = false;
                }
            }
            if(dto.getLastHours() == 0) {
                LocalDateTime before = LocalDateTime.now();
                before = before.minusHours(dto.getLastHours());
                spec = spec.and(UserSpecs.createdBefore(before));
                if(all == true) {
                    all = false;
                }
            }

            if(dto.getCount()) {
                long count = userRepo.count(spec);
                return ResponseEntity.ok(count);
            } else {
                Pageable pageRequest = PageRequest.of(dto.getPage() - 1,
                        dto.getSize(), Sort.by("createdOn").descending());
                Page<User> usersPage = null;
                if(all) {
                    usersPage = userRepo.findAll(pageRequest);
                } else {
                    usersPage = userRepo.findAll(spec, pageRequest);
                }
                List<User> users = usersPage.getContent();

                List<UserResponseDto> userDtos = new ArrayList<>();

                users
                        .stream()
                        .map((user) -> {
                            UserResponseDto userRes = new UserResponseDto(user);
                            userDtos.add(userRes);
                            return user;
                        })
                        .collect(Collectors.toList());;

                Map<String, Object> resDto = new HashMap<>();
                resDto.put("users", userDtos);
                resDto.put("total", usersPage.getTotalElements());
                resDto.put("totalPages", usersPage.getTotalPages());
                resDto.put("haveNextPage", usersPage.hasNext());
                resDto.put("havePrevPage", usersPage.hasPrevious());
                resDto.put("currentPage", dto.getPage());
                resDto.put("size", usersPage.getSize());

                return ResponseEntity.ok(resDto);
            }

        } catch(Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Its not you, its us", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users/search")
    public ResponseEntity<?> searchUsers(@Valid @RequestParam String search)
    {
        try {
            Specification<User> spec = Specification.where(null);
            spec = spec.and(UserSpecs.searchUsers(search));

            List<User> users = userRepo.findAll(spec);

            List<UserResponseDto> userDtos = new ArrayList<>();

            users
                    .stream()
                    .map((user) -> {
                        UserResponseDto userRes = new UserResponseDto(user);
                        userDtos.add(userRes);
                        return user;
                    })
                    .collect(Collectors.toList());;

            return ResponseEntity.ok(userDtos);

        } catch(Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Its not you, its us", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
