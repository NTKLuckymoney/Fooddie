
package com.example.fooddie;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MonAnAdapter
        extends RecyclerView.Adapter<MonAnAdapter.MonAnViewHolder> {

    private final List<MonAn> danhSach;

    public MonAnAdapter(List<MonAn> danhSach) {
        this.danhSach = danhSach;
    }

    // 1. Tạo View từ item_mon_an.xml
    @NonNull
    @Override
    public MonAnViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_mon_an, parent, false);

        return new MonAnViewHolder(view);
    }

    // 2. Đưa dữ liệu món ăn vào các View
    @Override
    public void onBindViewHolder(
            @NonNull MonAnViewHolder holder, int position) {

        MonAn mon = danhSach.get(position);

        holder.txtTenMon.setText(mon.getTenMon());
        holder.txtDanhMuc.setText(mon.getDanhMuc());

        NumberFormat format = NumberFormat.getNumberInstance(
                new Locale("vi", "VN"));

        holder.txtGia.setText(
                format.format(mon.getGia()) + "đ");

        if (mon.isDangBan()) {
            holder.txtTrangThai.setText("Đang bán");
            holder.txtTrangThai.setTextColor(
                    Color.parseColor("#16804A"));
        } else {
            holder.txtTrangThai.setText("Ngừng bán");
            holder.txtTrangThai.setTextColor(
                    Color.parseColor("#888888"));
        }

        // Không giữ ảnh cũ khi RecyclerView tái sử dụng View
        holder.imgMonAn.setImageDrawable(null);

        if (mon.getHinhAnh() != 0) {
            holder.imgMonAn.setImageResource(
                    mon.getHinhAnh());
        }

        // Sự kiện sửa, xóa sẽ bổ sung sau
        holder.btnSua.setOnClickListener(v -> {
            Intent intent = new Intent(
                    v.getContext(),
                    SuaMonAnActivity.class
            );

            v.getContext().startActivity(intent);
        });
    }


    // 3. Trả về số lượng món ăn
    @Override
    public int getItemCount() {
        return danhSach.size();
    }

    // 4. Giữ các View trong một item
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
