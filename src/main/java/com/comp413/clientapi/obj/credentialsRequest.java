package com.comp413.clientapi.obj;

public record credentialsRequest(
        String username,
        String password,
        String email,
        String firstName,
        String lastName,
        String registrationTimestamp,
        String portfolioId
) {
    @Override
    public String toString() {
        return "{" +
                "\"username\":\"" + username + '\"' +
                ", \"password:\"" + password + '\"' +
                ", \"email:\"" + email + '\"' +
                ", \"firstName:\"" + firstName + '\"' +
                ", \"lastName:\"" + lastName + '\"' +
                ", \"registrationTimestamp:\"" + registrationTimestamp + '\"' +
                ", \"portfolioId:\"" + portfolioId + '\"' +
                '}';
    }
}
