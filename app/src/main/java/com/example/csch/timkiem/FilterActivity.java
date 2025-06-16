package com.example.csch.timkiem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.csch.R;

import java.util.ArrayList;

public class FilterActivity extends AppCompatActivity {
    private CheckBox checkbox_hien_dai, checkbox_huyen_huyen, checkbox_hau_cung, checkbox_he_thong, checkbox_kinh_di, checkbox_lich_su, checkbox_mat_the, checkbox_ngon_tinh, checkbox_thanh_xuan, checkbox_truong_hoc, checkbox_trung_sinh, checkbox_trong_sinh, checkbox_tu_tien, checkbox_xuyen_khong;
    private Button btnClear, btnApply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        checkbox_hien_dai = findViewById(R.id.checkbox_hien_dai);
        checkbox_huyen_huyen = findViewById(R.id.checkbox_huyen_huyen);
        checkbox_hau_cung = findViewById(R.id.checkbox_hau_cung);
        checkbox_he_thong = findViewById(R.id.checkbox_he_thong);
        checkbox_kinh_di = findViewById(R.id.checkbox_kinh_di);
        checkbox_lich_su = findViewById(R.id.checkbox_lich_su);
        checkbox_mat_the = findViewById(R.id.checkbox_mat_the);
        checkbox_ngon_tinh = findViewById(R.id.checkbox_ngon_tinh);
        checkbox_thanh_xuan = findViewById(R.id.checkbox_thanh_xuan);
        checkbox_truong_hoc = findViewById(R.id.checkbox_truong_hoc);
        checkbox_trung_sinh = findViewById(R.id.checkbox_trung_sinh);
        checkbox_trong_sinh = findViewById(R.id.checkbox_trong_sinh);
        checkbox_tu_tien = findViewById(R.id.checkbox_tu_tien);
        checkbox_xuyen_khong = findViewById(R.id.checkbox_xuyen_khong);



        btnClear = findViewById(R.id.btn_clear);
        btnApply = findViewById(R.id.btn_apply);

        btnClear.setOnClickListener(v -> clearAllCheckboxes());

        btnApply.setOnClickListener(v -> applyFilters());
    }

    private void clearAllCheckboxes() {
        checkbox_hien_dai.setChecked(false);
        checkbox_huyen_huyen.setChecked(false);
        checkbox_hau_cung.setChecked(false);
        checkbox_he_thong.setChecked(false);
        checkbox_kinh_di.setChecked(false);
        checkbox_lich_su.setChecked(false);
        checkbox_mat_the.setChecked(false);
        checkbox_ngon_tinh.setChecked(false);
        checkbox_thanh_xuan.setChecked(false);
        checkbox_truong_hoc.setChecked(false);
        checkbox_trung_sinh.setChecked(false);
        checkbox_trong_sinh.setChecked(false);
        checkbox_tu_tien.setChecked(false);
        checkbox_xuyen_khong.setChecked(false);
        // Clear thêm các checkbox khác ở đây nếu có
    }

    private void applyFilters() {
        ArrayList<String> selectedGenres = new ArrayList<>();
        if (checkbox_hien_dai.isChecked()) selectedGenres.add("Hiện đại");
        if (checkbox_huyen_huyen.isChecked()) selectedGenres.add("Huyền huyễn");
        if (checkbox_hau_cung.isChecked()) selectedGenres.add("Hậu cung");
        if (checkbox_he_thong.isChecked()) selectedGenres.add("Hệ thống");
        if (checkbox_kinh_di.isChecked()) selectedGenres.add("Kinh dị");
        if (checkbox_lich_su.isChecked()) selectedGenres.add("Lịch sử");
        if (checkbox_mat_the.isChecked()) selectedGenres.add("Mạt thế");
        if (checkbox_ngon_tinh.isChecked()) selectedGenres.add("Ngôn tình");
        if (checkbox_thanh_xuan.isChecked()) selectedGenres.add("Thanh xuân");
        if (checkbox_truong_hoc.isChecked()) selectedGenres.add("Trường học");
        if (checkbox_trung_sinh.isChecked()) selectedGenres.add("Trùng sinh");
        if (checkbox_trong_sinh.isChecked()) selectedGenres.add("Trọng sinh");
        if (checkbox_tu_tien.isChecked()) selectedGenres.add("Tu tiên");
        if (checkbox_xuyen_khong.isChecked()) selectedGenres.add("Xuyên không");
        // Thêm các thể loại khác tương tự

        if (selectedGenres.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ít nhất một thể loại", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent resultIntent = new Intent();
        resultIntent.putStringArrayListExtra("selectedGenres", selectedGenres);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
