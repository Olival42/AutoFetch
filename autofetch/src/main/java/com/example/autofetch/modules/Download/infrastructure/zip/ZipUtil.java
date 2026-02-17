package com.example.autofetch.modules.Download.infrastructure.zip;

import java.nio.file.Path;
import java.util.zip.ZipOutputStream;

import com.example.autofetch.modules.Download.domain.entity.DownloadJob;

import java.io.IOException;
import java.nio.file.Files;

public class ZipUtil {

    public static Path createZipPath(DownloadJob job) throws Exception {
        return Files.createTempFile(
                "autofetch-" + job.getId(),
                ".zip");
    }

    public static ZipOutputStream openZip(Path zipPath) throws IOException {
        return new ZipOutputStream(Files.newOutputStream(zipPath));
    }
}
