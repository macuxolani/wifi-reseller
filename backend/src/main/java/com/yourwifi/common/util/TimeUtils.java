package com.yourwifi.common.util;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public final class TimeUtils {
    public static final ZoneId SOUTH_AFRICA = ZoneId.of("Africa/Johannesburg");

    private TimeUtils() {
    }

    public static Duration minutesToDuration(int minutes) {
        return Duration.ofMinutes(minutes);
    }

    public static ZonedDateTime nowSouthAfrica() {
        return ZonedDateTime.now(SOUTH_AFRICA);
    }

    public static long remainingMinutesFromSeconds(long seconds) {
        return Math.max(0, Duration.ofSeconds(seconds).toMinutes());
    }
}
