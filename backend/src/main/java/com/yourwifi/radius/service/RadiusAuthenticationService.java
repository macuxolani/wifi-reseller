package com.yourwifi.radius.service;

import com.yourwifi.radius.dto.RadiusAccessRequest;
import com.yourwifi.radius.dto.RadiusAccessResponse;
import org.springframework.stereotype.Service;

@Service
public class RadiusAuthenticationService {

    public RadiusAccessResponse authenticate(RadiusAccessRequest request) {
        if (request.username() == null || request.username().isBlank()) {
            return new RadiusAccessResponse(false, "INVALID_USER");
        }
        return new RadiusAccessResponse(true, "OK");
    }
}
