package com.example.autofetch.application.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoogleDriveFileIdExtractor {

    private static final List<String> PATTERNS_MATCHES = List.of(
            "^https://drive\\.google\\.com/file/d/([a-zA-Z0-9_-]+)",
            "^https://drive\\.google\\.com/drive/folders/([a-zA-Z0-9_-]+)",
            "^https://docs\\.google\\.com/document/d/([a-zA-Z0-9_-]+)",
            "^https://docs\\.google\\.com/spreadsheets/d/([a-zA-Z0-9_-]+)",
            "^https://docs\\.google\\.com/presentation/d/([a-zA-Z0-9_-]+)");

    public static String extractFileId(String url) {
        for (String pattern : PATTERNS_MATCHES) {
            Matcher matcher = Pattern.compile(pattern).matcher(url);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        throw new IllegalArgumentException("URL does not match any known Google Drive patterns.");
    }
}
