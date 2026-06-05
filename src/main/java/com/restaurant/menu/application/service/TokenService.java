package com.restaurant.menu.application.service;

import com.restaurant.menu.domain.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final long expirationSeconds;

    public TokenService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.expirationSeconds = 3600L;
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        String scope = "ROLE_" + user.role().name();

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("menu-api")
            .issuedAt(now)
            .expiresAt(now.plusSeconds(expirationSeconds))
            .subject(user.id().value().toString())
            .claim("email", user.email())
            .claim("name", user.name())
            .claim("scope", scope)
            .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public long getExpirationSeconds() {
        return expirationSeconds;
    }
}
