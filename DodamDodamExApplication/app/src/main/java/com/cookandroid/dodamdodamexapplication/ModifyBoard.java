package com.cookandroid.dodamdodamexapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ModifyBoard extends AppCompatActivity {
    String board_uid; // 글쓴사람 uid;
    String board_key; // 글쓴 사람 key;

    String uid = ""; // uid

    Map<String, String> board_content_type;

    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mDatabase;

    TextView textView_modify_type2;
    EditText editText_modify_title, editText_modifyContent;

    BoardValue board = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_board);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        uid = user.getUid();

        editText_modify_title = (EditText) findViewById(R.id.editText_modify_title);
        editText_modifyContent = (EditText) findViewById(R.id.editText_modifyContent);

        board_uid = getIntent().getStringExtra("board_uid");
        board_key = getIntent().getStringExtra("board_key");

        getBoardData();
    }

    public void getBoardData() {
        mDatabase.child("board").child(board_uid).child(board_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                board = dataSnapshot.getValue(BoardValue.class);

                editText_modify_title.setText(board.getTitle());
                editText_modifyContent.setText(board.getContent());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void button_board_modify_register(View view) {

        if (editText_modify_title.getText().toString().equals("")) {
            Toast.makeText(this, "제목을 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if (editText_modifyContent.getText().toString().equals("")) {
            Toast.makeText(this, "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        Modify_Dialog dialog = new Modify_Dialog();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("게시물 수정여부");
        builder.setMessage("정말로 게시글을 수정하시겠습니까?");
        builder.setPositiveButton("수정", dialog);
        builder.setNegativeButton("취소", dialog);

        builder.show();
    }

    public void button_board_modify_cancle(View view) {

        Cancle_Dialog cancle_dialog = new Cancle_Dialog();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("게시물 수정취소");
        builder.setMessage("정말로 게시글을 수정을 취소하시겠습니까? 지금까지 작성된 게시글은 저장되지 않습니다");
        builder.setPositiveButton("메인 페이지", cancle_dialog);
        builder.setNegativeButton("취소", cancle_dialog);

        builder.show();
    }

    class Modify_Dialog implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    Intent i = new Intent(ModifyBoard.this, BoardActivity.class);
                    startActivity(i);
                    Map<String, Object> updateBoard = new HashMap<>();
                    updateBoard.put("title", editText_modify_title.getText().toString());
                    updateBoard.put("content", editText_modifyContent.getText().toString());
                    mDatabase.child("board").child(board_uid).child(board_key).updateChildren(updateBoard);
                    finish();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    return;

            }
            finish();
        }
    }

    class Cancle_Dialog implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    //Intent i = new Intent(ModifyBoard.this, BoardActivity.class);
                    //startActivity(i);
                    finish();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    return;

            }
        }
    }
}