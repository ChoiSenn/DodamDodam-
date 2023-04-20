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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class FirstActivity  extends AppCompatActivity {

    // 로그인 모듈
    private FirebaseAuth mAuth;
    // 현재 로그인 된 유저 정보를 담을 변수
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_main);

        final Button btnLogin = findViewById(R.id.btnLogin);
        final Button btnSign = findViewById(R.id.btnSign);
        EditText edtID = (EditText) findViewById(R.id.edtID);
        EditText edtPw = (EditText) findViewById(R.id.edtPw);

        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                String email = edtID.getText().toString();
                String password = edtPw.getText().toString();

                if(email.isEmpty() || password.isEmpty()){
                    Toast.makeText(FirstActivity.this,"id와 패스워드를 입력해주세요." ,Toast.LENGTH_SHORT).show();
                } else{
                    //로그인 성공했을 때 -> 메인 페이지로 이동
                    loginStart(email, password);
                }
            }
        });

        btnSign.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(v.getContext(), SignUpActivity.class);

                startActivity(intent);
            }
        });
    }

    // 로그인 시작
    public void loginStart(String email, String password){
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (!task.isSuccessful()) {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        Toast.makeText(FirstActivity.this,"존재하지 않는 id 입니다." ,Toast.LENGTH_SHORT).show();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        Toast.makeText(FirstActivity.this,"id 혹은 패스워드가 틀립니다." ,Toast.LENGTH_SHORT).show();
                    } catch (FirebaseNetworkException e) {
                        Toast.makeText(FirstActivity.this,"Firebase NetworkException" ,Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(FirstActivity.this,"Exception" ,Toast.LENGTH_SHORT).show();
                    }

                }else{


                    currentUser = mAuth.getCurrentUser();

                    Toast.makeText(FirstActivity.this, "로그인 성공 : " + "/" + currentUser.getEmail(),Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(FirstActivity.this, MainActivity.class));
                    finish();
                }

            }
        });
    }

    // 로그인 되어있으면 메인페이지로 이동
    @Override
    public void onStart(){
        super.onStart();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(FirstActivity.this, MainActivity.class));
            finish();
        }
    }
}
