package com.example.pengaduan.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.pengaduan.R;
import com.example.pengaduan.databinding.ActivityAspirasiBinding;
import com.example.pengaduan.helper.SharedPrefManager;
import com.example.pengaduan.model.Aspirasi;
import com.example.pengaduan.model.User;
import com.example.pengaduan.service.OracleConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class AspirasiActivity extends AppCompatActivity {
    private ActivityAspirasiBinding binding;
    private final String[] jenisAspirasi = {"Pendapat", "Harapan", "Masukan", "Kritik"};
    String namaPengirim = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAspirasiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        ArrayAdapter<String> kategori = new ArrayAdapter<>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, jenisAspirasi);
        binding.kategoriAspirasi.setAdapter(kategori);
        String userName = SharedPrefManager.getUsername(this);

        new Thread(() -> {
            Connection connection;
            Statement statement;
            ResultSet resultSet;

            try {
                connection = OracleConnection.getConnection();
                statement = connection.createStatement();
                resultSet = statement.executeQuery("SELECT * FROM ACCOUNT WHERE USERNAME = '" + userName + "'");

                while (resultSet.next()) {
                    String namaLengkap = resultSet.getString("NAMA_LENGKAP");
                    binding.nama.setText(namaLengkap);
                    namaPengirim = namaLengkap;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                System.out.println("Something When Wrong");
            }
        }).start();

        binding.submit.setOnClickListener(view -> {
            new Thread(() -> {
            String jenisAspirasi = binding.kategoriAspirasi.getSelectedItem().toString().trim();
            String deskripsi = binding.desc.getText().toString().trim();
            String id = generateID();

            if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(jenisAspirasi) || TextUtils.isEmpty(deskripsi)) {
                Toast.makeText(AspirasiActivity.this, "Harap isi semua field", Toast.LENGTH_SHORT).show();
            } else {
                Aspirasi aspirasi = new Aspirasi();
                aspirasi.setId(id);
                aspirasi.setJenisAspirasi(jenisAspirasi);
                aspirasi.setDeskripsi(deskripsi);
                aspirasi.setNamaPengirim(namaPengirim);

                aspirasiSubmit(aspirasi);
            }
            }).start();
        });
    }
    private void aspirasiSubmit(Aspirasi aspirasi) {
        Connection connection;
        String sql = "INSERT INTO ASPIRASI (ID, JENIS_ASPIRASI, DESKRIPSI, NAMA_PENGIRIM) VALUES (?, ?, ?, ?)";

        try {
            connection = OracleConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, aspirasi.getId());
            statement.setString(2, aspirasi.getJenisAspirasi());
            statement.setString(3, aspirasi.getDeskripsi());
            statement.setString(4, aspirasi.getNamaPengirim());


            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                runOnUiThread(() -> {
                    Toast.makeText(AspirasiActivity.this, "Aspirasi berhasil di Kirim", Toast.LENGTH_SHORT).show();
                });
            } else {
                runOnUiThread(() -> Toast.makeText(AspirasiActivity.this, "Aspirasi gagal di kirim", Toast.LENGTH_SHORT).show());
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String generateID() {
        return UUID.randomUUID().toString();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}