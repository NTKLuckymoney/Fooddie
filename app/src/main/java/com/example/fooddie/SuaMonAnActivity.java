package com.example.fooddie;

import android.app.Activity;
import android.content.ContentValues;
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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;
public class SuaMonAnActivity extends AppCompatActivity {
    private EditText edtTen, edtGia;
    private Spinner spDanhMuc;
    private Switch swTrangThai;
    private ImageView imgMonAn;
    private DatabaseHelper dbHelper;
    private int maMon;
    private String duongDanAnh;
    private final ArrayList<String> tenDanhMuc = new ArrayList<>();
    private final ArrayList<Integer> maDanhMuc = new ArrayList<>();


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
        setContentView(R.layout.sua_mon_an);
        maMon = getIntent().getIntExtra("MaMon", -1);
        if (maMon == -1) {
            Toast.makeText(this,
                    "Không xác định được món ăn",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        dbHelper = new DatabaseHelper(this);
        edtTen = findViewById(R.id.edtTenMonSua);
        edtGia = findViewById(R.id.edtGiaSua);
        spDanhMuc = findViewById(R.id.spDanhMucSua);
        swTrangThai = findViewById(R.id.swTrangThaiSua);
        imgMonAn = findViewById(R.id.imgMonAnSua);
        taiDanhMuc();
        taiThongTinMon();
        findViewById(R.id.btnQuayLai)
                .setOnClickListener(v -> finish());

        findViewById(R.id.btnHuy)
                .setOnClickListener(v -> finish());

        findViewById(R.id.btnDoiAnh)
                .setOnClickListener(v -> chonAnhDrawable());

        findViewById(R.id.btnLuuThayDoi)
                .setOnClickListener(v -> capNhatMonAn());
    }
    private void taiDanhMuc() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.rawQuery(
                "SELECT MaDanhMuc, TenDanhMuc " +
                        "FROM DanhMuc ORDER BY TenDanhMuc",
                null
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

    private void taiThongTinMon() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT TenMon, DanhMuc, MaDanhMuc, " +
                        "Gia, TrangThai, HinhAnh " +
                        "FROM MonAn WHERE MaMon = ?",
                new String[]{String.valueOf(maMon)}
        )) {
            if (!cursor.moveToFirst()) {
                Toast.makeText(this,
                        "Không tìm thấy món ăn",
                        Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            edtTen.setText(cursor.getString(0));
            edtGia.setText(String.valueOf(cursor.getDouble(3)));
            swTrangThai.setChecked(cursor.getInt(4) == 1);

            duongDanAnh = cursor.getString(5);
            MonAnAdapter.hienThiAnh(imgMonAn, duongDanAnh);

            int maDanhMucHienTai = cursor.isNull(2)
                    ? -1 : cursor.getInt(2);

            int viTri = maDanhMuc.indexOf(maDanhMucHienTai);

            if (viTri == -1) {
                viTri = tenDanhMuc.indexOf(cursor.getString(1));
            }

            if (viTri >= 0) {
                spDanhMuc.setSelection(viTri);
            }
        }
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

    private void capNhatMonAn() {
        String ten = edtTen.getText().toString().trim();
        String giaNhap = edtGia.getText().toString().trim();
        if (ten.isEmpty()) {
            edtTen.setError("Vui lòng nhập tên món");
            return;
        }
        if (maDanhMuc.isEmpty()) {
            Toast.makeText(this,
                    "Chưa có danh mục",
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
        int soDong = db.update(
                "MonAn",
                values,
                "MaMon = ?",
                new String[]{String.valueOf(maMon)}
        );

        if (soDong > 0) {
            Toast.makeText(this,
                    "Cập nhật món ăn thành công",
                    Toast.LENGTH_SHORT).show();

            setResult(Activity.RESULT_OK);
            finish();
        } else {
            Toast.makeText(this,
                    "Không thể cập nhật món ăn",
                    Toast.LENGTH_SHORT).show();
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
