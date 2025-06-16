package com.example.csch.followvahistory;

import java.util.List;

public class FollowItem {
    private String name;
    private String imageBase64;
    private int latestChapter;
    private int readChapter;
    private String translatorGroup;
    private List<String> genres;
    private int bookId;

    public FollowItem(String imageBase64, int latestChapter, int readChapter, String translatorGroup, List<String> genres, int bookId, String name) {
        this.imageBase64 = imageBase64;
        this.latestChapter = latestChapter;
        this.readChapter = readChapter;
        this.translatorGroup = translatorGroup;
        this.genres = genres;
        this.bookId = bookId;
        this.name = name;
    }

    // Getters
    public String getImageBase64() {
        return imageBase64;
    }

    public int getLatestChapter() {
        return latestChapter;
    }

    public int getReadChapter() {
        return readChapter;
    }

    public String getTranslatorGroup() {
        return translatorGroup;
    }

    public List<String> getGenres() {
        return genres;
    }

    public int getBookId() {
        return bookId;
    }

    public String getName() {
        return name;
    }
}
