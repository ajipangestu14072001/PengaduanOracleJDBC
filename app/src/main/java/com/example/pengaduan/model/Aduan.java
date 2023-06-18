package com.example.pengaduan.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Blob;

public class Aduan implements Parcelable {
    private String id;
    private String jenisAduan;
    private String idPelanggan;
    private String namaLengkap;
    private String tanggal;
    private String titikLokasi;
    private String kondisiDevice;
    private String deskripsi;
    private String status;
    private String tanggapan;

    private byte[] rawImage;

    public Aduan() {
    }

    public Aduan(String id, String jenisAduan, String idPelanggan, String namaLengkap, String tanggal, String titikLokasi, String kondisiDevice, String deskripsi, String status, String tanggapan,  byte[] rawImage) {
        this.id = id;
        this.jenisAduan = jenisAduan;
        this.idPelanggan = idPelanggan;
        this.namaLengkap = namaLengkap;
        this.tanggal = tanggal;
        this.titikLokasi = titikLokasi;
        this.kondisiDevice = kondisiDevice;
        this.deskripsi = deskripsi;
        this.status = status;
        this.tanggapan = tanggapan;
        this.rawImage = rawImage;
    }

    protected Aduan(Parcel in) {
        id = in.readString();
        jenisAduan = in.readString();
        idPelanggan = in.readString();
        namaLengkap = in.readString();
        tanggal = in.readString();
        titikLokasi = in.readString();
        kondisiDevice = in.readString();
        deskripsi = in.readString();
        status = in.readString();
        tanggapan = in.readString();
        rawImage = in.createByteArray();
    }

    public static final Creator<Aduan> CREATOR = new Creator<Aduan>() {
        @Override
        public Aduan createFromParcel(Parcel in) {
            return new Aduan(in);
        }

        @Override
        public Aduan[] newArray(int size) {
            return new Aduan[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJenisAduan() {
        return jenisAduan;
    }

    public void setJenisAduan(String jenisAduan) {
        this.jenisAduan = jenisAduan;
    }

    public String getIdPelanggan() {
        return idPelanggan;
    }

    public void setIdPelanggan(String idPelanggan) {
        this.idPelanggan = idPelanggan;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getTitikLokasi() {
        return titikLokasi;
    }

    public void setTitikLokasi(String titikLokasi) {
        this.titikLokasi = titikLokasi;
    }

    public String getKondisiDevice() {
        return kondisiDevice;
    }

    public void setKondisiDevice(String kondisiDevice) {
        this.kondisiDevice = kondisiDevice;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTanggapan() {
        return tanggapan;
    }

    public void setTanggapan(String tanggapan) {
        this.tanggapan = tanggapan;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void setRawImage(byte[] rawImage) {
        this.rawImage = rawImage;
    }

    public byte[] getRawImage() {
        return rawImage;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(jenisAduan);
        dest.writeString(idPelanggan);
        dest.writeString(namaLengkap);
        dest.writeString(tanggal);
        dest.writeString(titikLokasi);
        dest.writeString(kondisiDevice);
        dest.writeString(deskripsi);
        dest.writeString(status);
        dest.writeString(tanggapan);
        dest.writeByteArray(rawImage);
    }
}
