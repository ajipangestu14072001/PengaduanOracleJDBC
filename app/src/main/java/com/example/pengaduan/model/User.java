package com.example.pengaduan.model;

public class User {
    private String id;
    private String username;
    private String namaLengkap;
    private String password;
    private String role;

    private String realId;


    public User() {

    }

    public User(String id, String username, String namaLengkap, String password, String role, String realId) {
        this.id = id;
        this.username = username;
        this.namaLengkap = namaLengkap;
        this.password = password;
        this.role = role;
        this.realId = realId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRealId(String realId) {
        this.realId = realId;
    }
    public String getRealId() {
        return realId;
    }
}
