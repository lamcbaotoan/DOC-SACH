package com.example.csch.chitiettruyen;

public class Chapter {
    private int chapterNumber;
    private final int index;
    private final String title;

    public Chapter(int index, String title, int chapterNumber) {
        this.index = index;
        this.title = title;
        this.chapterNumber = index ;
    }

    public int getIndex() {
        return index;
    }

    public String getTitle() {
        return title;
    }

    public int getChapterNumber() {
        return chapterNumber;
    }

}