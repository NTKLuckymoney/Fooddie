package com.example.fooddie;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SuaMonAnActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sua_mon_an);
        EditText edtTen = findViewById(R.id.edtTenMonSua);
        EditText edtGia = findViewById(R.id.edtGiaSua);
        Spinner spDanhMuc = findViewById(R.id.spDanhMucSua);
        Switch swTrangThai = findViewById(R.id.swTrangThaiSua);
        String[] danhMuc = {
                "Món nước", "Món cơm",
                "Đồ uống", "Món ăn vặt"
        };
        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        danhMuc
                );

        spinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        spDanhMuc.setAdapter(spinnerAdapter);

        // Dữ liệu minh họa, chưa kết nối SQLite
        edtTen.setText("Phở bò");
        edtGia.setText("45000");
        spDanhMuc.setSelection(0);
        swTrangThai.setChecked(true);
        findViewById(R.id.btnQuayLai)
                .setOnClickListener(v -> finish());
        findViewById(R.id.btnHuy)
                .setOnClickListener(v -> finish());
        findViewById(R.id.btnDoiAnh)
                .setOnClickListener(v -> Toast.makeText(this, "Chưa triển khai chọn ảnh", Toast.LENGTH_SHORT).show());
        findViewById(R.id.btnLuuThayDoi)
                .setOnClickListener(v -> Toast.makeText(this, "Chưa triển khai lưu dữ liệu", Toast.LENGTH_SHORT).show());
    }
}