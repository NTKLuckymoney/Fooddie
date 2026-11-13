
package com.example.fooddie;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ThemMonAnActivity extends AppCompatActivity {

    private EditText edtTenMon, edtGia;
    private Spinner spDanhMuc;
    private Switch swTrangThai;
    private Button btnLuuMon;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.them_mon_an);

        edtTenMon = findViewById(R.id.edtTenMon);
        edtGia = findViewById(R.id.edtGia);
        spDanhMuc = findViewById(R.id.spDanhMuc);
        swTrangThai = findViewById(R.id.swTrangThai);
        btnLuuMon = findViewById(R.id.btnLuuMon);

        dbHelper = new DatabaseHelper(this);

        // Danh mục tạm thời, sau này lấy từ bảng DanhMuc
        String[] danhMuc = {
                "Món nước",
                "Món cơm",
                "Đồ uống",
                "Món ăn vặt"
        };

        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        danhMuc
                );

        spinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spDanhMuc.setAdapter(spinnerAdapter);

        btnLuuMon.setOnClickListener(v -> luuMonAn());

        findViewById(R.id.btnQuayLai)
                .setOnClickListener(v -> finish());

        // Chức năng chọn ảnh sẽ bổ sung ở bước tiếp theo.
    }
    private void luuMonAn() {
        String tenMon = edtTenMon.getText().toString().trim();
        String giaNhap = edtGia.getText().toString().trim();
        String danhMuc = spDanhMuc.getSelectedItem().toString();
        boolean dangBan = swTrangThai.isChecked();

        if (tenMon.isEmpty()) {
            edtTenMon.setError("Vui lòng nhập tên món ăn");
            edtTenMon.requestFocus();
            return;
        }
        if (giaNhap.isEmpty()) {
            edtGia.setError("Vui lòng nhập giá");
            edtGia.requestFocus();
            return;
        }
        double gia;
        try {
            gia = Double.parseDouble(giaNhap);
        } catch (NumberFormatException e) {
            edtGia.setError("Giá không hợp lệ");
            return;
        }
        if (!Double.isFinite(gia) || gia < 0) {
            edtGia.setError("Giá phải lớn hơn hoặc bằng 0");
            return;
        }
        long ketQua = dbHelper.themMonAn(
                tenMon,
                danhMuc,
                gia,
                dangBan,
                null
        );
        if (ketQua != -1) {
            Toast.makeText(this,
                    "Thêm món ăn thành công",
                    Toast.LENGTH_SHORT).show();

            setResult(Activity.RESULT_OK);
            finish();
        } else {
            Toast.makeText(this,
                    "Không thể thêm món ăn",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
