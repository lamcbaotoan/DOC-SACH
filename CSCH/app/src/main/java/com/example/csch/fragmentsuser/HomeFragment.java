package com.example.csch.fragmentsuser;

import static android.content.Context.MODE_PRIVATE;
import static android.media.CamcorderProfile.get;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.csch.R;
import com.example.csch.Home.Home;
import com.example.csch.Home.HomeAdapter;
import com.example.csch.Home.HomeRecyclerViewAdapter;
import com.example.csch.chitiettruyen.DetailActivity;
import com.example.csch.chitiettruyen.DetailAdminActivity;
import com.example.csch.database.DBHelper;
import com.example.csch.notification.NotificationActivity;
import com.example.csch.timkiem.SearchActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private GridView listViewBookshome;
    private DBHelper dbHelper;
    private TextView tvSeeAll;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Lấy userId từ SharedPreferences
        SharedPreferences preferences = requireContext().getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        int userId = preferences.getInt("userId", -1); // Giá trị mặc định -1 nếu không tìm thấy

        // TextView cần ẩn/hiện
        TextView textView4 = view.findViewById(R.id.textView4);

        // Xử lý logic cho tvSeeAll
        tvSeeAll = view.findViewById(R.id.tvSeeAll);
        tvSeeAll.setOnClickListener(v -> {
            if (recyclerView.getVisibility() == View.VISIBLE) {
                // Nếu RecyclerView đang hiển thị, ẩn nó và đổi text
                recyclerView.setVisibility(View.GONE);
                textView4.setVisibility(View.GONE);
                tvSeeAll.setText("Hide All");
            } else {
                // Nếu RecyclerView đang ẩn, hiện nó và đổi text
                recyclerView.setVisibility(View.VISIBLE);
                textView4.setVisibility(View.VISIBLE);
                tvSeeAll.setText("See All");
            }
        });

        // RecyclerView hiển thị 5 sách ngẫu nhiên
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        dbHelper = new DBHelper(requireContext());
        List<Home> randomBooks = getRandomBooks(5);

        // Tạo adapter và gắn sự kiện click
        HomeRecyclerViewAdapter recyclerAdapter = new HomeRecyclerViewAdapter(requireContext(), randomBooks);
        recyclerAdapter.setOnItemClickListener(selectedBook -> {
            // Lấy thông tin user
            boolean isAdmin = preferences.getBoolean("isAdmin", false);
            Intent intent;
            if (isAdmin) {
                intent = new Intent(getContext(), DetailAdminActivity.class);
            } else {
                intent = new Intent(getContext(), DetailActivity.class);
            }
            intent.putExtra("BOOK_ID", String.valueOf(selectedBook.getBookId()));
            intent.putExtra("USER_ID", String.valueOf(userId));
            startActivity(intent);
        });

        // Gắn adapter cho RecyclerView
        recyclerView.setAdapter(recyclerAdapter);

        // GridView hiển thị toàn bộ sách
        listViewBookshome = view.findViewById(R.id.listViewBookshome);
        List<Home> allBooks = getAllBooks();
        HomeAdapter gridAdapter = new HomeAdapter(requireContext(), allBooks);
        listViewBookshome.setAdapter(gridAdapter);

        // Tìm kiếm
        EditText etSearchHome = view.findViewById(R.id.etSearch_home);
        etSearchHome.setOnClickListener(v -> search());

        // Thông báo
        ImageView ivNotification = view.findViewById(R.id.ivNotification);
        ivNotification.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), NotificationActivity.class);
            startActivity(intent);
        });

        // Thêm sự kiện nhấn vào item listViewBookshome
        listViewBookshome.setOnItemClickListener((parent, v, position, id) -> {
            Home selectedBook = allBooks.get(position);

            // Kiểm tra quyền của người dùng
            boolean isAdmin = preferences.getBoolean("isAdmin", false);

            Intent intent;
            if (isAdmin) {
                intent = new Intent(getContext(), DetailAdminActivity.class); // Nếu là admin
            } else {
                intent = new Intent(getContext(), DetailActivity.class); // Nếu là user hoặc khách
            }

            // Truyền thông tin sách sang Activity phù hợp
            intent.putExtra("BOOK_ID", String.valueOf(selectedBook.getBookId())); // Đảm bảo truyền bookId dưới dạng String
            intent.putExtra("USER_ID", String.valueOf(userId)); // Truyền userId vào Intent
            startActivity(intent);
        });

        return view;
    }

    private List<Home> getAllBooks() {
        List<Home> books = new ArrayList<>();
        Cursor cursor = dbHelper.getAllBooks();
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    String title = cursor.getString(cursor.getColumnIndexOrThrow("book_name"));
                    String chapter = "Chương: " + cursor.getInt(cursor.getColumnIndexOrThrow("chapters"));
                    String cover = cursor.getString(cursor.getColumnIndexOrThrow("cover"));
                    int bookId = cursor.getInt(cursor.getColumnIndexOrThrow("book_id"));
                    books.add(new Home(title, chapter, cover, bookId));
                }
            } finally {
                cursor.close();
            }
        }
        return books;
    }

    private List<Home> getRandomBooks(int count) {
        List<Home> books = getAllBooks();
        Collections.shuffle(books);
        return books.subList(0, Math.min(count, books.size()));
    }

    private void search() {
        Intent intent = new Intent(requireContext(), SearchActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) dbHelper.close();
    }
}