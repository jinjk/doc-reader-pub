package com.utilsvr.security;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Instant;

public class GenerateKey {
    public static void main(String[] args) throws IOException {
        String homeDir = System.getenv("HOME");
        File file = Path.of(homeDir, "utilsvr-secure-key").toFile();
        try(FileReader reader = new FileReader(file, StandardCharsets.UTF_8); BufferedReader bfReader = new BufferedReader(reader)) {
            String key = bfReader.readLine();
            AES aes = new AES(key);
            String text = "upload," + Instant.now().toString();
            String encrypted = aes.encrypt(text);
            System.out.println(encrypted);
        }
    }
}
