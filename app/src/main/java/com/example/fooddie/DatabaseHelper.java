package com.example.fooddie;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.util.Base64;
import java.security.SecureRandom;
import java.security.MessageDigest;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.ArrayList;
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "Fooddie.db";
    private static final int DB_VERSION = 3;
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE MonAn (" +
                "MaMon INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "TenMon TEXT NOT NULL, " +
                "DanhMuc TEXT NOT NULL, " +
                "Gia REAL NOT NULL, " +
                "TrangThai INTEGER NOT NULL, " +
                "HinhAnh TEXT)";

        db.execSQL(sql);
        taoBangTaiKhoan(db);

    }
    private void taoBangTaiKhoan(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS TaiKhoan (" +
                        "MaTaiKhoan INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "TenDangNhap TEXT NOT NULL UNIQUE, " +
                        "MatKhau TEXT NOT NULL, " +
                        "VaiTro TEXT NOT NULL DEFAULT 'ADMIN')"
        );
        ContentValues values = new ContentValues();
        values.put("TenDangNhap", "admin");
        values.put("MatKhau", "123456");
        values.put("VaiTro", "ADMIN");
        db.insertWithOnConflict(
                "TaiKhoan",
                null,
                values,
                SQLiteDatabase.CONFLICT_IGNORE
        );
    }
    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            taoBangTaiKhoan(db);
        }
        if (oldVersion < 3) {
            db.execSQL("DROP TABLE IF EXISTS TaiKhoan");
            taoBangTaiKhoan(db);
        }
    }
    public boolean kiemTraDangNhap(
            String taiKhoan, String matKhau) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.rawQuery(
                "SELECT MaTaiKhoan FROM TaiKhoan " +
                        "WHERE TenDangNhap = ? AND MatKhau = ?",
                new String[]{taiKhoan, matKhau})) {

            return cursor.moveToFirst();
        }
    }
    public long themMonAn(String tenMon, String danhMuc,
                          double gia, boolean dangBan,
                          String hinhAnh) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TenMon", tenMon);
        values.put("DanhMuc", danhMuc);
        values.put("Gia", gia);
        values.put("TrangThai", dangBan ? 1 : 0);
        values.put("HinhAnh", hinhAnh);
        return db.insert("MonAn", null, values);
    }
    public ArrayList<MonAn> layDanhSachMonAn() {
        ArrayList<MonAn> danhSach = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.rawQuery(
                "SELECT * FROM MonAn ORDER BY MaMon DESC",
                null)) {

            while (cursor.moveToNext()) {
                int ma = cursor.getInt(
                        cursor.getColumnIndexOrThrow("MaMon"));
                String ten = cursor.getString(
                        cursor.getColumnIndexOrThrow("TenMon"));
                String danhMuc = cursor.getString(
                        cursor.getColumnIndexOrThrow("DanhMuc"));
                double gia = cursor.getDouble(
                        cursor.getColumnIndexOrThrow("Gia"));

                boolean dangBan = cursor.getInt(
                        cursor.getColumnIndexOrThrow("TrangThai")) == 1;
                /*
                String hinhAnh = cursor.getString(
                        cursor.getColumnIndexOrThrow("HinhAnh"));

                danhSach.add(new MonAn(
                        ma, ten, danhMuc, gia, dangBan, hinhAnh));

                 */
            }
        }
        return danhSach;
    }
}
