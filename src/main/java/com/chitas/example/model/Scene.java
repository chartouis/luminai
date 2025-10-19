package com.chitas.example.model;

import lombok.Data;

@Data
public class Scene {
    private int scene;
    private String script;
    private String photoPrompt;
    private String audioPrompt;
    private String backgroundPrompt;
}
