package com.example.fooddie;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class SuaHoaDonActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private EditText edtNgayLap, edtGhiChu;
    private TextView txtTongTien;
    private LinearLayout layoutDanhSachMon;

    private final ArrayList<MonTrongHoaDon> danhSachMon =
            new ArrayList<>();
    private int maHoaDon;
    private String ngaySQLite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_hoa_don);

        dbHelper = new DatabaseHelper(this);

        maHoaDon = getIntent().getIntExtra("MaHoaDon", -1);

        if (maHoaDon == -1) {
            thongBao("Không nhận được mã hóa đơn");
            finish();
            return;
        }

        edtNgayLap = findViewById(R.id.edtNgayLap);
        edtGhiChu = findViewById(R.id.edtGhiChu);
        txtTongTien = findViewById(R.id.txtTongTien);

        layoutDanhSachMon =
                findViewById(R.id.layoutDanhSachMon);

        TextView txtTieuDe = findViewById(R.id.txtTieuDe);
        TextView txtMaHoaDon = findViewById(R.id.txtMaHoaDon);

        Button btnLuu = findViewById(R.id.btnLuuHoaDon);

        txtTieuDe.setText("Sửa hóa đơn");

        txtMaHoaDon.setText(String.format(
                Locale.US, "Mã hóa đơn: HD%03d", maHoaDon
        ));

        btnLuu.setText("Lưu thay đổi");

        findViewById(R.id.btnQuayLai)
                .setOnClickListener(v -> finish());

        findViewById(R.id.btnChonMon)
                .setOnClickListener(v -> chonMonAn());

        btnLuu.setOnClickListener(v -> luuThayDoi());

        edtNgayLap.setOnClickListener(v -> chonNgay());
        taiThongTinHoaDon();
        taiDanhSachMonCu();
    }
    private void taiThongTinHoaDon() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT NgayLap, GhiChu FROM HoaDon " +
                        "WHERE MaHoaDon = ?",
                new String[]{String.valueOf(maHoaDon)}
        )) {
            if (cursor.moveToFirst()) {
                ngaySQLite = cursor.getString(0);

                String ghiChu = cursor.getString(1);
                edtGhiChu.setText(
                        ghiChu == null ? "" : ghiChu
                );

                edtNgayLap.setText(
                        dinhDangNgay(ngaySQLite)
                );
            } else {
                thongBao("Không tìm thấy hóa đơn");
                finish();
            }
        }
    }

    // 2. Đọc danh sách món cũ trong hóa đơn
    private void taiDanhSachMonCu() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        danhSachMon.clear();

        String sql =
                "SELECT ct.MaMon, m.TenMon, " +
                        "ct.DonGia, ct.SoLuong " +
                        "FROM ChiTietHoaDon ct " +
                        "LEFT JOIN MonAn m ON ct.MaMon = m.MaMon " +
                        "WHERE ct.MaHoaDon = ?";

        try (Cursor cursor = db.rawQuery(
                sql,
                new String[]{String.valueOf(maHoaDon)}
        )) {
            while (cursor.moveToNext()) {
                danhSachMon.add(new MonTrongHoaDon(
                        cursor.getInt(0),
                        cursor.isNull(1)
                                ? "Món không tồn tại"
                                : cursor.getString(1),
                        cursor.getDouble(2),
                        cursor.getInt(3)
                ));
            }
        }

        hienThiDanhSachMon();
    }

    // 3. Chọn thêm món đang bán
    private void chonMonAn() {
        ArrayList<MonTrongHoaDon> cacMon =
                new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT MaMon, TenMon, Gia " +
                        "FROM MonAn WHERE TrangThai = 1 " +
                        "ORDER BY TenMon",
                null
        )) {
            while (cursor.moveToNext()) {
                cacMon.add(new MonTrongHoaDon(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getDouble(2),
                        1
                ));
            }
        }

        if (cacMon.isEmpty()) {
            thongBao("Không có món ăn đang bán");
            return;
        }

        String[] tenMon = new String[cacMon.size()];

        for (int i = 0; i < cacMon.size(); i++) {
            MonTrongHoaDon mon = cacMon.get(i);

            tenMon[i] = mon.getTenMon() + " - " +
                    dinhDangTien(mon.getDonGia());
        }

        new AlertDialog.Builder(this)
                .setTitle("Chọn món ăn")
                .setItems(tenMon, (dialog, which) ->
                        themMon(cacMon.get(which)))
                .setNegativeButton("Hủy", null)
                .show();
    }

    // 4. Thêm món mới hoặc tăng số lượng món đã có
    private void themMon(MonTrongHoaDon monMoi) {
        for (MonTrongHoaDon mon : danhSachMon) {
            if (mon.getMaMon() == monMoi.getMaMon()) {
                mon.setSoLuong(mon.getSoLuong() + 1);
                hienThiDanhSachMon();
                return;
            }
        }

        danhSachMon.add(monMoi);
        hienThiDanhSachMon();
    }

    // 5. Hiển thị và gắn sự kiện cho TẤT CẢ món
    private void hienThiDanhSachMon() {
        layoutDanhSachMon.removeAllViews();

        for (MonTrongHoaDon mon : danhSachMon) {

            View item = LayoutInflater.from(this)
                    .inflate(
                            R.layout.item_mon_hoa_don,
                            layoutDanhSachMon,
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

            Button btnTang = item.findViewById(R.id.btnTang);
            Button btnGiam = item.findViewById(R.id.btnGiam);

            txtTenMon.setText(mon.getTenMon());

            txtDonGia.setText(
                    dinhDangTien(mon.getDonGia())
            );

            txtSoLuong.setText(
                    String.valueOf(mon.getSoLuong())
            );

            txtThanhTien.setText(
                    dinhDangTien(mon.getThanhTien())
            );

            btnTang.setOnClickListener(v -> {
                mon.setSoLuong(mon.getSoLuong() + 1);
                hienThiDanhSachMon();
            });

            btnGiam.setOnClickListener(v -> {
                if (mon.getSoLuong() > 1) {
                    mon.setSoLuong(mon.getSoLuong() - 1);
                    hienThiDanhSachMon();
                }
            });

            item.findViewById(R.id.btnXoaMon)
                    .setOnClickListener(v -> {
                        danhSachMon.remove(mon);
                        hienThiDanhSachMon();
                    });

            layoutDanhSachMon.addView(item);
        }

        capNhatTongTien();
    }

    // 6. Tính lại tổng tiền
    private double tinhTongTien() {
        double tong = 0;

        for (MonTrongHoaDon mon : danhSachMon) {
            tong += mon.getThanhTien();
        }

        return tong;
    }

    private void capNhatTongTien() {
        txtTongTien.setText(
                dinhDangTien(tinhTongTien())
        );
    }

    // 7. Chọn ngày lập hóa đơn
    private void chonNgay() {
        Calendar calendar = Calendar.getInstance();

        if (ngaySQLite != null &&
                ngaySQLite.matches("\\d{4}-\\d{2}-\\d{2}")) {
            String[] p = ngaySQLite.split("-");
            calendar.set(
                    Integer.parseInt(p[0]),
                    Integer.parseInt(p[1]) - 1,
                    Integer.parseInt(p[2])
            );
        }

        new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    ngaySQLite = String.format(
                            Locale.US,
                            "%04d-%02d-%02d",
                            year, month + 1, day
                    );

                    edtNgayLap.setText(
                            dinhDangNgay(ngaySQLite)
                    );
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    // 8. Lưu hóa đơn đã sửa
    private void luuThayDoi() {
        if (danhSachMon.isEmpty()) {
            thongBao("Hóa đơn phải có ít nhất một món");
            return;
        }

        if (ngaySQLite == null ||
                ngaySQLite.trim().isEmpty()) {
            thongBao("Vui lòng chọn ngày lập");
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        boolean thanhCong = false;

        db.beginTransaction();

        try {
            ContentValues hoaDon = new ContentValues();

            hoaDon.put("NgayLap", ngaySQLite);
            hoaDon.put("TongTien", tinhTongTien());
            hoaDon.put(
                    "GhiChu",
                    edtGhiChu.getText().toString().trim()
            );

            int soDong = db.update(
                    "HoaDon",
                    hoaDon,
                    "MaHoaDon = ?",
                    new String[]{String.valueOf(maHoaDon)}
            );

            if (soDong != 1) {
                throw new IllegalStateException(
                        "Không tìm thấy hóa đơn"
                );
            }
            db.delete(
                    "ChiTietHoaDon",
                    "MaHoaDon = ?",
                    new String[]{String.valueOf(maHoaDon)}
            );
            for (MonTrongHoaDon mon : danhSachMon) {

                ContentValues chiTiet = new ContentValues();

                chiTiet.put("MaHoaDon", maHoaDon);
                chiTiet.put("MaMon", mon.getMaMon());
                chiTiet.put("SoLuong", mon.getSoLuong());
                chiTiet.put("DonGia", mon.getDonGia());
                chiTiet.put(
                        "ThanhTien",
                        mon.getThanhTien()
                );
                db.insertOrThrow(
                        "ChiTietHoaDon", null, chiTiet
                );
            }

            db.setTransactionSuccessful();
            thanhCong = true;
        } catch (Exception e) {
            thongBao("Lỗi lưu hóa đơn: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
        if (thanhCong) {
            thongBao("Cập nhật hóa đơn thành công");
            setResult(RESULT_OK);
            finish();
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
