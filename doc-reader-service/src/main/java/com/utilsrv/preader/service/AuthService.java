package com.utilsrv.preader.service;

import com.utilsrv.preader.auth.token.DeviceIdAuthenticationToken;
import com.utilsrv.preader.jpa.entities.Person;
import com.utilsrv.preader.util.AES;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {
    public static final int TWO_HOURS = 120;
    Logger logger = LoggerFactory.getLogger(AuthService.class);
    @Autowired
    UserService userService;

    public Person getLoggedInPerson() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            if (auth instanceof DeviceIdAuthenticationToken) {
                DeviceIdAuthenticationToken deviceAuth = (DeviceIdAuthenticationToken) auth;
                String deviceId = deviceAuth.getDeviceId();
                Person person = userService.findOrCreatePersonByDeviceId(deviceId);
                return person;
            }
        }
        return null;
    }

    public boolean verifyToken(String token) throws IOException {
        if (token == null) {
            return false;
        }
        Instant now = Instant.now();
        String homeDir = System.getenv("HOME");
        File file = Path.of(homeDir, "utilsvr-secure-key").toFile();
        try(FileReader reader = new FileReader(file, StandardCharsets.UTF_8); BufferedReader bfReader = new BufferedReader(reader)) {
            String key = bfReader.readLine();
            AES aes = new AES(key);
            String text = aes.decrypt(token);
            String time = text.split(",")[1];
            Instant tokenTime = Instant.parse(time);
            long mins = Duration.between(tokenTime, now).toMinutes();
            logger.info("token time: " + mins + " mins");
            if (mins < TWO_HOURS) {
                return true;
            }
            else {
                return false;
            }
        }
    }
}
