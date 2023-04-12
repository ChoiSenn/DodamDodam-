package com.cookandroid.dodamdodamexapplication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    // 파이어베이스 데이터베이스 연동
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    //DatabaseReference는 데이터베이스의 특정 위치로 연결하는 거라고 생각하면 된다.
    //현재 연결은 데이터베이스에만 딱 연결해놓고
    //키값(테이블 또는 속성)의 위치 까지는 들어가지는 않은 모습이다.
    private DatabaseReference databaseReference = database.getReference();

    TextView tv1, tv2, tv3;

    float SVTemp, SVLevel, SVTurb = 0;
    float SettingMaxTemp, SettingMinTemp, SettingMinLevel, SettingMinTurb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);

        // 센서 수치 설정 값 가져오기
        databaseReference.child("Setting").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SettingSensorValue settingSensorValue = dataSnapshot.getValue(SettingSensorValue.class);

                SettingMaxTemp = settingSensorValue.getSettingMaxTemp();
                SettingMinTemp = settingSensorValue.getSettingMinTemp();
                SettingMinLevel = settingSensorValue.getSettingMinLevel();
                SettingMinTurb = settingSensorValue.getSettingMinTurb();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });

        // 센서 값 읽어오기
        databaseReference.child("sensor").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SeosorValue seosorValue = dataSnapshot.getValue(SeosorValue.class);

                //각각의 값 받아오기. 함수들은 SeosorValue.class에서 지정한것
                SVTemp = seosorValue.gettemperature();
                SVLevel = seosorValue.getwaterLevel();
                SVTurb = seosorValue.getturbidity();

                //텍스트뷰에 받아온 문자열 대입하기
                tv1.setText(Float.toString(SVTemp));
                tv2.setText(Float.toString(SVLevel));
                tv3.setText(Float.toString(SVTurb));

                // 센서 값이 일정 수치 밖으로 나가면 경고창
                if(SVTemp > SettingMaxTemp){
                    showSensorAlert("수온이", SettingMaxTemp, "높습니다!");
                } else if(SVTemp < SettingMinTemp) {
                    showSensorAlert("수온이", SettingMinTemp, "낮습니다!");
                } else if(SVLevel < SettingMinLevel){
                    showSensorAlert("수위가", SettingMinLevel, "낮습니다!");
                } else if(SVTurb < SettingMinTurb){
                    showSensorAlert("수질이", SettingMinTurb, "낮습니다!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });
    }

    void showSensorAlert(String sensor, float value, String how) {  // 센서값이 일정 수치를 벗어나면 경고창
        AlertDialog.Builder msgBuilder = new AlertDialog.Builder(MainActivity.this)
                .setTitle("수조 관리 요망")
                .setMessage("수조의 " + sensor + " " + value + "보다 " + how)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        AlertDialog msgDlg = msgBuilder.create();
        msgDlg.show();
    }
}