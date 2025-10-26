package com.userservice.controller;

import com.userservice.dto.UserDTO;
import com.userservice.exception.CardInfoNotFoundException;
import com.userservice.exception.UserAlreadyExistsException;
import com.userservice.exception.UserFoundAfterDeletingException;
import com.userservice.exception.UserNotFoundException;
import com.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    public ResponseEntity<Page<UserDTO>> getAllUsers(@RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit) throws CardInfoNotFoundException {
        Page<UserDTO> users = userService.getAllUsers(PageRequest.of(offset, limit));
        return ResponseEntity.ok(users);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable("email") String email) throws UserNotFoundException {
        UserDTO user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) throws UserNotFoundException {
        UserDTO dto = userService.getUserById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/create")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO dto) throws UserAlreadyExistsException, UserNotFoundException {
        UserDTO response = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO dto) throws UserNotFoundException {
        UserDTO response = userService.updateUser(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) throws UserNotFoundException, UserFoundAfterDeletingException {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();

    }

}
