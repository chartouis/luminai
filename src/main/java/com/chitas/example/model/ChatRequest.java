package com.chitas.example.model;

import java.util.List;
import java.util.Map;

public class ChatRequest {
    private String model;
    private List<Map<String, String>> messages;

    public ChatRequest(String model, String userPrompt) {
        this.model = model;
        this.messages = List.of(
                Map.of("role", "user", "content", userPrompt));
    }

    public String getModel() {
        return model;
    }

    public List<Map<String, String>> getMessages() {
        return messages;
    }
}