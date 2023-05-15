package com.cookandroid.dodamdodamexapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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

    //이메일 비밀번호 로그인 모듈 변수
    private FirebaseAuth mAuth;
    //현재 로그인 된 유저 정보를 담을 변수
    public FirebaseUser currentUser;
    public final static UserValue userClass = new UserValue();

    private TextView mTextMessage;

    TextView tv1, tv2, tv3;
    Button btnLogout, btnSetting, btnBoard, btnMap, btnChatbot;

    float SVTemp, SVLevel, SVTurb = 0;
    float SettingMaxTemp, SettingMinTemp, SettingMinLevel, SettingMinTurb;
    boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);

        btnLogout = findViewById(R.id.logout);
        btnSetting = findViewById(R.id.btnSetting);
        btnBoard = findViewById(R.id.btnBoard);
        btnMap = findViewById(R.id.btnMap);
        btnChatbot = findViewById(R.id.btnChatbot);

        mTextMessage = findViewById(R.id.mTextMessage);

        // 로그인 안 되어있다면 메인으로
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        Log.v("d", String.valueOf(currentUser));
        if(currentUser == null){
            Intent intent = new Intent(MainActivity.this, FirstActivity.class);

            signOut();

            startActivity(intent);
        }

        // 첫 작동 시, 데이터 세팅
        if (flag){
            DataSet();
            Log.e("t", "DataSet 실행 !");
        }

        userClass.setName(currentUser.getDisplayName()); // 전역으로 이 값을 사용하기 위해서 세팅을 해줍니다

        // 회언 정보 가져오기
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                UserValue user = dataSnapshot.child("user").child(currentUser.getUid()).getValue(UserValue.class); // 이는 해당 데이터를 바로 클래스 형태로 넣는 방법입니다 이때 getter는 필수 입니다

                userClass.setName(user.getName()); // 전역으로 이 값을 사용하기 위해서 세팅을 해줍니다
                userClass.setPoint(user.getPoint());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mTextMessage.setText(currentUser.getDisplayName() + "님, 환영합니다! (" + currentUser.getEmail() + ")");

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

        btnLogout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                signOut();
            }
        });

        btnSetting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
            }
        });

        btnBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, BoardActivity.class));
            }
        });

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MapActivity.class));
            }
        });

        btnChatbot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // startActivity(new Intent(MainActivity.this, ChatbotActivity.class));
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

    // 로그아웃 버튼
    private void signOut() {
        FirebaseAuth.getInstance().signOut();

        startActivity(new Intent(MainActivity.this, FirstActivity.class));
        finish();
    }

    //최초 로그인시 DB 값 세팅
    public void DataSet(){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser(); // 이는 현재 로그인한 사람의 정보를 가지고 옵니다

        final String uid = user.getUid(); // uid 는 계정 하나당 하나의 값을 가지게 됩니다 그래서 이것으로 분간을 하겠습니다
        String nickname = user.getDisplayName();
        Log.e("t", "id : " + user.getUid());
        Log.e("t", "name : " + user.getDisplayName());
        Log.e("t", "email : " + user.getEmail());

        databaseReference.addValueEventListener(new ValueEventListener() {
            // 위의 함수는 전부 비동기 처리가 이루어지기 때문에 전부 이벤트가 발생하는 시점에서 이 함수를 써주셔야 합니다 앞으로 지겹게 볼 예정입니다
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //데이터 변경이 감지가 되면 이 함수가 자동으로 콜백이 됩니다 이때 dataSnapashot 는 값을 내려 받을떄 사용함으로 지금은 쓰지 않습니다

                databaseReference.child("user").child(uid).child("name").setValue(nickname);
                //databaseReference.child("user").child(uid).child("point").setValue(0);
                //RealTimeDB는 기본적으로 parent , child , value 값으로 이루어져 있습니다 지금은 최초로 로그인한 사람의
                //색인을 만들고자 지금과 같은 작업을 하는 중입니다 즉 처음 들어오는 사람에게 DB자리를 내준다고 생각하시면됩니다

                UserValue user = dataSnapshot.child("user").child(uid).getValue(UserValue.class); // 이는 해당 데이터를 바로 클래스 형태로 넣는 방법입니다 이때 getter는 필수 입니다
                // nickName.setText(user.getName()); //그래서 그 값을 가져오는 것이고

                //userClass.setName(user.getName()); // 전역으로 이 값을 사용하기 위해서 세팅을 해줍니다
                //userClass.setFirstLunch(user.getFirstLunch());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // RealTimeDB와 통신 에러 등등 데이터를 정상적으로 받지 못할때 콜백함수로서 이곳으로 들어옵니다
                // 저는 작성하지 않았습니다

            }
        });

        flag = false;
    }
}