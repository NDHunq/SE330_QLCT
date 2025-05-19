package com.example.qlct.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.qlct.Home.Home_My_wallets;
import com.example.qlct.Login_Signin;
import com.example.qlct.My_categories;

import com.example.qlct.Notification.Notification_activity;
import com.example.qlct.R;
import com.example.qlct.Setting;

// Mock data class to simulate UserProfile
class MockUserProfile {
    private MockUserData data;

    public MockUserProfile() {
        this.data = new MockUserData();
    }

    public MockUserData getData() {
        return data;
    }
}

class MockUserData {
    private String username;
    private String phone_number;

    public MockUserData() {
        this.username = "John Doe";
        this.phone_number = "+84912345678";
    }

    public String getUsername() {
        return username;
    }

    public String getPhone_number() {
        return phone_number;
    }
}

public class Account_fragment extends Fragment {

    LinearLayout mywallet;
    LinearLayout logout;
    LinearLayout mycate;
    LinearLayout setting;
    TextView phone;
    TextView name;
    ImageButton bell;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_fragment, container, false);

        // Initialize views
        mywallet = view.findViewById(R.id.mywallet);
        mycate = view.findViewById(R.id.mycates);
        setting = view.findViewById(R.id.setting);
        phone = view.findViewById(R.id.phone);
        name = view.findViewById(R.id.name);
        logout = view.findViewById(R.id.logout);
        bell = view.findViewById(R.id.bell);

        // Set mock user data
        MockUserProfile userProfile = new MockUserProfile();
        String phoneNumber = userProfile.getData().getPhone_number();

        // Format phone number (remove country code and ensure starts with 0)
        phoneNumber = "0" + phoneNumber.substring(3);

        // Set user data to views
        name.setText(userProfile.getData().getUsername());
        phone.setText(phoneNumber);

        // Set click listeners
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Clear any saved preferences (mock implementation)
                    Intent myintent = new Intent(getActivity(), Login_Signin.class);
                    startActivity(myintent);
                } catch (Exception e) {
                    Log.d("ErrorLogOut", "Error: ", e);
                }
            }
        });

        mywallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(getActivity(), Home_My_wallets.class);
                myintent.putExtra("spec", "spec");
                startActivity(myintent);
            }
        });

        mycate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(getActivity(), My_categories.class);
                startActivity(myintent);
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(getActivity(), Setting.class);
                startActivity(myintent);
            }
        });

        bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Notification_activity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}