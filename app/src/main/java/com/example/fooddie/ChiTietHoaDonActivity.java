
package com.example.fooddie;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ChiTietHoaDonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chi_tiet_hoa_don);

        TextView txtMaHoaDon = findViewById(R.id.txtMaHoaDon);
        TextView txtNgayLap = findViewById(R.id.txtNgayLap);
        TextView txtTongTien = findViewById(R.id.txtTongTien);
        TextView txtGhiChu = findViewById(R.id.txtGhiChu);

        // Dữ liệu minh họa
        txtMaHoaDon.setText("Hóa đơn: HD001");
        txtNgayLap.setText("Ngày lập: 14/11/2026");
        txtTongTien.setText("130.000đ");
        txtGhiChu.setText("Không hành");

        findViewById(R.id.btnQuayLai)
                .setOnClickListener(v -> finish());

        findViewById(R.id.btnDong)
                .setOnClickListener(v -> finish());
    }
}
