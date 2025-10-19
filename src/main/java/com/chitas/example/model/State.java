package com.chitas.example.model;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class State {
    private UUID id;
    private UUID batchId;
    private Status ImgStatus;
    private Status VidStatus;
    private Scene scene;
    private String imgurl;
    private String vidurl;
    private UUID vidId;

    public static class StateBuilder {
        private UUID id;
        public UUID batchId;
        private Status ImgStatus;
        private Status VidStatus;
        private Scene scene;
        private String imgurl;
        private String vidurl;
        private UUID vidId;

        public StateBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public StateBuilder ImgStatus(Status ImgStatus) {
            this.ImgStatus = ImgStatus;
            return this;
        }

        public StateBuilder VidStatus(Status VidStatus) {
            this.VidStatus = VidStatus;
            return this;
        }

        public StateBuilder scene(Scene scene) {
            this.scene = scene;
            return this;
        }

        public StateBuilder imgurl(String imgurl) {
            this.imgurl = imgurl;
            return this;
        }

        public StateBuilder vidurl(String vidurl) {
            this.vidurl = vidurl;
            return this;
        }

        public StateBuilder vidId(UUID vidId) {
            this.vidId = vidId;
            return this;
        }

        public StateBuilder batchId(UUID batchId) {
            this.batchId = batchId;
            return this;
        }

        public State build() {
            return new State(id, batchId, ImgStatus, VidStatus, scene, imgurl, vidurl, vidId);
        }
    }

    public static StateBuilder builder() {
        return new StateBuilder();
    }
}
