package com.example.fooddie;

public class DanhMuc {

    private int maDanhMuc;
    private String tenDanhMuc;
    private int soLuongMon;
    public DanhMuc(int maDanhMuc,
                   String tenDanhMuc,
                   int soLuongMon) {
        this.maDanhMuc = maDanhMuc;
        this.tenDanhMuc = tenDanhMuc;
        this.soLuongMon = soLuongMon;
    }
    public int getMaDanhMuc() {return maDanhMuc;}
    public String getTenDanhMuc() {return tenDanhMuc;}
    public int getSoLuongMon() {return soLuongMon;}
}
