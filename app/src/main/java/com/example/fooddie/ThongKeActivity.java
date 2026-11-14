package com.example.fooddie;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class ThongKeActivity extends AppCompatActivity {

    private Spinner spnLoaiThongKe;
    private EditText edtNgayThongKe;

    private final Calendar ngayDaChon = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thongke);

        spnLoaiThongKe = findViewById(R.id.spnLoaiThongKe);
        edtNgayThongKe = findViewById(R.id.edtNgayThongKe);

        String[] loaiThongKe = {
                "Theo ngày",
                "Theo tháng",
                "Theo năm"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                loaiThongKe
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spnLoaiThongKe.setAdapter(adapter);

        findViewById(R.id.btnQuayLai)
                .setOnClickListener(v -> finish());

        edtNgayThongKe.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    (picker, year, month, day) -> {
                        ngayDaChon.set(year, month, day);
                        capNhatNgayHienThi();
                    },
                    ngayDaChon.get(Calendar.YEAR),
                    ngayDaChon.get(Calendar.MONTH),
                    ngayDaChon.get(Calendar.DAY_OF_MONTH)
            );

            dialog.show();
        });

        capNhatNgayHienThi();

        findViewById(R.id.btnXemThongKe)
                .setOnClickListener(v ->
                        Toast.makeText(
                                this,
                                "Chưa kết nối SQLite",
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }
    private void capNhatNgayHienThi() {
        int ngay = ngayDaChon.get(Calendar.DAY_OF_MONTH);
        int thang = ngayDaChon.get(Calendar.MONTH) + 1;
        int nam = ngayDaChon.get(Calendar.YEAR);

        edtNgayThongKe.setText(String.format(
                Locale.getDefault(),
                "%02d/%02d/%04d",
                ngay, thang, nam
        ));
    }
}
