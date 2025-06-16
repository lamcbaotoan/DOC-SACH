package com.example.csch.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;


import com.example.csch.chitiettruyen.BookContent;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "library.db";
    private static final int DATABASE_VERSION = 2;

    // Tên bảng và cột cho bảng Users
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_PHONE = "phonenumber";
    public static final String COLUMN_DATE_CREATED = "date_created";
    public static final String COLUMN_AVATAR = "avatar"; // Ảnh đại diện
    public static final String COLUMN_ROLE = "role"; // Vai trò (quyền user)

    // Tên bảng và cột cho bảng Books
    public static final String TABLE_BOOKS = "books";
    public static final String COLUMN_BOOK_ID = "book_id";
    public static final String COLUMN_BOOK_NAME = "book_name";
    public static final String COLUMN_AUTHOR = "author";
    public static final String COLUMN_GENRE = "genre";
    public static final String COLUMN_STATUS = "status"; // Trạng thái: đang ra, đã kết thúc, tạm ngưng
    public static final String COLUMN_CHAPTERS = "chapters";
    public static final String COLUMN_VIEWS = "views";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_COVER = "cover"; // Bìa sách
    public static final String COLUMN_TRANSLATION_GROUP = "translation_group"; // Nhóm dịch


    // Tên bảng cho lịch sử đọc
    public static final String TABLE_READING_HISTORY = "reading_history";
    public static final String COLUMN_READ_CHAPTERS = "read_chapters"; // Số chương đã đọc
    public static final String COLUMN_TRACKED = "tracked"; // Theo dõi sách

    // Bảng lưu chương mới cập nhật
    public static final String TABLE_NEW_CHAPTERS = "new_chapters";
    public static final String COLUMN_NEW_CHAPTER = "new_chapter"; // Chương mới

    // Bảng lưu nội dung sách
    public static final String TABLE_BOOK_CONTENT = "book_content";
    public static final String COLUMN_CONTENT = "content"; // Nội dung sách
    public static final String COLUMN_CHAPTER_TITLE = "chapter_title"; // Tiêu đề chương
    public static final String COLUMN_CHAPTER_NUMBER = "chapter_number"; // Số chương
    public static final String COLUMN_CHAPTER_CONTENT = "chapter_content"; // Nội dung chương


    // Bảng lưu người đã follow sách
    public static final String FOLLOWED_BOOKS_TABLE = "followed_books";
    public static final String COLUMN_FOLLOWERS_BOOK = "follow";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng Users
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT NOT NULL, " +
                COLUMN_PASSWORD + " TEXT NOT NULL, " +
                COLUMN_PHONE + " TEXT, " +
                COLUMN_DATE_CREATED + " DATE DEFAULT CURRENT_DATE, " +
                COLUMN_AVATAR + " TEXT, " +
                COLUMN_ROLE + " TEXT DEFAULT 'Người dùng')";
        db.execSQL(CREATE_USERS_TABLE);

        // Tạo bảng Books
        String CREATE_BOOKS_TABLE = "CREATE TABLE " + TABLE_BOOKS + " (" +
                COLUMN_BOOK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_BOOK_NAME + " TEXT, " +
                COLUMN_AUTHOR + " TEXT, " +
                COLUMN_GENRE + " TEXT, " +
                COLUMN_STATUS + " TEXT, " +
                COLUMN_CHAPTERS + " INTEGER, " +
                COLUMN_VIEWS + " INTEGER, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_COVER + " TEXT, " +
                COLUMN_TRANSLATION_GROUP + " TEXT)";
        db.execSQL(CREATE_BOOKS_TABLE);

        // Tạo bảng Chapters
        String CREATE_CHAPTERS_TABLE = "CREATE TABLE chapters (" +
                "book_id INTEGER, " +
                "book_name TEXT, " + // Thêm cột này để truy vấn theo tên sách
                "chapter_index INTEGER, " +
                "chapter_title TEXT, " +
                "chapter_content TEXT, " +
                "PRIMARY KEY (book_id, chapter_index))";
        db.execSQL(CREATE_CHAPTERS_TABLE);



        // Tạo bảng Lịch sử đọc
        String CREATE_READING_HISTORY_TABLE = "CREATE TABLE " + TABLE_READING_HISTORY + " (" +
                COLUMN_USER_ID + " INTEGER, " +
                COLUMN_BOOK_ID + " INTEGER, " +
                COLUMN_READ_CHAPTERS + " INTEGER, " +
                COLUMN_TRACKED + " INTEGER, " +
                "PRIMARY KEY (" + COLUMN_USER_ID + ", " + COLUMN_BOOK_ID + "))";
        db.execSQL(CREATE_READING_HISTORY_TABLE);

        // Tạo bảng Chương mới
        String CREATE_NEW_CHAPTERS = "CREATE TABLE " + TABLE_NEW_CHAPTERS + " (" +
                COLUMN_BOOK_ID + " INTEGER, " +
                COLUMN_NEW_CHAPTER + " TEXT)";
        db.execSQL(CREATE_NEW_CHAPTERS);

        // Tạo bảng Nội dung sách
        String CREATE_BOOK_CONTENT = "CREATE TABLE " + TABLE_BOOK_CONTENT + " (" +
                COLUMN_BOOK_ID + " INTEGER, " +
                COLUMN_CHAPTER_NUMBER + " INTEGER, " +
                COLUMN_CHAPTER_TITLE + " TEXT, " +
                COLUMN_CONTENT + " TEXT, " +
                COLUMN_CHAPTER_CONTENT + " TEXT)";
        db.execSQL(CREATE_BOOK_CONTENT);


        //Tạo bảng lưu người đã follow sách
        String CREATE_FOLLOWED_BOOKS_TABLE = "CREATE TABLE " + FOLLOWED_BOOKS_TABLE + " (" +
                COLUMN_BOOK_ID + " INTEGER, " +
                COLUMN_USER_ID + " INTEGER, " +
                COLUMN_FOLLOWERS_BOOK + " TEXT, " +
                "PRIMARY KEY (" + COLUMN_BOOK_ID + ", " + COLUMN_USER_ID + "))";
        db.execSQL(CREATE_FOLLOWED_BOOKS_TABLE);
    }




    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_READING_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEW_CHAPTERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOK_CONTENT);
        db.execSQL("DROP TABLE IF EXISTS " + FOLLOWED_BOOKS_TABLE);
        onCreate(db);
    }

    // Thêm người dùng mới
    public long addUser(String username, String password, String phonenumber, String avatar, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_PHONE, phonenumber);
        values.put(COLUMN_AVATAR, avatar);
        values.put(COLUMN_ROLE, role);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result;
    }

    // Lấy tất cả người dùng
    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS, null);
    }

    // Tìm kiếm người dùng theo username
    public Cursor getUserByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null, COLUMN_USERNAME + " = ?", new String[]{username}, null, null, null);
    }

    // Cập nhật thông tin người dùng
    public int updateUser(int id, String username, String password, String phonenumber, String avatar, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_PHONE, phonenumber);
        values.put(COLUMN_AVATAR, avatar);
        values.put(COLUMN_ROLE, role);

        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected;
    }

    // Xóa người dùng
    public int deleteUser(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_USERS, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsDeleted;
    }

    // Xác thực người dùng bằng username/số điện thoại và mật khẩu
    public boolean validateUser(String identifier, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE (" + COLUMN_USERNAME + " = ? OR " + COLUMN_PHONE + " = ?) AND " + COLUMN_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{identifier, identifier, password});

        boolean isValid = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return isValid;
    }


    public Cursor getAllBooks() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_BOOKS, null);
    }

    public Cursor searchBooks(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_BOOKS +
                " WHERE " + COLUMN_BOOK_NAME + " LIKE ? OR " + COLUMN_TRANSLATION_GROUP + " LIKE ?";
        return db.rawQuery(sql, new String[]{"%" + query + "%", "%" + query + "%"});
    }


    // Lấy thông tin sách
    public Cursor getBookDetails(String bookId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_BOOKS + " WHERE " + COLUMN_BOOK_ID + " = ?", new String[]{bookId});
    }



    // Lấy danh sách chương của sách
    public ArrayList<String> getChaptersByBookId(String bookId) {
        ArrayList<String> chapters = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Corrected the query format
        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_CHAPTER_TITLE +
                        " FROM " + TABLE_BOOK_CONTENT +
                        " WHERE " + COLUMN_BOOK_ID + " = ?",  // Correct usage of placeholder
                new String[]{bookId} // Pass the bookId here as the second parameter
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                chapters.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CHAPTER_TITLE)));
            }
            cursor.close();
        }
        return chapters;
    }

    // Lấy số chương đã đọc
    public int getLastReadChapter(String userId, String bookId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int lastReadChapter = -1; // Mặc định không có lịch sử đọc

        Cursor cursor = db.query(
                TABLE_READING_HISTORY,
                new String[]{COLUMN_READ_CHAPTERS},
                COLUMN_USER_ID + " = ? AND " + COLUMN_BOOK_ID + " = ?",
                new String[]{userId, bookId},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            lastReadChapter = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_READ_CHAPTERS));
            cursor.close();
        }
        return lastReadChapter;
    }


    // Thêm chương mới
    public boolean addChapter(String bookId, int chapterNumber, String chapterTitle, String chapterContent) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_BOOK_ID, bookId);  // ID sách
        values.put(COLUMN_CHAPTER_NUMBER, chapterNumber);  // Số chương
        values.put(COLUMN_CHAPTER_TITLE, chapterTitle);  // Tiêu đề chương
        values.put(COLUMN_CONTENT, chapterContent);  // Nội dung chương
        long result = db.insert(TABLE_BOOK_CONTENT, null, values);
        return result != -1;
    }



    public int getMaxChapterIndex(String bookId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Update the query to use the correct column name: COLUMN_CHAPTER_NUMBER
        String query = "SELECT MAX(" + COLUMN_CHAPTER_NUMBER + ") FROM " + TABLE_BOOK_CONTENT + " WHERE " + COLUMN_BOOK_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{bookId});

        int maxIndex = 0;
        if (cursor.moveToFirst()) {
            maxIndex = cursor.getInt(0); // Get the maximum chapter number
        }
        cursor.close();

        return maxIndex;
    }


    public String getUserAvatar(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT avatar FROM users WHERE id = ?", new String[]{String.valueOf(userId)});
        String avatarBase64 = null;
        if (cursor != null && cursor.moveToFirst()) {
            avatarBase64 = cursor.getString(0);
            cursor.close();
        }
        db.close();
        return avatarBase64;
    }


    // Lưu sách vào cột "tracked"
    public void followBook(String userId, String bookId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_BOOK_ID, bookId);
        values.put(COLUMN_TRACKED, 1); // 1 nghĩa là đang theo dõi

        // Chèn hoặc cập nhật
        db.insertWithOnConflict(TABLE_READING_HISTORY, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    // Xóa thông tin "theo dõi" khỏi cột "tracked"
    public void unfollowBook(String userId, String bookId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_READING_HISTORY,
                COLUMN_USER_ID + " = ? AND " + COLUMN_BOOK_ID + " = ?",
                new String[]{userId, bookId});
        db.close();
    }

    // Kiểm tra xem sách có được theo dõi không
    public boolean isBookFollowed(String userId, String bookId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_READING_HISTORY,
                new String[]{COLUMN_TRACKED},
                COLUMN_USER_ID + " = ? AND " + COLUMN_BOOK_ID + " = ?",
                new String[]{userId, bookId},
                null, null, null);
        boolean isFollowed = false;
        if (cursor != null && cursor.moveToFirst()) {
            isFollowed = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TRACKED)) == 1;
            cursor.close();
        }
        db.close();
        return isFollowed;
    }

    // Cập nhật thông tin người dùng (trừ mật khẩu)
    public int updateUserInfo(int id, String username, String phone, String avatar) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_AVATAR, avatar);

        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected;
    }

    // Xác thực người dùng qua userId và mật khẩu
    public boolean validateUserById(int userId, String oldPassword) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_ID + " = ? AND " + COLUMN_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), oldPassword});

        boolean isValid = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return isValid;
    }

    // Cập nhật mật khẩu người dùng
    public int updatePassword(int userId, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, newPassword);

        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        db.close();
        return rowsAffected;
    }

    public Cursor getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM users WHERE id = ?";
        return db.rawQuery(query, new String[]{String.valueOf(userId)});
    }




}
