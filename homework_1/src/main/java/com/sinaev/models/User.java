package com.sinaev.models;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a user in the system.
 */
@Getter
@Setter
public class User {
    /**
     * The id of the user.
     */
    private Long id;
    /**
     * The username of the user.
     */
    private String username;

    /**
     * The password of the user.
     */
    private String password;

    /**
     * Indicates whether the user is logged in.
     */
    private boolean isLoggedIn;

    /**
     * Indicates whether the user has admin access.
     */
    private boolean isAdmin;

    /**
     * Constructs a new user with the specified username, password, and admin status.
     *
     * @param username the username of the user.
     * @param password the password of the user.
     * @param isAdmin  whether the user has admin privileges.
     */
    public User(String username, String password, boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    /**
     * Constructs a new user with the specified username and password.
     * The user will not have admin privileges by default.
     *
     * @param username the username of the user.
     * @param password the password of the user.
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.isAdmin = false;
    }

    /**
     * Constructs a new user with the specified login status.
     *
     * @param isLoggedIn whether the user is logged in.
     */
    public User(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }
}
