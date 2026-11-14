
package com.example.fooddie;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class HoaDonActivity extends AppCompatActivity {

    private ImageButton btnQuayLai;
    private Button btnThemHoaDon;
    private EditText edtTimHoaDon;
    private RecyclerView rvHoaDon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hoadon);
        // Ánh xạ các thành phần giao diện
        btnQuayLai = findViewById(R.id.btnQuayLai);
        btnThemHoaDon = findViewById(R.id.btnThemHoaDon);
        edtTimHoaDon = findViewById(R.id.edtTimHoaDon);
        rvHoaDon = findViewById(R.id.rvHoaDon);

        // Quay về trang chủ
        btnQuayLai.setOnClickListener(v -> finish());
        btnThemHoaDon.setOnClickListener(v -> {
            Intent intent =  new Intent(HoaDonActivity.this, ThemHoaDonActivity.class);
            startActivity(intent);
        });

    }
}
