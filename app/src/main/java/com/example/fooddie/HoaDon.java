
package com.example.fooddie;

public class HoaDon {

    private int maHoaDon;
    private String ngayLap;
    private double tongTien;
    private String ghiChu;
    private int soMon;

    public HoaDon(int maHoaDon, String ngayLap,
                  double tongTien, String ghiChu,
                  int soMon) {
        this.maHoaDon = maHoaDon;
        this.ngayLap = ngayLap;
        this.tongTien = tongTien;
        this.ghiChu = ghiChu;
        this.soMon = soMon;
    }
    public int getMaHoaDon() {return maHoaDon;}
    public String getNgayLap() {return ngayLap;}

    public double getTongTien() {return tongTien;}

    public String getGhiChu() {return ghiChu;}

    public int getSoMon() {return soMon;}
}
