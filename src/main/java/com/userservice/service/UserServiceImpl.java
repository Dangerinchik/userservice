package com.userservice.service;

import com.userservice.dto.UserDTO;
import com.userservice.entity.User;
import com.userservice.exception.CardInfoNotFoundException;
import com.userservice.exception.UserAlreadyExistsException;
import com.userservice.exception.UserFoundAfterDeletingException;
import com.userservice.exception.UserNotFoundException;
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
    public UserDTO createUser(UserDTO dto) throws UserAlreadyExistsException, UserNotFoundException {
        if(userRepository.existsByEmail(dto.getEmail())){
            throw new UserAlreadyExistsException("User with email: " + " already exists");
        }
        User u = userMapper.toUser(dto);
        userRepository.createUser(u);
        userRepository.flush();
        if(!userRepository.existsByEmail(u.getEmail())){
            throw new UserNotFoundException("User not found after creating");
        }
        return userMapper.toUserDTO(u);
    }

    @Override
    public UserDTO getUserById(Long id) throws UserNotFoundException {
        Optional<User> u = userRepository.getUserById(id);

        if(u.isPresent()) {
            return userMapper.toUserDTO(u.get());
        }
        else{
            throw new UserNotFoundException("User with id: " + id + " not found");
        }

    }

    @Override
    public Page<UserDTO> getAllUsers(Pageable pageable) throws CardInfoNotFoundException {

        Page<User> users = userRepository.getAllUsers(pageable);
        if(!users.hasContent()) {
            throw new CardInfoNotFoundException("There are not any users");
        }
        return userMapper.toUserDTOPage(users);

    }

    @Override
    public UserDTO getUserByEmail(String email) throws UserNotFoundException {

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
    public UserDTO updateUser(Long id, UserDTO dto) throws UserNotFoundException {
        if(!userRepository.existsById(id)){
            throw new UserNotFoundException("User with id: " + id + " not found for updating");
        }
        User u = userMapper.toUser(dto);
        userRepository.updateUserById(id, u);
        userRepository.flush();
        if(!userRepository.existsByEmail(u.getEmail())){
            throw new UserNotFoundException("User not found after updating");
        }
        return userMapper.toUserDTO(u);
    }

    @Override
    @Transactional
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
