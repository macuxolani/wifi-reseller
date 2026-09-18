package com.yourwifi.radius.dto;

public record RadiusAccessRequest(String username, String password, String macAddress, String ipAddress) {}
