package com.cookandroid.dodamdodamexapplication;

public class UserValue {

    public String name;
    public String firstLunch;

    public UserValue(){

    }

    public UserValue(String name , String  firstLunch){
        this.name = name;
        this.firstLunch = firstLunch;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstLunch() {
        return firstLunch;
    }

    public void setFirstLunch(String firstLunch) {
        this.firstLunch = firstLunch;
    }
}
