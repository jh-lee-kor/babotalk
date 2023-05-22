package org.infosec2023.babotalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NewFriend extends AppCompatActivity {
    private String myName;
    private String nf;
    private String nfRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);

        Intent listActivity = getIntent();
        myName = listActivity.getStringExtra("myID");

        Button btnNewFriend = findViewById(R.id.btnSubmitNF);
        EditText etNewFriend = findViewById(R.id.etFriendID);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("Users");
        DatabaseReference friendRef = database.getReference("Friends");
        DatabaseReference roomRef = database.getReference("Rooms");

        btnNewFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nf = etNewFriend.getText().toString();
                usersRef.child(nf).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(NewFriend.this, "데이터베이스 읽기에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else {
                            String loadPw = String.valueOf(task.getResult().getValue());
                            if (loadPw.equals("null")){
                                Toast.makeText(NewFriend.this, "없는 회원입니다.", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                friendRef.child(myName).child(nf).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        String friendRoom = String.valueOf(task.getResult().getValue());
                                        if(friendRoom.equals("null")){
                                            String roomKey = roomRef.push().getKey();
                                            friendRef.child(myName).child(nf).setValue(roomKey);
                                            friendRef.child(nf).child(myName).setValue(roomKey);
                                            Toast.makeText(NewFriend.this, "친구 등록이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                        else{
                                            Toast.makeText(NewFriend.this, "이미 등록된 친구입니다..", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
    }
}