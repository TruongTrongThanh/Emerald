package org.emerald.comicapi.controller;

import org.emerald.comicapi.config.GlobalVariable;
import org.emerald.comicapi.model.common.ResponseFormat;
import org.emerald.comicapi.model.data.User;
import org.emerald.comicapi.model.form.UserForm;
import org.emerald.comicapi.repository.UserRepository;
import org.emerald.comicapi.service.MongoUserDetailService;
import org.emerald.comicapi.validator.sequence.ValidationSequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    MongoUserDetailService mongoUserDetailService;
    @Autowired
    GlobalVariable globalVariable;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Validated({ValidationSequence.class}) UserForm userForm)
    {
        User user = mongoUserDetailService.register(userForm, "MEMBER");
        user.hideInformation();
        ResponseFormat format = new ResponseFormat(201, user, "/users/register");
        return ResponseEntity.status(201).body(format);
    }

    @PostMapping("/admin-creation")
    public ResponseEntity<?> createAdminAccount(
            @Validated({ValidationSequence.class}) UserForm userForm,
            @RequestParam String secretKey)
    {
        String message;

        if (!globalVariable.SECRET_KEY.equals(secretKey))
            message = "Invalid secret key";
        else if (!userRepository.existsInRoles("ADMIN")) {
            User user = mongoUserDetailService.register(userForm, "ADMIN");
            user.hideInformation();
            ResponseFormat format = new ResponseFormat(201, user, "/users/admin-creation");
            return ResponseEntity.status(201).body(format);
        }
        else
            message = "Only one admin account can exist";

        ResponseFormat format = new ResponseFormat(400, message, "/users/admin-creation");
        return ResponseEntity.badRequest().body(format);
    }

    @DeleteMapping("")
    public ResponseEntity<?> deleteAllAccounts() {
        userRepository.deleteAll();
        return ResponseEntity.ok("Delete successfully");
    }
}
