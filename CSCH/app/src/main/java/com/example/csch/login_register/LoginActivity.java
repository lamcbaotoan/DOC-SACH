package com.example.csch.login_register;

import static android.app.PendingIntent.getActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.csch.MainActivity;

import com.example.csch.R;
import com.example.csch.database.DBHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private ImageButton btnShowPassword;
    private TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Liên kết các view với mã Java
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        btnShowPassword = findViewById(R.id.btnShowPassword);

        //đăng ký
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // Lấy thông tin đăng nhập đã lưu (nếu có)
        SharedPreferences preferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String savedUsername = preferences.getString("username", "");
        String savedPassword = preferences.getString("password", "");
        edtUsername.setText(savedUsername);
        edtPassword.setText(savedPassword);

        // Sự kiện hiển thị mật khẩu
        btnShowPassword.setOnClickListener(v -> {
            if (edtPassword.getTransformationMethod() instanceof PasswordTransformationMethod) {
                edtPassword.setTransformationMethod(null);
                btnShowPassword.setImageResource(R.drawable.ic_menu_view);
            } else {
                edtPassword.setTransformationMethod(new PasswordTransformationMethod());
                btnShowPassword.setImageResource(R.drawable.ic_eye_closed);
            }
            edtPassword.setSelection(edtPassword.getText().length());
        });



        // Khởi tạo DBHelper
        DBHelper dbHelper = new DBHelper(this);

        // Sự kiện khi nhấn nút Đăng nhập
        btnLogin.setOnClickListener(v -> {
            String identifier = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (identifier.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Vui lòng nhập tên đăng nhập và mật khẩu", Toast.LENGTH_SHORT).show();
            } else if (identifier.equals("admin") && password.equals("123456")) {
                SharedPreferences.Editor sessionEditor = preferences.edit();
                sessionEditor.putBoolean("isAdmin", true);
                sessionEditor.apply();

                Intent adminIntent = new Intent(LoginActivity.this, MainActivity.class);
                adminIntent.putExtra("username", identifier);
                adminIntent.putExtra("isAdmin", true);
                adminIntent.putExtra("avatar", "");  // Gán avatar admin nếu có
                adminIntent.putExtra("phonenumber", "");
                adminIntent.putExtra("role", "admin");
                startActivity(adminIntent);
                finish();
            } else if (dbHelper.validateUser(identifier, password)) {
                Cursor cursor = dbHelper.getUserByUsername(identifier);
                if (cursor.moveToFirst()) {
                    String avatar = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_AVATAR));
                    String phonenumber = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PHONE));
                    String role = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ROLE));
                    int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_USER_ID)); // Lấy userId từ DB

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("username", identifier);
                    editor.putString("password", password);
                    editor.putBoolean("isAdmin", false);
                    editor.putBoolean("is_logged_in", true);
                    editor.putInt("userId", userId); // Lưu userId vào SharedPreferences
                    editor.apply();

                    Intent userIntent = new Intent(LoginActivity.this, MainActivity.class);
                    userIntent.putExtra("username", identifier);
                    userIntent.putExtra("avatar", avatar);
                    userIntent.putExtra("phonenumber", phonenumber);
                    userIntent.putExtra("role", role);
                    userIntent.putExtra("userId", userId); // Truyền userId qua Intent
                    startActivity(userIntent);
                    finish();
                }
                cursor.close();

        } else {
                Toast.makeText(LoginActivity.this, "Tài khoản hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
