package org.infosec2023.babotalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");

        System.out.println(usersRef.child("test1").get().toString());

        final EditText etId = (EditText)findViewById(R.id.eTId);
        final EditText etPw = (EditText)findViewById(R.id.eTPw);

        Button btnLogin = (Button)findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strId = etId.getText().toString();
                String strPw = etPw.getText().toString();
                if(strId.isEmpty()){
                    Toast.makeText(MainActivity.this, "이메일을 입력하시오", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(strPw.isEmpty()){
                    Toast.makeText(MainActivity.this, "비밀번호를 입력하시오", Toast.LENGTH_SHORT).show();
                    return;
                }
                usersRef.child(strId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "데이터베이스 읽기에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else {
                            String loadPw = String.valueOf(task.getResult().getValue());
                            if(strPw.equals(loadPw)){
                                Intent in = new Intent(MainActivity.this, ListActivity.class);
                                in.putExtra("userID", strId);
                                startActivity(in);
                            }
                            else{
                                Toast.makeText(MainActivity.this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });

        Button btnReg = (Button)findViewById(R.id.btnReg);
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strId = etId.getText().toString();
                String strPw = etPw.getText().toString();
                if(strId.isEmpty()){
                    Toast.makeText(MainActivity.this, "이메일을 입력하시오", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(strPw.isEmpty()){
                    Toast.makeText(MainActivity.this, "비밀번호를 입력하시오", Toast.LENGTH_SHORT).show();
                    return;
                }
                usersRef.child(strId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "데이터베이스 읽기에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else {
                            String loadPw = String.valueOf(task.getResult().getValue());
                            if (loadPw.equals("null")){

                                usersRef.child(strId).setValue(strPw);
                                Toast.makeText(MainActivity.this, "회원가입이 완료되었습니다. 로그인 해주세요.", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(MainActivity.this, "이미 등록된 회원입니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
    }
}