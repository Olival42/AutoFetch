package com.example.autofetch.modules.Download.domain.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.autofetch.modules.Download.domain.entity.DownloadJob;

@Repository
public interface IDownloadJobRepository extends JpaRepository<DownloadJob, UUID> {
}
