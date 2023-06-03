package com.example.pengaduan.model;

public class Aspirasi {
    private String id;
    private String jenisAspirasi;
    private String deskripsi;

    private String namaPengirim;

    public Aspirasi() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJenisAspirasi() {
        return jenisAspirasi;
    }

    public void setJenisAspirasi(String jenisAspirasi) {
        this.jenisAspirasi = jenisAspirasi;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public void setNamaPengirim(String namaPengirim) {
        this.namaPengirim = namaPengirim;
    }

    public String getNamaPengirim() {
        return namaPengirim;
    }
}
