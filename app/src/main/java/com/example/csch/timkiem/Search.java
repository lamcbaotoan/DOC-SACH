package com.example.csch.timkiem;

public class Search {
    private int bookID;
    private String bookImage;
    private String nameTruyen;
    private int bookChapter;
    private String translatorGroup;
    private String genre;

    public Search(String bookImage, String nameTruyen, int bookChapter, String translatorGroup, String genre, int bookID) {
        this.bookImage = bookImage;
        this.nameTruyen = nameTruyen;
        this.bookChapter = bookChapter;
        this.translatorGroup = translatorGroup;
        this.genre = genre;
        this.bookID = bookID;
    }

    public String getBookImage() {
        return bookImage;
    }

    public String getNameTruyen() {
        return nameTruyen;
    }

    public int getBookChapter() {
        return bookChapter;
    }

    public String getTranslatorGroup() {
        return translatorGroup;
    }

    public String getGenre() {
        return genre;
    }

    public int getBookID() {
        return bookID;
    }
}
