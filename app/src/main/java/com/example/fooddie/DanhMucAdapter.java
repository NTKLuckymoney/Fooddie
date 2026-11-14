
package com.example.fooddie;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DanhMucAdapter
        extends RecyclerView.Adapter<DanhMucAdapter.ViewHolder> {

    public interface OnDanhMucClickListener {
        void onXem(DanhMuc danhMuc);
        void onSua(DanhMuc danhMuc);
        void onXoa(DanhMuc danhMuc);
    }

    private final ArrayList<DanhMuc> danhSach;
    private final OnDanhMucClickListener listener;

    public DanhMucAdapter(
            ArrayList<DanhMuc> danhSach,
            OnDanhMucClickListener listener) {
        this.danhSach = danhSach;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_danh_muc,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DanhMuc danhMuc = danhSach.get(position);

        holder.txtTen.setText(danhMuc.getTenDanhMuc());

        holder.txtSoLuong.setText(danhMuc.getSoLuongMon() + " món ăn");
        holder.itemView.setOnClickListener(v -> listener.onXem(danhMuc));
        holder.btnSua.setOnClickListener(v -> listener.onSua(danhMuc));
        holder.btnXoa.setOnClickListener(v -> listener.onXoa(danhMuc));

    }
    @Override
    public int getItemCount() {
        return danhSach.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTen, txtSoLuong;
        ImageButton btnSua, btnXoa;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTen = itemView.findViewById(R.id.txtTenDanhMuc);
            txtSoLuong = itemView.findViewById(R.id.txtSoLuongMon);
            btnSua = itemView.findViewById(R.id.btnSuaDanhMuc);
            btnXoa = itemView.findViewById(R.id.btnXoaDanhMuc);
        }
    }
}
