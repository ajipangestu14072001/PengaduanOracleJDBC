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
import java.sql.SQLException;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {
private ActivityRegisterBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.idPelanggan.setText(generateID());

        binding.signUp.setOnClickListener(view -> {
            new Thread(() -> {
                String username = binding.username.getText().toString().trim();
                String namaLengkap = binding.namaLengkap.getText().toString().trim();
                String password = binding.password.getText().toString();
                String idPelanggan = binding.idPelanggan.getText().toString();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(namaLengkap) || TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "Harap isi semua field", Toast.LENGTH_SHORT).show();
                } else {
                    User user = new User();
                    user.setId(idPelanggan);
                    user.setUsername(username);
                    user.setNamaLengkap(namaLengkap);
                    user.setPassword(password);
                    user.setRole("USER");
                    registerUser(user);
                }
            }).start();
        });

    }

    private void registerUser(User user) {
        Connection connection;
        String sql = "INSERT INTO ACCOUNT (ID, NAMA_LENGKAP, USERNAME, PASSWORD, ROLE) VALUES (?, ?, ?, ?, ?)";

        try {
            connection = OracleConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, user.getId());
            statement.setString(2, user.getNamaLengkap());
            statement.setString(3, user.getUsername());
            statement.setString(4, user.getPassword());
            statement.setString(5, user.getRole());

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                runOnUiThread(() -> {
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

    private String generateID() {
        return UUID.randomUUID().toString();
    }
}