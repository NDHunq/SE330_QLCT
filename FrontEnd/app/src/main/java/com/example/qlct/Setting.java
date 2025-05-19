package com.example.qlct;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Setting extends AppCompatActivity {
    TextView phone;
    LinearLayout changepass;
    ImageButton backsetting;

    // Mock data class to replace UserProfile
    private static class MockUserProfile {
        private static class Data {
            String phone_number;
            Data(String phone_number) {
                this.phone_number = phone_number;
            }
            String getPhone_number() {
                return phone_number;
            }
        }
        Data data;
        MockUserProfile(String phone_number) {
            this.data = new Data(phone_number);
        }
        Data getData() {
            return data;
        }
    }

    // Mock user profile data
    private MockUserProfile getMockUserProfile() {
        return new MockUserProfile("+84 123 456 789");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        MockUserProfile userProfile = getMockUserProfile();

        phone = findViewById(R.id.phone);

        backsetting = findViewById(R.id.backsetting);

        String phoneNumber = userProfile.getData().getPhone_number();
        phoneNumber = "0" + phoneNumber.substring(3);
        phone.setText(phoneNumber);

        backsetting.setOnClickListener(v -> finish());

        changepass.setOnClickListener(v -> {
            Intent myintent = new Intent(Setting.this, NewPassword.class);
            startActivity(myintent);
        });
    }
}