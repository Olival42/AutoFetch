package com.example.autofetch.modules.Download.application.worker;

import java.util.UUID;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.autofetch.modules.Download.domain.enums.DownloadJobStatus;
import com.example.autofetch.modules.Download.domain.repository.IDownloadJobRepository;
import com.example.autofetch.modules.Download.domain.service.DownloadExecutionService;

@Component
public class DownloadWorker {

    private final DownloadExecutionService service;
    
    @Qualifier("taskExecutor")
    private final Executor taskExecutor;

    private final IDownloadJobRepository jobRepository;

    public DownloadWorker(DownloadExecutionService service, Executor taskExecutor, IDownloadJobRepository jobRepository) {
        this.service = service;
        this.taskExecutor = taskExecutor;
        this.jobRepository = jobRepository;
    }

    @Async
    @Transactional
    public void process(UUID jobId) {
        var jobOpt = jobRepository.findById(jobId);
        if (jobOpt.isEmpty()) {
            return;
        }

        var job = jobOpt.get();
        job.setStatus(DownloadJobStatus.IN_PROGRESS);

        jobRepository.save(job);

        try {
            var zipPath = service.execute(job);
            job.setFilePath(zipPath.toString());
            job.setStatus(DownloadJobStatus.COMPLETED);
        } catch (Exception e) {
            job.setStatus(DownloadJobStatus.FAILED);
            job.setErrorMessage(e.getMessage());
        }

        jobRepository.save(job);
    }
}
