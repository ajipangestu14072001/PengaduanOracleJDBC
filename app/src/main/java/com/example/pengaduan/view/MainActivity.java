package com.example.pengaduan.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import com.example.pengaduan.R;
import com.example.pengaduan.adapter.AdapterHome;
import com.example.pengaduan.adapter.SliderAdapter;
import com.example.pengaduan.databinding.ActivityMainBinding;
import com.example.pengaduan.helper.SharedPrefManager;
import com.example.pengaduan.model.SliderItems;
import com.example.pengaduan.service.OracleConnection;
import com.example.pengaduan.view.LoginActivity;
import com.example.pengaduan.view.SettingActivity;
import com.example.pengaduan.view.admin.RiwayatAspirasiActivity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private AdapterHome adapter;
    private Handler handler = new Handler();
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fullScreen(this);
        sharedPrefManager = new SharedPrefManager(getApplicationContext());

        String name = SharedPrefManager.getUsername(this);
        String role = SharedPrefManager.getRole(this);

        if (role.equals("USER")){
            binding.lihatAspirasi.setVisibility(View.GONE);
        }

        List<SliderItems> imageList = new ArrayList<>();
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        adapter = new AdapterHome(getApplicationContext());
        binding.recyclerView.setAdapter(adapter);

        binding.imageProfile.setOnClickListener(view -> {
            if (!sharedPrefManager.sPSudahLogin()) {
                startActivity(new Intent(getApplicationContext(), SettingActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            } else {
                startActivity(new Intent(getApplicationContext(), SettingActivity.class));
            }
        });

        binding.lihatAspirasi.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), RiwayatAspirasiActivity.class)));

        imageList.add(new SliderItems(R.drawable.bg_banner));
        imageList.add(new SliderItems(R.drawable.bg_banner));
        imageList.add(new SliderItems(R.drawable.bg_banner));
        imageList.add(new SliderItems(R.drawable.bg_banner));
        SliderAdapter imageAdapter = new SliderAdapter(imageList, binding.viewPagerImageSlider);
        binding.viewPagerImageSlider.setAdapter(imageAdapter);
        binding.viewPagerImageSlider.setOffscreenPageLimit(3);
        binding.viewPagerImageSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(65));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleX(0.90f + r * 0.25f);
        });
        binding.viewPagerImageSlider.setPageTransformer(compositePageTransformer);
        binding.viewPagerImageSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 5000);
            }
        });

        new Thread(() -> {
            Connection connection;
            Statement statement;
            ResultSet resultSet;

            try {
                connection = OracleConnection.getConnection();
                statement = connection.createStatement();
                resultSet = statement.executeQuery("SELECT * FROM ACCOUNT WHERE USERNAME = '" + name + "'");

                while (resultSet.next()) {
                    String id = resultSet.getString("ID");
                    String namaLengkap = resultSet.getString("NAMA_LENGKAP");
                    String idReal = resultSet.getString("REAL_ID");
                    SharedPrefManager.setNamaLengkap(this, namaLengkap);
                    SharedPrefManager.setIdPelanggan(this, id);
                    SharedPrefManager.setRealId(this, idReal);

                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                System.out.println("Something When Wrong");
            }
        }).start();

    }

    private final Runnable runnable = new Runnable() {
        public void run() {
            binding.viewPagerImageSlider.setCurrentItem(binding.viewPagerImageSlider.getCurrentItem() + 1);
        }
    };

    private void fullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (activity.getWindow().getInsetsController() != null) {
                WindowInsetsController insetsController = activity.getWindow().getInsetsController();
                if (insetsController != null) {
                    insetsController.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                    insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                }
            }
        } else {
            activity.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            );
        }
    }
}