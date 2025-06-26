package com.healthyForum.model.Enum;

public enum Visibility {
    PUBLIC("Public"),
    PRIVATE("Private"),
    FRIENDS_ONLY("Friends only");

    private final String label;

    Visibility(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
