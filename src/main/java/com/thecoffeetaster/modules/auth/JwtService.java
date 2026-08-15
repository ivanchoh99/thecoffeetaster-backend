package com.thecoffeetaster.modules.auth;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;

@Service
public class JwtService {

    public HashMap<String, String> extractClaims(String token) {
        return new HashMap<>();
    }
}
