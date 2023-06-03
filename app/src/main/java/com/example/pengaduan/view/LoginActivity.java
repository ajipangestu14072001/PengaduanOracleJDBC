package com.example.pengaduan.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.pengaduan.databinding.ActivityLoginBinding;
import com.example.pengaduan.helper.SharedPrefManager;
import com.example.pengaduan.service.OracleConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.signUp.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        binding.login.setOnClickListener(view -> new Thread(() -> {
            String username = binding.username.getText().toString().trim();
            String password = binding.pwd.getText().toString().trim();
            if (isValid(username, password)) {
                getRole(username, this);
                SharedPrefManager.setUsername(this, username);
//                if (role.equals("USER")) {
//                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                } else if (role.equals("ADMIN")) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

//                    finish();
//                }
            } else {
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Pastikan UserName dan Password Benar", Toast.LENGTH_SHORT).show());
            }
        }).start());
    }

    private boolean isValid(String username, String password) {
        boolean isValid = false;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = OracleConnection.getConnection();
            String query = "SELECT * FROM ACCOUNT WHERE USERNAME = ? AND PASSWORD = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                isValid = true;
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

        return isValid;

    }

    private String getRole(String username, Context context) {
        String role = "";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = OracleConnection.getConnection();
            String query = "SELECT ROLE FROM ACCOUNT WHERE USERNAME = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, username);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                role = resultSet.getString("ROLE");
                SharedPrefManager.setRole(context, role);
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

        return role;
    }
}