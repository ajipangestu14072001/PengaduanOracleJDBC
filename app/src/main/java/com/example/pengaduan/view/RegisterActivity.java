package com.example.pengaduan.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.pengaduan.R;
import com.example.pengaduan.databinding.ActivityRegisterBinding;
import com.example.pengaduan.model.User;
import com.example.pengaduan.service.OracleConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {
private ActivityRegisterBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.signUp.setOnClickListener(view -> new Thread(() -> {
            String username = binding.username.getText().toString().trim();
            String namaLengkap = binding.namaLengkap.getText().toString().trim();
            String password = binding.password.getText().toString();
            String idPelanggan = binding.idPelanggan.getText().toString();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(namaLengkap) || TextUtils.isEmpty(password)) {
                Toast.makeText(RegisterActivity.this, "Harap isi semua field", Toast.LENGTH_SHORT).show();
            } else {
                boolean idRealExists = checkRealIdExistence(idPelanggan);
                if (!idRealExists) {
                    runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "ID_REAL tidak valid", Toast.LENGTH_SHORT).show());
                    return;
                }

                String realId = getRealIdFromTable(idPelanggan);
                if (realId != null) {
                    User user = new User();
                    user.setId("KlikLapor"+idPelanggan);
                    user.setUsername(username);
                    user.setNamaLengkap(namaLengkap);
                    user.setPassword(password);
                    user.setRole("USER");
                    user.setRealId(realId);
                    registerUser(user);
                } else {
                    runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "ID Sudah Digunakan", Toast.LENGTH_SHORT).show());
                }
            }
        }).start());

    }

    private void registerUser(User user) {
        Connection connection;
        String sql = "INSERT INTO ACCOUNT (ID, NAMA_LENGKAP, USERNAME, PASSWORD, ROLE, REAL_ID) VALUES (?, ?, ?, ?, ?, ?)";

        boolean idRealExists = checkRealIdExistence(user.getId());
        if (!idRealExists) {
            runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "ID_REAL tidak valid", Toast.LENGTH_SHORT).show());
            return;
        }

        try {
            connection = OracleConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            String id = generateID();
            statement.setString(1,id );
            statement.setString(2, user.getNamaLengkap());
            statement.setString(3, user.getUsername());
            statement.setString(4, user.getPassword());
            statement.setString(5, user.getRole());
            statement.setString(6, user.getRealId());

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                runOnUiThread(() -> {
                    updateIdPelangganInRealId(user.getId(), id);
                    Toast.makeText(RegisterActivity.this, "Registrasi berhasil", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                });
            } else {
                runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Registrasi gagal", Toast.LENGTH_SHORT).show());
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean checkRealIdExistence(String idReal) {
        Connection connection;
        PreparedStatement statement;
        ResultSet resultSet;
        boolean exists = false;

        try {
            connection = OracleConnection.getConnection();

            String query = "SELECT COUNT(*) FROM REALID WHERE ID_REAL = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, idReal);
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

    private String getRealIdFromTable(String idPelanggan) {

        Connection connection;
        PreparedStatement statement;
        ResultSet resultSet;
        String realId = null;

        try {
            connection = OracleConnection.getConnection();

            String query = "SELECT ID_REAL FROM REALID WHERE ID_PELANGGAN = ? OR ID_PELANGGAN IS NULL";
            statement = connection.prepareStatement(query);
            statement.setString(1, idPelanggan);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                realId = resultSet.getString("ID_REAL");
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return realId;
    }

    private void updateIdPelangganInRealId(String idReal, String idPelanggan) {
        new Thread(() -> {
            Connection connection;
            PreparedStatement statement;

            try {
                connection = OracleConnection.getConnection();

                String query = "UPDATE REALID SET ID_PELANGGAN = ? WHERE ID_REAL = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1, idPelanggan);
                statement.setString(2, idReal);
                statement.executeUpdate();

                statement.close();
                connection.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private String generateID() {
        return UUID.randomUUID().toString();
    }
}
