package com.example.pengaduan.view.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.example.pengaduan.R;
import com.example.pengaduan.adapter.AdapterPelanggan;
import com.example.pengaduan.databinding.ActivityPelangganBinding;
import com.example.pengaduan.model.User;
import com.example.pengaduan.service.OracleConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PelangganActivity extends AppCompatActivity {
    private AdapterPelanggan adapter;
    private ArrayList<User> accountList;
    private ActivityPelangganBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPelangganBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        accountList = new ArrayList<>();
        adapter = new AdapterPelanggan(accountList);
        binding.recyclerView.setAdapter(adapter);

        adapter.setOnItemLongClickListener(new AdapterPelanggan.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int position) {
                showDeleteDialog(position);
            }
        });

        getDataFromAccountTable();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getDataFromAccountTable() {
        new Thread(() -> {
            Connection connection;
            PreparedStatement statement;
            ResultSet resultSet;

            try {
                connection = OracleConnection.getConnection();

                String query = "SELECT ID, NAMA_LENGKAP, USERNAME, PASSWORD, ROLE FROM ACCOUNT WHERE ROLE = 'USER'";
                statement = connection.prepareStatement(query);
                resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    String id = resultSet.getString("ID");
                    String namaLengkap = resultSet.getString("NAMA_LENGKAP");
                    String username = resultSet.getString("USERNAME");
                    String password = resultSet.getString("PASSWORD");
                    String role = resultSet.getString("ROLE");

                    User user = new User(id, namaLengkap, username, password, role);
                    accountList.add(user);
                }

                runOnUiThread(() -> adapter.notifyDataSetChanged());

                resultSet.close();
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();
    }
    private void showDeleteDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hapus Data");
        builder.setMessage("Apakah Anda yakin ingin menghapus data ini?");
        builder.setPositiveButton("Ya", (dialogInterface, i) -> {
            deleteDataFromAccountTable(position);
        });
        builder.setNegativeButton("Tidak", null);
        builder.create().show();
    }

    private void deleteDataFromAccountTable(int position) {
        new Thread(() -> {
            Connection connection;
            PreparedStatement statement;

            try {
                connection = OracleConnection.getConnection();

                String idToDelete = accountList.get(position).getId();
                String query = "DELETE FROM ACCOUNT WHERE ID = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1, idToDelete);
                int rowsDeleted = statement.executeUpdate();

                if (rowsDeleted > 0) {
                    runOnUiThread(() -> {
                        accountList.remove(position);
                        adapter.notifyItemRemoved(position);
                        Toast.makeText(PelangganActivity.this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(PelangganActivity.this, "Gagal menghapus data", Toast.LENGTH_SHORT).show());
                }

                statement.close();
                connection.close();
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