package com.comp413.clientapi.obj;

/**
 * A request object for login and register requests. All fields final.
 *
 * @param username  name of user
 * @param password  password of user - NOT PROTECTED NOR SECURELY STORED
 * @param email     email of user
 */
public record credentialsRequest(
        String username,
        String password,
        String email
) {
    @Override
    public String toString() {
        return "{" +
                "\"username\":\"" + username + '\"' +
                ", \"password\":\"" + password + '\"' +
                ", \"email\":\"" + email + '\"' +
                "}";
    }
}
