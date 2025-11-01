package com.userservice.service.impl;

import com.userservice.dto.ResponseUserDTO;
import com.userservice.dto.UserDTO;
import com.userservice.entity.User;
import com.userservice.exception.UserAlreadyExistsException;
import com.userservice.exception.UserFoundAfterDeletingException;
import com.userservice.exception.UserNotFoundException;
import com.userservice.mapper.UserMapper;
import com.userservice.repository.UserRepository;
import com.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    @CachePut(value = "users", key = "#result.id")
    public ResponseUserDTO createUser(UserDTO dto) throws UserAlreadyExistsException, UserNotFoundException {
        if(userRepository.existsByEmail(dto.getEmail())){
            throw new UserAlreadyExistsException("User with email: " + dto.getEmail() + " already exists");
        }
        User u = userMapper.toUser(dto);
        userRepository.save(u);

        return userMapper.toUserDTO(u);
    }

    @Override
    @Cacheable(value = "users", key = "#id")
    public ResponseUserDTO getUserById(Long id) throws UserNotFoundException {
        Optional<User> u = userRepository.getUserById(id);

        if(u.isPresent()) {
            return userMapper.toUserDTO(u.get());
        }
        else{
            throw new UserNotFoundException("User with id: " + id + " not found");
        }

    }

    @Override
    public Page<ResponseUserDTO> getAllUsers(Pageable pageable) throws UserNotFoundException {

        Page<User> users = userRepository.getAllUsers(pageable);
        if(!users.hasContent()) {
            throw new UserNotFoundException("There are not any users");
        }
        return userMapper.toUserDTOPage(users);

    }

    @Override
    public ResponseUserDTO getUserByEmail(String email) throws UserNotFoundException {

        Optional<User> u = userRepository.getUserByEmail(email);
        if(u.isPresent()) {

            return userMapper.toUserDTO(u.get());
        }
        else{
            throw new UserNotFoundException("User with email: " + email + " not found");
        }

    }

    @Override
    @Transactional
    @CachePut(value = "users", key = "#id")
    public ResponseUserDTO updateUser(Long id, UserDTO dto) throws UserNotFoundException {
        if(!userRepository.existsById(id)){
            throw new UserNotFoundException("User with id: " + id + " not found for updating");
        }
        User u = userMapper.toUser(dto);
        userRepository.updateUserById(id, u);
        userRepository.flush();
        User updated = userRepository.getUserById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found after updating"));
        return userMapper.toUserDTO(updated);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) throws UserNotFoundException, UserFoundAfterDeletingException {
        if(!userRepository.existsById(id)){
            throw new UserNotFoundException("User with id: " + id + " not found for deleting");
        }
        userRepository.deleteUserById(id);
        userRepository.flush();

        if(userRepository.existsById(id)){
            throw new UserFoundAfterDeletingException("User with id: " + id + " found after deleting");
        }
    }
}
