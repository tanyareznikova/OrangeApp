package com.example.orange_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.orange_app.adapters.MessageAdapter;
import com.example.orange_app.models.Contacts;
import com.example.orange_app.models.Messages;
import com.example.orange_app.models.Users;
import com.example.orange_app.notifications.APIService;
import com.example.orange_app.notifications.Client;
import com.example.orange_app.notifications.Data;
import com.example.orange_app.notifications.Response;
import com.example.orange_app.notifications.Sender;
import com.example.orange_app.notifications.Token;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

public class ChatActivity extends AppCompatActivity {

    private String messageReceiverID, messageReceiverName, messageReceiverImage, messageSenderID;

    private TextView userName, userLastSeen;
    private CircleImageView userImage;

    private Toolbar ChatToolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef, NotificationRef;

    private ImageButton SendMessageButton, SendFilesButton;
    private EditText MessageInputText;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView userMessagesList;

    private String saveCurrentTime, saveCurrentDate;
    private String checker = "", myUrl = "";
    private StorageTask uploadTask;
    private Uri fileUri;
    private ProgressDialog loadingBar;

    //APIService apiService;
    //boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        NotificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");

        messageReceiverID = getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName = getIntent().getExtras().get("visit_user_name").toString();
        messageReceiverImage = getIntent().getExtras().get("visit_image").toString();

        //apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);
        //apiService = Client.getRetrofit("https://fcm.googleapis.com/fcm/send/").create(APIService.class);


        InitializeControllers();

        RootRef.child("Messages").child(messageSenderID).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        Messages messages = dataSnapshot.getValue(Messages.class);

                        messagesList.add(messages);

                        messageAdapter.notifyDataSetChanged();

