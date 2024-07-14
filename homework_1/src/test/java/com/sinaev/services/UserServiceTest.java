package com.sinaev.services;

import com.sinaev.exceptions.UsernameAlreadyTakenException;
import com.sinaev.models.dto.UserDTO;
import com.sinaev.models.entities.User;
import com.sinaev.repositories.UserRepository;
import com.sinaev.services.impl.UserServiceImpl;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserServiceImpl userService;
    private UserRepository userRepository;
    private HttpServletRequest httpRequest;
    private HttpSession httpSession;

    @BeforeEach
    public void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        httpRequest = Mockito.mock(HttpServletRequest.class);
        httpSession = Mockito.mock(HttpSession.class);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    @DisplayName("Should register new user successfully")
    public void testRegisterUser() {
        UserDTO userDTO = new UserDTO("user1", "password", false);
        User user = new User("user1", "password");

        when(userRepository.existsByUsername(userDTO.username())).thenReturn(false);

        userService.register(userDTO);

        SoftAssertions softly = new SoftAssertions();
        verify(userRepository, times(1)).save(any(User.class));
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        softly.assertThat(userRepository.findByUsername("user1")).isPresent();
        softly.assertAll();
    }

    @Test
    @DisplayName("Should not register user with taken username")
    public void testRegisterUserWithTakenUsername() {
        UserDTO userDTO = new UserDTO("user1", "password", false);

        when(userRepository.existsByUsername(userDTO.username())).thenReturn(true);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThatThrownBy(() -> userService.register(userDTO))
                .isInstanceOf(UsernameAlreadyTakenException.class)
                .hasMessageContaining("User with login user1 already exist");
        verify(userRepository, never()).save(any(User.class));
        when(userRepository.findByUsername("user1")).thenReturn(Optional.empty());
        softly.assertThat(userRepository.findByUsername("user1")).isNotPresent();
        softly.assertAll();
    }

    @Test
    @DisplayName("Should login user successfully")
    public void testLoginUser() {
        User user = new User("user1", "password");
        UserDTO userDTO = new UserDTO("user1", "password", false);

        when(userRepository.findByUsername(userDTO.username())).thenReturn(Optional.of(user));

        userService.login(httpRequest, userDTO);

        SoftAssertions softly = new SoftAssertions();
        verify(userRepository, times(2)).findByUsername("user1");
        softly.assertAll();
    }

    @Test
    @DisplayName("Should not login user with wrong password")
    public void testLoginUserWithWrongPassword() {
        User user = new User("user1", "password");
        UserDTO userDTO = new UserDTO("user1", "wrongpassword", false);

        when(userRepository.findByUsername(userDTO.username())).thenReturn(Optional.of(user));

        SoftAssertions softly = new SoftAssertions();
        softly.assertThatThrownBy(() -> userService.login(httpRequest, userDTO))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("User: 'user1' not found");
        verify(userRepository, times(1)).findByUsername("user1");
        softly.assertAll();
    }

    @Test
    @DisplayName("Should login admin user successfully")
    public void testLoginAdminUser() {
        User adminUser = new User("admin", "adminpass");
        adminUser.setAdmin(true);
        UserDTO adminUserDTO = new UserDTO("admin", "adminpass", true);

        when(userRepository.findByUsername(adminUserDTO.username())).thenReturn(Optional.of(adminUser));

        userService.login(httpRequest, adminUserDTO);

        SoftAssertions softly = new SoftAssertions();
        verify(userRepository, times(2)).findByUsername("admin");
        softly.assertAll();
    }

    @Test
    @DisplayName("Should retrieve user by username")
    public void testGetUserByUsername() {
        User user = new User("user1", "password");

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.getUserByUsername("user1");

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(foundUser).isPresent();
        softly.assertThat(foundUser.get().getUsername()).isEqualTo("user1");
        verify(userRepository, times(1)).findByUsername("user1");
        softly.assertAll();
    }

    @Test
    @DisplayName("Should not retrieve non-existing user by username")
    public void testGetNonExistingUserByUsername() {
        when(userRepository.findByUsername("nonexisting")).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.getUserByUsername("nonexisting");

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(foundUser).isNotPresent();
        verify(userRepository, times(1)).findByUsername("nonexisting");
        softly.assertAll();
    }
}