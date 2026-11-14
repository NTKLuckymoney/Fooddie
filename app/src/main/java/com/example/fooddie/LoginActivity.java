package com.example.fooddie;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.Toast;
import android.content.SharedPreferences;

public class LoginActivity extends AppCompatActivity {
    Button btnDangNhap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ImageView imgLogo = findViewById(R.id.imgLogo);
        imgLogo.setImageResource(R.drawable.ic_logo_chef);
        Button btnDangNhap = findViewById(R.id.btnDangNhap);
        EditText edtTaiKhoan = findViewById(R.id.edtTaiKhoan);
        EditText edtMatKhau = findViewById(R.id.edtMatKhau);
        CheckBox cbCookie = findViewById(R.id.cbCookie);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SharedPreferences preferences = getSharedPreferences(
                "FooddieSession", MODE_PRIVATE);

// Kiểm tra ghi nhớ đăng nhập
        if (preferences.getBoolean("daDangNhap", false)) {
            startActivity(new Intent(
                    LoginActivity.this,
                    HomeActivity.class));
            finish();
            return;
        }
        btnDangNhap.setOnClickListener(v -> {
            String taiKhoan = edtTaiKhoan.getText()
                    .toString().trim();
            String matKhau = edtMatKhau.getText()
                    .toString();
            if (taiKhoan.isEmpty()) {
                edtTaiKhoan.setError("Nhập tài khoản");
                edtTaiKhoan.requestFocus();
                return;
            }
            if (matKhau.isEmpty()) {
                edtMatKhau.setError("Nhập mật khẩu");
                edtMatKhau.requestFocus();
                return;
            }
            if (dbHelper.kiemTraDangNhap(taiKhoan, matKhau)) {
                preferences.edit()
                        .putBoolean("daDangNhap",cbCookie.isChecked())
                        .apply();
                startActivity(new Intent(
                        LoginActivity.this,
                        HomeActivity.class));
                finish();
            } else {
                Toast.makeText(this,
                        "Sai tài khoản hoặc mật khẩu",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }
}