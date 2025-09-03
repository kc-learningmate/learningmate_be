package org.kc5.learningmate.api.v1.dto.request.auth;

import jakarta.validation.constraints.Email;

public record AuthCodeGetRequest(@Email String email) {
}
