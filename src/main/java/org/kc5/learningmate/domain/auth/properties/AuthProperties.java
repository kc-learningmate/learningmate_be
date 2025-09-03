package org.kc5.learningmate.domain.auth.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth")
@Getter
@Setter
public class AuthProperties {
    private String secret;
    private long expirationMills;
    private String sameSite;
    private long refreshTokenExpirationDays;
    private String baseUrl;
    private String passwdResetUrl;
}
