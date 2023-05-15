package com.cookandroid.dodamdodamexapplication;

public class UserValue {

    public String name;
    public int point;

    public UserValue(){

    }

    public UserValue(String name , int  point){
        this.name = name;
        this.point = point;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }
}
