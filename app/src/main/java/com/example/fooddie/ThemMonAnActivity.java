package com.example.fooddie;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import android.app.AlertDialog;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

public class ThemMonAnActivity extends AppCompatActivity {
    private EditText edtTenMon, edtGia;
    private Spinner spDanhMuc;
    private Switch swTrangThai;
    private ImageView imgMonAn;

    private DatabaseHelper dbHelper;
    private final ArrayList<String> tenDanhMuc = new ArrayList<>();
    private final ArrayList<Integer> maDanhMuc = new ArrayList<>();

    private String duongDanAnh = null;

    private void chonAnhDrawable() {
        String[] tenMon = {"Phở bò", "Cơm rang", "Bún chả","Bún thang","Chè bốn mùa","Bún đậu","Cháo","Sữa chua việt quất","Gà ủ muối","Kem dừa","Thanh Long lướt sóng","Salad","Cháo Dinh Dưỡng"};
        String[] tenAnh = {"img_phobo", "img_comrang", "img_buncha","img_bunthang","img_bundau","img_chao","img_suachuavq","img_gaumuoi","img_rauluoc","img_salad","img_chaodinhduong"};
        int[] drawableIds = {
                R.drawable.img_phobo,
                R.drawable.img_comrang,
                R.drawable.img_buncha,
                R.drawable.img_bunthang,
                R.drawable.img_chebonmua,
                R.drawable.img_bundau,
                R.drawable.img_chao,
                R.drawable.img_suachuavq,
                R.drawable.img_gaumuoi,
                R.drawable.img_kemdua,
                R.drawable.img_rauluoc,
                R.drawable.img_salad,
                R.drawable.img_chaodinhduong
        };
        new AlertDialog.Builder(this)
                .setTitle("Chọn ảnh món ăn")
                .setItems(tenMon, (dialog, which) -> {
                    duongDanAnh = tenAnh[which];
                    imgMonAn.setImageResource(drawableIds[which]);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.them_mon_an);

        dbHelper = new DatabaseHelper(this);

        edtTenMon = findViewById(R.id.edtTenMon);
        edtGia = findViewById(R.id.edtGia);
        spDanhMuc = findViewById(R.id.spDanhMuc);
        swTrangThai = findViewById(R.id.swTrangThai);
        imgMonAn = findViewById(R.id.imgMonAn);

        taiDanhMuc();
        findViewById(R.id.btnChonAnh)
                .setOnClickListener(v -> chonAnhDrawable());

        findViewById(R.id.btnLuuMon)
                .setOnClickListener(v -> luuMonAn());

        findViewById(R.id.btnQuayLai)
                .setOnClickListener(v -> finish());
    }

    private void taiDanhMuc() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        tenDanhMuc.clear();
        maDanhMuc.clear();

        try (Cursor cursor = db.rawQuery(
                "SELECT MaDanhMuc, TenDanhMuc " +
                        "FROM DanhMuc ORDER BY TenDanhMuc", null
        )) {
            while (cursor.moveToNext()) {
                maDanhMuc.add(cursor.getInt(0));
                tenDanhMuc.add(cursor.getString(1));
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                tenDanhMuc
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spDanhMuc.setAdapter(adapter);
    }

    private String saoChepAnh(Uri uri) throws Exception {
        File thuMuc = new File(getFilesDir(), "images");

        if (!thuMuc.exists() && !thuMuc.mkdirs()) {
            throw new Exception("Không tạo được thư mục ảnh");
        }

        File file = new File(
                thuMuc, UUID.randomUUID() + ".jpg"
        );

        try (InputStream input = getContentResolver()
                .openInputStream(uri);
             FileOutputStream output = new FileOutputStream(file)) {

            if (input == null) {
                throw new Exception("Không đọc được ảnh");
            }

            byte[] buffer = new byte[8192];
            int length;

            while ((length = input.read(buffer)) != -1) {
                output.write(buffer, 0, length);
            }
        }

        return file.getAbsolutePath();
    }

    private void luuMonAn() {
        String ten = edtTenMon.getText().toString().trim();
        String giaNhap = edtGia.getText().toString().trim();

        if (ten.isEmpty()) {
            edtTenMon.setError("Vui lòng nhập tên món");
            edtTenMon.requestFocus();
            return;
        }

        if (maDanhMuc.isEmpty()) {
            Toast.makeText(this,
                    "Chưa có danh mục món ăn",
                    Toast.LENGTH_SHORT).show();
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

        int viTri = spDanhMuc.getSelectedItemPosition();

        ContentValues values = new ContentValues();
        values.put("TenMon", ten);
        values.put("DanhMuc", tenDanhMuc.get(viTri));
        values.put("MaDanhMuc", maDanhMuc.get(viTri));
        values.put("Gia", gia);
        values.put("TrangThai",
                swTrangThai.isChecked() ? 1 : 0);
        values.put("HinhAnh", duongDanAnh);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long ketQua = db.insert(
                "MonAn", null, values
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
    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
