package com.example.androidfrontend.model;

import java.io.Serializable;

/**
 * PUBLIC_INTERFACE
 * Represents a Note.
 */
public class Note implements Serializable {
    private String id;
    private String title;
    private String content;
    private String createdAt;
    private String updatedAt;

    public Note(String id, String title, String content, String createdAt, String updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public String getCreatedAt() { return createdAt; }

    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
