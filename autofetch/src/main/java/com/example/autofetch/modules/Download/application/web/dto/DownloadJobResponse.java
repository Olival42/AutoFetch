package com.example.autofetch.modules.Download.application.web.dto;

import java.util.UUID;

import com.example.autofetch.modules.Download.domain.enums.DownloadJobStatus;

public record DownloadJobResponse(UUID jobId, DownloadJobStatus status) {

}
