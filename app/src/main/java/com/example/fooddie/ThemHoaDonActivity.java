
package com.example.fooddie;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ThemHoaDonActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    private EditText edtNgayLap, edtGhiChu;
    private TextView txtTongTien;
    private LinearLayout layoutDanhSachMon;

    private final ArrayList<MonTrongHoaDon> danhSachMon =
            new ArrayList<>();

    private String ngaySQLite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_hoa_don);

        dbHelper = new DatabaseHelper(this);

        edtNgayLap = findViewById(R.id.edtNgayLap);
        edtGhiChu = findViewById(R.id.edtGhiChu);
        txtTongTien = findViewById(R.id.txtTongTien);
        layoutDanhSachMon = findViewById(
                R.id.layoutDanhSachMon
        );

        TextView txtTieuDe = findViewById(R.id.txtTieuDe);
        TextView txtMaHoaDon = findViewById(R.id.txtMaHoaDon);

        txtTieuDe.setText("Thêm hóa đơn");
        txtMaHoaDon.setText("Mã hóa đơn: Tự động tạo");

        findViewById(R.id.btnQuayLai)
                .setOnClickListener(v -> finish());

        findViewById(R.id.btnChonMon)
                .setOnClickListener(v -> chonMonAn());

        findViewById(R.id.btnLuuHoaDon)
                .setOnClickListener(v -> luuHoaDon());

        khoiTaoNgay();
        capNhatTongTien();
    }

    // 1. Khởi tạo ngày hiện tại
    private void khoiTaoNgay() {
        Calendar calendar = Calendar.getInstance();
        hienThiNgay(calendar);

        edtNgayLap.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    (picker, year, month, day) -> {
                        Calendar ngayChon =
                                Calendar.getInstance();

                        ngayChon.set(year, month, day);
                        hienThiNgay(ngayChon);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            dialog.show();
        });
    }

    private void hienThiNgay(Calendar calendar) {
        int ngay = calendar.get(Calendar.DAY_OF_MONTH);
        int thang = calendar.get(Calendar.MONTH) + 1;
        int nam = calendar.get(Calendar.YEAR);

        edtNgayLap.setText(String.format(
                Locale.US, "%02d/%02d/%04d",
                ngay, thang, nam
        ));

        // Định dạng dùng trong SQLite
        ngaySQLite = String.format(
                Locale.US, "%04d-%02d-%02d",
                nam, thang, ngay
        );
    }

    // 2. Lấy danh sách món đang bán từ SQLite
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
            thongBao("Chưa có món ăn đang bán");
            return;
        }

        String[] tenMon = new String[cacMon.size()];

        for (int i = 0; i < cacMon.size(); i++) {
            MonTrongHoaDon mon = cacMon.get(i);

            tenMon[i] = mon.getTenMon() +
                    " - " + dinhDangTien(mon.getDonGia());
        }

        new AlertDialog.Builder(this)
                .setTitle("Chọn món ăn")
                .setItems(tenMon, (dialog, which) ->
                        themMonVaoHoaDon(cacMon.get(which)))
                .setNegativeButton("Hủy", null)
                .show();
    }

    // 3. Thêm món, tránh trùng MaMon
    private void themMonVaoHoaDon(MonTrongHoaDon monMoi) {

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

    // 4. Hiển thị các món đã chọn
    private void hienThiDanhSachMon() {
        layoutDanhSachMon.removeAllViews();

        for (MonTrongHoaDon mon : danhSachMon) {

            View item = LayoutInflater.from(this)
                    .inflate(
                            R.layout.item_mon_hoa_don,
                            layoutDanhSachMon,
                            false
                    );

            TextView txtTen = item.findViewById(
                    R.id.txtTenMon
            );
            TextView txtDonGia = item.findViewById(
                    R.id.txtDonGia
            );
            TextView txtSoLuong = item.findViewById(
                    R.id.txtSoLuong
            );
            TextView txtThanhTien = item.findViewById(
                    R.id.txtThanhTien
            );

            ImageButton btnTang = item.findViewById(
                    R.id.btnTang
            );
            ImageButton btnGiam = item.findViewById(
                    R.id.btnGiam
            );

            txtTen.setText(mon.getTenMon());
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

    // 5. Tính tổng tiền
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

    private String dinhDangTien(double soTien) {
        return String.format(
                new Locale("vi", "VN"),
                "%,.0fđ",
                soTien
        );
    }

    // 6. Lưu hóa đơn và chi tiết hóa đơn
    private void luuHoaDon() {

        if (danhSachMon.isEmpty()) {
            thongBao("Vui lòng chọn ít nhất một món ăn");
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.beginTransaction();

        try {
            // Lưu hóa đơn chính
            ContentValues hoaDon = new ContentValues();

            hoaDon.put("NgayLap", ngaySQLite);
            hoaDon.put("TongTien", tinhTongTien());
            hoaDon.put(
                    "GhiChu",
                    edtGhiChu.getText().toString().trim()
            );

            long maHoaDon = db.insertOrThrow(
                    "HoaDon", null, hoaDon
            );
            // Lưu từng món trong hóa đơn
            for (MonTrongHoaDon mon : danhSachMon) {
                ContentValues chiTiet =
                        new ContentValues();
                chiTiet.put("MaHoaDon", maHoaDon);
                chiTiet.put("MaMon", mon.getMaMon());
                chiTiet.put("SoLuong", mon.getSoLuong());
                chiTiet.put("DonGia", mon.getDonGia());
                chiTiet.put(
                        "ThanhTien",
                        mon.getThanhTien()
                );
                db.insertOrThrow(
                        "ChiTietHoaDon",
                        null,
                        chiTiet
                );
            }
            db.setTransactionSuccessful();

            thongBao("Thêm hóa đơn thành công");
            setResult(RESULT_OK);
            finish();

        } catch (Exception e) {
            thongBao("Không thể lưu hóa đơn: " +
                    e.getMessage());

        } finally {
            db.endTransaction();
        }
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
