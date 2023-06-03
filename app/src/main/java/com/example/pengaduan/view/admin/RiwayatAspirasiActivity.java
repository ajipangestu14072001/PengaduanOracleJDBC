package com.example.pengaduan.view.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.example.pengaduan.R;
import com.example.pengaduan.adapter.AdapterAspirasi;
import com.example.pengaduan.databinding.ActivityAspirasiBinding;
import com.example.pengaduan.databinding.ActivityRiwayatAspirasiBinding;
import com.example.pengaduan.helper.SharedPrefManager;
import com.example.pengaduan.model.Aspirasi;
import com.example.pengaduan.service.OracleConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class RiwayatAspirasiActivity extends AppCompatActivity {

    private ActivityRiwayatAspirasiBinding binding;
    private AdapterAspirasi adapter;
    private ArrayList<Aspirasi> aspirasiList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRiwayatAspirasiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        aspirasiList = new ArrayList<>();

        adapter = new AdapterAspirasi(aspirasiList);
        recyclerView.setAdapter(adapter);

        new Thread(this::fetchDataFromDatabase).start();

    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchDataFromDatabase() {
        try {
            Connection connection = OracleConnection.getConnection();
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM ASPIRASI";
            ResultSet resultSet = statement.executeQuery(query);
            aspirasiList.clear();
            while (resultSet.next()) {
                Aspirasi aspirasi = new Aspirasi();
                aspirasi.setId(resultSet.getString("ID"));
                aspirasi.setJenisAspirasi(resultSet.getString("JENIS_ASPIRASI"));
                aspirasi.setDeskripsi(resultSet.getString("DESKRIPSI"));
                aspirasi.setNamaPengirim(resultSet.getString("NAMA_PENGIRIM"));

                aspirasiList.add(aspirasi);
            }

            runOnUiThread(() -> adapter.notifyDataSetChanged());

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}