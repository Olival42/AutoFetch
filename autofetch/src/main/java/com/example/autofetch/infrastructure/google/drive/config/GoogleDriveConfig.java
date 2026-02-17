package com.example.autofetch.infrastructure.google.drive.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.autofetch.infrastructure.google.drive.auth.ServiceAccountAuthProvider;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;

@Configuration
public class GoogleDriveConfig {

    @Value("${google.drive.application-name:AutoFetch}")
    private String applicationName;

    @Bean
    public Drive googleDrive(ServiceAccountAuthProvider authProvider) throws Exception {

        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        return new Drive.Builder(
                httpTransport,
                jsonFactory,
                authProvider.getCredentials()
        )
        .setApplicationName(applicationName)
        .build();
    }
}
