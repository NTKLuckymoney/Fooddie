
package com.example.fooddie;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DanhMucActivity extends AppCompatActivity {

    private final ArrayList<String> danhSach = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.danhmuc);

        danhSach.add("Món nước");
        danhSach.add("Món cơm");
        danhSach.add("Đồ uống");
        danhSach.add("Món ăn vặt");

        RecyclerView rvDanhMuc = findViewById(R.id.rvDanhMuc);
        rvDanhMuc.setLayoutManager(new LinearLayoutManager(this));
        DanhMucAdapter adapter = new DanhMucAdapter(
                danhSach,
                new DanhMucAdapter.OnDanhMucClickListener() {
                    @Override
                    public void onSua(int position) {
                        hienThiDialog(danhSach.get(position));
                    }
                    @Override
                    public void onXoa(int position) {
                        new AlertDialog.Builder(DanhMucActivity.this)
                                .setTitle("Xóa danh mục")
                                .setMessage("Bạn có muốn xóa danh mục "
                                        + danhSach.get(position) + "?")
                                .setNegativeButton("Hủy", null)
                                .setPositiveButton("Xóa", (d, w) ->
                                        Toast.makeText(
                                                DanhMucActivity.this,
                                                "Chưa kết nối SQLite",
                                                Toast.LENGTH_SHORT
                                        ).show())
                                .show();
                    }
                }
        );
        rvDanhMuc.setAdapter(adapter);

        findViewById(R.id.btnQuayLai)
                .setOnClickListener(v -> finish());

        findViewById(R.id.btnThemDanhMuc)
                .setOnClickListener(v -> hienThiDialog(""));
    }
    private void hienThiDialog(String tenCu) {
        EditText edtTen = new EditText(this);
        edtTen.setSingleLine(true);
        edtTen.setHint("Nhập tên danh mục");
        edtTen.setText(tenCu);
        edtTen.setPadding(32, 16, 32, 16);

        boolean laSua = !tenCu.isEmpty();

        new AlertDialog.Builder(this)
                .setTitle(laSua ? "Sửa danh mục" : "Thêm danh mục")
                .setView(edtTen)
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Lưu", (dialog, which) ->
                        Toast.makeText(
                                this,
                                "Chưa kết nối SQLite",
                                Toast.LENGTH_SHORT
                        ).show())
                .show();
    }
}
