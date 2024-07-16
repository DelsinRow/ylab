package com.sinaev.services;

import com.sinaev.exceptions.UsernameAlreadyTakenException;
import com.sinaev.mappers.UserMapper;
import com.sinaev.models.dto.UserDTO;
import com.sinaev.models.entities.User;
import com.sinaev.repositories.UserRepository;
import com.sinaev.services.impl.UserServiceImpl;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SoftAssertionsExtension.class})
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest httpRequest;

    @Mock
    private HttpSession httpSession;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Should register new user successfully")
    public void testRegisterUser(SoftAssertions softly) {
        UserDTO userDTO = new UserDTO("user1", "password", false);
        User user = new User("user1", "password");

        when(userMapper.toEntity(userDTO)).thenReturn(user);
        when(userRepository.existsByUsername(userDTO.username())).thenReturn(false);
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        userService.register(userDTO);

        verify(userRepository, times(1)).save(user);

        softly.assertThat(userRepository.findByUsername("user1")).isPresent();
    }

    @Test
    @DisplayName("Should not register user with taken username")
    public void testRegisterUserWithTakenUsername(SoftAssertions softly) {
        UserDTO userDTO = new UserDTO("user1", "password", false);
        User user = new User("user1", "password");

        when(userMapper.toEntity(userDTO)).thenReturn(user);
        when(userRepository.existsByUsername(userDTO.username())).thenReturn(true);

        softly.assertThatThrownBy(() -> userService.register(userDTO))
                .isInstanceOf(UsernameAlreadyTakenException.class)
                .hasMessageContaining("User with login user1 already exist");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should login user successfully")
    public void testLoginUser() {
        User user = new User("user1", "password");
        UserDTO userDTO = new UserDTO("user1", "password", false);

        when(userRepository.findByUsername(userDTO.username())).thenReturn(Optional.of(user));
        when(userMapper.toEntity(userDTO)).thenReturn(user);

        UserServiceImpl spyUserService = Mockito.spy(userService);
        doNothing().when(spyUserService).setUserDTOInSession(httpRequest, userDTO);

        spyUserService.login(httpRequest, userDTO);

        verify(userRepository, times(2)).findByUsername("user1");
        verify(spyUserService, times(1)).setUserDTOInSession(httpRequest, userDTO);
    }

    @Test
    @DisplayName("Should not login user with wrong password")
    public void testLoginUserWithWrongPassword(SoftAssertions softly) {
        User user = new User("user1", "password");
        UserDTO userDTO = new UserDTO("user1", "wrongpassword", false);

        softly.assertThatThrownBy(() -> userService.login(httpRequest, userDTO))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("User: 'user1' not found");
        verify(userRepository, times(1)).findByUsername("user1");
    }

    @Test
    @DisplayName("Should login admin user successfully")
    public void testLoginAdminUser() {
        User adminUser = new User("admin", "adminpass");
        adminUser.setAdmin(true);
        UserDTO adminUserDTO = new UserDTO("admin", "adminpass", true);

        when(userRepository.findByUsername(adminUserDTO.username())).thenReturn(Optional.of(adminUser));
        when(userMapper.toEntity(adminUserDTO)).thenReturn(adminUser);

        UserServiceImpl spyUserService = Mockito.spy(userService);

        doNothing().when(spyUserService).setUserDTOInSession(httpRequest, adminUserDTO);
        spyUserService.login(httpRequest, adminUserDTO);

        verify(userRepository, times(2)).findByUsername("admin");
        verify(spyUserService, times(1)).setUserDTOInSession(httpRequest, adminUserDTO);
    }

    @Test
    @DisplayName("Should retrieve user by username")
    public void testGetUserByUsername(SoftAssertions softly) {
        User user = new User("user1", "password");

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.getUserByUsername("user1");

        softly.assertThat(foundUser).isPresent();
        softly.assertThat(foundUser.get().getUsername()).isEqualTo("user1");
        verify(userRepository, times(1)).findByUsername("user1");
    }

    @Test
    @DisplayName("Should not retrieve non-existing user by username")
    public void testGetNonExistingUserByUsername(SoftAssertions softly) {
        when(userRepository.findByUsername("nonexisting")).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.getUserByUsername("nonexisting");

        softly.assertThat(foundUser).isNotPresent();
        verify(userRepository, times(1)).findByUsername("nonexisting");
    }
}