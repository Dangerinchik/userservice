package com.userservice.service;

import com.userservice.dto.ResponseUserDTO;
import com.userservice.dto.UserDTO;
import com.userservice.entity.User;
import com.userservice.exception.CardInfoNotFoundException;
import com.userservice.exception.UserAlreadyExistsException;
import com.userservice.exception.UserFoundAfterDeletingException;
import com.userservice.exception.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    ResponseUserDTO createUser(UserDTO dto) throws UserAlreadyExistsException, UserNotFoundException;

    ResponseUserDTO getUserById(Long id) throws UserNotFoundException;

    Page<ResponseUserDTO> getAllUsers(Pageable pageable) throws UserNotFoundException;

    ResponseUserDTO getUserByEmail(String email) throws UserNotFoundException;

    ResponseUserDTO updateUser(Long id, UserDTO dto) throws UserNotFoundException;

    void deleteUser(Long id) throws UserNotFoundException, UserFoundAfterDeletingException;

}
