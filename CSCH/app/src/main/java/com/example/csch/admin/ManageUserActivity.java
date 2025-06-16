package com.example.csch.admin;

import static com.example.csch.database.DBHelper.COLUMN_AVATAR;
import static com.example.csch.database.DBHelper.COLUMN_DATE_CREATED;
import static com.example.csch.database.DBHelper.COLUMN_PASSWORD;
import static com.example.csch.database.DBHelper.COLUMN_PHONE;
import static com.example.csch.database.DBHelper.COLUMN_USERNAME;
import static com.example.csch.database.DBHelper.COLUMN_USER_ID;
import static com.example.csch.database.DBHelper.TABLE_USERS;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.csch.R;
import com.example.csch.database.DBHelper;
import com.example.csch.user.User;
import com.example.csch.user.UserAdapter;

import java.util.ArrayList;

public class ManageUserActivity extends AppCompatActivity {
    private ListView lvUsers;
    private DBHelper dbHelper;
    private EditText etSearch;
    private Button btnAddUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);

        lvUsers = findViewById(R.id.lv_users);
        btnAddUser = findViewById(R.id.btn_add_user);
        etSearch = findViewById(R.id.etSearch_manage_user);

        dbHelper = new DBHelper(this);

        // Load all users initially
        loadUsers("");

        // Button to add a new user
        btnAddUser.setOnClickListener(v -> {
            Intent intent = new Intent(ManageUserActivity.this, AddUserActivity.class);
            startActivity(intent);
        });

        // Search functionality
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadUsers(s.toString()); // Reload users based on search query
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No additional actions needed after text change
            }
        });

        // Item click listener for editing a user
        lvUsers.setOnItemClickListener((parent, view, position, id) -> {
            User selectedUser = (User) parent.getItemAtPosition(position); // Get the selected user
            Intent intent = new Intent(ManageUserActivity.this, EditUserActivity.class);
            intent.putExtra("USER_ID", selectedUser.getId()); // Pass the user ID to EditUserActivity
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsers(etSearch.getText().toString().trim());
        Toast.makeText(this, "Danh sách người dùng đã được cập nhật.", Toast.LENGTH_SHORT).show();
    }

    // Modify loadUsers to use ArrayList and UserAdapter
    private void loadUsers(String query) {
        ArrayList<User> userList = new ArrayList<>();

        String sqlQuery = "SELECT " + COLUMN_USER_ID + ", " +
                COLUMN_USERNAME + ", " +
                COLUMN_PHONE + ", " +
                COLUMN_DATE_CREATED + ", " +
                COLUMN_PASSWORD + ", " +
                COLUMN_AVATAR + " FROM " + TABLE_USERS;

        String[] selectionArgs = null;
        if (!query.isEmpty()) {
            sqlQuery += " WHERE " + COLUMN_USERNAME + " LIKE ? OR " + COLUMN_PHONE + " LIKE ?";
            selectionArgs = new String[]{"%" + query + "%", "%" + query + "%"};
        }

        Cursor cursor = null;
        try {
            cursor = dbHelper.getReadableDatabase().rawQuery(sqlQuery, selectionArgs);
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
                    String username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
                    String phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE));
                    String dateCreated = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_CREATED));
                    String password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));
                    String avatar = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AVATAR)); // Chuyển sang kiểu String cho avatar

                    // Tạo đối tượng User
                    userList.add(new User(id, username, phone, dateCreated, password, avatar));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        // Cập nhật adapter
        UserAdapter adapter = new UserAdapter(this, userList);
        lvUsers.setAdapter(adapter);
    }

}
