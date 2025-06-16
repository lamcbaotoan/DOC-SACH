package com.example.csch.fragmentsuser;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.csch.R;
import com.example.csch.chitiettruyen.DetailActivity;
import com.example.csch.database.DBHelper;
import com.example.csch.followvahistory.FollowAdapter;
import com.example.csch.followvahistory.FollowItem;

import java.util.ArrayList;
import java.util.List;

public class ItemFollowFragment extends Fragment {

    private ListView lvFollow;
    private FollowAdapter adapter;
    private int userId;
    private DBHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_follow, container, false);

        // Lấy userId từ SharedPreferences
        SharedPreferences preferences = requireContext().getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        userId = preferences.getInt("userId", -1); // Giá trị mặc định -1 nếu không tìm thấy

        lvFollow = view.findViewById(R.id.lvFollow);
        dbHelper = new DBHelper(getContext());

        // Load data from DBHelper
        List<FollowItem> followList = loadDataFromDatabase();
        adapter = new FollowAdapter(getContext(), followList);
        lvFollow.setAdapter(adapter);

        // Set item click listener
        lvFollow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FollowItem item = (FollowItem) adapter.getItem(position);

                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra("BOOK_ID", String.valueOf(item.getBookId())); // Truyền bookId
                intent.putExtra("USER_ID", String.valueOf(userId));           // Truyền userId
                startActivity(intent);
            }
        });

        return view;
    }

    private List<FollowItem> loadDataFromDatabase() {
        List<FollowItem> followList = new ArrayList<>();
        Cursor cursor = null;

        try {
            // Query database for tracked books
            String query = "SELECT b." + DBHelper.COLUMN_BOOK_ID + ", " +
                    "b." + DBHelper.COLUMN_BOOK_NAME + ", " +
                    "b." + DBHelper.COLUMN_COVER + ", " +
                    "b." + DBHelper.COLUMN_GENRE + ", " +
                    "b." + DBHelper.COLUMN_TRANSLATION_GROUP + ", " +
                    "b." + DBHelper.COLUMN_CHAPTERS + ", " +
                    "r." + DBHelper.COLUMN_READ_CHAPTERS +
                    " FROM " + DBHelper.TABLE_BOOKS + " b" +
                    " INNER JOIN " + DBHelper.TABLE_READING_HISTORY + " r" +
                    " ON b." + DBHelper.COLUMN_BOOK_ID + " = r." + DBHelper.COLUMN_BOOK_ID +
                    " WHERE r." + DBHelper.COLUMN_USER_ID + " = ?" +
                    " AND r." + DBHelper.COLUMN_TRACKED + " = 1";

            cursor = dbHelper.getReadableDatabase().rawQuery(query, new String[]{String.valueOf(userId)});

            // Process each row
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int bookId = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_BOOK_ID));
                    String bookName = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_BOOK_NAME));
                    String coverBase64 = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_COVER));
                    String genreString = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_GENRE));
                    String translatorGroup = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TRANSLATION_GROUP));
                    int latestChapter = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CHAPTERS));
                    int readChapter = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_READ_CHAPTERS));

                    // Parse genres into a List
                    List<String> genres = parseGenres(genreString);

                    // Create FollowItem and add to list
                    FollowItem item = new FollowItem(coverBase64, latestChapter, readChapter, translatorGroup, genres, bookId, bookName);
                    followList.add(item);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Ensure the cursor is closed
            if (cursor != null) {
                cursor.close();
            }
        }

        return followList;
    }

    /**
     * Parse a comma-separated genre string into a List of genres.
     *
     * @param genreString Comma-separated string of genres
     * @return List of genres
     */
    private List<String> parseGenres(String genreString) {
        List<String> genres = new ArrayList<>();
        if (genreString != null && !genreString.isEmpty()) {
            String[] genreArray = genreString.split(",");
            for (String genre : genreArray) {
                genres.add(genre.trim());
            }
        }
        return genres;
    }

}
