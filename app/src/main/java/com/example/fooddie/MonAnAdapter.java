package com.example.fooddie;

import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MonAnAdapter
        extends RecyclerView.Adapter<MonAnAdapter.MonAnViewHolder> {

    public interface OnMonAnActionListener {
        void onSua(MonAn mon);
        void onXoa(MonAn mon);
    }

    private final List<MonAn> danhSach;
    private final OnMonAnActionListener listener;
    public MonAnAdapter(
            List<MonAn> danhSach,
            OnMonAnActionListener listener) {
        this.danhSach = danhSach;
        this.listener = listener;
    }
    @NonNull
    @Override
    public MonAnViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_mon_an, parent, false);
        return new MonAnViewHolder(view);
    }
    @Override
    public void onBindViewHolder(
            @NonNull MonAnViewHolder holder, int position) {
        MonAn mon = danhSach.get(position);
        holder.txtTenMon.setText(mon.getTenMon());
        holder.txtDanhMuc.setText(mon.getDanhMuc());
        NumberFormat format = NumberFormat.getNumberInstance(
                new Locale("vi", "VN")
        );
        holder.txtGia.setText(
                format.format(mon.getGia()) + "đ"
        );
        if (mon.isDangBan()) {
            holder.txtTrangThai.setText("Đang bán");
            holder.txtTrangThai.setTextColor(
                    Color.parseColor("#16804A")
            );
        } else {
            holder.txtTrangThai.setText("Ngừng bán");
            holder.txtTrangThai.setTextColor(
                    Color.parseColor("#888888")
            );
        }
        hienThiAnh(holder.imgMonAn, mon.getHinhAnh());
        holder.btnSua.setOnClickListener(v ->
                listener.onSua(mon));
        holder.btnXoa.setOnClickListener(v ->
                listener.onXoa(mon));
    }
    public static void hienThiAnh(ImageView imageView, String hinhAnh) {
        imageView.setImageDrawable(null);
        if (hinhAnh == null || hinhAnh.isEmpty()) {
            imageView.setImageResource(
                    android.R.drawable.ic_menu_gallery
            );
            return;
        }
        File file = new File(hinhAnh);
        if (file.isFile()) {
            imageView.setImageURI(Uri.fromFile(file));
            return;
        }
        int id = imageView.getContext()
                .getResources().getIdentifier(
                        hinhAnh,
                        "drawable",
                        imageView.getContext().getPackageName()
                );

        if (id != 0) {
            imageView.setImageResource(id);
        } else {
            imageView.setImageResource(
                    android.R.drawable.ic_menu_gallery
            );
        }
    }

    @Override
    public int getItemCount() {
        return danhSach.size();
    }
    public static class MonAnViewHolder
            extends RecyclerView.ViewHolder {
        ImageView imgMonAn;
        TextView txtTenMon, txtDanhMuc;
        TextView txtGia, txtTrangThai;
        ImageButton btnSua, btnXoa;
        public MonAnViewHolder(@NonNull View itemView) {
            super(itemView);

            imgMonAn = itemView.findViewById(R.id.imgMonAn);
            txtTenMon = itemView.findViewById(R.id.txtTenMon);
            txtDanhMuc = itemView.findViewById(R.id.txtDanhMuc);
            txtGia = itemView.findViewById(R.id.txtGia);
            txtTrangThai = itemView.findViewById(R.id.txtTrangThai);
            btnSua = itemView.findViewById(R.id.btnSua);
            btnXoa = itemView.findViewById(R.id.btnXoa);
        }
    }
}
