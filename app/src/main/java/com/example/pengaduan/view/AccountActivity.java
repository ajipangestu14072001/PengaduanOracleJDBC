package com.example.pengaduan.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.pengaduan.R;
import com.example.pengaduan.databinding.ActivityAccountBinding;
import com.example.pengaduan.helper.SharedPrefManager;
import com.example.pengaduan.service.OracleConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AccountActivity extends AppCompatActivity {
private ActivityAccountBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        String name = SharedPrefManager.getUsername(this);

        new Thread(() -> {
            Connection connection;
            Statement statement;
            ResultSet resultSet;

            try {
                connection = OracleConnection.getConnection();
                statement = connection.createStatement();
                resultSet = statement.executeQuery("SELECT * FROM ACCOUNT WHERE USERNAME = '" + name + "'");

                while (resultSet.next()) {
                    String id = resultSet.getString("REAL_ID");
                    String namaLengkap = resultSet.getString("NAMA_LENGKAP");
                    String username = resultSet.getString("USERNAME");
                    String password = resultSet.getString("PASSWORD");
                    String role = resultSet.getString("ROLE");
                    binding.idPelanggan.setText(id);
                    binding.namaLengkap.setText(namaLengkap);
                    binding.username.setText(username);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                System.out.println("Something When Wrong");
            }
        }).start();

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}