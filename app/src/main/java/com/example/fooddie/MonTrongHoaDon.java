package com.example.fooddie;

public class MonTrongHoaDon {
    private int maMon;
    private String tenMon;
    private double donGia;
    private int soLuong;
    public MonTrongHoaDon(int maMon, String tenMon,
                          double donGia, int soLuong) {
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.donGia = donGia;
        this.soLuong = soLuong;
    }
    public int getMaMon() {
        return maMon;
    }
    public String getTenMon() {
        return tenMon;
    }
    public double getDonGia() {
        return donGia;
    }
    public int getSoLuong() {
        return soLuong;
    }
    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }
    public double getThanhTien() {
        return donGia * soLuong;
    }
}
