package com.example.csch.notification;



public class NotificationItem {
    private String bookName;
    private String coverBase64;
    private int newChapter;

    public NotificationItem(String bookName, String coverBase64, int newChapter) {
        this.bookName = bookName;
        this.coverBase64 = coverBase64;
        this.newChapter = newChapter;
    }

    public String getBookName() {
        return bookName;
    }

    public String getCoverBase64() {
        return coverBase64;
    }

    public int getNewChapter() {
        return newChapter;
    }
}
