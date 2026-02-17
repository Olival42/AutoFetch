package com.example.autofetch.modules.Download.domain.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.autofetch.modules.Download.domain.entity.DownloadItem;

@Repository
public interface IDownloadItemRepository extends JpaRepository<DownloadItem, UUID> {

    List<DownloadItem> findByJobId(UUID jobId);
}
