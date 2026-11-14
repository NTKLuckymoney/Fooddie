package com.example.fooddie;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DanhMucActivity extends AppCompatActivity {

    private EditText edtTimDanhMuc;
    private DanhMucAdapter adapter;
    private DatabaseHelper dbHelper;

    private final ArrayList<DanhMuc> danhSach =
            new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.danhmuc);
        dbHelper = new DatabaseHelper(this);
        edtTimDanhMuc = findViewById(
                R.id.edtTimDanhMuc
        );
        RecyclerView rvDanhMuc = findViewById(R.id.rvDanhMuc);

        rvDanhMuc.setLayoutManager(new LinearLayoutManager(this));


        adapter = new DanhMucAdapter(
                danhSach,
                new DanhMucAdapter.OnDanhMucClickListener() {
                    @Override
                    public void onXem(DanhMuc danhMuc) {
                        Intent intent = new Intent(DanhMucActivity.this, MonTheoDanhMucActivity.class
                        );
                        intent.putExtra("MaDanhMuc", danhMuc.getMaDanhMuc()
                        );
                        intent.putExtra("TenDanhMuc", danhMuc.getTenDanhMuc()
                        );
                        startActivity(intent);
                    }
                    @Override
                    public void onSua(DanhMuc danhMuc) {
                        hienThiDialog(danhMuc);
                    }

                    @Override
                    public void onXoa(DanhMuc danhMuc) {
                        xacNhanXoa(danhMuc);
                    }
                }
        );

        rvDanhMuc.setAdapter(adapter);

        findViewById(R.id.btnQuayLai).setOnClickListener(v -> finish());

        findViewById(R.id.btnThemDanhMuc).setOnClickListener(v -> hienThiDialog(null));
        edtTimDanhMuc.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start,
                            int count, int after) {}
                    @Override
                    public void onTextChanged(
                            CharSequence s, int start,
                            int before, int count) {
                        taiDanhSach();
                    }
                    @Override
                    public void afterTextChanged(
                            Editable s) {}
                }
        );

        taiDanhSach();
    }

    private void taiDanhSach() {
        String tuKhoa = edtTimDanhMuc.getText().toString().trim();
        ArrayList<DanhMuc> ketQua = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql =
                "SELECT d.MaDanhMuc, d.TenDanhMuc, " +
                        "COUNT(m.MaMon) AS SoLuongMon " +
                        "FROM DanhMuc d " +
                        "LEFT JOIN MonAn m ON " +
                        "(m.MaDanhMuc = d.MaDanhMuc OR " +
                        "(m.MaDanhMuc IS NULL AND " +
                        "m.DanhMuc = d.TenDanhMuc)) " +
                        "WHERE d.TenDanhMuc LIKE ? " +
                        "GROUP BY d.MaDanhMuc, d.TenDanhMuc " +
                        "ORDER BY d.MaDanhMuc DESC";

        try (Cursor cursor = db.rawQuery(
                sql,
                new String[]{"%" + tuKhoa + "%"}
        )) {
            while (cursor.moveToNext()) {
                ketQua.add(new DanhMuc(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2)
                ));
            }
        }

        danhSach.clear();
        danhSach.addAll(ketQua);
        adapter.notifyDataSetChanged();
    }

    // 2. Hộp thoại thêm / sửa danh mục
    private void hienThiDialog(DanhMuc danhMuc) {
        boolean laSua = danhMuc != null;

        EditText edtTen = new EditText(this);
        edtTen.setSingleLine(true);
        edtTen.setHint("Nhập tên danh mục");

        if (laSua) {
            edtTen.setText(danhMuc.getTenDanhMuc());
            edtTen.setSelection(edtTen.length());
        }

        android.widget.LinearLayout khung =
                new android.widget.LinearLayout(this);

        khung.setOrientation(
                android.widget.LinearLayout.VERTICAL
        );

        int padding = (int) (
                20 * getResources()
                        .getDisplayMetrics().density
        );

        khung.setPadding(padding, 8, padding, 0);
        khung.addView(edtTen);

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle(
                                laSua
                                        ? "Sửa danh mục"
                                        : "Thêm danh mục"
                        )
                        .setView(khung)
                        .setNegativeButton("Hủy", null)
                        .setPositiveButton("Lưu", null)
                        .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(
                    AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener(v -> {

                String tenMoi = edtTen.getText()
                        .toString().trim();

                if (tenMoi.isEmpty()) {
                    edtTen.setError(
                            "Vui lòng nhập tên danh mục"
                    );
                    return;
                }

                boolean thanhCong;

                if (laSua) {
                    thanhCong = suaDanhMuc(
                            danhMuc, tenMoi
                    );
                } else {
                    thanhCong = themDanhMuc(tenMoi);
                }

                if (thanhCong) {
                    dialog.dismiss();
                    taiDanhSach();
                }
            });
        });

        dialog.show();
    }
    private boolean themDanhMuc(String ten) {
        SQLiteDatabase db =
                dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("TenDanhMuc", ten);

        try {
            long ketQua = db.insertOrThrow(
                    "DanhMuc", null, values
            );

            if (ketQua != -1) {
                thongBao("Thêm danh mục thành công");
                return true;
            }
        } catch (SQLiteConstraintException e) {
            thongBao("Tên danh mục đã tồn tại");
            return false;
        } catch (Exception e) {
            thongBao("Không thể thêm danh mục");
            return false;
        }

        return false;
    }

    private boolean suaDanhMuc(
            DanhMuc danhMuc, String tenMoi) {

        String tenCu = danhMuc.getTenDanhMuc();

        if (tenCu.equals(tenMoi)) {
            return true;
        }

        SQLiteDatabase db =
                dbHelper.getWritableDatabase();

        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put("TenDanhMuc", tenMoi);

            int soDong = db.update(
                    "DanhMuc",
                    values,
                    "MaDanhMuc = ?",
                    new String[]{
                            String.valueOf(
                                    danhMuc.getMaDanhMuc()
                            )
                    }
            );

            if (soDong == 0) {
                thongBao("Không tìm thấy danh mục");
                return false;
            }

            ContentValues monValues =
                    new ContentValues();

            monValues.put("DanhMuc", tenMoi);

            db.update(
                    "MonAn",
                    monValues,
                    "MaDanhMuc = ? OR " +
                            "(MaDanhMuc IS NULL AND DanhMuc = ?)",
                    new String[]{
                            String.valueOf(
                                    danhMuc.getMaDanhMuc()
                            ),
                            tenCu
                    }
            );

            db.setTransactionSuccessful();

            thongBao("Sửa danh mục thành công");
            return true;

        } catch (SQLiteConstraintException e) {
            thongBao("Tên danh mục đã tồn tại");
            return false;

        } catch (Exception e) {
            thongBao("Không thể sửa danh mục");
            return false;

        } finally {
            db.endTransaction();
        }
    }

    private void xacNhanXoa(DanhMuc danhMuc) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa danh mục")
                .setMessage("Bạn có chắc muốn xóa danh mục \"" + danhMuc.getTenDanhMuc() + "\"?")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xóa", (dialog, which) -> xoaDanhMuc(danhMuc))
                .show();
    }
    private void xoaDanhMuc(DanhMuc danhMuc) {
        SQLiteDatabase db =
                dbHelper.getWritableDatabase();
        int ma = danhMuc.getMaDanhMuc();
        try {
            int soMon = 0;

            try (Cursor cursor = db.rawQuery(
                    "SELECT COUNT(*) FROM MonAn " +
                            "WHERE MaDanhMuc = ? OR " +
                            "(MaDanhMuc IS NULL AND DanhMuc = ?)",
                    new String[]{
                            String.valueOf(ma),
                            danhMuc.getTenDanhMuc()
                    }
            )) {
                if (cursor.moveToFirst()) {
                    soMon = cursor.getInt(0);
                }
            }
            if (soMon > 0) {
                thongBao("Danh mục đang có " + soMon + " món ăn, không thể xóa");
                return;
            }

            int soDong = db.delete("DanhMuc", "MaDanhMuc = ?",
                    new String[]{String.valueOf(ma)}
            );

            if (soDong > 0) {
                thongBao("Xóa danh mục thành công");
                taiDanhSach();
            } else {
                thongBao("Không tìm thấy danh mục");
            }

        } catch (SQLiteConstraintException e) {
            thongBao(
                    "Danh mục đang được sử dụng"
            );
        } catch (Exception e) {
            thongBao("Không thể xóa danh mục");
        }
    }
    private void thongBao(String noiDung) {
        Toast.makeText(
                this, noiDung, Toast.LENGTH_SHORT
        ).show();
    }
    @Override
    protected void onResume() {
        super.onResume();

        if (adapter != null) {
            taiDanhSach();
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
