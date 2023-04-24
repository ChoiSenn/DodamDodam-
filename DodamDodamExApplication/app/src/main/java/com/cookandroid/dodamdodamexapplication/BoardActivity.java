package com.cookandroid.dodamdodamexapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BoardActivity extends AppCompatActivity {

    DatabaseReference mDatabase;  // 레퍼런스 정의

    //이메일 비밀번호 로그인 모듈 변수
    private FirebaseAuth mAuth;

    Button btnWrite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board);

        mDatabase = FirebaseDatabase.getInstance().getReference();  // 레퍼런스 주소 값

        btnWrite = findViewById(R.id.btnWrite);

        // 글쓰기 버튼
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BoardActivity.this, WriteActivity.class);
                startActivity(intent);
            }
        });
    }


}
