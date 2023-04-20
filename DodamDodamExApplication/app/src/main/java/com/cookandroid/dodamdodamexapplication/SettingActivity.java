package com.cookandroid.dodamdodamexapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingActivity extends AppCompatActivity {

    // 파이어베이스 데이터베이스 연동
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();

    EditText minTemp, maxTemp, minTurb, minLevel;
    Button btnSet;

    int maxTempV = 0;
    int minTempV = 0;
    int minLevelV = 0;
    int minTurbV = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        maxTemp = (EditText) findViewById(R.id.maxTemp);
        minTemp = (EditText) findViewById(R.id.minTemp);
        minLevel = (EditText) findViewById(R.id.minLevel);
        minTurb = (EditText) findViewById(R.id.minTurb);

        databaseReference.child("Setting").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SettingSensorValue settingSensorValue = dataSnapshot.getValue(SettingSensorValue.class);

                //maxTemp.setText((int) settingSensorValue.getSettingMaxTemp());
                //minTemp.setText((int) settingSensorValue.getSettingMinTemp());
                //minLevel.setText((int) settingSensorValue.getSettingMinLevel());
                //minTurb.setText((int) settingSensorValue.getSettingMinTurb());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });

        btnSet = (Button) findViewById(R.id.btnSet);

        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    maxTempV = Integer.parseInt(maxTemp.getText().toString());
                    minTempV = Integer.parseInt(minTemp.getText().toString());
                    minLevelV = Integer.parseInt(minLevel.getText().toString());
                    minTurbV = Integer.parseInt(minTurb.getText().toString());
                    Log.v("d", maxTempV + " " + minTempV + " ");
                } catch (NumberFormatException e){
                    Log.v("d", "!!!!!!!!!!!!!NumberFormatException 오류!!!!!!!!!!!!!");
                } catch (Exception e){
                    Log.v("d", "!!!!!!!!!!!!!Exception 오류!!!!!!!!!!!!!");
                }

                databaseReference.child("Setting").child("SettingMaxTemp").setValue(maxTempV);
                databaseReference.child("Setting").child("SettingMinTemp").setValue(minTempV);
                databaseReference.child("Setting").child("SettingMinLevel").setValue(minLevelV);
                databaseReference.child("Setting").child("SettingMinTurb").setValue(minTurbV);

                Toast.makeText(SettingActivity.this,"성공적으로 설정을 저장하였습니다!" ,Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(SettingActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}