package com.example.autofetch.modules.Download.application.listener;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.autofetch.modules.Download.application.event.DownloadJobCreatedEvent;
import com.example.autofetch.modules.Download.application.worker.DownloadWorker;

@Component
public class DownloadJobListener {

    private final DownloadWorker worker;

    public DownloadJobListener(DownloadWorker worker) {
        this.worker = worker;
    }

    @Async
    @TransactionalEventListener(
        phase = TransactionPhase.AFTER_COMMIT
    )
    public void onJobCreated(DownloadJobCreatedEvent event) {
        worker.process(event.jobId());
    }
}
