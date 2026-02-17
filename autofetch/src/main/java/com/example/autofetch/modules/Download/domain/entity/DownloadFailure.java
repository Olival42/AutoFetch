package com.example.autofetch.modules.Download.domain.entity;

import lombok.Data;

@Data
public class DownloadFailure {
    private String url;
    private String reason;
    private int httpStatus;
}
