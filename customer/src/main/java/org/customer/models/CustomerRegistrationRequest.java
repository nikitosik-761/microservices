package org.customer.models;

public record CustomerRegistrationRequest(
        String firstname,
        String lastname,
        String email
) {
}
