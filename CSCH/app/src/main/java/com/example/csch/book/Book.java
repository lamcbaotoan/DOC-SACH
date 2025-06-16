package com.example.csch.book;

public class Book {
    private int id;
    private String name;
    private String genre;
    private int chapters;
    private String translatorGroup;
    private String cover;

    public Book(int id, String name, String genre, int chapters, String translatorGroup, String cover) {
        this.id = id;
        this.name = name;
        this.genre = genre;
        this.chapters = chapters;
        this.translatorGroup = translatorGroup;
        this.cover = cover;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGenre() {
        return genre;
    }

    public int getChapters() {
        return chapters;
    }

    public String getTranslatorGroup() {
        return translatorGroup;
    }

    public String getCover() {
        return cover;
    }
}
