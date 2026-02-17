package com.example.autofetch.infrastructure.google.drive.auth;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.stereotype.Component;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

@Component
public class ServiceAccountAuthProvider {

    private static final String CREDENTIALS_PATH = "google/service-account.json";

    public HttpRequestInitializer getCredentials() throws IOException {

        InputStream in = getClass()
                .getClassLoader()
                .getResourceAsStream(CREDENTIALS_PATH);

        if (in == null) {
            throw new RuntimeException("Archive service-account.json not found");
        }

        GoogleCredentials credentials = GoogleCredentials.fromStream(in)
                .createScoped(List.of(DriveScopes.DRIVE_READONLY));

        return new HttpCredentialsAdapter(credentials);
    }
}
