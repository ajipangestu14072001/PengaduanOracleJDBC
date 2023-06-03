package com.example.pengaduan.model;

public class RealId {
    private String id;
    private String realId;
    private String namaKaryawan;
    private String idPelanggan;

    public RealId() {
    }

    public RealId(String id, String realId, String namaKaryawan, String idPelanggan) {
        this.id = id;
        this.realId = realId;
        this.namaKaryawan = namaKaryawan;
        this.idPelanggan = idPelanggan;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRealId() {
        return realId;
    }

    public void setRealId(String realId) {
        this.realId = realId;
    }

    public String getNamaKaryawan() {
        return namaKaryawan;
    }

    public void setNamaKaryawan(String namaKaryawan) {
        this.namaKaryawan = namaKaryawan;
    }

    public String getIdPelanggan() {
        return idPelanggan;
    }

    public void setIdPelanggan(String idPelanggan) {
        this.idPelanggan = idPelanggan;
    }
}
