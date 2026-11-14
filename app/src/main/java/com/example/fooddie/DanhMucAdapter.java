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
        void onSua(int position);
        void onXoa(int position);
    }

    private final ArrayList<String> danhSach;
    private final OnDanhMucClickListener listener;

    public DanhMucAdapter(
            ArrayList<String> danhSach,
            OnDanhMucClickListener listener) {
        this.danhSach = danhSach;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_danh_muc, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder, int position) {

        holder.txtTen.setText(danhSach.get(position));
        holder.txtSoLuong.setText("Dữ liệu mẫu");

        holder.btnSua.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                listener.onSua(pos);
            }
        });

        holder.btnXoa.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                listener.onXoa(pos);
            }
        });
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
