package com.healthyForum.model.Enum;

public enum Visibility {
    PUBLIC("Public"),
    PRIVATE("Private"),
    FOLLOWERS_ONLY("Followers only"),
    DRAFTS("Drafts");


    private final String label;

    Visibility(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
