package com.example.fooddie;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class HomeActivity extends AppCompatActivity {
    ImageButton btnDangXuat;
    CardView cardMonAn,cardDanhMuc,cardHoaDon,cardThongKe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        btnDangXuat=findViewById(R.id.btnDangXuat);
        cardMonAn=findViewById(R.id.cardMonAn);
        cardDanhMuc=findViewById(R.id.cardDanhMuc);
        cardHoaDon=findViewById(R.id.cardHoaDon);
        cardThongKe=findViewById(R.id.cardThongKe);
        btnDangXuat.setOnClickListener(v -> {
            getSharedPreferences("FooddieSession", MODE_PRIVATE)
                    .edit()
                    .remove("daDangNhap")
                    .apply();
            Intent intent = new Intent(
                    HomeActivity.this,
                    LoginActivity.class
            );
            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );
            startActivity(intent);
        });
        cardMonAn.setOnClickListener(v -> {
            Intent intent = new Intent(
                    HomeActivity.this,
                    FoodActivity.class
            );
            startActivity(intent);
        });
    }

}
