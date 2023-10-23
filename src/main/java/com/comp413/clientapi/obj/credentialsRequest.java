package com.comp413.clientapi.obj;

/**
 * Request object for login and register requests. All fields final.
 *
 * @param username  name of user
 * @param password  password of user - NOT PROTECTED NOR SECURELY STORED
 * @param email     email of user
 * @param firstName first name of user
 * @param lastName  last name of user
// * @param registrationTimestamp time at which the user registers their account
// * @param portfolioId   unique identifier of this user's portfolio.
 */
public record credentialsRequest(
        String username,
        String password,
        String email,
        String firstName,
        String lastName
//        String registrationTimestamp,
//        String portfolioId
) {
    @Override
    public String toString() {
        return "{" +
                "\"username\":\"" + username + '\"' +
                ", \"password:\"" + password + '\"' +
                ", \"email:\"" + email + '\"' +
                ", \"firstName:\"" + firstName + '\"' +
                ", \"lastName:\"" + lastName + '\"' +
//                ", \"registrationTimestamp:\"" + registrationTimestamp + '\"' +
//                ", \"portfolioId:\"" + portfolioId + '\"' +
                '}';
    }
}
