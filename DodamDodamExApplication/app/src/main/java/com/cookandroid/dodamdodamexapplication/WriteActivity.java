package com.cookandroid.dodamdodamexapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class WriteActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    String uid;

    EditText write_title , write_content; // 제목 , 내용

    ArrayList<String> writeKey = null;
    ArrayList<String> writeValue = null;

    ArrayAdapter<String> adapter = null;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();
    //현재 로그인 된 유저 정보를 담을 변수
    public FirebaseUser currentUser;
    public final static UserValue userClass = new UserValue();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        uid = user.getUid();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

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

        write_title = (EditText) findViewById(R.id.editText_write_title);
        write_content = (EditText) findViewById(R.id.editText_writeContent);

        writeKey = new ArrayList<>();
        writeValue = new ArrayList<>();
    }

    // write.xml의 button onClick으로 처리
    public void board_write(View view){

        if( write_title.getText().toString().equals("")){
            Toast.makeText(this , "제목을 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if(write_content.getText().toString().equals("")){
            Toast.makeText(this , "내용을 입력하세요" , Toast.LENGTH_SHORT).show();
            return;
        }

        MainActivity mainPage = new MainActivity();
        UserValue user = mainPage.userClass; // 전역으로 쓰는 user에 저장된 값을 불러옴

        //날짜 포맷
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

        Date time = new Date();

        String today = dateFormat.format(time);
        String order_today = dateFormat2.format(time);

        BoardValue board= new BoardValue();
        board.setTitle(write_title.getText().toString());
        board.setContent(write_content.getText().toString());
        board.setUid(uid);
        board.setName(user.getName());
        board.setDate(today);
        board.setOrder_date(order_today);

        mDatabase.child("board").child(uid).push().setValue(board); //push 는 FireBase에서 제공하는 api 로 여러명이 동시에 클라이언트를 이용할때 어떤 값에 대해서 독립을 보장하는 프라이머리 key

        board_write_point(); // 랜덤 포인트 지급

        mDatabase.child("user").child(uid).child("point").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("t", "6 : " + (int) snapshot.getValue(Integer.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        startActivity(new Intent(WriteActivity.this, BoardActivity.class));
        finish();

    }

    // 게시글 작성 시, 랜덤한 포인트 지급
    public void board_write_point(){
        Random random = new Random();
        int point = random.nextInt(10) + 1;  // 랜덤 포인트

        Log.e("t", uid + "  " + userClass.getName() + "  " + userClass.getPoint());

        mDatabase.child("user").child(uid).child("point").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int allPoint = (int) snapshot.getValue(Integer.class);  //저장된 값을 숫자로 받아오고
                Log.e("t", "1 : " + allPoint + " + " + point);

                allPoint += point;  //숫자를 증가시켜서
                mDatabase.child("user").child(uid).child("point").setValue(allPoint);  //저장

                mDatabase.child("user").child(uid).child("point").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.e("t", "4 : " + (int) snapshot.getValue(Integer.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Toast.makeText(WriteActivity.this,"게시글 작성으로 포인트" + point + "점을 획득하였습니다!  총 " + allPoint + "점" ,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Log.e("MainActivity", String.valueOf(databaseError.toException()));
            }



        });

        FirebaseUser user = mAuth.getCurrentUser();
        Log.e("t", "id : " + user.getUid());
        Log.e("t", "name : " + user.getDisplayName());
        Log.e("t", "email : " + user.getEmail());

        mDatabase.child("user").child(uid).child("point").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("t", "5 : " + (int) snapshot.getValue(Integer.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}