
package com.example.fooddie;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class ChiTietHoaDonActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private LinearLayout layoutChiTietMon;
    private TextView txtMaHoaDon;
    private TextView txtNgayLap;
    private TextView txtTongTien;
    private TextView txtGhiChu;
    private int maHoaDon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chi_tiet_hoa_don);
        dbHelper = new DatabaseHelper(this);
        txtMaHoaDon = findViewById(R.id.txtMaHoaDon);
        txtNgayLap = findViewById(R.id.txtNgayLap);
        txtTongTien = findViewById(R.id.txtTongTien);
        txtGhiChu = findViewById(R.id.txtGhiChu);
        layoutChiTietMon = findViewById(R.id.layoutChiTietMon);

        maHoaDon = getIntent().getIntExtra("MaHoaDon", -1);

        findViewById(R.id.btnQuayLai).setOnClickListener(v -> finish());
        findViewById(R.id.btnDong).setOnClickListener(v -> finish());

        if (maHoaDon == -1) {
            thongBao("Không nhận được mã hóa đơn");
            finish();
            return;
        }

        taiThongTinHoaDon();
        taiChiTietMon();
    }

    private void taiThongTinHoaDon() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql =
                "SELECT MaHoaDon, NgayLap, TongTien, GhiChu " +
                        "FROM HoaDon WHERE MaHoaDon = ?";

        try (Cursor cursor = db.rawQuery(
                sql,
                new String[]{String.valueOf(maHoaDon)}
        )) {
            if (cursor.moveToFirst()) {
                int ma = cursor.getInt(0);
                String ngay = cursor.getString(1);
                double tong = cursor.getDouble(2);
                String ghiChu = cursor.getString(3);

                txtMaHoaDon.setText(String.format(
                        Locale.US,
                        "Hóa đơn: HD%03d",
                        ma
                ));

                txtNgayLap.setText(
                        "Ngày lập: " + dinhDangNgay(ngay)
                );

                txtTongTien.setText(dinhDangTien(tong));

                txtGhiChu.setText(
                        ghiChu == null || ghiChu.trim().isEmpty()
                                ? "Không có ghi chú"
                                : ghiChu
                );
            } else {
                thongBao("Không tìm thấy hóa đơn");
                finish();
            }
        }
    }

    private void taiChiTietMon() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        layoutChiTietMon.removeAllViews();

        String sql =
                "SELECT m.TenMon, ct.SoLuong, " +
                        "ct.DonGia, ct.ThanhTien " +
                        "FROM ChiTietHoaDon ct " +
                        "LEFT JOIN MonAn m ON ct.MaMon = m.MaMon " +
                        "WHERE ct.MaHoaDon = ? " +
                        "ORDER BY ct.MaMon";

        int soMon = 0;

        try (Cursor cursor = db.rawQuery(
                sql,
                new String[]{String.valueOf(maHoaDon)}
        )) {
            while (cursor.moveToNext()) {
                soMon++;

                String tenMon = cursor.getString(0);
                int soLuong = cursor.getInt(1);
                double donGia = cursor.getDouble(2);
                double thanhTien = cursor.getDouble(3);

                View item = LayoutInflater.from(this)
                        .inflate(
                                R.layout.item_mon_hoa_don,
                                layoutChiTietMon,
                                false
                        );

                TextView txtTenMon =
                        item.findViewById(R.id.txtTenMon);

                TextView txtDonGia =
                        item.findViewById(R.id.txtDonGia);

                TextView txtSoLuong =
                        item.findViewById(R.id.txtSoLuong);

                TextView txtThanhTien =
                        item.findViewById(R.id.txtThanhTien);

                txtTenMon.setText(
                        tenMon == null
                                ? "Món ăn không tồn tại"
                                : tenMon
                );

                txtDonGia.setText(dinhDangTien(donGia));
                txtSoLuong.setText(String.valueOf(soLuong));
                txtThanhTien.setText(
                        dinhDangTien(thanhTien)
                );

                // Màn hình chi tiết chỉ xem, không chỉnh sửa
                item.findViewById(R.id.btnTang)
                        .setVisibility(View.GONE);

                item.findViewById(R.id.btnGiam)
                        .setVisibility(View.GONE);

                item.findViewById(R.id.btnXoaMon)
                        .setVisibility(View.GONE);

                layoutChiTietMon.addView(item);
            }
        }

        if (soMon == 0) {
            TextView txtRong = new TextView(this);
            txtRong.setText(
                    "Hóa đơn này chưa có món ăn"
            );
            txtRong.setTextSize(15);
            txtRong.setPadding(8, 16, 8, 16);

            layoutChiTietMon.addView(txtRong);
        }
    }
    private String dinhDangNgay(String ngay) {
        if (ngay != null &&
                ngay.matches("\\d{4}-\\d{2}-\\d{2}")) {
            String[] p = ngay.split("-");
            return p[2] + "/" + p[1] + "/" + p[0];
        }
        return ngay == null ? "" : ngay;
    }
    private String dinhDangTien(double tien) {
        return String.format(
                new Locale("vi", "VN"),
                "%,.0fđ",
                tien
        );
    }
    private void thongBao(String noiDung) {
        Toast.makeText(
                this,
                noiDung,
                Toast.LENGTH_SHORT
        ).show();
    }
    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}
