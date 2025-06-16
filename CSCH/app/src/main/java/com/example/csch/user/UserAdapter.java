package com.example.csch.user;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.csch.R;
import com.example.csch.admin.EditUserActivity;
import com.example.csch.chitiettruyen.DetailActivity;
import com.example.csch.database.DBHelper;

import java.util.ArrayList;

public class UserAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<User> users;
    private LayoutInflater inflater;

    public UserAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_user_manage, parent, false);
        }

        ImageView imgAvatar = convertView.findViewById(R.id.img_user_avatar);
        TextView tvName = convertView.findViewById(R.id.tv_user_name);
        TextView tvPhone = convertView.findViewById(R.id.tv_user_phone);
        TextView tvDate = convertView.findViewById(R.id.tv_registration_date);
        Button btn_edituser = convertView.findViewById(R.id.btn_edituser);
        Button btn_deleteuser = convertView.findViewById(R.id.btn_deleteuser);

        User user = users.get(position);

        // Thiết lập thông tin người dùng
        tvName.setText(user.getUsername());
        tvPhone.setText(user.getPhoneNumber());
        tvDate.setText(user.getDateCreated());

        // Giải mã Base64 và hiển thị hình ảnh
        String avatarBase64 = user.getAvatar();
        if (avatarBase64 != null && !avatarBase64.isEmpty()) {
            Bitmap avatarBitmap = base64ToBitmap(avatarBase64);
            imgAvatar.setImageBitmap(avatarBitmap);
        } else {
            imgAvatar.setImageResource(R.drawable.ic_profile); // Hình ảnh mặc định
        }
/*
        // Tự động gửi Intent tới DetailActivity với user_id dưới dạng String
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("USER_ID", String.valueOf(user.getId())); // Chuyển user_id thành String
        context.startActivity(intent);


*/
        // Xử lý click Edit
        btn_edituser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditUserActivity.class);

                // Truyền dữ liệu người dùng qua Intent
                intent.putExtra("user_id", user.getId());
                intent.putExtra("username", user.getUsername());
                intent.putExtra("password", user.getPassword());
                intent.putExtra("phone", user.getPhoneNumber());
                intent.putExtra("avatar", user.getAvatar()); // Truyền avatar dưới dạng Base64

                context.startActivity(intent);
            }
        });

        // Xử lý click Delete
        btn_deleteuser.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xóa người dùng")
                    .setMessage("Bạn có chắc chắn muốn xóa người dùng này không?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        DBHelper dbHelper = new DBHelper(context);
                        dbHelper.deleteUser(user.getId()); // Gọi hàm xóa trong DBHelper
                        users.remove(position); // Xóa khỏi danh sách
                        notifyDataSetChanged(); // Cập nhật ListView
                        Toast.makeText(context, "Đã xóa người dùng", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        return convertView;
    }


    private Bitmap base64ToBitmap(String base64Str) {
        byte[] decodedBytes = android.util.Base64.decode(base64Str, android.util.Base64.DEFAULT);
        return android.graphics.BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }


}
