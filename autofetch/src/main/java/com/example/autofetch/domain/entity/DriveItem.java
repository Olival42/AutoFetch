package com.example.autofetch.domain.entity;

import lombok.Data;

@Data
public class DriveItem {

    private String id;
    private String name;
    private boolean folder;
    private String mimeType;
}
