package com.userservice.service;

import com.userservice.dto.UserDTO;
import com.userservice.entity.User;
import com.userservice.mapper.UserMapper;
import com.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public void createUser(UserDTO dto) {
        User u = userMapper.toUser(dto);
        userRepository.createUser(u);
    }

    @Override
    public UserDTO getUserById(Long id) {
        Optional<User> u = userRepository.getUserById(id);

        if(u.isPresent()) {
            return userMapper.toUserDTO(u.get());
        }
        return null;

    }

    @Override
    public Page<UserDTO> getAllUsers(Pageable pageable) {

        Page<User> users = userRepository.getAllUsers(pageable);
        Page<UserDTO> dtos = userMapper.toUserDTOPage(users);
        return dtos;

    }

    @Override
    public UserDTO getUserByEmail(String email) {

        Optional<User> u = userRepository.getUserByEmail(email);
        if(u.isPresent()) {
            return userMapper.toUserDTO(u.get());
        }
        return null;

    }

    @Override
    @Transactional
    public void updateUser(Long id, UserDTO dto) {
        User u = userMapper.toUser(dto);
        userRepository.updateUserById(id, u);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteUserById(id);
    }
}
