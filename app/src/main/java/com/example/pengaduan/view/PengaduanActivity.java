package com.example.pengaduan.view;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pengaduan.R;
import com.example.pengaduan.databinding.ActivityPengaduanBinding;
import com.example.pengaduan.helper.SharedPrefManager;
import com.example.pengaduan.model.Aduan;
import com.example.pengaduan.model.User;
import com.example.pengaduan.service.OracleConnection;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class PengaduanActivity extends AppCompatActivity {
    private ActivityPengaduanBinding binding;
    private Uri imageUri;

    private String ID = "";

    private String nama = "";
    private String id = "";
    private String jenisAduan = "";
    private String namaLengkap = "";
    private String tanggal = "";
    private String titikLokasi = "";
    private String kondisiDevice = "";
    private String deskripsi = "";
    private String status = "";
    private String idPelanggan = "";
    private String tanggapan = "";
    private String pathPhoto = "";
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private final String storagePath = "PotoAduan";
    private ProgressDialog progressDialog;
    private final String[] jenisPengaduan = {"Internet Mati", "Internet Lambat"};
    public static final String Database_Path = "PengaduanInternet";
    private static final int RESULT_LOAD_IMAGE = 123;
    private static final int IMAGE_CAPTURE_CODE = 654;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPengaduanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd/MM/yyyy HH:mm", Locale.getDefault());
        String tanggalSekarang = dateFormat.format(new Date());
        ArrayAdapter<String> kategori = new ArrayAdapter<>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, jenisPengaduan);
        binding.kategoriAduan.setAdapter(kategori);
        binding.date.setText(tanggalSekarang);
        binding.runningText.setSelected(true);
        nama = SharedPrefManager.getNamaLengkap(this);
        ID = SharedPrefManager.getIdPelanggan(this);
        binding.namaLengkap.setText(nama);
        binding.idPelanggan.setText(ID);
        setSupportActionBar(binding.toolbar);

        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permission, 111);
        }

        binding.submit.setOnClickListener(view -> uploadImageFileToFirebaseStorage());

        binding.action2.setOnClickListener(view -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
        });

        binding.action3.setOnClickListener(view -> openCamera());
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CAPTURE_CODE && resultCode == RESULT_OK) {
            Glide.with(this).load(imageUri).into(binding.imgview);
        }
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(binding.imgview);
        }
    }

    private String getFileExtension(Uri uri) {
        String extension = null;
        if (uri != null && getContentResolver().getType(uri) != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(getContentResolver().getType(uri));
        }
        return extension;
    }

    private void uploadImageFileToFirebaseStorage() {

        progressDialog.setTitle("Data Aduan Sedang di Proses...");
        progressDialog.setMessage("Mengirim Aduan");
        progressDialog.setIndeterminate(false);
        progressDialog.show();

        if (imageUri != null) {
            StorageReference storageReference2 = storageReference.child(storagePath + System.currentTimeMillis() + "." + getFileExtension(imageUri));
            storageReference2.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                progressDialog.dismiss();

                Task<Uri> downloadUrlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!downloadUrlTask.isSuccessful()) ;
                Uri downloadUrl = downloadUrlTask.getResult();
                new Thread(() -> {
                    id = generateID();
                    jenisAduan = binding.kategoriAduan.getSelectedItem().toString().trim();
                    namaLengkap = binding.namaLengkap.getText().toString().trim();
                    tanggal = binding.date.getText().toString().trim();
                    titikLokasi = binding.location.getText().toString().trim();
                    kondisiDevice = binding.deviceCondition.getText().toString().trim();
                    deskripsi = binding.desc.getText().toString().trim();
                    status = "Menunggu";
                    idPelanggan = binding.idPelanggan.getText().toString().trim();
                    tanggapan = "";
                    pathPhoto = Objects.requireNonNull(downloadUrl).toString();
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, length);
                        }

                        byte[] imageBytes = outputStream.toByteArray();

                        inputStream.close();
                        outputStream.close();

                        Aduan aduan = new Aduan();
                        aduan.setId(id);
                        aduan.setJenisAduan(jenisAduan);
                        aduan.setNamaLengkap(namaLengkap);
                        aduan.setTanggal(tanggal);
                        aduan.setTitikLokasi(titikLokasi);
                        aduan.setKondisiDevice(kondisiDevice);
                        aduan.setDeskripsi(deskripsi);
                        aduan.setPathPhoto(pathPhoto);
                        aduan.setStatus(status);
                        aduan.setTanggapan(tanggapan);
                        aduan.setIdPelanggan(idPelanggan);
                        aduan.setRawImage(imageBytes);
                        aduanPelanggan(aduan);

                        aduanPelanggan(aduan);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
                Aduan imageUploadInfo = new Aduan(
                        generateID(),
                        binding.kategoriAduan.getSelectedItem().toString().trim(),
                        binding.idPelanggan.getText().toString().trim(),
                        binding.namaLengkap.getText().toString().trim(),
                        binding.date.getText().toString().trim(),
                        binding.location.getText().toString().trim(),
                        binding.deviceCondition.getText().toString().trim(),
                        binding.desc.getText().toString().trim(),
                        Objects.requireNonNull(downloadUrl).toString(),
                        "Menunggu",
                        ""
                );
                String key = databaseReference.push().getKey();
                DatabaseReference databaseReference = PengaduanActivity.this.databaseReference;
                databaseReference.child("ListAduan" + key).setValue(imageUploadInfo)
                        .addOnSuccessListener(aVoid -> {
                            progressDialog.dismiss();
                            Toast.makeText(PengaduanActivity.this, "Pengaduan Behasil di Kirim", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            Toast.makeText(PengaduanActivity.this, "Terjadi kesalahan saat mengirim pengaduan", Toast.LENGTH_SHORT).show();
                        });
            }).addOnFailureListener(exc -> {
                progressDialog.dismiss();
                Toast.makeText(PengaduanActivity.this, exc.getMessage(), Toast.LENGTH_SHORT).show();
            }).addOnProgressListener(snapshot -> progressDialog.setTitle("Data is Uploading..."));
        } else {
            new Thread(() -> {
                id = generateID();
                jenisAduan = binding.kategoriAduan.getSelectedItem().toString().trim();
                namaLengkap = binding.namaLengkap.getText().toString().trim();
                tanggal = binding.date.getText().toString().trim();
                titikLokasi = binding.location.getText().toString().trim();
                kondisiDevice = binding.deviceCondition.getText().toString().trim();
                deskripsi = binding.desc.getText().toString().trim();
                status = "Menunggu";
                idPelanggan = binding.idPelanggan.getText().toString().trim();
                tanggapan = "";
                pathPhoto = "";

                Aduan aduan = new Aduan();
                aduan.setId(id);
                aduan.setJenisAduan(jenisAduan);
                aduan.setNamaLengkap(namaLengkap);
                aduan.setTanggal(tanggal);
                aduan.setTitikLokasi(titikLokasi);
                aduan.setKondisiDevice(kondisiDevice);
                aduan.setDeskripsi(deskripsi);
                aduan.setPathPhoto(pathPhoto);
                aduan.setStatus(status);
                aduan.setTanggapan(tanggapan);
                aduan.setIdPelanggan(idPelanggan);
                aduanPelanggan(aduan);

                Aduan imageUploadInfo = new Aduan(
                        generateID(),
                        binding.kategoriAduan.getSelectedItem().toString().trim(),
                        binding.idPelanggan.getText().toString().trim(),
                        binding.namaLengkap.getText().toString().trim(),
                        binding.date.getText().toString().trim(),
                        binding.location.getText().toString().trim(),
                        binding.deviceCondition.getText().toString().trim(),
                        binding.desc.getText().toString().trim(),
                        "",
                        "Menunggu",
                        ""
                );
                String key = databaseReference.push().getKey();
                DatabaseReference databaseReference = PengaduanActivity.this.databaseReference;
                databaseReference.child("ListAduan" + key).setValue(imageUploadInfo)
                        .addOnSuccessListener(aVoid -> {
                            progressDialog.dismiss();
                            Toast.makeText(PengaduanActivity.this, "Pengaduan Behasil di Kirim", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            Toast.makeText(PengaduanActivity.this, "Terjadi kesalahan saat mengirim pengaduan", Toast.LENGTH_SHORT).show();
                        });
            }).start();
        }
    }


    private void aduanPelanggan(Aduan aduan) {
        Connection connection;
        String sql = "INSERT INTO PENGADUAN (ID, JENIS_ADUAN, NAMA_LENGKAP, TANGGAL, TITIK_LOKASI, KONDISI_DEVICE, DESKRIPSI, PHOTO, STATUS, ID_PELANGGAN, TANGGAPAN, RAW_IMAGE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            connection = OracleConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, aduan.getId());
            statement.setString(2, aduan.getJenisAduan());
            statement.setString(3, aduan.getNamaLengkap());
            statement.setString(4, aduan.getTanggal());
            statement.setString(5, aduan.getTitikLokasi());
            statement.setString(6, aduan.getKondisiDevice());
            statement.setString(7, aduan.getDeskripsi());
            statement.setString(8, aduan.getPathPhoto());
            statement.setString(9, aduan.getStatus());
            statement.setString(10, aduan.getIdPelanggan());
            statement.setString(11, aduan.getTanggapan());
            if (aduan.getRawImage() != null) {
                InputStream inputStream = new ByteArrayInputStream(aduan.getRawImage());
                statement.setBinaryStream(12, inputStream);
            } else {
                statement.setNull(12, Types.BINARY);
            }

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                runOnUiThread(() -> {
                    Toast.makeText(PengaduanActivity.this, "Pengaduan Behasil di Kirim", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PengaduanActivity.this, RiwayatActivity.class);
                    startActivity(intent);
                    finish();
                });
            } else {
                runOnUiThread(() -> Toast.makeText(PengaduanActivity.this, "Pengaduan gagal", Toast.LENGTH_SHORT).show());
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