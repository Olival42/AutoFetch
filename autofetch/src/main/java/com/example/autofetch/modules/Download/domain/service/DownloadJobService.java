package com.example.autofetch.modules.Download.domain.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.autofetch.modules.Download.application.event.DownloadJobCreatedEvent;
import com.example.autofetch.modules.Download.application.web.dto.DownloadRequestDTO;
import com.example.autofetch.modules.Download.domain.entity.DownloadItem;
import com.example.autofetch.modules.Download.domain.entity.DownloadJob;
import com.example.autofetch.modules.Download.domain.enums.DownloadItemStatus;
import com.example.autofetch.modules.Download.domain.enums.DownloadJobStatus;
import com.example.autofetch.modules.Download.domain.repository.IDownloadItemRepository;
import com.example.autofetch.modules.Download.domain.repository.IDownloadJobRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class DownloadJobService {

    private final IDownloadJobRepository downloadJobRepository;
    private final IDownloadItemRepository itemRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public DownloadJobService(IDownloadJobRepository downloadJobRepository, IDownloadItemRepository itemRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.downloadJobRepository = downloadJobRepository;
        this.itemRepository = itemRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional
    public DownloadJob createJob(DownloadRequestDTO data) {

        var job = downloadJobRepository.save(new DownloadJob());

        for (String url : data.urls()) {
            DownloadItem item = new DownloadItem();
            item.setJob(job); // agora o job já tem ID
            item.setSourceUrl(url);
            item.setStatus(DownloadItemStatus.PENDING);
            itemRepository.save(item);
        }

        applicationEventPublisher.publishEvent(
            new DownloadJobCreatedEvent(job.getId())
        );

        return job;
    }


    public DownloadJob getJob(UUID jobId) {
        return downloadJobRepository.findById(jobId)
                .orElseThrow(() -> new EntityNotFoundException("Download job not found"));
    }

    public Path getZipPath(UUID jobId) {

        DownloadJob job = downloadJobRepository.findById(jobId)
                .orElseThrow(() -> new EntityNotFoundException("Job not found"));

        if (job.getStatus() != DownloadJobStatus.COMPLETED) {
            throw new IllegalStateException("Job not completed");
        }

        if (job.getFilePath() == null) {
            throw new IllegalStateException("ZIP not generated");
        }

        Path zip = Paths.get(job.getFilePath());

        if (!Files.exists(zip)) {
            throw new IllegalStateException("ZIP file missing");
        }

        return zip;
    }
}
