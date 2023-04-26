package com.cookandroid.dodamdodamexapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WriteActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    String uid;

    EditText write_title , write_content; // 제목 , 내용

    ArrayList<String> writeKey = null;
    ArrayList<String> writeValue = null;

    ArrayAdapter<String> adapter = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        uid = user.getUid();

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

        Intent i = new Intent(WriteActivity.this , BoardActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}