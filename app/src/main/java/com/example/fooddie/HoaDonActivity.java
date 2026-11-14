package com.example.fooddie;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class HoaDonActivity extends AppCompatActivity {

    private EditText edtTimHoaDon;
    private RecyclerView rvHoaDon;

    private DatabaseHelper dbHelper;
    private HoaDonAdapter adapter;

    private final ArrayList<HoaDon> danhSachHoaDon =
            new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hoadon);

        // 1. Khởi tạo database
        dbHelper = new DatabaseHelper(this);

        // 2. Ánh xạ giao diện
        edtTimHoaDon = findViewById(R.id.edtTimHoaDon);
        rvHoaDon = findViewById(R.id.rvHoaDon);

        // 3. Thiết lập RecyclerView
        rvHoaDon.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new HoaDonAdapter(
                danhSachHoaDon,
                new HoaDonAdapter.OnHoaDonActionListener() {

                    @Override
                    public void onChiTiet(HoaDon hoaDon) {
                        Intent intent = new Intent(
                                HoaDonActivity.this,
                                ChiTietHoaDonActivity.class
                        );

                        intent.putExtra(
                                "MaHoaDon",
                                hoaDon.getMaHoaDon()
                        );

                        startActivity(intent);
                    }

                    @Override
                    public void onSua(HoaDon hoaDon) {
                        Intent intent = new Intent(
                                HoaDonActivity.this,
                                SuaHoaDonActivity.class
                        );

                        intent.putExtra(
                                "MaHoaDon",
                                hoaDon.getMaHoaDon()
                        );

                        startActivity(intent);
                    }

                    @Override
                    public void onXoa(HoaDon hoaDon) {
                        xacNhanXoa(hoaDon);
                    }
                }
        );

        rvHoaDon.setAdapter(adapter);

        // 4. Nút quay lại
        findViewById(R.id.btnQuayLai)
                .setOnClickListener(v -> finish());

        // 5. Nút thêm hóa đơn
        findViewById(R.id.btnThemHoaDon)
                .setOnClickListener(v -> {
                    Intent intent = new Intent(
                            HoaDonActivity.this,
                            ThemHoaDonActivity.class
                    );

                    startActivity(intent);
                });

        // 6. Nút tìm kiếm
        findViewById(R.id.btnSearchHoaDon)
                .setOnClickListener(v ->
                        taiDanhSachHoaDon());

        // 7. Tìm kiếm ngay khi nhập
        edtTimHoaDon.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {
                        taiDanhSachHoaDon();
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );

        // 8. Tải danh sách ban đầu
        taiDanhSachHoaDon();
    }

    // SELECT hóa đơn từ SQLite
    private void taiDanhSachHoaDon() {

        String tuKhoa = edtTimHoaDon.getText()
                .toString()
                .trim();

        // Cho phép nhập HD001 hoặc 1
        if (tuKhoa.toUpperCase(Locale.ROOT)
                .startsWith("HD")) {
            tuKhoa = tuKhoa.substring(2);
        }

        ArrayList<HoaDon> ketQua = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql =
                "SELECT h.MaHoaDon, h.NgayLap, " +
                        "h.TongTien, h.GhiChu, " +
                        "COUNT(ct.MaMon) AS SoMon " +
                        "FROM HoaDon h " +
                        "LEFT JOIN ChiTietHoaDon ct " +
                        "ON h.MaHoaDon = ct.MaHoaDon " +
                        "WHERE CAST(h.MaHoaDon AS TEXT) LIKE ? " +
                        "GROUP BY h.MaHoaDon, h.NgayLap, " +
                        "h.TongTien, h.GhiChu " +
                        "ORDER BY h.MaHoaDon DESC";

        try (Cursor cursor = db.rawQuery(
                sql,
                new String[]{"%" + tuKhoa + "%"}
        )) {

            while (cursor.moveToNext()) {

                int maHoaDon = cursor.getInt(0);
                String ngayLap = cursor.getString(1);
                double tongTien = cursor.getDouble(2);
                String ghiChu = cursor.getString(3);
                int soMon = cursor.getInt(4);

                HoaDon hoaDon = new HoaDon(
                        maHoaDon,
                        ngayLap,
                        tongTien,
                        ghiChu,
                        soMon
                );

                ketQua.add(hoaDon);
            }

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Lỗi tải hóa đơn: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }

        danhSachHoaDon.clear();
        danhSachHoaDon.addAll(ketQua);

        adapter.notifyDataSetChanged();
    }
    private void xacNhanXoa(HoaDon hoaDon) {

        String maHienThi = String.format(
                Locale.US,
                "HD%03d",
                hoaDon.getMaHoaDon()
        );

        new AlertDialog.Builder(this)
                .setTitle("Xóa hóa đơn")
                .setMessage(
                        "Bạn có chắc muốn xóa hóa đơn " +
                                maHienThi + "?"
                )
                .setNegativeButton("Hủy", null)
                .setPositiveButton(
                        "Xóa",
                        (dialog, which) ->
                                xoaHoaDon(
                                        hoaDon.getMaHoaDon()
                                )
                )
                .show();
    }

    // DELETE hóa đơn và chi tiết hóa đơn
    private void xoaHoaDon(int maHoaDon) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.beginTransaction();

        try {
            String[] dieuKien = {
                    String.valueOf(maHoaDon)
            };

            // Xóa chi tiết trước
            db.delete(
                    "ChiTietHoaDon",
                    "MaHoaDon = ?",
                    dieuKien
            );

            // Xóa hóa đơn chính
            int soDong = db.delete(
                    "HoaDon",
                    "MaHoaDon = ?",
                    dieuKien
            );
            if (soDong > 0) {
                db.setTransactionSuccessful();
            }
            if (soDong > 0) {
                Toast.makeText(this, "Xóa hóa đơn thành công", Toast.LENGTH_SHORT
                ).show();
            } else {
                Toast.makeText(this, "Không tìm thấy hóa đơn", Toast.LENGTH_SHORT
                ).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi xóa hóa đơn: " + e.getMessage(), Toast.LENGTH_LONG
            ).show();
        } finally {
            db.endTransaction();
        }
        taiDanhSachHoaDon();
    }
    @Override
    protected void onResume() {
        super.onResume();

        if (adapter != null) {
            taiDanhSachHoaDon();
        }
    }
    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}
