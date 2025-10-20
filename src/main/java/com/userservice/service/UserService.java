package com.userservice.service;

import com.userservice.dto.UserDTO;
import com.userservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    void createUser(UserDTO dto);

    UserDTO getUserById(Long id);

    Page<UserDTO> getAllUsers(Pageable pageable);

    UserDTO getUserByEmail(String email);

    void updateUser(Long id, UserDTO dto);

    void deleteUser(Long id);

}
