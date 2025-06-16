package com.example.csch.Home;

public class Home {
    private int bookId;
    private String bookTitle;
    private String chapter;
    private String coverImage;

    public Home(String bookTitle, String chapter, String coverImage, int bookId) {
        this.bookTitle = bookTitle;
        this.chapter = chapter;
        this.coverImage = coverImage;
        this.bookId = bookId;

    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getChapter() {
        return chapter;
    }

    public String getCoverImage() {
        return coverImage;
    }
    public int getBookId() {
        return bookId;
    }
}
