
package com.example.fooddie;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ThongKeActivity extends AppCompatActivity {

    private Spinner spnLoaiThongKe;
    private EditText edtNgayThongKe;
    private TextView txtTongDoanhThu, txtSoHoaDon;
    private FrameLayout layoutBieuDo;
    private LinearLayout layoutChiTietThongKe;
    private DatabaseHelper dbHelper;
    private final Calendar ngayDaChon = Calendar.getInstance();
    private final Locale localeVN = new Locale("vi", "VN");
    private final int mauCam = Color.rgb(255, 120, 84);

    private static class DuLieu {
        String nhan;
        double doanhThu;
        int soHoaDon;

        DuLieu(String nhan, double doanhThu, int soHoaDon) {
            this.nhan = nhan;
            this.doanhThu = doanhThu;
            this.soHoaDon = soHoaDon;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thongke);

        dbHelper = new DatabaseHelper(this);

        spnLoaiThongKe = findViewById(R.id.spnLoaiThongKe);
        edtNgayThongKe = findViewById(R.id.edtNgayThongKe);
        txtTongDoanhThu = findViewById(R.id.txtTongDoanhThu);
        txtSoHoaDon = findViewById(R.id.txtSoHoaDon);
        layoutBieuDo = findViewById(R.id.layoutBieuDo);
        layoutChiTietThongKe =
                findViewById(R.id.layoutChiTietThongKe);

        String[] loaiThongKe = {
                "Theo ngày", "Theo tháng", "Theo năm"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                loaiThongKe
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spnLoaiThongKe.setAdapter(adapter);

        spnLoaiThongKe.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {
                        capNhatNgayHienThi();
                        xemThongKe();
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {
                    }
                }
        );

        findViewById(R.id.btnQuayLai)
                .setOnClickListener(v -> finish());

        edtNgayThongKe.setOnClickListener(
                v -> chonThoiGian()
        );

        findViewById(R.id.btnXemThongKe)
                .setOnClickListener(v -> xemThongKe());

        capNhatNgayHienThi();
    }

    // Chọn thời gian phù hợp với loại thống kê
    private void chonThoiGian() {
        int loai = spnLoaiThongKe.getSelectedItemPosition();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (picker, year, month, day) -> {
                    ngayDaChon.set(year, month, day);
                    capNhatNgayHienThi();
                    xemThongKe();
                },
                ngayDaChon.get(Calendar.YEAR),
                ngayDaChon.get(Calendar.MONTH),
                ngayDaChon.get(Calendar.DAY_OF_MONTH)
        );

        if (loai == 1) {
            // Theo tháng: chỉ hiện tháng và năm
            int idNgay = getResources().getIdentifier(
                    "day", "id", "android"
            );

            View ngay = dialog.getDatePicker()
                    .findViewById(idNgay);

            if (ngay != null) {
                ngay.setVisibility(View.GONE);
            }
        } else if (loai == 2) {
            // Theo năm: chỉ hiện năm
            int idNgay = getResources().getIdentifier(
                    "day", "id", "android"
            );
            int idThang = getResources().getIdentifier(
                    "month", "id", "android"
            );

            View ngay = dialog.getDatePicker()
                    .findViewById(idNgay);

            View thang = dialog.getDatePicker()
                    .findViewById(idThang);

            if (ngay != null) {
                ngay.setVisibility(View.GONE);
            }

            if (thang != null) {
                thang.setVisibility(View.GONE);
            }
        }

        dialog.show();
    }

    private void capNhatNgayHienThi() {
        int ngay = ngayDaChon.get(Calendar.DAY_OF_MONTH);
        int thang = ngayDaChon.get(Calendar.MONTH) + 1;
        int nam = ngayDaChon.get(Calendar.YEAR);

        int loai = spnLoaiThongKe.getSelectedItemPosition();

        if (loai == 0) {
            edtNgayThongKe.setText(String.format(
                    Locale.US, "%02d/%02d/%04d",
                    ngay, thang, nam
            ));
        } else if (loai == 1) {
            edtNgayThongKe.setText(String.format(
                    Locale.US, "%02d/%04d", thang, nam
            ));
        } else {
            edtNgayThongKe.setText(
                    String.valueOf(nam)
            );
        }
    }

    // Truy vấn doanh thu theo ngày / tháng / năm
    private void xemThongKe() {
        int loai = spnLoaiThongKe.getSelectedItemPosition();

        int ngay = ngayDaChon.get(Calendar.DAY_OF_MONTH);
        int thang = ngayDaChon.get(Calendar.MONTH) + 1;
        int nam = ngayDaChon.get(Calendar.YEAR);

        String dieuKien;
        String giaTri;
        String nhom;
        String tenCot;

        if (loai == 0) {
            // Thống kê ngày: nhóm theo hóa đơn
            dieuKien = "NgayLap = ?";
            giaTri = String.format(
                    Locale.US,
                    "%04d-%02d-%02d",
                    nam, thang, ngay
            );

            nhom = "MaHoaDon";
            tenCot = "MaHoaDon";

        } else if (loai == 1) {
            // Thống kê tháng: nhóm theo ngày
            dieuKien = "substr(NgayLap, 1, 7) = ?";
            giaTri = String.format(
                    Locale.US, "%04d-%02d", nam, thang
            );

            nhom = "NgayLap";
            tenCot = "NgayLap";

        } else {
            // Thống kê năm: nhóm theo tháng
            dieuKien = "substr(NgayLap, 1, 4) = ?";
            giaTri = String.valueOf(nam);

            nhom = "substr(NgayLap, 1, 7)";
            tenCot = "substr(NgayLap, 1, 7)";
        }

        String sql =
                "SELECT " + tenCot + " AS Nhom, " +
                        "COUNT(*) AS SoHoaDon, " +
                        "COALESCE(SUM(TongTien), 0) AS DoanhThu " +
                        "FROM HoaDon " +
                        "WHERE " + dieuKien + " " +
                        "GROUP BY " + nhom + " " +
                        "ORDER BY Nhom ASC";

        ArrayList<DuLieu> danhSach = new ArrayList<>();

        double tongDoanhThu = 0;
        int tongHoaDon = 0;

        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            try (Cursor cursor = db.rawQuery(
                    sql, new String[]{giaTri}
            )) {
                while (cursor.moveToNext()) {
                    String nhomGiaTri = cursor.getString(0);
                    int soHoaDon = cursor.getInt(1);
                    double doanhThu = cursor.getDouble(2);

                    String nhan;

                    if (loai == 0) {
                        nhan = String.format(
                                Locale.US,
                                "HD%03d",
                                Integer.parseInt(nhomGiaTri)
                        );
                    } else if (loai == 1) {
                        // yyyy-MM-dd -> dd/MM
                        String[] p = nhomGiaTri.split("-");
                        nhan = p[2] + "/" + p[1];
                    } else {
                        // yyyy-MM -> MM/yyyy
                        String[] p = nhomGiaTri.split("-");
                        nhan = p[1] + "/" + p[0];
                    }

                    danhSach.add(new DuLieu(
                            nhan, doanhThu, soHoaDon
                    ));

                    tongDoanhThu += doanhThu;
                    tongHoaDon += soHoaDon;
                }
            }

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Lỗi thống kê: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        txtTongDoanhThu.setText(
                dinhDangTien(tongDoanhThu)
        );

        txtSoHoaDon.setText(
                String.valueOf(tongHoaDon)
        );

        hienThiChiTiet(danhSach);
        veBieuDo(danhSach);
    }

    // Hiển thị danh sách chi tiết
    private void hienThiChiTiet(ArrayList<DuLieu> danhSach) {
        layoutChiTietThongKe.removeAllViews();

        if (danhSach.isEmpty()) {
            TextView rong = taoText(
                    "Không có hóa đơn trong thời gian này",
                    14, Color.GRAY
            );
            layoutChiTietThongKe.addView(rong);
            return;
        }

        for (DuLieu duLieu : danhSach) {
            LinearLayout dong = new LinearLayout(this);
            dong.setOrientation(LinearLayout.VERTICAL);
            dong.setPadding(dp(12), dp(10), dp(12), dp(10));

            LinearLayout.LayoutParams dongParams =
                    new LinearLayout.LayoutParams(
                            -1, -2
                    );
            dongParams.bottomMargin = dp(8);
            dong.setLayoutParams(dongParams);

            GradientDrawable nen = new GradientDrawable();
            nen.setColor(Color.rgb(250, 250, 250));
            nen.setCornerRadius(dp(8));
            dong.setBackground(nen);

            TextView ten = taoText(
                    duLieu.nhan, 15, Color.DKGRAY
            );
            ten.setTypeface(null, Typeface.BOLD);

            TextView thongTin = taoText(
                    duLieu.soHoaDon + " hóa đơn • " +
                            dinhDangTien(duLieu.doanhThu),
                    14, mauCam
            );

            dong.addView(ten);
            dong.addView(thongTin);

            layoutChiTietThongKe.addView(dong);
        }
    }

    // Biểu đồ cột ngang, không cần thư viện ngoài
    private void veBieuDo(ArrayList<DuLieu> danhSach) {
        layoutBieuDo.removeAllViews();

        if (danhSach.isEmpty()) {
            TextView rong = taoText(
                    "Chưa có dữ liệu để vẽ biểu đồ",
                    14, Color.GRAY
            );
            rong.setGravity(Gravity.CENTER);
            layoutBieuDo.addView(rong);
            return;
        }

        double lonNhat = 0;

        for (DuLieu item : danhSach) {
            lonNhat = Math.max(lonNhat, item.doanhThu);
        }

        ScrollView scroll = new ScrollView(this);

        LinearLayout cot = new LinearLayout(this);
        cot.setOrientation(LinearLayout.VERTICAL);
        cot.setPadding(dp(10), dp(8), dp(10), dp(8));

        for (DuLieu item : danhSach) {
            TextView nhan = taoText(
                    item.nhan + " - " +
                            dinhDangTien(item.doanhThu),
                    12, Color.DKGRAY
            );

            LinearLayout.LayoutParams nhanParams =
                    new LinearLayout.LayoutParams(-1, -2);
            nhanParams.topMargin = dp(8);
            nhan.setLayoutParams(nhanParams);

            cot.addView(nhan);

            FrameLayout thanhNen = new FrameLayout(this);

            LinearLayout.LayoutParams thanhParams =
                    new LinearLayout.LayoutParams(
                            -1, dp(16)
                    );
            thanhParams.topMargin = dp(4);
            thanhNen.setLayoutParams(thanhParams);

            GradientDrawable nen = new GradientDrawable();
            nen.setColor(Color.rgb(238, 238, 238));
            nen.setCornerRadius(dp(6));
            thanhNen.setBackground(nen);

            View thanh = new View(this);

            GradientDrawable mau = new GradientDrawable();
            mau.setColor(mauCam);
            mau.setCornerRadius(dp(6));
            thanh.setBackground(mau);

            float tiLe = lonNhat == 0
                    ? 0
                    : (float) (item.doanhThu / lonNhat);

            FrameLayout.LayoutParams p =
                    new FrameLayout.LayoutParams(
                            -1, dp(16)
                    );

            thanh.setLayoutParams(p);

            // Đặt chiều rộng theo tỷ lệ sau khi đo
            thanhNen.addView(thanh);

            thanhNen.post(() -> {
                int rong = thanhNen.getWidth();
                FrameLayout.LayoutParams params =
                        (FrameLayout.LayoutParams)
                                thanh.getLayoutParams();

                params.width = Math.max(
                        0, Math.round(rong * tiLe)
                );

                thanh.setLayoutParams(params);
            });

            cot.addView(thanhNen);
        }

        scroll.addView(cot);

        layoutBieuDo.addView(
                scroll,
                new FrameLayout.LayoutParams(-1, -1)
        );
    }

    private TextView taoText(
            String noiDung, int kichThuoc, int mau) {
        TextView txt = new TextView(this);
        txt.setText(noiDung);
        txt.setTextSize(kichThuoc);
        txt.setTextColor(mau);
        return txt;
    }

    private String dinhDangTien(double tien) {
        return String.format(
                localeVN, "%,.0fđ", tien
        );
    }
    private int dp(int value) {
        return Math.round(
                value * getResources()
                        .getDisplayMetrics().density
        );
    }
    @Override
    protected void onResume() {
        super.onResume();

        if (dbHelper != null && spnLoaiThongKe != null) {
            xemThongKe();
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
