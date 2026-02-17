package com.example.autofetch.modules.Download.domain.entity;

import java.util.UUID;

import com.example.autofetch.modules.Download.domain.enums.DownloadItemStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "download_items")
@Data
@NoArgsConstructor
public class DownloadItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private DownloadJob job;

    @Column(nullable = false)
    private String sourceUrl;

    private String sourceId;

    @Enumerated(EnumType.STRING)
    private DownloadItemStatus status;
}
