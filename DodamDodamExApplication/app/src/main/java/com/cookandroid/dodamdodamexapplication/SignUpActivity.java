package com.cookandroid.dodamdodamexapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {
    //이메일 비밀번호 로그인 모듈 변수
    private FirebaseAuth mAuth;
    //현재 로그인 된 유저 정보를 담을 변수
    private FirebaseUser currentUser;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();

    String name = "test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        mAuth = FirebaseAuth.getInstance();

        //이메일
        final EditText emailTxt = (EditText)findViewById(R.id.user_email);
        //이름
        final EditText nameTxt = (EditText)findViewById(R.id.user_name);
        //비밀번호
        final EditText pwTxt = (EditText)findViewById(R.id.user_password);
        final EditText pwTxtCheck = (EditText)findViewById(R.id.user_password_check);
        //버튼
        Button joinBtn = (Button)findViewById(R.id.check);


        //버튼이 눌렀을 때
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailTxt.getText().toString();
                name = nameTxt.getText().toString();
                String pw = pwTxt.getText().toString();
                String pwC = pwTxtCheck.getText().toString();


                //Toast.makeText(SignUpActivity.this,email +"/=가입 버튼 눌리고" + name +"/" + pw,Toast.LENGTH_SHORT).show();


                //가입 성공했을 때 -> 메인 페이지로 이동하기

                //Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                //startActivityForResult(signInIntent, 100);Toast.makeText(AuthActivity.this,"btn",Toast.LENGTH_SHORT).show();
                //Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                // startActivityForResult(signInIntent, 100);

                joinStart(email,name,pw);
            }
        });

    }
    //가입 함수
    public void joinStart(String email, String name, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                Toast.makeText(SignUpActivity.this, "비밀번호가 간단해요..", Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                Toast.makeText(SignUpActivity.this, "email 형식에 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthUserCollisionException e) {
                                Toast.makeText(SignUpActivity.this, "이미존재하는 email 입니다.", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(SignUpActivity.this, "다시 확인해주세요..", Toast.LENGTH_SHORT).show();
                            }
                        } else {

                            currentUser = mAuth.getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();

                            currentUser.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.e("e", "User profile updated.");
                                            }
                                        }
                                    });

                            Toast.makeText(SignUpActivity.this, "가입 성공!!  어서오세요, " + currentUser.getDisplayName() + currentUser.getEmail(), Toast.LENGTH_SHORT).show();

                            final String uid = currentUser.getUid(); // uid 는 계정 하나당 하나의 값을 가지게 됩니다 그래서 이것으로 분간을 하겠습니다
                            String nickname = currentUser.getDisplayName();

                            databaseReference.addValueEventListener(new ValueEventListener() {
                                // 위의 함수는 전부 비동기 처리가 이루어지기 때문에 전부 이벤트가 발생하는 시점에서 이 함수를 써주셔야 합니다 앞으로 지겹게 볼 예정입니다
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    //데이터 변경이 감지가 되면 이 함수가 자동으로 콜백이 됩니다 이때 dataSnapashot 는 값을 내려 받을떄 사용함으로 지금은 쓰지 않습니다

                                    //databaseReference.child("user").child(uid).child("name").setValue(nickname);
                                    databaseReference.child("user").child(uid).child("point").setValue(0);
                                    //RealTimeDB는 기본적으로 parent , child , value 값으로 이루어져 있습니다 지금은 최초로 로그인한 사람의
                                    //색인을 만들고자 지금과 같은 작업을 하는 중입니다 즉 처음 들어오는 사람에게 DB자리를 내준다고 생각하시면됩니다

                                    //UserValue user = dataSnapshot.child("user").child(uid).getValue(UserValue.class); // 이는 해당 데이터를 바로 클래스 형태로 넣는 방법입니다 이때 getter는 필수 입니다
                                    // nickName.setText(user.getName()); //그래서 그 값을 가져오는 것이고
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // RealTimeDB와 통신 에러 등등 데이터를 정상적으로 받지 못할때 콜백함수로서 이곳으로 들어옵니다
                                    // 저는 작성하지 않았습니다

                                }
                            });

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(SignUpActivity.this, FirstActivity.class));
                                    finish();
                                }
                            }, 2000);
                        }
                    }
                });

    }
}