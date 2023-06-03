package com.example.pengaduan.view.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pengaduan.R;
import com.example.pengaduan.databinding.ActivityRealIdBinding;
import com.example.pengaduan.helper.SharedPrefManager;
import com.example.pengaduan.service.OracleConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RealIdActivity extends AppCompatActivity {
    private EditText idEditText;
    private EditText karyawanEditText;
    private ActivityRealIdBinding binding;

    private static final String DEFAULT_PREFIX = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRealIdBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        idEditText = binding.idReal;
        karyawanEditText = binding.karyawan;
        Button submitButton = binding.submit;
        String namaLengkap = SharedPrefManager.getNamaLengkap(this);
        karyawanEditText.setText(namaLengkap);

        submitButton.setOnClickListener(v -> new Thread(this::insertRealId).start());
    }

    private void insertRealId() {
        String ids = idEditText.getText().toString().trim();
        String karyawan = karyawanEditText.getText().toString().trim();

        if (TextUtils.isEmpty(ids)) {
            idEditText.setError("ID harus diisi");
            idEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(karyawan)) {
            karyawanEditText.setError("Nama karyawan harus diisi");
            karyawanEditText.requestFocus();
            return;
        }

        String[] idArray = ids.split(",");
        Set<String> uniqueIds = new HashSet<>();
        for (String id : idArray) {
            String idPelanggan = DEFAULT_PREFIX + id.trim();

            if (!isIdPelangganExists(idPelanggan)) {
                uniqueIds.add(idPelanggan);
            }
        }

        if (uniqueIds.isEmpty()) {
            runOnUiThread(() -> Toast.makeText(this, "Semua ID Pelanggan sudah ada", Toast.LENGTH_SHORT).show());
            return;
        }

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = OracleConnection.getConnection();

            for (String uniqueId : uniqueIds) {
                String query = "INSERT INTO REALID (ID, ID_PELANGGAN, NAMA_PELANGGAN, NAMA_KARYAWAN) VALUES (?, ?, ?, ?)";
                statement = connection.prepareStatement(query);
                statement.setString(1, generateID());
                statement.setString(2, uniqueId);
                statement.setString(3, "");
                statement.setString(4, karyawan);

                statement.executeUpdate();
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "Real ID berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                clearForm();
            });

        } catch (SQLException e) {
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(this, "Gagal menambahkan Real ID", Toast.LENGTH_SHORT).show());
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

    private boolean isIdPelangganExists(String idPelanggan) {
        Connection connection;
        PreparedStatement statement;
        ResultSet resultSet;
        boolean exists = false;

        try {
            connection = OracleConnection.getConnection();

            String query = "SELECT COUNT(*) FROM REALID WHERE ID_PELANGGAN = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, idPelanggan);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                exists = count > 0;
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return exists;
    }

    private String generateID() {
        return UUID.randomUUID().toString();
    }

    private void clearForm() {
        idEditText.getText().clear();
        karyawanEditText.getText().clear();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}