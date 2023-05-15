package com.cookandroid.dodamdodamexapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class BoardActivity extends AppCompatActivity {

    DatabaseReference mDatabase;  // 레퍼런스 정의

    //이메일 비밀번호 로그인 모듈 변수
    private FirebaseAuth mAuth;

    Button btnWrite, btnBack;
    ListView mainListView;
    ListAdapter listAdapter = null;

    ArrayList<String> arr_uid = null;
    ArrayList<String> arr_board_key = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board);

        mDatabase = FirebaseDatabase.getInstance().getReference();  // 레퍼런스 주소 값

        btnWrite = findViewById(R.id.btnWrite);
        btnBack = findViewById(R.id.btnBack);

        mainListView = (ListView) findViewById(R.id.list_mainListView);
        listAdapter = new ListAdapter();  // 객체 주입

        arr_uid = new ArrayList<>();
        arr_board_key = new ArrayList<>();

        ListListener listListener = new ListListener();
        mainListView.setOnItemClickListener(listListener);

        // 보드 게시글 가져오기
        getBoard();

        // 글쓰기 버튼
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BoardActivity.this, WriteActivity.class);
                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BoardActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void getBoard() {
        mDatabase.child("board").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Map<String, String> map = (Map) dataSnapshot.getValue();
                mainListView.setAdapter(listAdapter);

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BoardValue board = snapshot.getValue(BoardValue.class);
                    arr_uid.add(board.getUid());
                    arr_board_key.add(snapshot.getKey());
                    listAdapter.addItem(board.getTitle(), board.getDate(), board.getName());

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // 게시판 리스트 클릭시 반응
    class ListListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Intent i = new Intent(BoardActivity.this, ReadBoardActivity.class);
            i.putExtra("arr_uid", arr_uid.get(position));
            i.putExtra("arr_board_key", arr_board_key.get(position));
            startActivity(i);
        }
    }
}
