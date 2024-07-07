package com.sinaev.services;

import com.sinaev.models.dto.UserDTO;
import com.sinaev.models.entities.User;
import com.sinaev.repositories.UserRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    @DisplayName("Should register new user successfully")
    public void testRegisterUser() {
        UserDTO userDTO = new UserDTO("user1", "password", false);
        User user = new User("user1", "password");

        when(userRepository.existsByUsername(userDTO.username())).thenReturn(false);

        boolean isRegistered = userService.register(userDTO);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(isRegistered).isTrue();
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

        boolean isRegistered = userService.register(userDTO);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(isRegistered).isFalse();
        verify(userRepository, never()).save(any(User.class));
        when(userRepository.findByUsername("user1")).thenReturn(Optional.empty());
        softly.assertThat(userRepository.findByUsername("user1")).isNotPresent();
        softly.assertAll();
    }

    @Test
    @DisplayName("Should login user successfully")
    public void testLoginUser() {
        User user = new User("user1", "password", false);
        UserDTO userDTO = new UserDTO("user1", "password", false);

        when(userRepository.findByUsername(userDTO.username())).thenReturn(Optional.of(user));

        Optional<UserDTO> loggedInUserDTO = userService.login(userDTO);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(loggedInUserDTO).isPresent();
        softly.assertThat(loggedInUserDTO.get().username()).isEqualTo("user1");
        softly.assertAll();
    }

    @Test
    @DisplayName("Should not login user with wrong password")
    public void testLoginUserWithWrongPassword() {
        User user = new User("user1", "password", false);
        UserDTO userDTO = new UserDTO("user1", "wrongpassword", false);

        when(userRepository.findByUsername(userDTO.username())).thenReturn(Optional.of(user));

        Optional<UserDTO> loggedInUserDTO = userService.login(userDTO);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(loggedInUserDTO).isNotPresent();
        verify(userRepository, times(1)).findByUsername("user1");
        softly.assertAll();
    }

    @Test
    @DisplayName("Should login admin user successfully")
    public void testLoginAdminUser() {
        User adminUser = new User("admin", "adminpass", true);
        UserDTO adminUserDTO = new UserDTO("admin", "adminpass", true);

        when(userRepository.findByUsername(adminUserDTO.username())).thenReturn(Optional.of(adminUser));

        Optional<UserDTO> loggedInAdminDTO = userService.login(adminUserDTO);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(loggedInAdminDTO).isPresent();
        softly.assertThat(loggedInAdminDTO.get().username()).isEqualTo("admin");
        softly.assertThat(loggedInAdminDTO.get().admin()).isTrue();
        softly.assertAll();
    }

    @Test
    @DisplayName("Should retrieve user by username")
    public void testGetUserByUsername() {
        User user = new User("user1", "password", false);

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