package com.userservice.service;

import com.userservice.dto.UserDTO;
import com.userservice.entity.User;
import com.userservice.exception.CardInfoNotFoundException;
import com.userservice.exception.UserAlreadyExistsException;
import com.userservice.exception.UserFoundAfterDeletingException;
import com.userservice.exception.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserDTO createUser(UserDTO dto) throws UserAlreadyExistsException, UserNotFoundException;

    UserDTO getUserById(Long id) throws UserNotFoundException;

    Page<UserDTO> getAllUsers(Pageable pageable) throws UserNotFoundException;

    UserDTO getUserByEmail(String email) throws UserNotFoundException;

    UserDTO updateUser(Long id, UserDTO dto) throws UserNotFoundException;

    void deleteUser(Long id) throws UserNotFoundException, UserFoundAfterDeletingException;

}