                        userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());

                    }//onChildAdded

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }//onChildChanged

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }//onChildRemoved

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }//onChildMoved

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }//onCancelled

                });

        userName.setText(messageReceiverName);
        Picasso.get().load(messageReceiverImage).placeholder(R.drawable.profile_image).into(userImage);

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //notify = true;

                SendMessage();

                //MessageInputText.setText("");

            }//onClick

        });//SendMessageButton.setOnClickListener

        DisplayLastSeen();

        SendFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //notify = true;

                CharSequence options[] = new CharSequence[]{

                        "Фото",
                        "PDF Файл",
                        "Word Файл"

                };//CharSequence

                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("Выберите Файл");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        /*
                        switch (which){

                            case 0:
                                checker = "image";

                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
                                startActivityForResult(intent.createChooser(intent, "Выберите фото"), 100);
                                break;

                            case 1:
                                checker = "pdf";

                                intent = new Intent();
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                intent.setType("application/pdf");
                                startActivityForResult(intent.createChooser(intent, "Выберите PDF файл"), 100);
                                break;

                            case 2:
                                checker = "docx";

                                intent = new Intent();
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                intent.setType("application/msword");
                                startActivityForResult(intent.createChooser(intent, "Выберите Word файл"), 100);
                                break;

                        }//switch

                         */



                        if(which == 0){

                            checker = "image";

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent, "Выберите фото"), 100);

                        }//if
                        if(which == 1){

                            checker = "pdf";

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/pdf");
                            startActivityForResult(intent.createChooser(intent, "Выберите PDF файл"), 100);

                        }//if
                        if(which == 2){

                            checker = "docx";

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/msword");
                            startActivityForResult(intent.createChooser(intent, "Выберите Word файл"), 100);

                        }//if



                    }//onClick

                });//DialogInterface.OnClickListener

                builder.show();

            }//onClick

        });//SendFilesButton.setOnClickListener

    }//onCreate

    private void InitializeControllers() {

        ChatToolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(ChatToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar, null);
        actionBar.setCustomView(actionBarView);

        userImage = (CircleImageView) findViewById(R.id.custom_profile_image);
        userName = (TextView) findViewById(R.id.custom_profile_name);
        userLastSeen = (TextView) findViewById(R.id.custom_user_last_seen);

        SendMessageButton = (ImageButton) findViewById(R.id.send_message_btn);
        SendFilesButton = (ImageButton) findViewById(R.id.send_files_btn);
        MessageInputText = (EditText) findViewById(R.id.input_message);

        messageAdapter = new MessageAdapter(messagesList);
        userMessagesList = (RecyclerView) findViewById(R.id.private_messages_list_of_users);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);

        loadingBar = new ProgressDialog(this);

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calendar.getTime());

    }//InitializeControllers



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && resultCode == RESULT_OK && data != null && data.getData() != null){

            loadingBar.setTitle("Отправка файлов");
            loadingBar.setMessage("Отправляем Ваш Файл");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            fileUri = data.getData();

            if(!checker.equals("image")){

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Document Files");

                final String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
                final String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;

                DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                        .child(messageSenderID)
                        .child(messageReceiverID)
                        .push();

                final String messagePushID = userMessageKeyRef.getKey();

                final StorageReference filePath = storageReference.child(messagePushID + "." + checker);

                filePath.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                    @Override

                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                            @Override

                            public void onSuccess(Uri uri) {

                                String downloadUrl = uri.toString();



                                Map messageImageBody = new HashMap();

                                messageImageBody.put("message",downloadUrl);

                                messageImageBody.put("name",fileUri.getLastPathSegment());

                                messageImageBody.put("type",checker);

                                messageImageBody.put("from",messageSenderID);

                                messageImageBody.put("to", messageReceiverID);

                                messageImageBody.put("messageID", messagePushID);

                                messageImageBody.put("time", saveCurrentTime);

                                messageImageBody.put("date", saveCurrentDate);



                                Map messageBodyDetail = new HashMap();

                                messageBodyDetail.put(messageSenderRef+ "/" + messagePushID, messageImageBody);

                                messageBodyDetail.put(messageReceiverRef+ "/" + messagePushID, messageImageBody);



                                RootRef.updateChildren(messageBodyDetail);

                                loadingBar.dismiss();


                            }

                        }).addOnFailureListener(new OnFailureListener() {

                            @Override

                            public void onFailure(@NonNull Exception e) {

                                loadingBar.dismiss();

                                Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                            }

                        });

                    }

                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                    @Override

                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        double p = (100.0* taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                        loadingBar.setMessage("Загружается... " + (int) p + " %");

                    }

                });

                /*

                filePath.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()){

                            Map messageTextBody = new HashMap();
                            messageTextBody.put("message", task.getResult().getMetadata().getReference().getDownloadUrl().toString());
                            messageTextBody.put("name", fileUri.getLastPathSegment());
                            messageTextBody.put("type", checker);
                            messageTextBody.put("from", messageSenderID);
                            messageTextBody.put("to", messageReceiverID);
                            messageTextBody.put("messageID", messagePushID);
                            messageTextBody.put("time", saveCurrentTime);
                            messageTextBody.put("date", saveCurrentDate);

                            Map messageBodyDetails = new HashMap();
                            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);

                            RootRef.updateChildren(messageBodyDetails);
                            loadingBar.dismiss();

                        }//if

                    }//onComplete

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        loadingBar.dismiss();
                        Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }//onFailure

                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                        double p = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        loadingBar.setMessage("Загружается... " + (int) p + " %");

                    }//onProgress

                });

                 */

            }//if
            else if(checker.equals("image")){

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");

                final String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
                final String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;

                DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                        .child(messageSenderID)
                        .child(messageReceiverID)
                        .push();

                final String messagePushID = userMessageKeyRef.getKey();

                final StorageReference filePath = storageReference.child(messagePushID + "." + "jpg");

                uploadTask = filePath.putFile(fileUri);

                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {

                        if(!task.isSuccessful()){

                            throw task.getException();

                        }//if

                        return filePath.getDownloadUrl();

                    }//then

                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if(task.isSuccessful()){

                            Uri downloadUri = task.getResult();
                            myUrl = downloadUri.toString();

                            Map messageTextBody = new HashMap();
                            messageTextBody.put("message", myUrl);
                            messageTextBody.put("name", fileUri.getLastPathSegment());
                            messageTextBody.put("type", checker);
                            messageTextBody.put("from", messageSenderID);
                            messageTextBody.put("to", messageReceiverID);
                            messageTextBody.put("messageID", messagePushID);
                            messageTextBody.put("time", saveCurrentTime);
                            messageTextBody.put("date", saveCurrentDate);

                            Map messageBodyDetails = new HashMap();
                            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);

                            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {

                                    if (task.isSuccessful()){

                                        loadingBar.dismiss();
                                        Toast.makeText(ChatActivity.this, "Сообщение отправлено...", Toast.LENGTH_SHORT).show();

                                    }//if
                                    else {

                                        loadingBar.dismiss();

                                        Toast.makeText(ChatActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();

                                    }//else
                                    MessageInputText.setText("");

                                }//onComplete

                            });//RootRef.updateChildren

                        }//if

                    }//onComplete

                });//Continuation

            }//else if
            else {

                loadingBar.dismiss();

                Toast.makeText(this, "Ничего не было выбрано.", Toast.LENGTH_SHORT).show();

            }//else

        }//if

    }//onActivityResult




    private void DisplayLastSeen(){

        RootRef.child("Users").child(messageReceiverID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.child("userState").hasChild("state")){

                            String state = dataSnapshot.child("userState").child("state").getValue().toString();
                            String date = dataSnapshot.child("userState").child("date").getValue().toString();
                            String time = dataSnapshot.child("userState").child("time").getValue().toString();

                            if(state.equals("онлайн")){

                                userLastSeen.setText("онлайн");

                            }//if
                            else if(state.equals("офлайн")){

                                userLastSeen.setText("В сети был(а): " + date + " " + time);

                            }//if

                        }//if
                        else {

                            userLastSeen.setText("офлайн");

                        }//else

                    }//onDataChange

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }//onCancelled

                });//RootRef.child

    }//DisplayLastSeen

    /*
    @Override
    protected void onStart() {

        super.onStart();

        RootRef.child("Messages").child(messageSenderID).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        Messages messages = dataSnapshot.getValue(Messages.class);

                        messagesList.add(messages);

                        messageAdapter.notifyDataSetChanged();

                        userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());

                    }//onChildAdded

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }//onChildChanged

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }//onChildRemoved

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }//onChildMoved

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }//onCancelled

                });


    }//onStart


     */

    private void SendMessage(){

        final String messageText = MessageInputText.getText().toString();

        if(TextUtils.isEmpty(messageText)){

            Toast.makeText(this, "Напишите сообщение...", Toast.LENGTH_SHORT).show();
            
        }//if
        else {

            String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
            String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;

            DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                    .child(messageSenderID)
                    .child(messageReceiverID)
                    .push();

            String messagePushID = userMessageKeyRef.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messageSenderID);
            messageTextBody.put("to", messageReceiverID);
            messageTextBody.put("messageID", messagePushID);
            messageTextBody.put("time", saveCurrentTime);
            messageTextBody.put("date", saveCurrentDate);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);

            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if (task.isSuccessful()){

                        HashMap<String, String> messageNotificationMap = new HashMap<>();
                        messageNotificationMap.put("from", messageSenderID);
                        messageNotificationMap.put("message", messageText);
                        messageNotificationMap.put("type", "message");

                        NotificationRef.child(messageReceiverID).push()
                                .setValue(messageNotificationMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()){

                                            Toast.makeText(ChatActivity.this, "Сообщение отправлено...", Toast.LENGTH_SHORT).show();

                                        }//if

                                    }//onComplete

                                });

                    }//if
                    else {

                        Toast.makeText(ChatActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();

                    }//else
                    MessageInputText.setText("");

/*
                    final String msg = messageText;
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users").child(messageSenderID);
                    database.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            //Contacts contact = dataSnapshot.getValue(Contacts.class);
                            Users user = dataSnapshot.getValue(Users.class);

                            if(notify) {

                                sendNewNotification(messageReceiverID, user.getName(), msg);

                            }//if

                            notify = false;

                        }//onDataChange

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }//onCancelled

                    });//database.addValueEventListener

 */


                }//onComplete

            });//RootRef.updateChildren

        }//else



    }//SendMessage
/*
    private void sendNewNotification(final String messageReceiverID, final String name, final String messageText) {

        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(messageReceiverID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds: dataSnapshot.getChildren()){

                    Token token = ds.getValue(Token.class);
                    Data data = new Data(messageSenderID, name + " : " + messageText, "Новое Сообщение", messageReceiverID, R.drawable.message_icon);

                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                                    Toast.makeText(ChatActivity.this, "" + response.message(), Toast.LENGTH_SHORT).show();

                                }//onResponse

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {

                                }//onFailure

                            });//apiService.sendNotification

                }//for

            }//onDataChange

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }//onCancelled

        });//query.addValueEventListener

    }//sendNewNotification

 */

}//ChatActivity
