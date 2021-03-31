package com.example.basicviewer.database;

import java.util.List;

public class DataEKG {

    /**
     * dataEKG : [3001,3001,3001]
     * _id : 5f927fce23371e000402c609
     * id_rompi : 001
     * id_sensor : EKG01
     * id_pasien : 000003
     * createdAt : 2020-10-23T07:01:34.602Z
     * updatedAt : 2020-10-23T07:01:34.602Z
     * __v : 0
     */

    private String _id;
    private String id_rompi;
    private String id_sensor;
    private String id_pasien;
    private String createdAt;
    private String updatedAt;
    private int __v;
    private List<Integer> dataEKG;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getId_rompi() {
        return id_rompi;
    }

    public void setId_rompi(String id_rompi) {
        this.id_rompi = id_rompi;
    }

    public String getId_sensor() {
        return id_sensor;
    }

    public void setId_sensor(String id_sensor) {
        this.id_sensor = id_sensor;
    }

    public String getId_pasien() {
        return id_pasien;
    }

    public void setId_pasien(String id_pasien) {
        this.id_pasien = id_pasien;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int get__v() {
        return __v;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }

    public List<Integer> getDataEKG() {
        return dataEKG;
    }

    public void setDataEKG(List<Integer> dataEKG) {
        this.dataEKG = dataEKG;
    }
}
