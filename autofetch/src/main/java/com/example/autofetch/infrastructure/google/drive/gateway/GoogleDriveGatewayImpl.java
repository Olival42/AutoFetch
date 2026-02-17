package com.example.autofetch.infrastructure.google.drive.gateway;

import java.io.OutputStream;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.autofetch.application.IStorageGateway;
import com.example.autofetch.domain.entity.DriveItem;
import com.example.autofetch.infrastructure.google.drive.client.GoogleDriveClientFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

@Component
public class GoogleDriveGatewayImpl implements IStorageGateway {

    private final Drive drive;

    public GoogleDriveGatewayImpl(GoogleDriveClientFactory googleDrivefactory) {
        this.drive = googleDrivefactory.getClient();
    }

    @Override
    public boolean exists(String fileId) {
        try {
            drive.files()
                    .get(fileId)
                    .execute();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isFolder(String fileId) {
        try {
            File file = drive.files()
                    .get(fileId)
                    .setFields("mimeType")
                    .execute();

            return "application/vnd.google-apps.folder".equals(file.getMimeType());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getFileName(String fileId) {
        try {
            File file = drive.files()
                    .get(fileId)
                    .setFields("name")
                    .execute();

            return file.getName();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get file name for file ID: " + fileId, e);
        }
    }

    @Override
    public String getMimeType(String fileId) {
        try {
            File file = drive.files()
                    .get(fileId)
                    .setFields("mimeType")
                    .execute();

            return file.getMimeType();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get file MIME type for file ID: " + fileId, e);
        }
    }

    @Override
    public List<DriveItem> listChildren(String folderId) {
        try {
            List<File> files = drive.files()
                    .list()
                    .setQ("'" + folderId + "' in parents and trashed = false")
                    .setFields("files(id, name, mimeType)")
                    .execute()
                    .getFiles();

            return files.stream()
                    .map(file -> {
                        DriveItem item = new DriveItem();
                        item.setId(file.getId());
                        item.setName(file.getName());
                        item.setFolder("application/vnd.google-apps.folder".equals(file.getMimeType()));
                        item.setMimeType(file.getMimeType());
                        return item;
                    })
                    .toList();

        } catch (Exception e) {
            throw new RuntimeException("Failed to list children for folder ID: " + folderId, e);
        }
    }

    @Override
    public void download(String fileId, OutputStream out) {
        try {
            drive.files()
                    .get(fileId)
                    .executeMediaAndDownloadTo(out);

        } catch (Exception e) {
            throw new RuntimeException("Failed to download file with ID: " + fileId, e);
        }
    }

    @Override
    public void export(String fileId, String exportMimeType, OutputStream out) {
        try {
            drive.files()
                    .export(fileId, exportMimeType)
                    .executeMediaAndDownloadTo(out);
                    
        } catch (Exception e) {
            throw new RuntimeException("Failed to export file with ID: " + fileId, e);
        }
    }
}
