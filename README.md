# LuminAI Backend

This is the backend service for **LuminAI**, an AI-powered cinematic video generation system.  
It transforms plain text into short narrated video clips with visuals, audio, and atmosphere — all generated automatically through a multi-stage pipeline.

---

## 🧩 Overview

The backend exposes three main endpoints under `/api/upload/`:

| Endpoint | Method | Description |
|-----------|--------|-------------|
| `/text` | `POST` | Accepts raw input text and a number of scenes to generate. Sends the text to an LLM which returns structured JSON with scene definitions. |
| `/states` | `GET` | Returns the current processing queue of all scenes and their statuses. |
| `/video/{id}` | `GET` | Once all scenes for a batch are complete, concatenates all generated videos into one final video and returns its path. |

---

## ⚙️ Pipeline

### 1. LLM (Scene Generation)
The backend sends the given text and scene number to **OpenAI GPT-4o-mini**.  
The LLM returns a JSON array describing each scene.

Each scene includes:
```
scene
script
photoPrompt
audioPrompt
backgroundPrompt
```

### 2. Seedream (Image Generation)
```
→ Each scene’s photoPrompt is sent to Higgsfield Seedream API.
→ API returns an image job ID.
→ The job is tracked until status = FINISHED.
→ The resulting image URL is stored in memory.
```

### 3. VEO3 (Video Generation)
```
→ Once an image is ready, Higgsfield VEO3 API is triggered with:
   - script
   - audioPrompt
   - backgroundPrompt
   - image_url
→ Returns video job ID.
→ The job is polled until complete.
→ Final video URL is stored in queue.
```

### 4. Video Concatenation
```
→ After all scenes in a batch are finished:
   - Download all video URLs.
   - Concatenate them using FFmpeg.
→ Final output: src/main/resources/videos/{batchId}.mp4
```

---

## 🔁 Queue System
```
HashMap<UUID, State> queue
```

Each State includes:
```
id
batchId
scene
imgStatus / vidStatus
imgurl / vidurl
```

A scheduled task runs every 10 seconds to:
```
- Check job status on Higgsfield
- Update URLs when done
- Trigger next stage automatically
```

---

## 📡 Environment Variables
```
OPEN_AI_KEY        → OpenAI API key
API_KEY_HIGGS      → Higgsfield public key
API_SECRET_HIGGS   → Higgsfield secret key
```

---

## 🧰 Technologies
```
Java 23
Spring Boot
Gradle
Unirest (HTTP)
Jackson (JSON)
FFmpeg (video concatenation)
OpenAI GPT-4o-mini
Higgsfield Seedream & VEO3 APIs
```

---

## 🚀 How It Works
```
1. /upload/text   → send text → returns batchId
2. Scheduler auto-tracks generation
3. /upload/states → monitor queue progress
4. /upload/video/{batchId} → returns final video file path
```

---

## 🧩 Example Flow
```
POST /api/upload/text
{
  "text": "The town celebrated the opening of a new park.",
  "scene_number": 3
}
```

Response:
```
{
  "batchId": "b22eaf23-...-451c"
}
```

After completion:
```
GET /api/upload/video/b22eaf23-...-451c
```

Returns:
```
src/main/resources/videos/b22eaf23-...-451c.mp4
```

---

## 🧠 Notes
```
- LLM output must be valid JSON.
- Polling interval: 10 seconds.
- All jobs are stored in-memory.
- Persistent storage (DB, S3) can be added later.
```

---

## 🪄 Summary
```
Text
→ LLM
→ Scenes
→ Seedream (Images)
→ VEO3 (Videos)
→ FFmpeg (Concatenation)
```

LuminAI backend orchestrates the entire creative chain — from plain text to a finished cinematic video.
