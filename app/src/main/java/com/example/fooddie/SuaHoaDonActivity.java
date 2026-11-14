
package com.example.fooddie;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SuaHoaDonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_hoa_don);

        TextView txtTieuDe = findViewById(R.id.txtTieuDe);
        TextView txtMaHoaDon = findViewById(R.id.txtMaHoaDon);
        TextView txtTongTien = findViewById(R.id.txtTongTien);
        TextView btnLuu = findViewById(R.id.btnLuuHoaDon);
        EditText edtNgayLap = findViewById(R.id.edtNgayLap);
        EditText edtGhiChu = findViewById(R.id.edtGhiChu);

        txtTieuDe.setText("Sửa hóa đơn");
        btnLuu.setText("Lưu thay đổi");

        // Dữ liệu minh họa, chưa đọc từ SQLite
        txtMaHoaDon.setText("Mã hóa đơn: HD001");
        edtNgayLap.setText("14/11/2026");
        edtGhiChu.setText("Không hành");
        txtTongTien.setText("130.000đ");

        findViewById(R.id.btnQuayLai)
                .setOnClickListener(v -> finish());

        // Sẽ bổ sung chọn ngày, danh sách món và lưu sau.
    }
}
