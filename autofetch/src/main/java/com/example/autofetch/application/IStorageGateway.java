package com.example.autofetch.application;

import java.io.OutputStream;
import java.util.List;

import com.example.autofetch.domain.entity.DriveItem;

public interface IStorageGateway {

    boolean exists(String fileId);

    boolean isFolder(String fileId);

    void download(String fileId, OutputStream out);

    void export(String fileId, String exportMimeType, OutputStream out);

    String getFileName(String fileId);

    String getMimeType(String fileId);

    List<DriveItem> listChildren(String folderId);
}
