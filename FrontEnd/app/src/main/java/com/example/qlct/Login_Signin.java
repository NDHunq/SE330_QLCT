package com.example.qlct;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
// import android.widget.ImageButton; // Không cần nếu không dùng showpass
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
// import com.google.android.material.textfield.TextInputLayout; // Không cần nếu không validate phức tạp

public class Login_Signin extends AppCompatActivity {

    Button signin_button; // Đổi tên để rõ ràng hơn
    EditText enterpass_edittext;
    TextInputEditText username_edittext;
    MaterialButton login_button;
    TextView forgetpass_textview;

    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_signin); // Đảm bảo layout này khớp với các ID dưới đây
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ View
        signin_button = findViewById(R.id.signin); // Giả sử ID của nút "Sign In" là 'signin'
        enterpass_edittext = findViewById(R.id.enterpass);
        username_edittext = findViewById(R.id.username); // Đảm bảo ID này đúng trong XML
        login_button = findViewById(R.id.Login); // Giả sử ID của nút "Log In" là 'Login'
        forgetpass_textview = findViewById(R.id.forgetpass);

        if (enterpass_edittext != null) {
            enterpass_edittext.setTransformationMethod(new PasswordTransformationMethod());
        }

        if (forgetpass_textview != null) {
            forgetpass_textview.setOnClickListener(v -> {
                // Chuyển đến màn hình quên mật khẩu (nếu có)
                // Intent myintent = new Intent(Login_Signin.this, ForgotPasswordActivity.class);
                // startActivity(myintent);
                Toast.makeText(Login_Signin.this, "Forgot Password Clicked (Demo)", Toast.LENGTH_SHORT).show();
            });
        }

        if (signin_button != null) {
            signin_button.setOnClickListener(v -> {
                // Chuyển đến màn hình tạo tài khoản (nếu có)
                 Intent myintent = new Intent(Login_Signin.this, Create_Account.class);
                 startActivity(myintent);
                Toast.makeText(Login_Signin.this, "Sign In Clicked (Demo)", Toast.LENGTH_SHORT).show();
            });
        }

        if (login_button != null) {
            login_button.setOnClickListener(v -> {
                String enteredUsername = "";
                if (username_edittext != null && username_edittext.getText() != null) {
                    enteredUsername = username_edittext.getText().toString().trim();
                }

                String enteredPassword = "";
                if (enterpass_edittext != null && enterpass_edittext.getText() != null) {
                    enteredPassword = enterpass_edittext.getText().toString().trim();
                }

                if (enteredUsername.equals("1") && enteredPassword.equals("1")) {
                    Toast.makeText(Login_Signin.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                    Intent mainActivityIntent = new Intent(Login_Signin.this, MainActivity.class);
                    startActivity(mainActivityIntent);
                    finish(); // Đóng màn hình đăng nhập sau khi thành công
                } else {
                    Toast.makeText(Login_Signin.this, "Invalid username or password.", Toast.LENGTH_SHORT).show();
                    // Xóa lỗi trên các field (nếu bạn có TextInputLayout)
                    // TextInputLayout usernameLayout = findViewById(R.id.username_layout);
                    // TextInputLayout passwordLayout = findViewById(R.id.enterpass_layout);
                    // if (usernameLayout != null) usernameLayout.setError("Invalid username or password");
                    // if (passwordLayout != null) passwordLayout.setError("Invalid username or password");
                }
            });
        }
    }
}