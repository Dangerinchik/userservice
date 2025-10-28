package com.userservice.unit;

import com.userservice.dto.UserDTO;
import com.userservice.entity.User;
import com.userservice.exception.UserAlreadyExistsException;
import com.userservice.exception.UserFoundAfterDeletingException;
import com.userservice.exception.UserNotFoundException;
import com.userservice.mapper.UserMapper;
import com.userservice.service.impl.UserServiceImpl;
import com.userservice.repository.UserRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private UserServiceImpl userService;

    private static UserDTO userDTO;
    private static User user;
    private static User saved;
    private static Pageable pageable;
    private static PageImpl<User> pageImpl;
    private static Page<User> page;
    private static PageImpl<UserDTO> pageImplDTO;
    private static Page<UserDTO> pageDTO;
    private static long id;

    @BeforeEach
    public void setUp() throws Exception {

        id = 1;

        userDTO = new UserDTO();
        userDTO.setName("Daniil");
        userDTO.setEmail("daniil@gmail.com");
        userDTO.setSurname("Rainchyk");
        userDTO.setBirthDate(LocalDate.of(2007, 5, 29));

        user = new User();
        user.setName("Daniil");
        user.setEmail("daniil@gmail.com");
        user.setSurname("Rainchyk");
        user.setBirthDate(LocalDate.of(2007, 5, 29));

        saved = new User();
        saved.setId(id);
        saved.setName("Daniil");
        saved.setEmail("daniil@gmail.com");
        saved.setSurname("Rainchyk");
        saved.setBirthDate(LocalDate.of(2007, 5, 29));

        pageable = Pageable.ofSize(10);
        pageImpl = new PageImpl<>(List.of(user), pageable, 1);
        page = pageImpl;

        pageImplDTO = new PageImpl<>(List.of(userDTO), pageable, 1);
        pageDTO = pageImplDTO;


    }
    @Test
    public void testCreateUser() throws Exception {
        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(false).thenReturn(true);
        when(userMapper.toUser(userDTO)).thenReturn(user);
        when(userMapper.toUserDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.createUser(userDTO);

        Assertions.assertNotNull(result);
        verify(userRepository).save(user);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testCreateUser__WhenUserAlreadyExists() throws Exception {
        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(true);

        Assertions.assertThrows(UserAlreadyExistsException.class,
                () -> userService.createUser(userDTO));

        verify(userRepository, never()).save(any(User.class));

    }

//    @Test
//    public void testCreateUser__WhenUserNotFound() throws Exception {
//        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(false);
//        when(userMapper.toUser(userDTO)).thenReturn(user);
//        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
//
//        Assertions.assertThrows(UserNotFoundException.class,
//                () -> userService.createUser(userDTO));
//
//        verify(userRepository, times(1)).save(any(User.class));
//    }

    @Test
    public void testGetUserById() throws Exception {

        when(userRepository.getUserById(id)).thenReturn(Optional.of(saved));
        when(userMapper.toUserDTO(saved)).thenReturn(userDTO);

        UserDTO result = userService.getUserById(id);

        Assertions.assertNotNull(result);
        verify(userRepository).getUserById(id);

    }

    @Test
    public void testGetUserById__WhenUserNotFound() throws Exception {

        when(userRepository.getUserById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class,
                () -> userService.getUserById(id));

    }

    @Test
    public void testGetUserByEmail() throws Exception {

        when(userRepository.getUserByEmail(userDTO.getEmail())).thenReturn(Optional.of(saved));
        when(userMapper.toUserDTO(saved)).thenReturn(userDTO);
        when(cacheManager.getCache("users")).thenReturn(cache);

        UserDTO result = userService.getUserByEmail(userDTO.getEmail());

        Assertions.assertNotNull(result);
        verify(userRepository).getUserByEmail(anyString());
        verify(cache, times(1)).putIfAbsent(eq(saved.getId()), eq(userDTO));

    }

    @Test
    public void testGetUserByEmail__WhenUserNotFound() throws Exception {

        when(userRepository.getUserByEmail(userDTO.getEmail())).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class,
                () -> userService.getUserByEmail(userDTO.getEmail()));

    }

    @Test
    public void testGetAllUsers() throws Exception {

        when(userRepository.getAllUsers(pageable)).thenReturn(page);
        when(userMapper.toUserDTOPage(page)).thenReturn(pageDTO);

        Page<UserDTO> result = userService.getAllUsers(pageable);

        Assertions.assertNotNull(result);

        verify(userRepository).getAllUsers(any(Pageable.class));
    }

    @Test
    public void testGetAllUsers__WhenUserNotFound() throws Exception {
        when(userRepository.getAllUsers(pageable)).thenReturn(Page.empty());

        Assertions.assertThrows(UserNotFoundException.class,
                () -> userService.getAllUsers(pageable));
    }

    @Test
    public void testUpdateUserById() throws Exception {

        when(userRepository.existsById(id)).thenReturn(true);
        when(userMapper.toUser(userDTO)).thenReturn(user);
        when(userRepository.getUserById(id)).thenReturn(Optional.of(saved));
        when(userMapper.toUserDTO(saved)).thenReturn(userDTO);

        UserDTO result = userService.updateUser(id, userDTO);

        Assertions.assertNotNull(result);
        verify(userRepository, times(1))
                .updateUserById(eq(id), eq(user));
        verify(userRepository, times(1)).flush();
    }

    @Test
    public void testUpdateUserById__WhenUserNotFoundBeforeUpdating() throws Exception {

        when(userRepository.existsById(id)).thenReturn(false);

        Assertions.assertThrows(UserNotFoundException.class,
                () -> userService.updateUser(id, userDTO));

        verify(userRepository, never()).updateUserById(eq(id), eq(user));
    }

    @Test
    public void testUpdateUserById__WhenUserNotFoundAfterUpdating() throws Exception {

        when(userRepository.existsById(id)).thenReturn(true);
        when(userMapper.toUser(userDTO)).thenReturn(user);
        when(userRepository.getUserById(id)).thenReturn(Optional.empty());
        Assertions.assertThrows(UserNotFoundException.class,
                () -> userService.updateUser(id, userDTO));

        verify(userRepository, times(1)).updateUserById(eq(id), eq(user));
        verify(userRepository, times(1)).flush();
    }

    @Test
    public void testDeleteUserById() throws Exception {

        when(userRepository.existsById(id)).thenReturn(true);
        when(userRepository.existsById(id)).thenReturn(false);

        userService.createUser(userDTO);
        userService.deleteUser(id);

        verify(userRepository, times(1)).deleteUserById(eq(id));
        verify(userRepository, times(1)).flush();
    }

    @Test
    public void testDeleteUserById__WhenUserNotFoundBeforeDeleting() throws Exception {

        when(userRepository.existsById(id)).thenReturn(false);

        Assertions.assertThrows(UserNotFoundException.class,
                () -> userService.deleteUser(id));

        verify(userRepository, never()).deleteUserById(eq(id));
    }

    @Test
    public void testDeleteUserById__WhenUserFoundAfterDeleting() throws Exception {

        when(userRepository.existsById(id)).thenReturn(true);
        when(userRepository.existsById(id)).thenReturn(true);
        Assertions.assertThrows(UserFoundAfterDeletingException.class,
                () -> userService.deleteUser(id));

        verify(userRepository, times(1)).deleteUserById(eq(id));
        verify(userRepository, times(1)).flush();
    }
}
