package org.infosec2023.babotalk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView.Adapter adapter;
    private String myID;
    private String oppositeID;
    private String roomKey;

    private EditText chatText;
    private Button sendButton;
    private TextView textview;
    private DatabaseReference chatRef;
    private DatabaseReference friendRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent listIntent = getIntent();
        myID = listIntent.getStringExtra("myID");
        oppositeID = listIntent.getStringExtra("oppositeID");

        System.out.println("myID : " + myID + ", opID : "+oppositeID);

        chatText = findViewById(R.id.chatText);
        sendButton = findViewById(R.id.sendButton);
        textview = findViewById(R.id.textView);
        textview.setText(oppositeID);

        sendButton.setOnClickListener(v -> {
            String msg = chatText.getText().toString();

            if(msg != null){
                Chat chat = new Chat();
                chat.setName(myID);
                chat.setMsg(msg);
                chatRef.push().setValue(chat);
                chatText.setText("");
            }

        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        List<Chat> chatList = new ArrayList<>();
        adapter = new ChatAdapter(chatList, myID);
        recyclerView.setAdapter(adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        friendRef = database.getReference("Friends");
        friendRef.child(myID).child(oppositeID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                roomKey = String.valueOf(task.getResult().getValue());
                System.out.println(roomKey);

                chatRef = database.getReference("Rooms").child(roomKey);
                chatRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Chat chat = snapshot.getValue(Chat.class);
                        ((ChatAdapter)adapter).addChat(chat);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
}