package com.example.pengaduan.view.admin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.pengaduan.R;
import com.example.pengaduan.databinding.ActivityTanggapanBinding;
import com.example.pengaduan.model.Aduan;
import com.example.pengaduan.service.OracleConnection;
import com.example.pengaduan.view.MainActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TanggapanActivity extends AppCompatActivity {
    private ActivityTanggapanBinding binding;
    private final String[] jenisPengaduan = {"Menunggu", "Di Proses", "Selesai", "Di Tolak"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTanggapanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        Aduan data = intent.getParcelableExtra("data");
        binding.jenisAduan.setText(data.getJenisAduan());
        binding.namaLengkap.setText(data.getNamaLengkap());
        binding.location.setText(data.getTitikLokasi());
        binding.date.setText(data.getTanggal());
        binding.desc.setText(data.getDeskripsi());
        binding.deviceCondition.setText(data.getKondisiDevice());
        binding.idPelanggan.setText(data.getIdPelanggan());
        binding.tanggapan.setText(data.getTanggapan());
        ArrayAdapter<String> kategori = new ArrayAdapter<>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, jenisPengaduan);
        binding.statusAduan.setAdapter(kategori);
        int selectedPosition = getSelectedPosition(data.getStatus());
        binding.statusAduan.setSelection(selectedPosition);
        byte[] imageData = data.getRawImage();

        if (imageData != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            Glide.with(this)
                    .asBitmap()
                    .load(bitmap)
                    .into(binding.imgview);
        } else {
            binding.imgview.setImageResource(R.drawable.kliklapor);
        }
        binding.submit.setOnClickListener(view -> {
            new Thread(() -> {
                String tanggapan = binding.tanggapan.getText().toString();
                if (!TextUtils.isEmpty(tanggapan)) {
                    String selectedStatus = binding.statusAduan.getSelectedItem().toString();
                    updateTanggapanAndStatusInDatabase(data.getId(), tanggapan, selectedStatus);
                } else {
                    Toast.makeText(TanggapanActivity.this, "Isi tanggapan terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
            }).start();
            });
    }
    private int getSelectedPosition(String status) {
        for (int i = 0; i < jenisPengaduan.length; i++) {
            if (jenisPengaduan[i].equals(status)) {
                return i;
            }
        }
        return 0;
    }

    private void updateTanggapanAndStatusInDatabase(String pengaduanId, String tanggapan, String status) {
        Connection connection = null;
        try {
            connection = OracleConnection.getConnection();
            String sql = "UPDATE PENGADUAN SET TANGGAPAN = ?, STATUS = ? WHERE ID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, tanggapan);
            preparedStatement.setString(2, status);
            preparedStatement.setString(3, pengaduanId);
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Tanggapan dan status berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(TanggapanActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                finish();
                });
            } else {
                runOnUiThread(() -> {

                    Toast.makeText(this, "Gagal memperbarui tanggapan dan status", Toast.LENGTH_SHORT).show();
            });
        }
        } catch (SQLException e) {
            e.printStackTrace();
            runOnUiThread(() -> {
                Toast.makeText(this, "Terjadi kesalahan pada database", Toast.LENGTH_SHORT).show();
            });
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}