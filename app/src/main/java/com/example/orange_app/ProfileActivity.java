package com.example.orange_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.orange_app.notifications.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String receiverUserID, senderUserID, Current_State;

    private CircleImageView userProfileImage;
    private TextView userProfileName, userProfileStatus;
    private Button SendMessageRequestButton, DeclineMessageRequestButton;

    private DatabaseReference UserRef, ChatRequestRef, ContactsRef, NotificationRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ChatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        NotificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");

        receiverUserID = getIntent().getExtras().get("visit_user_id").toString();
        senderUserID = mAuth.getCurrentUser().getUid();

        userProfileImage = (CircleImageView) findViewById(R.id.visit_profile_image);
        userProfileName = (TextView) findViewById(R.id.visit_user_name);
        userProfileStatus = (TextView) findViewById(R.id.visit_profile_status);
        SendMessageRequestButton = (Button) findViewById(R.id.send_message_request_button);
        DeclineMessageRequestButton = (Button) findViewById(R.id.decline_message_request_button);
        Current_State = "new";

        RetrieveUserInfo();

        //checkUserStatus();

        //updateToken(FirebaseInstanceId.getInstance().getInstanceId().getResult().getToken());

    }//onCreate

    @Override
    protected void onResume() {

        //checkUserStatus();

        super.onResume();

    }//onResume

    /*
    private void checkUserStatus() {

        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null){

            senderUserID = user.getUid();

            SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID", senderUserID);
            editor.apply();

        }//if
        else {

        }//else

    }//checkUserStatus

    public void updateToken(String token){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
        ref.child(senderUserID).setValue(mToken);

    }//updateToken

     */

    private void RetrieveUserInfo() {

        UserRef.child(receiverUserID).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if((dataSnapshot.exists()) && (dataSnapshot.hasChild("image"))){

                    String userImage = dataSnapshot.child("image").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();

                    Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(userProfileImage);
                    userProfileName.setText(userName);
                    userProfileStatus.setText(userStatus);

                    ManageChatRequest();

                }//if
                else {

                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();

                    userProfileName.setText(userName);
                    userProfileStatus.setText(userStatus);

                    ManageChatRequest();

                }//else

            }//onDataChange

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }//onCancelled
        });

    }//RetrieveUserInfo

    private void ManageChatRequest() {

        ChatRequestRef.child(senderUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(receiverUserID)){

                            String request_type = dataSnapshot.child(receiverUserID).child("request_type").getValue().toString();

                            if(request_type.equals("sent")){

                                Current_State = "request_sent";
                                SendMessageRequestButton.setText("Отменить запрос");

                            }//if
                            else if (request_type.equals("received")) {

                                Current_State = "request_received";
                                SendMessageRequestButton.setText("Принять запрос");

                                DeclineMessageRequestButton.setVisibility(View.VISIBLE);
                                DeclineMessageRequestButton.setEnabled(true);

                                DeclineMessageRequestButton.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {

                                        CancelChatRequest();

                                    }//onClick

                                });

                            }//else if

                        }//if
                        else {

                            ContactsRef.child(senderUserID)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {

                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            if(dataSnapshot.hasChild(receiverUserID)){

                                                Current_State = "friends";
                                                SendMessageRequestButton.setText("Удалить этот контакт");

                                            }//if

                                        }//onDataChange

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }//onCancelled

                                    });

                        }//else

                    }//onDataChange

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }//onCancelled
                });

        if(!senderUserID.equals(receiverUserID)){

            SendMessageRequestButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    SendMessageRequestButton.setEnabled(false);

                    if(Current_State.equals("new")){

                        SendChatRequest();

                    }//if
                    if(Current_State.equals("request_sent")){

                        CancelChatRequest();

                    }//if
                    if(Current_State.equals("request_received")){

                        AcceptChatRequest();

                    }//if
                    if(Current_State.equals("friends")){

                        RemoveSpecificContact();

                    }//if

                }//onClick

            });

        }//if
        else {

            SendMessageRequestButton.setVisibility(View.INVISIBLE);

        }//else

    }//ManageChatRequest

    private void RemoveSpecificContact() {

        ContactsRef.child(senderUserID).child(receiverUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            ContactsRef.child(receiverUserID).child(senderUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {

                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                SendMessageRequestButton.setEnabled(true);
                                                Current_State = "new";
                                                SendMessageRequestButton.setText("Отправить запрос");

                                                DeclineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                DeclineMessageRequestButton.setEnabled(false);

                                            }//if

                                        }//onComplete

                                    });

                        }//if

                    }//onComplete

                });

    }//RemoveSpecificContact

    private void AcceptChatRequest() {

        ContactsRef.child(senderUserID).child(receiverUserID)
                .child("Contact").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            ContactsRef.child(receiverUserID).child(senderUserID)
                                    .child("Contact").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {

                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                ChatRequestRef.child(senderUserID).child(receiverUserID)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {

                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if(task.isSuccessful()){

                                                                    ChatRequestRef.child(receiverUserID).child(senderUserID)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {

                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                    SendMessageRequestButton.setEnabled(true);
                                                                                    Current_State = "friends";
                                                                                    SendMessageRequestButton.setText("Удалить этот контакт");

                                                                                    DeclineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                                                    DeclineMessageRequestButton.setEnabled(false);

                                                                                }//onComplete

                                                                            });

                                                                }//if

                                                            }//onComplete

                                                        });

                                            }//if

                                        }//onComplete

                                    });

                        }//if

                    }//onComplete

                });

    }//AcceptChatRequest

    private void CancelChatRequest() {

        ChatRequestRef.child(senderUserID).child(receiverUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            ChatRequestRef.child(receiverUserID).child(senderUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {

                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                SendMessageRequestButton.setEnabled(true);
                                                Current_State = "new";
                                                SendMessageRequestButton.setText("Отправить запрос");

                                                DeclineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                DeclineMessageRequestButton.setEnabled(false);

                                            }//if

                                        }//onComplete

                                    });

                        }//if

                    }//onComplete

                });

    }//CancelChatRequest

    private void SendChatRequest() {

        ChatRequestRef.child(senderUserID).child(receiverUserID)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            ChatRequestRef.child(receiverUserID).child(senderUserID)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {

                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                HashMap<String, String> chatNotificationMap = new HashMap<>();
                                                chatNotificationMap.put("from", senderUserID);
                                                chatNotificationMap.put("type", "request");

                                                NotificationRef.child(receiverUserID).push()
                                                        .setValue(chatNotificationMap)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if(task.isSuccessful()){

                                                                    SendMessageRequestButton.setEnabled(true);
                                                                    Current_State = "request_sent";
                                                                    SendMessageRequestButton.setText("Отменить запрос");

                                                                }//if

                                                            }//onComplete

                                                        });

                                            }//if

                                        }//onComplete

                                    });

                        }//if

                    }//onComplete

                });

    }//SendChatRequest

}//ProfileActivity
