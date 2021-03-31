package com.example.basicviewer.database;

import java.util.List;

public class DataAccelerometer {

    /**
     * dataAccelerometer_X : [3001,3001,3001]
     * dataAccelerometer_Y : [3001,3001,3001]
     * dataAccelerometer_Z : [3001,3001,3001]
     * _id : 5f927f9f23371e000402c608
     * id_rompi : 001
     * id_sensor : Acce01
     * id_pasien : 000003
     * createdAt : 2020-10-23T07:00:47.119Z
     * updatedAt : 2020-10-23T07:00:47.119Z
     * __v : 0
     */

    private String _id;
    private String id_rompi;
    private String id_sensor;
    private String id_pasien;
    private String createdAt;
    private String updatedAt;
    private int __v;
    private List<Integer> dataAccelerometer_X;
    private List<Integer> dataAccelerometer_Y;
    private List<Integer> dataAccelerometer_Z;

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

    public List<Integer> getDataAccelerometer_X() {
        return dataAccelerometer_X;
    }

    public void setDataAccelerometer_X(List<Integer> dataAccelerometer_X) {
        this.dataAccelerometer_X = dataAccelerometer_X;
    }

    public List<Integer> getDataAccelerometer_Y() {
        return dataAccelerometer_Y;
    }

    public void setDataAccelerometer_Y(List<Integer> dataAccelerometer_Y) {
        this.dataAccelerometer_Y = dataAccelerometer_Y;
    }

    public List<Integer> getDataAccelerometer_Z() {
        return dataAccelerometer_Z;
    }

    public void setDataAccelerometer_Z(List<Integer> dataAccelerometer_Z) {
        this.dataAccelerometer_Z = dataAccelerometer_Z;
    }
}
