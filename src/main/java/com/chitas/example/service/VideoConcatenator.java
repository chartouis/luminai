package com.chitas.example.service;

import java.io.*;
import java.util.List;

import java.io.*;
import java.util.List;

public class VideoConcatenator {

    /**
     * Concatenate multiple video files using FFmpeg
     * 
     * @param videoFiles List of video file paths to concatenate
     * @param outputPath Path for the output video file
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the process is interrupted
     */
    public static void concatenateVideos(List<String> videoFiles, String outputPath)
            throws IOException, InterruptedException {

        // Filter out empty strings
        List<String> validFiles = videoFiles.stream()
                .filter(f -> f != null && !f.trim().isEmpty())
                .toList();

        if (validFiles.isEmpty()) {
            throw new IllegalArgumentException("No valid video files to concatenate");
        }

        // Create a temporary concat demuxer file
        File concatFile = createConcatDemuxerFile(validFiles);

        try {
            // Build FFmpeg command
            ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg",
                    "-f", "concat",
                    "-safe", "0",
                    "-i", concatFile.getAbsolutePath(),
                    "-c", "copy",
                    outputPath);

            // Redirect error stream to see FFmpeg output
            pb.redirectErrorStream(true);

            Process process = pb.start();

            // Print FFmpeg output
            printProcessOutput(process);

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException("FFmpeg process failed with exit code: " + exitCode);
            }

            System.out.println("Video concatenation completed successfully!");

        } finally {
            // Clean up temporary concat file
            if (concatFile.exists()) {
                concatFile.delete();
            }
        }
    }

    /**
     * Create a concat demuxer file for FFmpeg
     * 
     * @param videoFiles List of video file paths
     * @return File object pointing to the created concat file
     * @throws IOException if an I/O error occurs
     */
    private static File createConcatDemuxerFile(List<String> videoFiles)
            throws IOException {

        File concatFile = new File("concat_" + System.nanoTime() + ".txt");

        try (FileWriter writer = new FileWriter(concatFile)) {
            for (String videoFile : videoFiles) {
                // Convert to absolute path and use forward slashes for Windows compatibility
                File f = new File(videoFile);
                String absolutePath = f.getAbsolutePath().replace("\\", "/");
                // Escape single quotes in file paths
                String escapedPath = absolutePath.replace("'", "'\\''");
                writer.write("file '" + escapedPath + "'\n");
            }
        }

        return concatFile;
    }

    /**
     * Print the output from the FFmpeg process
     * 
     * @param process The process to read output from
     */
    private static void printProcessOutput(Process process) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}