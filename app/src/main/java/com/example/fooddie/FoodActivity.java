package com.example.fooddie;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import android.content.Intent;
import android.app.Activity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
public class FoodActivity extends AppCompatActivity {

    private RecyclerView rvMonAn;
    private MonAnAdapter adapter;
    private ImageButton btnSua;
    private ArrayList<MonAn> danhSachMonAn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food);


        rvMonAn = findViewById(R.id.rvMonAn);


        danhSachMonAn = new ArrayList<>();

        danhSachMonAn.add(new MonAn(
                1, "Phở bò", "Món nước", 45000, true,R.drawable.img_phobo
        ));

        danhSachMonAn.add(new MonAn(
                2, "Cơm rang", "Món cơm", 40000, true, R.drawable.img_comrang
                ));

        danhSachMonAn.add(new MonAn(
                3, "Bún chả", "Món nước", 35000, false, R.drawable.img_buncha));

        // 3. Thiết lập danh sách theo chiều dọc
        rvMonAn.setLayoutManager(
                new LinearLayoutManager(this));

        // 4. Tạo Adapter với danh sách món ăn
        adapter = new MonAnAdapter(danhSachMonAn);

        // 5. Gắn Adapter vào RecyclerView
        rvMonAn.setAdapter(adapter);

        // 6. Nút quay lại Home
        ImageButton btnQuayLai = findViewById(R.id.btnQuayLai);
        btnQuayLai.setOnClickListener(v -> finish());
        findViewById(R.id.btnThemMon).setOnClickListener(v -> {
            Intent intent = new Intent(FoodActivity.this, ThemMonAnActivity.class);
            themMonLauncher.launch(intent);
        });
    }
    private void taiLaiDanhSach() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        danhSachMonAn.clear();
        danhSachMonAn.addAll(dbHelper.layDanhSachMonAn());

        adapter.notifyDataSetChanged();
    }
    private final ActivityResultLauncher<Intent> themMonLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            taiLaiDanhSach();
                        }
                    }
            );
}
