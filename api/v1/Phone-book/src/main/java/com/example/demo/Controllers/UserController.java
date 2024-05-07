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

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController
{
    private final JwtService jwtService;

    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final UserRepo userRepo;

    private final AuthenticationManager authenticationManager;

    @Autowired
    private final ContactRepo contactRepo;

    @Autowired
    private final JwtBlacklistRepo blackRepo;

    @GetMapping("/signout")
    public ResponseEntity<?> signOut(HttpServletRequest req)
    {
        String jwt = jwtService.setJwt(req);

        try
        {
            JwtBlacklist blacklisted = new JwtBlacklist();
            blacklisted.setJwt(jwt);
            blackRepo.save(blacklisted);
        }
        catch (PersistenceException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("User is successfully signed out", HttpStatus.OK);
    }

    @PostMapping("/profile/update")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UserProfileDto dto, HttpServletRequest req)
    {
        try {
            String jwt = jwtService.setJwt(req);
            User user = jwtService.getUser(jwt);

            user = dto.updateUser(user, userRepo, passwordEncoder, authenticationManager);

            if(user == null) {
                return new ResponseEntity<>(dto.getMessage(), HttpStatus.BAD_REQUEST);
            }
            UserResponseDto resDto = new UserResponseDto(user);
            return ResponseEntity.ok(resDto);
        } catch(BadCredentialsException e) {
            return new ResponseEntity<>("Password incorrect", HttpStatus.BAD_REQUEST);
        } catch(Exception e) {
            System.out.println(e);
            return new ResponseEntity<>("Its not you, its us", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get")
    public ResponseEntity<?> getUser(@Valid @RequestParam long id, HttpServletRequest req)
    {
        try {
            String jwt = jwtService.setJwt(req);
            User authUser = jwtService.getUser(jwt);
            List<Role> roles = new ArrayList<>();
            roles.add(Role.ADMIN);
            roles.add(Role.SUPER_ADMIN);

            boolean userExists = authUser.getId() != id || roles.contains(authUser.getRole()) ?
                    userRepo.existsById(id) : id == authUser.getId();
            if(userExists == false) {
                return new ResponseEntity<>("User does not exist", HttpStatus.BAD_REQUEST);
            }

            User user = id != authUser.getId() ? userRepo.findById(id).get() : authUser;

            UserResponseDto resDto = new UserResponseDto(user);
            return ResponseEntity.ok(resDto);
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Its not you, its us", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/contact/get")
    public ResponseEntity<?> getContact(@Valid @RequestParam long id, HttpServletRequest req)
    {
        try {
            String jwt = jwtService.setJwt(req);
            User user = jwtService.getUser(jwt);
            List<Role> roles = new ArrayList<>();
            roles.add(Role.ADMIN);
            roles.add(Role.SUPER_ADMIN);
            boolean contactExists = roles.contains(user.getRole()) ?
                    contactRepo.existsById(id) : contactRepo.existsByIdAndUser(id, user);
            if(contactExists == false) {
                return new ResponseEntity<>("Contact does not exist", HttpStatus.BAD_REQUEST);
            }
            Contact contact = contactRepo.findById(id).get();

            ContactResponseDto resDto = new ContactResponseDto(contact);
            return ResponseEntity.ok(resDto);
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Its not you, its us", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/contact/create")
    public ResponseEntity<?> createContact(@Valid @RequestBody ContactDto dto, HttpServletRequest req)
    {
        try {
            String jwt = jwtService.setJwt(req);
            User user = jwtService.getUser(jwt);
            boolean phoneExists = contactRepo.existsByPhoneAndUser(dto.getPhone(), user);
            if(phoneExists) {
                return new ResponseEntity<>("You have a contact with phone number "+
                        dto.getPhone()+" already", HttpStatus.BAD_REQUEST);
            }
            Contact con = dto.getContact(user);
            con = contactRepo.save(con);

            ContactResponseDto resDto = new ContactResponseDto(con);
            return ResponseEntity.ok(resDto);
        } catch(Exception e) {
            return new ResponseEntity<>("Its not you, its us", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/contact/update")
    public ResponseEntity<?> updateContact(@Valid @RequestBody ContactUpdateDto dto, HttpServletRequest req)
    {
        try {
            boolean contactExists = contactRepo.existsById(dto.getId());
            if(contactExists == false) {
                return new ResponseEntity<>("Contact does not exist", HttpStatus.BAD_REQUEST);
            }
            String jwt = jwtService.setJwt(req);
            User user = jwtService.getUser(jwt);
            if(dto.getPhone() != null) {
                boolean phoneExists = contactRepo.existsByPhoneAndUser(dto.getPhone(), user);
                if(phoneExists) {
                    return new ResponseEntity<>("You have a contact with phone number "+
                            dto.getPhone()+" already", HttpStatus.BAD_REQUEST);
                }
            }
            Contact contact = contactRepo.findById(dto.getId()).get();
            Contact updatedContact = dto.updateContact(contact);
            contact = contactRepo.save(updatedContact);

            ContactResponseDto resDto = new ContactResponseDto(contact);
            return ResponseEntity.ok(resDto);
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Its not you, its us", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/contact/delete")
    public ResponseEntity<?> deleteContact(@Valid @RequestParam long id, HttpServletRequest req)
    {
        try {
            String jwt = jwtService.setJwt(req);
            User user = jwtService.getUser(jwt);

            boolean contactExists = contactRepo.existsByIdAndUser(id, user);
            if(contactExists == false) {
                return new ResponseEntity<>("Contact does not exist", HttpStatus.BAD_REQUEST);
            }
            Contact contact = contactRepo.findById(id).get();
            contact.setUser(user);

            contactRepo.delete(contact);
            String res = "Contact with id: "+id+" deleted";
            return ResponseEntity.ok(res);
        } catch(Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Its not you, its us", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/wants")
    public ResponseEntity<?> deleteOrDeactivateAccount(@Valid @RequestParam String want, HttpServletRequest req)
    {
        try {
            List<String> wants = new ArrayList<>();
            wants.add(Status.DELETED.name());
            wants.add(Status.INACTIVE.name());
            if(wants.contains(want) == false) {
                return new ResponseEntity<>("want can only be "+
                        Status.DELETED.name()+" or "+Status.INACTIVE.name(), HttpStatus.BAD_REQUEST);
            }
            String jwt = jwtService.setJwt(req);
            User user = jwtService.getUser(jwt);

            user.setStatus(Status.valueOf(want));
            user = userRepo.save(user);
            emailService.sendEmail(user.getEmail(),
                    "Account "+user.getStatus().name(),
                    "Your account have been succesfully "+user.getStatus().name());
            UserResponseDto resDto = new UserResponseDto(user);
            return ResponseEntity.ok(resDto);
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Its not you, its us", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/contact/contacts")
    public ResponseEntity<?> getContacts(@Valid @RequestBody ContactsDto dto, HttpServletRequest req)
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
            String jwt = jwtService.setJwt(req);
            User user = jwtService.getUser(jwt);
            spec = spec.and(ContactSpecs.userEquals(user));
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
    public ResponseEntity<?> searchContacts(@Valid @RequestParam String search, HttpServletRequest req)
    {
        try {
            String jwt = jwtService.setJwt(req);
            User user = jwtService.getUser(jwt);
            Specification<Contact> spec = Specification.where(null);
            spec = spec.and(ContactSpecs.userEquals(user));
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
}
