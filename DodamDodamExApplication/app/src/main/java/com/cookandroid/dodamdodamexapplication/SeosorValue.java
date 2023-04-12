package com.cookandroid.dodamdodamexapplication;

public class SeosorValue {

    //변수 선언
    public float temperature;
    public float waterLevel;
    public float turbidity;

    public SeosorValue(){}

    //여기서부터 get,set 함수를 사용하는데 이부분을 통해 값을 가져옴
    public float gettemperature() {
        return temperature;
    }

    public void settemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getwaterLevel() {
        return waterLevel;
    }

    public void setwaterLevel(float waterLevel) {
        this.waterLevel = waterLevel;
    }

    public float getturbidity() {
        return turbidity;
    }

    public void setturbidity(float turbidity) {
        this.turbidity = turbidity;
    }

    // 그룹을 생성할때 사용하는 부분
    public SeosorValue(float temperature, float waterLevel, float turbidity) {
        this.temperature = temperature;
        this.waterLevel = waterLevel;
        this.turbidity = turbidity;
    }
}
