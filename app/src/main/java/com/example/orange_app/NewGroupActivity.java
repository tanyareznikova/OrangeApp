package com.example.orange_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.orange_app.adapters.UserListAdapter;
import com.example.orange_app.models.Contacts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewGroupActivity extends AppCompatActivity {

    public EditText newGroupTitleEditText;
    public Button newGroupCreateButton;
    public RecyclerView newGroupRecyclerView;
    private CircleImageView newGroupsImage;

    //public CheckedTextView newGroupCheckedTextView;
    //public TextView newGroupUserNameTextView;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, GroupNameRef, ContactsRef;

    private static final int GalleryPick = 1;
    private StorageReference NewGroupsImageRef;

    private String currentGroupName, currentUserID, currentUserName, messageReceiverID;
    private ProgressDialog loadingBar;
    Boolean checkedValue;
    private Toolbar GroupsToolbar;

    //private List<Contacts> userContactsList;
    private List<Contacts> userContactsList;
    private LinearLayoutManager linearLayoutManager;
    private UserListAdapter userListAdapter;

    private Uri ImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        //newGroupTitleEditText = (EditText) itemView.findViewById(R.id.new_group_title_edit_text);
        //newGroupCreateButton = (Button) itemView.findViewById(R.id.create_new_group_btn);
        //newGroupCheckedTextView = (CheckedTextView) itemView.findViewById(R.id.new_group_checked_text_view);


        //currentGroupName = getIntent().getExtras().get("groupName").toString();
        //Toast.makeText(NewGroupActivity.this, currentGroupName, Toast.LENGTH_SHORT).show();

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        //messageReceiverID = getIntent().getExtras().get("visit_user_id").toString();
        //currentGroupName = getIntent().getExtras().get("groupName").toString();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        GroupNameRef = FirebaseDatabase.getInstance().getReference();
        NewGroupsImageRef = FirebaseStorage.getInstance().getReference().child("Group Images");

        userContactsList = new ArrayList<>();

        InitializeFields();

        newGroupCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RequestNewGroup();

            }//onClick

        });//newGroupCreateButton.setOnClickListener

        newGroupsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPick);

            }//onClick

        });//newGroupsImage.setOnClickListener


    }//onCreate

    private void InitializeFields() {

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        newGroupTitleEditText = (EditText) findViewById(R.id.new_group_title_edit_text);
        newGroupCreateButton = (Button) findViewById(R.id.create_new_group_btn);
        newGroupsImage = (CircleImageView) findViewById(R.id.new_group_set_groups_image);

        userListAdapter = new UserListAdapter(userContactsList);
        newGroupRecyclerView = (RecyclerView) findViewById(R.id.new_group_recycler_view);
        linearLayoutManager = new LinearLayoutManager(this);
        newGroupRecyclerView.setLayoutManager(linearLayoutManager);
        newGroupRecyclerView.setAdapter(userListAdapter);

        //newGroupCheckedTextView = (CheckedTextView) findViewById(R.id.new_group_checked_text_view);
        //checkedValue = newGroupCheckedTextView.isChecked();
        //newGroupUserNameTextView = (TextView) findViewById(R.id.new_group_user_name_text_view);
        loadingBar = new ProgressDialog(this);

        GroupsToolbar = (Toolbar) findViewById(R.id.groups_toolbar);
        setSupportActionBar(GroupsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Создание группы");


        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setDisplayShowCustomEnabled(true);
        //actionBar.setTitle("Создание группы");


    }//InitializeFields

    @Override
    protected void onStart() {

        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(ContactsRef, Contacts.class)
                        .build();

        FirebaseRecyclerAdapter<Contacts, UserListAdapter.UserListViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, UserListAdapter.UserListViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull final UserListAdapter.UserListViewHolder contactsViewHolder, int i, @NonNull Contacts contacts) {

                        final String userIDs = getRef(i).getKey();

                        UsersRef.child(userIDs).addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if(dataSnapshot.exists()){

                                    if(dataSnapshot.hasChild("name")){

                                        String profileName = dataSnapshot.child("name").getValue().toString();

                                        contactsViewHolder.textView.setText(profileName);

                                    }//if
                                    else {

                                        String profileName = dataSnapshot.child("name").getValue().toString();

                                        contactsViewHolder.textView.setText(profileName);

                                    }//else

                                }//if

                            }//onDataChange

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }//onCancelled

                        });//UsersRef

                    }//onBindViewHolder

                    @NonNull
                    @Override
                    public UserListAdapter.UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
                        UserListAdapter.UserListViewHolder viewHolder = new UserListAdapter.UserListViewHolder(view);
                        return viewHolder;

                    }//onCreateViewHolder

                };//FirebaseRecyclerAdapter

        newGroupRecyclerView.setAdapter(adapter);
        adapter.startListening();

    }//onStart

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GalleryPick && resultCode==RESULT_OK && data!=null){

            ImageUri =  data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);

        }//if

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){

                loadingBar.setTitle("Фото группы");
                loadingBar.setMessage("Фото группы загружается");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                Uri resultUri = result.getUri();

                final StorageReference filePath = NewGroupsImageRef.child(resultUri.getLastPathSegment() + ".jpg");

                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                    @Override

                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                            @Override

                            public void onSuccess(Uri uri) {


                                final String downloadUrl = uri.toString();

                                GroupNameRef.child("Groups").child(currentUserID).child("image").setValue(downloadUrl)

                                        .addOnCompleteListener(new OnCompleteListener<Void>() {

                                            @Override

                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {

                                                    Toast.makeText(NewGroupActivity.this, "Фото сохранено в БД...", Toast.LENGTH_SHORT).show();

                                                    loadingBar.dismiss();

                                                }//if
                                                else {

                                                    String message = task.getException().getMessage();

                                                    Toast.makeText(NewGroupActivity.this, "Ошибка: " + message, Toast.LENGTH_SHORT).show();

                                                    loadingBar.dismiss();

                                                }//else

                                            }//onComplete

                                        });

                            }//onSuccess
                        });
                    }//onSuccess
                });
            }//if
        }//if

    }//onActivityResult

    private void CreateChat(){

        String key = FirebaseDatabase.getInstance().getReference().child("Groups").push().getKey();

        DatabaseReference contactRef = FirebaseDatabase.getInstance().getReference().child("Users");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Groups").child(key).child("Info");

        HashMap newChatMap = new HashMap();
        newChatMap.put("uid", key);
        newChatMap.put("Users/" + FirebaseAuth.getInstance().getUid(), true);

        Boolean validChat = false;
        for(Contacts mContact : userContactsList){

            if(mContact.getSelected()){

                validChat = true;
                newChatMap.put("Users/" + mContact.getName(), true);
                contactRef.child(mContact.getName()).child("Groups").child(key).setValue(true);

            }//if

        }//for

        if(validChat){
            databaseReference.updateChildren(newChatMap);
            contactRef.child(FirebaseAuth.getInstance().getUid()).child("Groups").child(key).setValue(true);
        }//if


    }//CreateChat

    private void RequestNewGroup(){

        final String nGroup = newGroupTitleEditText.getText().toString();

        //Boolean validGroup = false;

        if(TextUtils.isEmpty(nGroup)){

            Toast.makeText(this, "Введите название группы...", Toast.LENGTH_SHORT).show();
        }//if
        else {

                CreateNewGroup(nGroup);

            SendUserToMainActivity();

            }//else

            //HashMap<String, Object> groupMembersMap = new HashMap<>();
            //groupMembersMap.put("uid", messageReceiverID);
            //groupMembersMap.put("name", userContactsList.toString());


            //for (Contacts mContact : userContactsList) {
                //validGroup = true;
/*
                HashMap<String, Object> groupMembersMap = new HashMap<>();
                //groupMembersMap.put("uid", messageReceiverID);
                groupMembersMap.put("name", mContact.getName());

 */



                /*
                GroupNameRef.child("Groups").child(nGroup).child(currentUserID).push()
                        .updateChildren(groupMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){

                                    Toast.makeText(NewGroupActivity.this, "Группа создана...", Toast.LENGTH_SHORT).show();

                                }//if

                            }//onComplete

                        });

                 */





            //}//for


            //validGroup = true;

/*
            HashMap<String, Object> groupMap = new HashMap<>();
            groupMap.put("uid", currentUserID);
            groupMap.put("groupName", nGroup);

            GroupNameRef.child("Groups").child(nGroup).updateChildren(groupMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            SendUserToMainActivity();
                            if(task.isSuccessful()){
                                Toast.makeText(NewGroupActivity.this, "Группа " + nGroup + " создана...", Toast.LENGTH_SHORT).show();
                            }//if
                            else{
                                String message = task.getException().toString();
                                Toast.makeText(NewGroupActivity.this, "Ошибка:" + message, Toast.LENGTH_SHORT).show();
                            }//else

                        }//onComplete

                    });

 */
/*
            GroupNameRef.child("Groups").child(nGroup).setValue("")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                Toast.makeText(NewGroupActivity.this, "Группа " + nGroup + " создана...", Toast.LENGTH_SHORT).show();
                            }//if

                        }//onComplete
                    });

            //Intent intent = new Intent(NewGroupActivity.this, MainActivity.class);
            //startActivity(intent);

 */

        /*
        for(Contacts mContact : userContactsList){

            if(mContact.getSelected()){
                validGroup = true;

                HashMap<String, Object> groupMembersMap = new HashMap<>();
                //groupMembersMap.put("uid", messageReceiverID);
                groupMembersMap.put("name", mContact.getName());


                GroupNameRef.child("Groups").child(nGroup).child(currentUserID).child("members").push()
                        .setValue(groupMembersMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){

                                    Toast.makeText(NewGroupActivity.this, "Члены группы добавлены...", Toast.LENGTH_SHORT).show();

                                }//if

                            }//onComplete

                        });

                SendUserToMainActivity();

            }//if
            else if(TextUtils.isEmpty(nGroup)){
                Toast.makeText(this, "Введите название группы...", Toast.LENGTH_SHORT).show();
            }//else if

        }//for

         */

    }//RequestNewGroup

    private void CreateNewGroup(final String groupName) {

        GroupNameRef.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(NewGroupActivity.this, "Группа " + groupName + " создана...", Toast.LENGTH_SHORT).show();
                        }//if

                    }//onComplete
                });

        /*
        HashMap<String, Object> groupMap = new HashMap<>();
        groupMap.put("groupName", groupName);
        groupMap.put("admin", currentUserID);
        groupMap.put("image", ImageUri.getLastPathSegment());

        GroupNameRef.child("Groups").child(groupName).push()
                .updateChildren(groupMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            Toast.makeText(NewGroupActivity.this, "Группа создана...", Toast.LENGTH_SHORT).show();

                        }//if
                        else {

                            Toast.makeText(NewGroupActivity.this, "Ошибка при создании группы...", Toast.LENGTH_SHORT).show();

                        }//else

                    }//onComplete

                });

         */


        /*
        GroupNameRef.child("Groups").child(currentUserID)
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if((dataSnapshot.exists()) && (dataSnapshot.hasChild("groupName") && (dataSnapshot.hasChild("image")))){

                            final UserListAdapter.UserListViewHolder contactsViewHolder;

                            String nGroupName = dataSnapshot.child("groupName").getValue().toString();
                            //String nAdmin = dataSnapshot.child("admin").getValue().toString();
                            String nGroupImage = dataSnapshot.child("image").getValue().toString();



                            newGroupTitleEditText.setText(nGroupName);
                            Picasso.get().load(nGroupImage).into(newGroupsImage);

                        }//if
                        else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("groupName"))){

                            String nGroupName = dataSnapshot.child("groupName").getValue().toString();
                            //String nMember = dataSnapshot.child("members").getValue().toString();

                            newGroupTitleEditText.setText(nGroupName);
                            //newGroupRecyclerView.setText(nMember);

                        }//else if
                        else{

                            //userName.setVisibility(View.VISIBLE);
                            Toast.makeText(NewGroupActivity.this, "Проверьте правильность заполнения полей...", Toast.LENGTH_SHORT).show();

                        }//else

                    }//onDataChange

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }//onCancelled
                });

         */
/*
            GroupNameRef.child("Groups").child(groupName).setValue("")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                Toast.makeText(NewGroupActivity.this, "Группа " + groupName + " создана...", Toast.LENGTH_SHORT).show();
                            }//if

                        }//onComplete
                    });

 */


    }//CreateNewGroup

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(NewGroupActivity.this, MainActivity.class);
        //mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        //finish();
    }//SendUserToMainActivity

}//NewGroupActivity
