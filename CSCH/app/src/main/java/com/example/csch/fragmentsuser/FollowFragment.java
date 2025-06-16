package com.example.csch.fragmentsuser;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.csch.R;
import com.example.csch.notification.NotificationActivity;

import androidx.fragment.app.FragmentTransaction;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FollowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FollowFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private ImageView ivNotification;

    public FollowFragment() {
        // Required empty public constructor
    }

    public static FollowFragment newInstance(String param1, String param2) {
        FollowFragment fragment = new FollowFragment();
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



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_follow, container, false);

        // Find views
        TextView tvFollow = view.findViewById(R.id.tvFollow);
        TextView tvHistory = view.findViewById(R.id.tvHistory);
        ivNotification = view.findViewById(R.id.ivNotification);

        // Set click listener for ivNotification
        ivNotification.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), NotificationActivity.class);
            startActivity(intent);
        });

        // Initial fragment setup (show ItemFollowFragment by default)
        replaceFragment(new ItemFollowFragment());

        // Set click listeners
        tvFollow.setOnClickListener(v -> {
            // Underline Follow and remove underline from History
            tvFollow.setPaintFlags(tvFollow.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            tvHistory.setPaintFlags(tvHistory.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
            // Replace with ItemFollowFragment
            replaceFragment(new ItemFollowFragment());
        });

        tvHistory.setOnClickListener(v -> {
            // Underline History and remove underline from Follow
            tvHistory.setPaintFlags(tvHistory.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            tvFollow.setPaintFlags(tvFollow.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
            // Replace with ItemHistoryFragment
            replaceFragment(new ItemHistoryFragment());
        });

        return view;
    }

    // Helper method to replace fragments
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.FreameLayoutview, fragment); // `fragmentContainer` là id của FrameLayout
        transaction.commit();
    }

}
