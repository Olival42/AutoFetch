package com.example.autofetch.modules.Download.application.web.controller;

import java.util.UUID;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.autofetch.modules.Download.application.web.dto.DownloadJobResponse;
import com.example.autofetch.modules.Download.application.web.dto.DownloadRequestDTO;
import com.example.autofetch.modules.Download.domain.service.DownloadJobService;
import com.example.autofetch.shared.ApiResponse;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

@RestController
@RequestMapping("/downloads")
public class DownloadController_ {

    private final DownloadJobService downloadJobService;

    public DownloadController_(DownloadJobService downloadJobService) {
        this.downloadJobService = downloadJobService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DownloadJobResponse>> startDownload(
            @RequestBody DownloadRequestDTO req) {

        var job = downloadJobService.createJob(req);

        var response = new DownloadJobResponse(job.getId(), job.getStatus());

        ApiResponse<DownloadJobResponse> apiResponse = ApiResponse.<DownloadJobResponse>builder()
                .success(true)
                .data(response)
                .error(null)
                .build();

        return ResponseEntity.accepted().body(apiResponse);
    }

    @GetMapping("/{jobId}/status")
    public ResponseEntity<ApiResponse<DownloadJobResponse>> status(@PathVariable UUID jobId) {

        var job = downloadJobService.getJob(jobId);

        var response = new DownloadJobResponse(job.getId(), job.getStatus());

        ApiResponse<DownloadJobResponse> apiResponse = ApiResponse.<DownloadJobResponse>builder()
                .success(true)
                .data(response)
                .error(null)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<Resource> download(
            @PathVariable UUID jobId) {

        var zipPath = downloadJobService.getZipPath(jobId);

        Resource resource = new FileSystemResource(zipPath);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + zipPath.getFileName() + "\""
                )
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
