package com.example.fooddie;
public class MonAn {
    private int maMon;
    private String tenMon;
    private String danhMuc;
    private double gia;
    private boolean dangBan;
    private int hinhAnh;
    public MonAn(int maMon, String tenMon, String danhMuc, double gia, boolean dangBan, int hinhAnh) {
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.danhMuc = danhMuc;
        this.gia = gia;
        this.dangBan = dangBan;
        this.hinhAnh = hinhAnh;
    }
    public int getMaMon() {
        return maMon;
    }
    public String getTenMon() {
        return tenMon;
    }
    public String getDanhMuc() {
        return danhMuc;
    }
    public double getGia() {
        return gia;
    }
    public boolean isDangBan() {
        return dangBan;
    }
    public int getHinhAnh() {
        return hinhAnh;
    }
}
