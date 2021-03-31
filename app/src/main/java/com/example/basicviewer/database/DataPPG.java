package com.example.basicviewer.database;

import java.util.List;

public class DataPPG {

    /**
     * dataPPG : [77920,77791,77646,77483,77427,77417,77438,77547,77623,77600,77546,77603,77602,77723,77691,77859,77894,77822,77857,77881,77873,77784,77823,77636,77368,77308,77323,77290,77394,77591,77493,77494,77446,77560,77513,77600,77678,77598,77632,77644,77875,77806,77835,77735,77643,77429,77380,77428,77419,77583,77600,77723,77558,77551,77633,77606,77715,77756,77887,77745,77858,77868,77954,77905,77958,77805,77633,77620,77499,77627,77541,77627,77640,77674,77628,77623,77766,77711,77880,77926,78027,77943,78129,78197,78214,78204,78089,78056,77744,77780,77778,77783,77783,77876,77838,77675,77795,77831,77826,77869,78088,78017,78074,78185,78183,78279,78230,78201,78002,77817,77684,77757,77811,77781,77862,77890,77818,77809,77939,77861,77891,77998,78006,78072,78105,78206,78088,78023]
     * _id : 5fa704bd2065b900046ef658
     * id_rompi : 001
     * id_sensor : PPG01
     * id_pasien : 000003
     * createdAt : 2020-11-07T20:34:05.902Z
     * updatedAt : 2020-11-07T20:34:05.902Z
     * __v : 0
     */

    private String _id;
    private String id_rompi;
    private String id_sensor;
    private String id_pasien;
    private String createdAt;
    private String updatedAt;
    private int __v;
    private List<Integer> dataPPG;

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

    public List<Integer> getDataPPG() {
        return dataPPG;
    }

    public void setDataPPG(List<Integer> dataPPG) {
        this.dataPPG = dataPPG;
    }
}
