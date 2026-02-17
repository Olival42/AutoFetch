package com.example.autofetch.modules.Download.domain.service;

import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.stereotype.Service;

import com.example.autofetch.application.IStorageGateway;
import com.example.autofetch.application.utils.GoogleDriveFileIdExtractor;
import com.example.autofetch.modules.Download.domain.entity.DownloadJob;
import com.example.autofetch.modules.Download.domain.enums.DownloadItemStatus;
import com.example.autofetch.modules.Download.domain.repository.IDownloadItemRepository;
import com.example.autofetch.modules.Download.infrastructure.zip.ZipUtil;

@Service
public class DownloadExecutionService {

    private final IStorageGateway gateway;
    private final IDownloadItemRepository repository;

    public DownloadExecutionService(IStorageGateway gateway, IDownloadItemRepository repository) {
        this.gateway = gateway;
        this.repository = repository;
    }

    public Path execute(DownloadJob job) throws Exception {
        try {
            Path zipPath = ZipUtil.createZipPath(job);
            try (ZipOutputStream zipOutputStream = ZipUtil.openZip(zipPath)) {
                processItem(job, zipOutputStream);
            }
            return zipPath;
        } catch (Exception e) {
            throw new Exception("Failed to execute download job: " + e.getMessage(), e);
        }
    }

    private void processItem(DownloadJob job, ZipOutputStream zipOutputStream) {
        var items = repository.findByJobId(job.getId());

        for (var item : items) {
            item.setStatus(DownloadItemStatus.IN_PROGRESS);
            try {
                processUrl(item.getSourceUrl(), zipOutputStream);
                item.setStatus(DownloadItemStatus.COMPLETED);
            } catch (Exception e) {
                item.setStatus(DownloadItemStatus.FAILED);
            }
            repository.save(item);
        }
    }

    private void processUrl(
            String url,
            ZipOutputStream zip) {
        String fileId = GoogleDriveFileIdExtractor.extractFileId(url);

        if (!gateway.exists(fileId)) {
            throw new IllegalArgumentException(
                    "ID não encontrado ou não acessível: " + fileId +
                            "\nURL original: " + url);
        }

        if (gateway.isFolder(fileId)) {
            downloadFolder(fileId, zip);
        } else {
            zipSingleFile(fileId, zip);
        }
    }

    private void zipSingleFile(
            String fileId,
            ZipOutputStream zip) {
        try {
            var mimeType = gateway.getMimeType(fileId);
            var extension = resolveExtension(mimeType);
            var name = resolveFileName(fileId, extension);

            zip.putNextEntry(new ZipEntry(name));

            if (mimeType.startsWith("application/vnd.google-apps.")) {
                var exportMimeType = resolveExportMimeType(mimeType);
                gateway.export(fileId, exportMimeType, zip);
            } else {
                gateway.download(fileId, zip);
            }

            zip.closeEntry();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Erro ao zipar arquivo " + fileId, e);
        }
    }

    private void downloadFolder(
            String folderId,
            ZipOutputStream zip) {
        var fileIds = gateway.listChildren(folderId).stream().map(c -> c.getId()).toList();

        for (String fileId : fileIds) {
            zipSingleFile(fileId, zip);
        }
    }

    private String resolveFileName(String fileId, String extension) {

        var name = gateway.getFileName(fileId);

        if (name == null || name.isBlank()) {
            name = fileId;
        }

        if (!name.endsWith(extension)) {
            name += extension;
        }

        return name;
    }

    private String resolveExtension(String mimeType) {
        return switch (mimeType) {
            case "application/vnd.google-apps.document" -> ".pdf";
            case "application/vnd.google-apps.spreadsheet" -> ".xlsx";
            case "application/vnd.google-apps.presentation" -> ".pptx";
            default -> "";
        };
    }

    private String resolveExportMimeType(String mimeType) {
        return switch (mimeType) {
            case "application/vnd.google-apps.document" ->
                "application/pdf";
            case "application/vnd.google-apps.spreadsheet" ->
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "application/vnd.google-apps.presentation" ->
                "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            default ->
                throw new IllegalArgumentException("Export não suportado: " + mimeType);
        };
    }

}
