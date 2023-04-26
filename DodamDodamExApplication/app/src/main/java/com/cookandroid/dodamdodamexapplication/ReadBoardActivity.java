package com.cookandroid.dodamdodamexapplication;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class ReadBoardActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mDatabase;

    String uid;

    String board_uid, board_key;

    TextView textView_read_board_contentType2, textView_read_board_writer2, textView_read_board_title2, textView_read_board_content, textView_read_board_date2;
    Button button_board_delete, button_board_modify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_board);
        
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        uid = user.getUid();

        textView_read_board_writer2 = (TextView) findViewById(R.id.textView_read_board_writer2); // 작성자
        textView_read_board_title2 = (TextView) findViewById(R.id.textView_read_board_title2); // 제목
        textView_read_board_content = (TextView) findViewById(R.id.textView_read_board_content); // 내욜
        textView_read_board_date2 = (TextView) findViewById(R.id.textView_read_board_date2); // 날짜
        button_board_delete = (Button) findViewById(R.id.button_board_delete);
        button_board_modify = (Button) findViewById(R.id.button_board_modify);

        board_uid = getIntent().getStringExtra("arr_uid"); // intent 로 넘어오는 값을 세팅함
        board_key = getIntent().getStringExtra("arr_board_key");

        board_set(); // 화면 구성 시작
    }

    public void board_set() {
        mDatabase.child("board").child(board_uid).child(board_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("data_snap", "" + dataSnapshot);
                BoardValue board = dataSnapshot.getValue(BoardValue.class);

                textView_read_board_writer2.setText(board.getName());
                textView_read_board_title2.setText(board.getTitle());
                textView_read_board_date2.setText(board.getDate());
                textView_read_board_content.setText(board.getContent());

                textView_read_board_content.setMovementMethod(new ScrollingMovementMethod()); // TextView가 스크롤이 생김

                if (uid.equals(board.getUid())) {
                    button_board_delete.setEnabled(true);
                    button_board_modify.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void button_board_modify(View view){
        Intent i = new Intent(this , ModifyBoard.class);
        i.putExtra("board_uid" , board_uid);
        i.putExtra("board_key" , board_key);
        startActivity(i);
    }

    public void button_board_remove(View view) {
        Dialog dialog = new Dialog();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("게시물 삭제여부");
        builder.setMessage("정말로 게시글을 삭제하시겠습니까? 삭제된 게시글은 복구할 수 없습니다");
        builder.setPositiveButton("삭제" , dialog);
        builder.setNegativeButton("취소" , dialog);

        builder.show();
    }

    class Dialog implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    Toast.makeText(ReadBoardActivity.this, "승낙", Toast.LENGTH_SHORT);
                    Intent i = new Intent(ReadBoardActivity.this , BoardActivity.class);
                    startActivity(i);
                    finish();
                    mDatabase.child("board").child(board_uid).child(board_key).removeValue();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    return;

            }
        }
    }
}
