package com.chitas.example.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.chitas.example.model.State;
import com.chitas.example.service.VideoGenService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private final VideoGenService vService;

    public UploadController(VideoGenService vService) {
        this.vService = vService;
    }

    @PostMapping(value = "/plaintext", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> uploadPlainText(@RequestBody String text) {
        if (text == null || text.isBlank()) {
            return ResponseEntity.badRequest().body("Empty text");
        }
        vService.pipeline(text);
        return ResponseEntity.ok("Sent");
    }

    @GetMapping("/states")
    public List<State> getStates() {
        return vService.getStates();
    }

    @PostMapping("/load")
    public void load(@RequestBody List<State> states) {
        vService.load(states);
    }

    @GetMapping("/video/{id}")
    public ResponseEntity<byte[]> getVideo(@PathVariable String id) {
        byte[] bytes;
        try {
            Path path = Paths.get(vService.getFullVideo(UUID.fromString(id)));
            bytes = Files.readAllBytes(path);
        } catch (java.io.IOException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("video/mp4"))
                .body(bytes);
    }

}
