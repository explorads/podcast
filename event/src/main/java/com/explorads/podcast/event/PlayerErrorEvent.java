package com.explorads.podcast.event;

public class PlayerErrorEvent {
    private final String message;

    public PlayerErrorEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
