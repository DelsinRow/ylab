package com.sinaev.services;

import com.sinaev.models.User;
import com.sinaev.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    /**
     * Tests the successful registration of a new user.
     * Steps:
     * 1. Create a new user.
     * 2. Mock the repository to return false for username existence.
     * 3. Call the register method.
     * 4. Verify that the user is saved in the repository.
     * 5. Check that the user can be found in the repository.
     * Expected result: The user is registered and saved in the repository.
     */
    @Test
    public void testRegisterUser() {
        User user = new User("user1", "password", false);

        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);

        userService.register(user);

        verify(userRepository, times(1)).save(user);
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        assertThat(userRepository.findByUsername("user1")).isPresent();
    }

    /**
     * Tests the registration of a user with an already taken username.
     * Steps:
     * 1. Create a new user.
     * 2. Mock the repository to return true for username existence.
     * 3. Call the register method.
     * 4. Verify that the user is not saved in the repository.
     * 5. Check that the user cannot be found in the repository.
     * Expected result: The user is not registered and not saved in the repository.
     */
    @Test
    public void testRegisterUserWithTakenUsername() {
        User user = new User("user1", "password", false);

        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);

        userService.register(user);

        verify(userRepository, never()).save(user);
        when(userRepository.findByUsername("user1")).thenReturn(Optional.empty());
        assertThat(userRepository.findByUsername("user1")).isNotPresent();
    }

    /**
     * Tests the successful login of a registered user.
     * Steps:
     * 1. Create a new user and register them.
     * 2. Mock the repository to return the user.
     * 3. Call the login method.
     * 4. Verify the user's login status and username.
     * 5. Verify repository interactions.
     * Expected result: The user is logged in and their status is updated.
     */
    @Test
    public void testLoginUser() {
        User user = new User("user1", "password", false);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        User loggedInUser = userService.login("user1", "password");

        assertThat(loggedInUser.isLoggedIn()).isTrue();
        assertThat(loggedInUser.getUsername()).isEqualTo("user1");
        verify(userRepository, times(2)).findByUsername("user1");
    }

    /**
     * Tests the login attempt with an incorrect password.
     * Steps:
     * 1. Create a new user and register them.
     * 2. Mock the repository to return the user.
     * 3. Call the login method with the wrong password.
     * 4. Verify the user's login status.
     * 5. Verify repository interactions.
     * Expected result: The user is not logged in.
     */
    @Test
    public void testLoginUserWithWrongPassword() {
        User user = new User("user1", "password", false);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        User loggedInUser = userService.login("user1", "wrongpassword");

        assertThat(loggedInUser.isLoggedIn()).isFalse();
        verify(userRepository, times(1)).findByUsername("user1");
    }

    /**
     * Tests the successful login of an admin user.
     * Steps:
     * 1. Create a new admin user and register them.
     * 2. Mock the repository to return the admin user.
     * 3. Call the login method.
     * 4. Verify the user's login status, admin status, and username.
     * 5. Verify repository interactions.
     * Expected result: The admin user is logged in and their status is updated.
     */
    @Test
    public void testLoginAdminUser() {
        User adminUser = new User("admin", "adminpass", true);

        when(userRepository.findByUsername(adminUser.getUsername())).thenReturn(Optional.of(adminUser));

        User loggedInAdmin = userService.login("admin", "adminpass");

        assertThat(loggedInAdmin.isLoggedIn()).isTrue();
        assertThat(loggedInAdmin.isAdmin()).isTrue();
        assertThat(loggedInAdmin.getUsername()).isEqualTo("admin");
        verify(userRepository, times(2)).findByUsername("admin");
    }

    /**
     * Tests the retrieval of a user by their username.
     * Steps:
     * 1. Create a new user and register them.
     * 2. Mock the repository to return the user.
     * 3. Call the getUserByUsername method.
     * 4. Verify the user is found and their username matches.
     * 5. Verify repository interactions.
     * Expected result: The user is found in the repository.
     */
    @Test
    public void testGetUserByUsername() {
        User user = new User("user1", "password", false);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.getUserByUsername("user1");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("user1");
        verify(userRepository, times(1)).findByUsername("user1");
    }

    /**
     * Tests the retrieval of a non-existing user by their username.
     * Steps:
     * 1. Mock the repository to return empty for a non-existing user.
     * 2. Call the getUserByUsername method.
     * 3. Verify the user is not found.
     * 4. Verify repository interactions.
     * Expected result: The user is not found in the repository.
     */
    @Test
    public void testGetNonExistingUserByUsername() {
        when(userRepository.findByUsername("nonexisting")).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.getUserByUsername("nonexisting");

        assertThat(foundUser).isNotPresent();
        verify(userRepository, times(1)).findByUsername("nonexisting");
    }
}