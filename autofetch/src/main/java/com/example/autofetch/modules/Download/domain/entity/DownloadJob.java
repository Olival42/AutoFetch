package com.example.autofetch.modules.Download.domain.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.autofetch.modules.Download.domain.enums.DownloadJobStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "download_jobs")
@Data
public class DownloadJob {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DownloadJobStatus status;

    private String filePath;

    private String errorMessage;

    @OneToMany(
        mappedBy = "job",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<DownloadItem> items = new ArrayList<>();

    private int successCount = 0;

    public DownloadJob() {
        this.status = DownloadJobStatus.PENDING;
    }
}
