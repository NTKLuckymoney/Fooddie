package com.example.fooddie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText edtTaiKhoan, edtMatKhau;
    private CheckBox cbCookie;
    private Button btnDangNhap;
    private DatabaseHelper dbHelper;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        edtTaiKhoan = findViewById(R.id.edtTaiKhoan);
        edtMatKhau = findViewById(R.id.edtMatKhau);
        cbCookie = findViewById(R.id.cbCookie);
        btnDangNhap = findViewById(R.id.btnDangNhap);
        ImageView imgLogo = findViewById(R.id.imgLogo);
        imgLogo.setImageResource(R.drawable.ic_logo_chef);

        dbHelper = new DatabaseHelper(this);

        preferences = getSharedPreferences("FooddieSession", MODE_PRIVATE);

        if (preferences.getBoolean("daDangNhap", false)) {
            moTrangChu();
            return;
        }

        // Xử lý nút đăng nhập
        btnDangNhap.setOnClickListener(v -> dangNhap());
    }

    private void dangNhap() {
        String taiKhoan = edtTaiKhoan.getText()
                .toString().trim();
        String matKhau = edtMatKhau.getText()
                .toString();
        if (taiKhoan.isEmpty()) {
            edtTaiKhoan.setError("Vui lòng nhập tài khoản");
            edtTaiKhoan.requestFocus();
            return;
        }

        if (matKhau.isEmpty()) {
            edtMatKhau.setError("Vui lòng nhập mật khẩu");
            edtMatKhau.requestFocus();
            return;
        }

        if (kiemTraDangNhap(taiKhoan, matKhau)) {

            // Chỉ ghi nhớ khi checkbox được chọn
            preferences.edit()
                    .putBoolean("daDangNhap", cbCookie.isChecked())
                    .apply();
            Toast.makeText(
                    this,
                    "Đăng nhập thành công",
                    Toast.LENGTH_SHORT
            ).show();
            moTrangChu();
        } else {
            Toast.makeText(
                    this,
                    "Sai tài khoản hoặc mật khẩu",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private boolean kiemTraDangNhap(String taiKhoan, String matKhau){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql =
                "SELECT MaTaiKhoan FROM TaiKhoan " +
                        "WHERE TenDangNhap = ? AND MatKhau = ? " +
                        "LIMIT 1";

        try (Cursor cursor = db.rawQuery(
                sql,
                new String[]{taiKhoan, matKhau}
        )) {
            return cursor.moveToFirst();
        }
    }
    private void moTrangChu() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}
