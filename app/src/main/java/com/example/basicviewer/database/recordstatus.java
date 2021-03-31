package com.example.basicviewer.database;

public class recordstatus {

    /**
     * recordStat : false
     */

    private boolean recordStat;
    /**
     * _id : 5fd2f6f4f8368e24388849c3
     * id_pasien : 5fd2f6f4f8368e24388849c2
     * __v : 0
     */

    private String _id;
    private String id_pasien;
    private int __v;

    public boolean isRecordStat() {
        return recordStat;
    }

    public void setRecordStat(boolean recordStat) {
        this.recordStat = recordStat;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getId_pasien() {
        return id_pasien;
    }

    public void setId_pasien(String id_pasien) {
        this.id_pasien = id_pasien;
    }

    public int get__v() {
        return __v;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }
}
