package com.yourwifi.auth.dto;

import java.util.Set;

public record AuthResponse(
	String accessToken,
	String refreshToken,
	String tokenType,
	String username,
	Set<String> roles
) {
	public AuthResponse(String accessToken, String refreshToken, String tokenType, String username) {
		this(accessToken, refreshToken, tokenType, username, Set.of("CUSTOMER"));
	}
}
