package com.chitas.example.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class VideoDownloadService {

    @Value("${video.download.path:src/main/resources/videos}")
    private String downloadPath;

    public String downloadVideo(String videoUrl) throws Exception {
        // Validate URL
        if (videoUrl == null || videoUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Video URL cannot be null or empty");
        }

        // Create download directory if it doesn't exist
        Path downloadDir = Paths.get(downloadPath);
        Files.createDirectories(downloadDir);

        // Generate unique filename
        String fileName = generateFileName(videoUrl);
        String filePath = downloadDir.resolve(fileName).toString();

        try {
            // Download using UniRest
            HttpResponse<InputStream> response = Unirest.get(videoUrl)
                    .asBinary();

            // Check if download was successful
            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed to download video. Status code: " + response.getStatus());
            }

            // Save file
            try (InputStream inputStream = response.getBody();
                    FileOutputStream fileOut = new FileOutputStream(filePath)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOut.write(buffer, 0, bytesRead);
                }
            }

            System.out.println("Video downloaded successfully: " + filePath);
            return filePath;

        } catch (Exception e) {
            // Clean up partially downloaded file
            Files.deleteIfExists(Paths.get(filePath));
            throw new RuntimeException("Error downloading video from URL: " + videoUrl, e);
        }
    }

    public String downloadVideoWithProgress(String videoUrl) throws Exception {
        if (videoUrl == null || videoUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Video URL cannot be null or empty");
        }

        Path downloadDir = Paths.get(downloadPath);
        Files.createDirectories(downloadDir);

        String fileName = generateFileName(videoUrl);
        String filePath = downloadDir.resolve(fileName).toString();

        try {
            HttpResponse<InputStream> response = Unirest.get(videoUrl)
                    .asBinary();

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed to download video. Status code: " + response.getStatus());
            }

            long contentLength = getContentLength(response);

            try (InputStream inputStream = response.getBody();
                    FileOutputStream fileOut = new FileOutputStream(filePath)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                long totalBytesRead = 0;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOut.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;

                    if (contentLength > 0) {
                        double progress = (totalBytesRead * 100.0) / contentLength;
                        System.out.printf("Download progress: %.2f%% (%d/%d bytes)%n",
                                progress, totalBytesRead, contentLength);
                    }
                }
            }

            System.out.println("Video downloaded successfully: " + filePath);
            return filePath;

        } catch (Exception e) {
            Files.deleteIfExists(Paths.get(filePath));
            throw new RuntimeException("Error downloading video from URL: " + videoUrl, e);
        }
    }

    private String generateFileName(String videoUrl) {
        String extension = extractExtension(videoUrl);
        return "video_" + UUID.randomUUID() + extension;
    }

    private String extractExtension(String videoUrl) {
        int dotIndex = videoUrl.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < videoUrl.length() - 1) {
            int queryIndex = videoUrl.indexOf('?', dotIndex);
            if (queryIndex == -1) {
                return videoUrl.substring(dotIndex);
            }
            return videoUrl.substring(dotIndex, queryIndex);
        }
        return ".mp4"; // Default extension
    }

    private long getContentLength(HttpResponse<InputStream> response) {
        try {
            String contentLength = response.getHeaders().getFirst("content-length");
            return contentLength != null ? Long.parseLong(contentLength) : -1L;
        } catch (Exception e) {
            return -1L;
        }
    }
}