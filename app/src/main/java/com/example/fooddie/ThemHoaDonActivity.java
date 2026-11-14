package com.example.fooddie;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import java.util.Locale;
public class ThemHoaDonActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_hoa_don);
        TextView txtTieuDe = findViewById(R.id.txtTieuDe);
        TextView txtMaHoaDon = findViewById(R.id.txtMaHoaDon);
        EditText edtNgayLap = findViewById(R.id.edtNgayLap);
        txtTieuDe.setText("Thêm hóa đơn");
        txtMaHoaDon.setText("Mã hóa đơn: Tự động tạo");
        findViewById(R.id.btnQuayLai)
                .setOnClickListener(v -> finish());

        Calendar calendar = Calendar.getInstance();

        edtNgayLap.setText(String.format(
                Locale.getDefault(), "%02d/%02d/%04d",
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR)
        ));
        edtNgayLap.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    (picker, year, month, day) ->
                            edtNgayLap.setText(String.format(
                                    Locale.getDefault(),
                                    "%02d/%02d/%04d",
                                    day, month + 1, year
                            )),
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            dialog.show();
        });

        // Chọn món và lưu sẽ xử lý sau khi hoàn thành UI.
    }
}
