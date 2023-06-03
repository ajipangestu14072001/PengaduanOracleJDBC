package com.example.pengaduan.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.pengaduan.R;
import com.example.pengaduan.databinding.ActivityDetailRiwayatBinding;
import com.example.pengaduan.helper.SharedPrefManager;
import com.example.pengaduan.model.Aduan;
import com.example.pengaduan.service.OracleConnection;
import com.example.pengaduan.view.admin.TanggapanActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DetailRiwayatActivity extends AppCompatActivity {

    private ActivityDetailRiwayatBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailRiwayatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        String role = SharedPrefManager.getRole(this);
        Intent intent = getIntent();
        Aduan data = intent.getParcelableExtra("aduan");
        if (role.equals("USER")) {
            binding.delete.setVisibility(View.GONE);
            binding.beriTanggapan.setVisibility(View.GONE);
        } else {
            binding.delete.setOnClickListener(v -> showConfirmationDialog(data.getId()));
        }
        binding.kendalaDetail.setText(data.getJenisAduan());
        binding.tanggalDetail.setText(data.getTitikLokasi());
        binding.lokasiDetail.setText(data.getTanggal());
        binding.kendalaDesc.setText(data.getDeskripsi());
        binding.keadaanDetail.setText(data.getKondisiDevice());
        binding.statusDetail.setText(data.getStatus());
        Glide.with(this)
                .load(data.getPathPhoto())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.imgHistory);
        binding.beriTanggapan.setOnClickListener(view -> {
            Intent intent1 = new Intent(getApplicationContext(), TanggapanActivity.class);
            intent1.putExtra("data", data);
            startActivity(intent1);
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showConfirmationDialog(String aduanID) {
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(DetailRiwayatActivity.this);
            builder.setTitle("Konfirmasi Hapus Data")
                    .setMessage("Apakah Anda yakin ingin menghapus data ini?")
                    .setPositiveButton("Ya", (dialog, which) -> new Thread(() -> deleteData(aduanID)).start())
                    .setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    private void deleteData(String id) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = OracleConnection.getConnection();

            String query = "DELETE FROM PENGADUAN WHERE ID = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, id);
            statement.executeUpdate();
            runOnUiThread(() -> {
                Toast.makeText(this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), RiwayatActivity.class);
                startActivity(intent);
                finish();
            });
        } catch (SQLException e) {
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(this, "Gagal menghapus data", Toast.LENGTH_SHORT).show());

    } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
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