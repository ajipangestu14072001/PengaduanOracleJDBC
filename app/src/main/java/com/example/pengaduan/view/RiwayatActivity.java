package com.example.pengaduan.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.pengaduan.adapter.RiwayatAdapter;
import com.example.pengaduan.callback.FetchRecyclerViewItems;
import com.example.pengaduan.databinding.ActivityRiwayatBinding;
import com.example.pengaduan.helper.SharedPrefManager;
import com.example.pengaduan.model.Aduan;
import com.example.pengaduan.service.OracleConnection;
import com.example.pengaduan.view.admin.RiwayatTanggapanActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RiwayatActivity extends AppCompatActivity implements FetchRecyclerViewItems {

    private ActivityRiwayatBinding binding;
    private ArrayList<Aduan> aduanList;
    private RiwayatAdapter riwayatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRiwayatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        String role = SharedPrefManager.getRole(this);
        if (role.equals("USER")) {
            binding.riwayatTanggapan.setVisibility(View.GONE);
        }
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        binding.recyclerView.setHasFixedSize(true);
        aduanList = new ArrayList<>();
        riwayatAdapter = new RiwayatAdapter(aduanList, this);
        binding.recyclerView.setAdapter(riwayatAdapter);

        new Thread(() -> {
            String idPelanggan = SharedPrefManager.getIdPelanggan(this);
            fetchData(idPelanggan);
        }).start();

        binding.riwayatTanggapan.setOnClickListener(view -> {
            Intent intent = new Intent(this, RiwayatTanggapanActivity.class);
            startActivity(intent);
        });
    }


    @Override
    public void onItemClicked(View view, Aduan aduan) {
        String status = aduan.getStatus();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Status Aduan")
                .setMessage("Status: " + status)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    @Override
    public void onIntent(Aduan aduan) {
        Intent intent = new Intent(this, DetailRiwayatActivity.class);
        intent.putExtra("aduan", aduan);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchData(String idPelanggan) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String role = SharedPrefManager.getRole(this);

        try {
            connection = OracleConnection.getConnection();

            String query;

            if (role.equals("ADMIN")) {
                query = "SELECT * FROM PENGADUAN ORDER BY TANGGAL DESC";
                statement = connection.prepareStatement(query);
            } else {
                query = "SELECT * FROM PENGADUAN WHERE ID_PELANGGAN = ? ORDER BY TANGGAL DESC";
                statement = connection.prepareStatement(query);
                statement.setString(1, idPelanggan);
            }

            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String id = resultSet.getString("ID");
                String jenisAduan = resultSet.getString("JENIS_ADUAN");
                String namaLengkap = resultSet.getString("NAMA_LENGKAP");
                String tanggal = resultSet.getString("TANGGAL");
                String titikLokasi = resultSet.getString("TITIK_LOKASI");
                String kondisiDevice = resultSet.getString("KONDISI_DEVICE");
                String deskripsi = resultSet.getString("DESKRIPSI");
                String pathPhoto = resultSet.getString("PHOTO");
                String status = resultSet.getString("STATUS");
                String tanggapan = resultSet.getString("TANGGAPAN");

                Aduan aduan = new Aduan(id, jenisAduan, idPelanggan, namaLengkap, tanggal, titikLokasi, kondisiDevice, deskripsi, pathPhoto, status, tanggapan);
                aduanList.add(aduan);
            }

            runOnUiThread(() -> riwayatAdapter.notifyDataSetChanged());

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
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