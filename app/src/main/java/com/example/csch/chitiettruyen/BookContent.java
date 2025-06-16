package com.example.csch.chitiettruyen;

public class BookContent {
    private int bookId;
    private int chapterIndex;
    private int chapterNumber;
    private String chapterTitle;
    private String content;
    private String chapterContent;

    // Constructor
    public BookContent(int bookId, int chapterIndex, int chapterNumber, String chapterTitle, String content, String chapterContent) {
        this.bookId = bookId;
        this.chapterIndex = chapterIndex;
        this.chapterNumber = chapterNumber;
        this.chapterTitle = chapterTitle;
        this.content = content;
        this.chapterContent = chapterContent;
    }

    // Getter và Setter
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public int getChapterIndex() { return chapterIndex; }
    public void setChapterIndex(int chapterIndex) { this.chapterIndex = chapterIndex; }

    public int getChapterNumber() { return chapterNumber; }
    public void setChapterNumber(int chapterNumber) { this.chapterNumber = chapterNumber; }

    public String getChapterTitle() { return chapterTitle; }
    public void setChapterTitle(String chapterTitle) { this.chapterTitle = chapterTitle; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getChapterContent() { return chapterContent; }
    public void setChapterContent(String chapterContent) { this.chapterContent = chapterContent; }
}
