package com.cookandroid.dodamdodamexapplication;

import android.os.Bundle;

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

    public final static UserValue userClass = new UserValue();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board);

        mDatabase = FirebaseDatabase.getInstance().getReference();  // 레퍼런스 주소 값

        DataSet();
    }

    //최초 로그인시 DB 값 세팅
    public void DataSet(){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser(); // 이는 현재 로그인한 사람의 정보를 가지고 옵니다
        final String uid = user.getUid(); // uid 는 계정 하나당 하나의 값을 가지게 됩니다 그래서 이것으로 분간을 하겠습니다

        mDatabase.addValueEventListener(new ValueEventListener() {
            // 위의 함수는 전부 비동기 처리가 이루어지기 때문에 전부 이벤트가 발생하는 시점에서 이 함수를 써주셔야 합니다 앞으로 지겹게 볼 예정입니다
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //데이터 변경이 감지가 되면 이 함수가 자동으로 콜백이 됩니다 이때 dataSnapashot 는 값을 내려 받을떄 사용함으로 지금은 쓰지 않습니다

                mDatabase.child("user").child(uid).child("name").setValue(user.getDisplayName());
                mDatabase.child("user").child(uid).child("firstLunch").setValue("");
                //RealTimeDB는 기본적으로 parent , child , value 값으로 이루어져 있습니다 지금은 최초로 로그인한 사람의
                //색인을 만들고자 지금과 같은 작업을 하는 중입니다 즉 처음 들어오는 사람에게 DB자리를 내준다고 생각하시면됩니다

                UserValue user = dataSnapshot.child("user").child(uid).getValue(UserValue.class); // 이는 해당 데이터를 바로 클래스 형태로 넣는 방법입니다 이때 getter는 필수 입니다
                // nickName.setText(user.getName()); //그래서 그 값을 가져오는 것이고

                userClass.setName(user.getName()); // 전역으로 이 값을 사용하기 위해서 세팅을 해줍니다
                userClass.setFirstLunch(user.getFirstLunch());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // RealTimeDB와 통신 에러 등등 데이터를 정상적으로 받지 못할때 콜백함수로서 이곳으로 들어옵니다
                // 저는 작성하지 않았습니다

            }
        });
    }
}
