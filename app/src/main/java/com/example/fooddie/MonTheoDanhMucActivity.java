package com.example.fooddie;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
public class MonTheoDanhMucActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private MonAnAdapter adapter;
    private final ArrayList<MonAn> danhSach = new ArrayList<>();

    private int maDanhMuc;
    private String tenDanhMuc;
    private TextView txtThongBao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mon_theo_danh_muc);

        maDanhMuc = getIntent().getIntExtra(
                "MaDanhMuc", -1
        );

        tenDanhMuc = getIntent().getStringExtra(
                "TenDanhMuc"
        );

        if (maDanhMuc == -1) {
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);

        TextView txtTen = findViewById(R.id.txtTenDanhMuc);
        txtTen.setText(tenDanhMuc);

        txtThongBao = findViewById(R.id.txtThongBao);

        RecyclerView recyclerView =
                findViewById(R.id.rvMonTheoDanhMuc);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new MonAnAdapter(
                danhSach,
                new MonAnAdapter.OnMonAnActionListener() {

                    @Override
                    public void onSua(MonAn mon) {
                        Intent intent = new Intent(
                                MonTheoDanhMucActivity.this,
                                SuaMonAnActivity.class
                        );

                        intent.putExtra(
                                "MaMon", mon.getMaMon()
                        );

                        startActivity(intent);
                    }

                    @Override
                    public void onXoa(MonAn mon) {
                        xacNhanXoa(mon);
                    }
                }
        );

        recyclerView.setAdapter(adapter);

        findViewById(R.id.btnQuayLai)
                .setOnClickListener(v -> finish());

        taiDanhSachMon();
    }

    private void taiDanhSachMon() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        danhSach.clear();

        String sql =
                "SELECT MaMon, TenMon, DanhMuc, Gia, " +
                        "TrangThai, HinhAnh " +
                        "FROM MonAn " +
                        "WHERE MaDanhMuc = ? OR " +
                        "(MaDanhMuc IS NULL AND DanhMuc = ?) " +
                        "ORDER BY MaMon DESC";

        try (Cursor cursor = db.rawQuery(
                sql,
                new String[]{
                        String.valueOf(maDanhMuc),
                        tenDanhMuc
                }
        )) {
            while (cursor.moveToNext()) {
                danhSach.add(new MonAn(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getDouble(3),
                        cursor.getInt(4) == 1,
                        cursor.getString(5)
                ));
            }
        }

        adapter.notifyDataSetChanged();

        txtThongBao.setVisibility(
                danhSach.isEmpty()
                        ? View.VISIBLE
                        : View.GONE
        );
    }

    private void xacNhanXoa(MonAn mon) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa món ăn")
                .setMessage(
                        "Bạn muốn xóa món " +
                                mon.getTenMon() + "?"
                )
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xóa", (dialog, which) ->
                        xoaMon(mon.getMaMon()))
                .show();
    }

    private void xoaMon(int maMon) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            int ketQua = db.delete(
                    "MonAn",
                    "MaMon = ?",
                    new String[]{String.valueOf(maMon)}
            );

            if (ketQua > 0) {
                Toast.makeText(
                        this,
                        "Xóa món thành công",
                        Toast.LENGTH_SHORT
                ).show();

                taiDanhSachMon();
            }
        } catch (SQLiteConstraintException e) {
            Toast.makeText(
                    this,
                    "Món đã có trong hóa đơn, không thể xóa",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (adapter != null) {
            taiDanhSachMon();
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
