package com.example.pengaduan.view.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.pengaduan.R;
import com.example.pengaduan.adapter.RiwayatAdapter;
import com.example.pengaduan.adapter.TanggapanAdapter;
import com.example.pengaduan.callback.FetchRecyclerViewItems;
import com.example.pengaduan.databinding.ActivityRiwayatTanggapanBinding;
import com.example.pengaduan.helper.SharedPrefManager;
import com.example.pengaduan.model.Aduan;
import com.example.pengaduan.service.OracleConnection;
import com.example.pengaduan.view.DetailRiwayatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RiwayatTanggapanActivity extends AppCompatActivity implements FetchRecyclerViewItems {

    private ActivityRiwayatTanggapanBinding binding;
    private ArrayList<Aduan> aduanList;
    private TanggapanAdapter riwayatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRiwayatTanggapanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        binding.recyclerView.setHasFixedSize(true);
        aduanList = new ArrayList<>();
        riwayatAdapter = new TanggapanAdapter(aduanList, this);
        binding.recyclerView.setAdapter(riwayatAdapter);
        new Thread(() -> {
            String idPelanggan = SharedPrefManager.getIdPelanggan(this);
            fetchData(idPelanggan);
        }).start();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchData(String idPelanggan) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = OracleConnection.getConnection();

            String query = "SELECT * FROM PENGADUAN WHERE STATUS != ? ORDER BY TANGGAL DESC";
            statement = connection.prepareStatement(query);
            statement.setString(1, "Menunggu");
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
        Intent intent = new Intent(this, TanggapanActivity.class);
        intent.putExtra("data", aduan);
        startActivity(intent);
    }
}