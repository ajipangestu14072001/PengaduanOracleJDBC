package com.example.pengaduan.view.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

        adapter.setOnItemDeleteClickListener(new AdapterAspirasi.OnItemDeleteClickListener() {
            @Override
            public void onItemDeleteClick(int position) {
                showDeleteConfirmationDialog(position);
            }
        });

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

    private void showDeleteConfirmationDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hapus Data");
        builder.setMessage("Apakah Anda yakin ingin menghapus data ini?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteDataFromDatabase(position);
            }
        });
        builder.setNegativeButton("Tidak", null);
        builder.show();
    }

    private void deleteDataFromDatabase(int position) {
        new Thread(() -> {
            Aspirasi aspirasi = aspirasiList.get(position);
        String id = aspirasi.getId();

        try {
            Connection connection = OracleConnection.getConnection();
            Statement statement = connection.createStatement();
            String query = "DELETE FROM ASPIRASI WHERE ID = '" + id + "'";
            statement.executeUpdate(query);

            statement.close();
            connection.close();

            runOnUiThread(() -> {
                aspirasiList.remove(position);
                adapter.notifyItemRemoved(position);
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
        }).start();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}