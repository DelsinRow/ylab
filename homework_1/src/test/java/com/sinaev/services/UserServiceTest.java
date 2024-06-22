package com.sinaev.services;

import com.sinaev.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class UserServiceTest {

    private UserService userService;

    @BeforeEach
    public void setUp() {
        userService = new UserService();
    }

    @Test
    public void testRegisterUser() {
        User user = new User("user1", "password", false);
        userService.register(user);

        Optional<User> registeredUser = userService.getUserByUsername("user1");
        assert registeredUser.isPresent();
        assert "user1".equals(registeredUser.get().getUsername());
    }

    @Test
    public void testRegisterUserWithTakenUsername() {
        User user1 = new User("user1", "password1", false);
        User user2 = new User("user1", "password2", false);
        userService.register(user1);
        userService.register(user2);

        Optional<User> registeredUser = userService.getUserByUsername("user1");
        Optional<User> nonRegisteredUser = userService.getUserByUsername("user2");
        assert registeredUser.isPresent();
        assert !nonRegisteredUser.isPresent();
        assert "password1".equals(registeredUser.get().getPassword());
    }

    @Test
    public void testLoginUser() {
        User user = new User("user1", "password", false);
        userService.register(user);

        User loggedInUser = userService.login("user1", "password");

        assert loggedInUser.isLoggedIn();
        assert "user1".equals(loggedInUser.getUsername());
    }

    @Test
    public void testLoginUserWithWrongPassword() {
        User user = new User("user1", "password", false);
        userService.register(user);

        User loggedInUser = userService.login("user1", "wrongpassword");

        assert !loggedInUser.isLoggedIn();
    }

    @Test
    public void testLoginAdminUser() {
        User adminUser = new User("admin", "adminpass", true);
        userService.register(adminUser);

        User loggedInAdmin = userService.login("admin", "adminpass");

        assert loggedInAdmin.isLoggedIn();
        assert loggedInAdmin.isAdmin();
        assert "admin".equals(loggedInAdmin.getUsername());
    }

    @Test
    public void testGetUserByUsername() {
        User user = new User("user1", "password", false);
        userService.register(user);

        Optional<User> foundUser = userService.getUserByUsername("user1");

        assert foundUser.isPresent();
        assert "user1".equals(foundUser.get().getUsername());
    }

    @Test
    public void testGetNonExistingUserByUsername() {
        Optional<User> foundUser = userService.getUserByUsername("nonexisting");

        assert !foundUser.isPresent();
    }
}