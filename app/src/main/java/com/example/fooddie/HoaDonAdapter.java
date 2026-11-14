
package com.example.fooddie;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class HoaDonAdapter
        extends RecyclerView.Adapter<HoaDonAdapter.ViewHolder> {

    public interface OnHoaDonActionListener {
        void onChiTiet(HoaDon hoaDon);
        void onSua(HoaDon hoaDon);
        void onXoa(HoaDon hoaDon);
    }

    private final ArrayList<HoaDon> danhSach;
    private final OnHoaDonActionListener listener;

    public HoaDonAdapter(
            ArrayList<HoaDon> danhSach,
            OnHoaDonActionListener listener) {

        this.danhSach = danhSach;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.item_hoa_don,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        HoaDon hoaDon = danhSach.get(position);

        holder.txtMaHoaDon.setText(
                String.format(
                        Locale.US,
                        "HD%03d",
                        hoaDon.getMaHoaDon()
                )
        );

        holder.txtTongTien.setText(
                String.format(
                        new Locale("vi", "VN"),
                        "%,.0fđ",
                        hoaDon.getTongTien()
                )
        );

        String ngayLap = hoaDon.getNgayLap();

        // Chuyển yyyy-MM-dd thành dd/MM/yyyy
        if (ngayLap != null &&
                ngayLap.matches("\\d{4}-\\d{2}-\\d{2}")) {

            String[] parts = ngayLap.split("-");

            ngayLap = parts[2] + "/" +
                    parts[1] + "/" + parts[0];
        }

        holder.txtNgayLap.setText(
                "Ngày lập: " + ngayLap
        );

        holder.txtSoMon.setText(
                "Số món: " + hoaDon.getSoMon()
        );

        holder.btnChiTiet.setOnClickListener(v ->
                listener.onChiTiet(hoaDon));

        holder.btnSua.setOnClickListener(v ->
                listener.onSua(hoaDon));

        holder.btnXoa.setOnClickListener(v ->
                listener.onXoa(hoaDon));
    }

    @Override
    public int getItemCount() {
        return danhSach.size();
    }

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtMaHoaDon;
        TextView txtNgayLap;
        TextView txtTongTien;
        TextView txtSoMon;

        ImageButton btnChiTiet;
        ImageButton btnSua;
        ImageButton btnXoa;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtMaHoaDon = itemView.findViewById(
                    R.id.txtMaHoaDon
            );

            txtNgayLap = itemView.findViewById(
                    R.id.txtNgayLap
            );

            txtTongTien = itemView.findViewById(
                    R.id.txtTongTien
            );

            txtSoMon = itemView.findViewById(
                    R.id.txtSoMon
            );

            btnChiTiet = itemView.findViewById(
                    R.id.btnChiTietHoaDon
            );

            btnSua = itemView.findViewById(
                    R.id.btnSuaHoaDon
            );

            btnXoa = itemView.findViewById(
                    R.id.btnXoaHoaDon
            );
        }
    }
}
