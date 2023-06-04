package com.example.pengaduan.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.pengaduan.R;
import com.example.pengaduan.databinding.ActivitySettingBinding;
import com.example.pengaduan.helper.SharedPrefManager;
import com.example.pengaduan.view.admin.PelangganActivity;
import com.example.pengaduan.view.admin.RealIdActivity;

public class SettingActivity extends AppCompatActivity {
    private ActivitySettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        binding.cardRealId.setVisibility(View.GONE);
        binding.cardPelanggan.setVisibility(View.GONE);

        binding.cardPelanggan.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), PelangganActivity.class);
            startActivity(intent);
        });

        binding.cardRealId.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), RealIdActivity.class);
            startActivity(intent);
        });

        binding.card1.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), AccountActivity.class);
            startActivity(intent);
        });

        binding.card2.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
            startActivity(intent);
        });

        binding.card3.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}