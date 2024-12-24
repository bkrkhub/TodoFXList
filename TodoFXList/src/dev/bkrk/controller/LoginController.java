package dev.bkrk.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class LoginController {

    public boolean validateUser(String userName, String password) {
        String fileName = "/resources/credentials.txt";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream(fileName))))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // username:password format.
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String storedUserName = parts[0].trim();
                    String storedPassword = parts[1].trim();
                    if (storedUserName.equals(userName) && storedPassword.equals(password)) {
                        // the credentials match.
                        return true;
                    }
                }
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        // if credentials don't match.
        return false;
    }
}