package com.example.autofetch.infrastructure.google.drive.client;

import org.springframework.stereotype.Component;

import com.google.api.services.drive.Drive;

@Component
public class GoogleDriveClientFactory {

    private final Drive drive;

    public GoogleDriveClientFactory(Drive drive) {
        this.drive = drive;
    }

    public Drive getClient() {
        return drive;
    }
}
