package com.userservice.controller;

import com.userservice.dto.ResponseUserDTO;
import com.userservice.dto.UserDTO;
import com.userservice.exception.UserAlreadyExistsException;
import com.userservice.exception.UserFoundAfterDeletingException;
import com.userservice.exception.UserNotFoundException;
import com.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    public ResponseEntity<Page<ResponseUserDTO>> getAllUsers(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit) throws UserNotFoundException {
        Page<ResponseUserDTO> users = userService.getAllUsers(PageRequest.of(offset, limit));
        return ResponseEntity.ok(users);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ResponseUserDTO> getUserByEmail(@PathVariable("email") String email) throws UserNotFoundException {
        ResponseUserDTO user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseUserDTO> getUser(@PathVariable Long id) throws UserNotFoundException {
        ResponseUserDTO dto = userService.getUserById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseUserDTO> createUser(@Valid @RequestBody UserDTO dto) throws UserAlreadyExistsException, UserNotFoundException {
        ResponseUserDTO response = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<ResponseUserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO dto) throws UserNotFoundException {
        ResponseUserDTO response = userService.updateUser(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) throws UserNotFoundException, UserFoundAfterDeletingException {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();

    }

}
