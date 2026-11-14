package com.example.fooddie;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FoodActivity extends AppCompatActivity {

    private RecyclerView rvMonAn;
    private EditText edtTimKiem;
    private MonAnAdapter adapter;
    private ArrayList<MonAn> danhSachMonAn;
    private DatabaseHelper dbHelper;
    private final ActivityResultLauncher<Intent> monAnLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            taiDanhSachMonAn();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food);

        dbHelper = new DatabaseHelper(this);

        rvMonAn = findViewById(R.id.rvMonAn);
        edtTimKiem = findViewById(R.id.edtTimKiem);

        danhSachMonAn = new ArrayList<>();

        rvMonAn.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new MonAnAdapter(danhSachMonAn, new MonAnAdapter.OnMonAnActionListener() {
                    @Override
                    public void onSua(MonAn mon) {
                        Intent intent = new Intent(
                                FoodActivity.this,
                                SuaMonAnActivity.class
                        );

                        intent.putExtra("MaMon", mon.getMaMon());
                        monAnLauncher.launch(intent);
                    }
                    @Override
                    public void onXoa(MonAn mon) {
                        xacNhanXoa(mon);
                    }
                }
        );
        rvMonAn.setAdapter(adapter);

        findViewById(R.id.btnQuayLai).setOnClickListener(v -> finish());
        findViewById(R.id.btnThemMon).setOnClickListener(v -> {
                    Intent intent = new Intent(this, ThemMonAnActivity.class);
                    monAnLauncher.launch(intent);
                });

        findViewById(R.id.btnTimKiem).setOnClickListener(v -> taiDanhSachMonAn());
        taiDanhSachMonAn();
    }
    // SELECT danh sách và tìm kiếm
    private void taiDanhSachMonAn() {
        String tuKhoa = edtTimKiem.getText().toString().trim();
        ArrayList<MonAn> ketQua = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql =
                "SELECT MaMon, TenMon, DanhMuc, Gia, " +
                        "TrangThai, HinhAnh FROM MonAn " +
                        "WHERE TenMon LIKE ? OR DanhMuc LIKE ? " +
                        "ORDER BY MaMon DESC";
        String tim = "%" + tuKhoa + "%";
        try (Cursor cursor = db.rawQuery(
                sql, new String[]{tim, tim}
        )) {
            while (cursor.moveToNext()) {
                int ma = cursor.getInt(0);
                String ten = cursor.getString(1);
                String danhMuc = cursor.getString(2);
                double gia = cursor.getDouble(3);
                boolean dangBan = cursor.getInt(4) == 1;
                String hinhAnh = cursor.getString(5);
                ketQua.add(new MonAn(
                        ma, ten, danhMuc,
                        gia, dangBan, hinhAnh
                ));
            }
        }
        danhSachMonAn.clear();
        danhSachMonAn.addAll(ketQua);
        adapter.notifyDataSetChanged();
    }
    // Xác nhận xóa
    private void xacNhanXoa(MonAn mon) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa món ăn")
                .setMessage("Bạn có chắc muốn xóa món \"" + mon.getTenMon() + "\"?")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xóa", (dialog, which) ->
                        xoaMonAn(mon.getMaMon()))
                .show();
    }

    // DELETE món ăn
    private void xoaMonAn(int maMon) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            int soDong = db.delete("MonAn", "MaMon = ?", new String[]{String.valueOf(maMon)});
            if (soDong > 0) {
                Toast.makeText(this, "Đã xóa món ăn", Toast.LENGTH_SHORT)
                        .show();
                taiDanhSachMonAn();
            } else {
                Toast.makeText(this, "Không tìm thấy món ăn", Toast.LENGTH_SHORT)
                        .show();
            }
        } catch (android.database.sqlite.SQLiteConstraintException e) {
            Toast.makeText(this, "Món đã có trong hóa đơn, không thể xóa", Toast.LENGTH_LONG
            ).show();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            taiDanhSachMonAn();
        }
    }
    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
