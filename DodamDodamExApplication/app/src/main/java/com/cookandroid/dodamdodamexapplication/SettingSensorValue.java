package com.cookandroid.dodamdodamexapplication;

public class SettingSensorValue {

    //변수 선언
    private float SettingMaxTemp;
    private float SettingMinTemp;
    private float SettingMinLevel;
    private float SettingMinTurb;

    public SettingSensorValue(){}

    public float getSettingMaxTemp() {
        return SettingMaxTemp;
    }

    public void setSettingMaxTemp(float SettingMaxTemp) {
        this.SettingMaxTemp = SettingMaxTemp;
    }

    public float getSettingMinTemp() {
        return SettingMinTemp;
    }

    public void setSettingMinTemp(float SettingMinTemp) {
        this.SettingMinTemp = SettingMinTemp;
    }

    public float getSettingMinLevel() {
        return SettingMinLevel;
    }

    public void setSettingMinLevel(float SettingMinLevel) {
        this.SettingMinLevel = SettingMinLevel;
    }

    public float getSettingMinTurb() {
        return SettingMinTurb;
    }

    public void setSettingMinTurb(float SettingMinTurb) {
        this.SettingMinTurb = SettingMinTurb;
    }

    public SettingSensorValue(float SettingMaxTemp, float SettingMinTemp, float SettingMinLevel, float SettingMinTurb) {
        this.SettingMaxTemp = SettingMaxTemp;
        this.SettingMinTemp = SettingMinTemp;
        this.SettingMinLevel = SettingMinLevel;
        this.SettingMinTurb = SettingMinTurb;
    }
}
