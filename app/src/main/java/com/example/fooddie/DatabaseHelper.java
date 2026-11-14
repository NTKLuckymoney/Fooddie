package com.example.fooddie;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "Fooddie.db";
    private static final int DB_VERSION = 4;
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        taoBangTaiKhoan(db);
        taoBangDanhMuc(db);
        taoBangMonAn(db);
        taoBangHoaDon(db);
        taoBangChiTietHoaDon(db);
        themDuLieuMau(db);
    }
    private void taoBangTaiKhoan(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS TaiKhoan (" +
                        "MaTaiKhoan INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "TenDangNhap TEXT NOT NULL UNIQUE, " +
                        "MatKhau TEXT NOT NULL, " +
                        "VaiTro TEXT NOT NULL DEFAULT 'ADMIN')"
        );
    }
    private void taoBangDanhMuc(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS DanhMuc (" +
                        "MaDanhMuc INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "TenDanhMuc TEXT NOT NULL UNIQUE)"
        );
    }
    private void taoBangMonAn(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS MonAn (" +
                        "MaMon INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "TenMon TEXT NOT NULL, " +
                        "DanhMuc TEXT NOT NULL, " +
                        "MaDanhMuc INTEGER, " +
                        "Gia REAL NOT NULL CHECK(Gia >= 0), " +
                        "TrangThai INTEGER NOT NULL DEFAULT 1, " +
                        "HinhAnh TEXT, " +
                        "FOREIGN KEY(MaDanhMuc) " +
                        "REFERENCES DanhMuc(MaDanhMuc))"
        );
    }

    private void taoBangHoaDon(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS HoaDon (" +
                        "MaHoaDon INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "NgayLap TEXT NOT NULL, " +
                        "TongTien REAL NOT NULL DEFAULT 0, " +
                        "GhiChu TEXT)"
        );
    }

    private void taoBangChiTietHoaDon(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS ChiTietHoaDon (" +
                        "MaHoaDon INTEGER NOT NULL, " +
                        "MaMon INTEGER NOT NULL, " +
                        "SoLuong INTEGER NOT NULL CHECK(SoLuong > 0), " +
                        "DonGia REAL NOT NULL CHECK(DonGia >= 0), " +
                        "ThanhTien REAL NOT NULL, " +
                        "PRIMARY KEY(MaHoaDon, MaMon), " +
                        "FOREIGN KEY(MaHoaDon) " +
                        "REFERENCES HoaDon(MaHoaDon) ON DELETE CASCADE, " +
                        "FOREIGN KEY(MaMon) " +
                        "REFERENCES MonAn(MaMon) ON DELETE RESTRICT)"
        );
    }

    private void themDuLieuMau(SQLiteDatabase db) {
        themTaiKhoanMau(db);
        themDanhMucMau(db);
        themMonAnMau(db);
        themHoaDonMau(db);
    }

    private void themTaiKhoanMau(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("TenDangNhap", "admin");
        values.put("MatKhau", "123456");
        values.put("VaiTro", "ADMIN");

        db.insertWithOnConflict(
                "TaiKhoan", null, values,
                SQLiteDatabase.CONFLICT_IGNORE
        );
    }
    private void themDanhMucMau(SQLiteDatabase db) {
        String[] danhMuc = {"Món nước", "Món cơm", "Đồ uống", "Món tráng miệng","Món chay","Món mặn"};
        for (String ten : danhMuc) {
            ContentValues values = new ContentValues();
            values.put("TenDanhMuc", ten);
            db.insertWithOnConflict(
                    "DanhMuc", null, values,
                    SQLiteDatabase.CONFLICT_IGNORE
            );
        }
    }
    private long layMaDanhMuc(SQLiteDatabase db, String ten) {
        try (Cursor cursor = db.rawQuery(
                "SELECT MaDanhMuc FROM DanhMuc " +
                        "WHERE TenDanhMuc = ?",
                new String[]{ten})) {
            if (cursor.moveToFirst()) {
                return cursor.getLong(0);
            }
        }
        return -1;
    }
    private void chenMon(SQLiteDatabase db, String ten, String danhMuc, double gia, int trangThai, String hinhAnh) {
        ContentValues values = new ContentValues();
        values.put("TenMon", ten);
        values.put("DanhMuc", danhMuc);
        values.put("MaDanhMuc", layMaDanhMuc(db, danhMuc));
        values.put("Gia", gia);
        values.put("TrangThai", trangThai);
        values.put("HinhAnh", hinhAnh);
        db.insertOrThrow("MonAn", null, values);
    }
    private void themMonAnMau(SQLiteDatabase db) {
        try (Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM MonAn", null)) {

            if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
                return;
            }
        }

        chenMon(db, "Phở bò", "Món nước", 45000, 1, "img_phobo");
        chenMon(db, "Cơm rang", "Món cơm", 40000, 1, "img_comrang");
        chenMon(db, "Bún chả", "Món nước", 35000, 0, "img_buncha");
        chenMon(db,"Bún thang","Món Nước",50000,1,"img_bunthang");
        chenMon(db,"Bún đậu nước mắm","Món mặn",30000,1,"img_bundau");
        chenMon(db,"Sưa chưa Việt Quất","Đồ uống",20000,1,"img_suachuavq");
        chenMon(db,"Thanh Long lướt sóng","Món chay",15000,1,"img_rauluoc");
        chenMon(db,"Chè bốn mùa","Món tráng miệng",30000,1,"img_chebonmua");
        chenMon(db,"Kem dừa","Món tráng miệng",30000,1,"img_kemdua");
        chenMon(db,"Salad","Món chay",15000,1,"img_salad");
        chenMon(db,"Gà ủ muối","Món mặn",35000,1,"img_gaumuoi");
        chenMon(db,"Cháo Dinh Dưỡng","Món cơm",5000000,1,"img_chaodinhduong");
    }
    private void themHoaDonMau(SQLiteDatabase db) {
        try (Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM HoaDon", null)) {
            if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
                return;
            }
        }
        long pho = layMaMon(db, "Phở bò");
        long com = layMaMon(db, "Cơm rang");
        long bun = layMaMon(db, "Bún chả");
        if (pho == -1 || com == -1 || bun == -1) {
            return;
        }
        long hd1 = chenHoaDon(db, "2026-11-14",
                130000, "Không hành");
        chenChiTiet(db, hd1, pho, 2, 45000);
        chenChiTiet(db, hd1, com, 1, 40000);

        long hd2 = chenHoaDon(db, "2026-11-13",
                80000, "Mang về");
        chenChiTiet(db, hd2, com, 2, 40000);

        long hd3 = chenHoaDon(db, "2026-11-12",
                35000, null);
        chenChiTiet(db, hd3, bun, 1, 35000);
    }
    private long layMaMon(SQLiteDatabase db, String ten) {
        try (Cursor cursor = db.rawQuery(
                "SELECT MaMon FROM MonAn " +
                        "WHERE TenMon = ? ORDER BY MaMon LIMIT 1",
                new String[]{ten})) {
            if (cursor.moveToFirst()) {
                return cursor.getLong(0);
            }
        }
        return -1;
    }
    private long chenHoaDon(SQLiteDatabase db,
                            String ngay, double tongTien,
                            String ghiChu) {
        ContentValues values = new ContentValues();
        values.put("NgayLap", ngay);
        values.put("TongTien", tongTien);
        values.put("GhiChu", ghiChu);
        return db.insertOrThrow("HoaDon", null, values);
    }
    private void chenChiTiet(SQLiteDatabase db,
                             long maHD, long maMon,
                             int soLuong, double donGia) {

        ContentValues values = new ContentValues();
        values.put("MaHoaDon", maHD);
        values.put("MaMon", maMon);
        values.put("SoLuong", soLuong);
        values.put("DonGia", donGia);
        values.put("ThanhTien", soLuong * donGia);
        db.insertOrThrow("ChiTietHoaDon", null, values);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            taoBangTaiKhoan(db);
        }
        // Không xóa bảng TaiKhoan của phiên bản 3.
        if (oldVersion < 4) {
            taoBangTaiKhoan(db);
            taoBangDanhMuc(db);
            // Bảng MonAn của phiên bản cũ chưa có MaDanhMuc
            try (Cursor cursor = db.rawQuery(
                    "PRAGMA table_info(MonAn)", null)) {
                boolean coMaDanhMuc = false;
                while (cursor.moveToNext()) {
                    String tenCot = cursor.getString(1);
                    if ("MaDanhMuc".equals(tenCot)) {
                        coMaDanhMuc = true;
                        break;
                    }
                }
                if (!coMaDanhMuc) {
                    db.execSQL(
                            "ALTER TABLE MonAn " + "ADD COLUMN MaDanhMuc INTEGER"
                    );
                }
            }
            taoBangHoaDon(db);
            taoBangChiTietHoaDon(db);
            themTaiKhoanMau(db);
            themDanhMucMau(db);
            db.execSQL(
                    "UPDATE MonAn SET MaDanhMuc = (" +
                            "SELECT MaDanhMuc FROM DanhMuc " +
                            "WHERE DanhMuc.TenDanhMuc = MonAn.DanhMuc" +
                            ") WHERE MaDanhMuc IS NULL"
            );
            themMonAnMau(db);
            themHoaDonMau(db);
        }
    }
}
