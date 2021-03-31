package com.example.basicviewer.model;

public class PasienStatus {
    public boolean pasien_recordStat = false;
    public String pasien_id = "";
    public String pasien_nama = "";

    public boolean isPasien_recordStat() {
        return pasien_recordStat;
    }

    public void setPasien_recordStat(boolean pasien_recordStat) {
        this.pasien_recordStat = pasien_recordStat;
    }

    public String getPasien_id() {
        return pasien_id;
    }

    public void setPasien_id(String pasien_id) {
        this.pasien_id = pasien_id;
    }

    public String getPasien_nama() {
        return pasien_nama;
    }

    public void setPasien_nama(String pasien_nama) {
        this.pasien_nama = pasien_nama;
    }
}
