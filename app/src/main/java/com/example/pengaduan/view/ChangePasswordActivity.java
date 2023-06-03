package com.example.pengaduan.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.pengaduan.R;
import com.example.pengaduan.databinding.ActivityChangePasswordBinding;
import com.example.pengaduan.helper.SharedPrefManager;
import com.example.pengaduan.service.OracleConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChangePasswordActivity extends AppCompatActivity {
    private ActivityChangePasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        binding.updatePassword.setOnClickListener(view -> new Thread(() -> {
            String oldPassword = binding.oldPwd.getText().toString().trim();
            String newPassword = binding.newPwd.getText().toString().trim();
            String confirmPassword = binding.confirmNewPwd.getText().toString().trim();

            if (isValidPassword(oldPassword, newPassword, confirmPassword)) {
                if (changePassword(oldPassword, newPassword)) {
                    runOnUiThread(() -> {
                    Toast.makeText(ChangePasswordActivity.this, "Password berhasil diubah", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(ChangePasswordActivity.this, "Password lama tidak sesuai", Toast.LENGTH_SHORT).show());
                }
            }
        }).start());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private boolean isValidPassword(String oldPassword, String newPassword, String confirmPassword) {
        if (TextUtils.isEmpty(oldPassword)) {
            binding.oldPwd.setError("Masukkan password lama");
            return false;
        }

        if (TextUtils.isEmpty(newPassword)) {
            binding.newPwd.setError("Masukkan password baru");
            return false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            binding.confirmNewPwd.setError("Masukkan konfirmasi password");
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            binding.confirmNewPwd.setError("Konfirmasi password tidak cocok");
            return false;
        }

        return true;
    }

    private boolean changePassword(String oldPassword, String newPassword) {
        boolean isSuccess = false;
        String username = SharedPrefManager.getUsername(this);

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = OracleConnection.getConnection();
            String query = "SELECT * FROM ACCOUNT WHERE USERNAME = ? AND PASSWORD = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, oldPassword);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                query = "UPDATE ACCOUNT SET PASSWORD = ? WHERE USERNAME = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1, newPassword);
                statement.setString(2, username);
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    isSuccess = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return isSuccess;
    }
}
