package com.example.csch.fragmentadmin;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.csch.MainActivity;
import com.example.csch.R;
import com.example.csch.admin.ManageBookActivity;
import com.example.csch.admin.ManageUserActivity;
import com.example.csch.login_register.LoginActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountAdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountAdminFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AccountAdminFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountAdminFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountAdminFragment newInstance(String param1, String param2) {
        AccountAdminFragment fragment = new AccountAdminFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public static AccountAdminFragment newInstance(String username) {
        AccountAdminFragment fragment = new AccountAdminFragment();
        Bundle args = new Bundle();
        args.putString("username", username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_admin, container, false);

        // Ánh xạ các TextView
        TextView adminName = view.findViewById(R.id.txtUsernameadmin);
        TextView adminPhone = view.findViewById(R.id.txtPhoneadmin);
        Button logoutadmin = view.findViewById(R.id.logoutadmin);
        Button manageUsersButton = view.findViewById(R.id.manageUsersButton);
        Button manageBooksButton = view.findViewById(R.id.manageBooksButton);

        // Xử lý sự kiện khi người dùng nhấn nút quản lý người dùng
        manageUsersButton.setOnClickListener(v -> {
            // Chuyển hướng đến ManageUserActivity
            Intent intent = new Intent(getActivity(), ManageUserActivity.class);
            startActivity(intent);
        });

        // Xử lý sự kiện khi người dùng nhấn nút quản lý sách
        manageBooksButton.setOnClickListener(v -> {
            // Chuyển hướng đến ManageBookActivity
            Intent intent = new Intent(getActivity(), ManageBookActivity.class);
            startActivity(intent);
        });

        // Nhận dữ liệu từ Bundle
        Bundle args = getArguments();
        if (args != null) {
            String username = args.getString("username");
            adminName.setText("Admin: " + username);

            // Cập nhật thông tin khác nếu cần
            adminPhone.setText("Phone: xxxxxxxxxx"); // Ví dụ

        }



        // Đăng xuất
        logoutadmin.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Đăng xuất")
                    .setMessage("Bạn có muốn đăng xuất không?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        // Cập nhật trạng thái đăng xuất
                        SharedPreferences preferences = requireActivity().getSharedPreferences("user_session", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("is_logged_in", false); // Explicitly set this to false
                        editor.clear(); // Ensure that other login data is also cleared
                        editor.apply();

                        // Chuyển hướng đến LoginActivity
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        requireActivity().finish(); // Ensure the current activity is finished to remove it from stack
                    })
                    .setNegativeButton("Không", null)
                    .show();
        });


        return view;

    }

}