package com.example.orange_app;

import android.content.Intent;
import androidx.annotation.NonNull;

import com.example.orange_app.adapters.TabsAccessorAdapter;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessorAdapter myTabsAccessorAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        RootRef = FirebaseDatabase.getInstance().getReference();

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Orange");

        myViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
        myTabsAccessorAdapter = new TabsAccessorAdapter (getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAccessorAdapter);

        myTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);

    }//onCreate

    @Override
    protected void onStart() {

        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null){

            SendUserToLoginActivity();

        }//if
        else {

            UpdateUserStatus("онлайн");

            VerifyUserExistance();

        }//else

    }//onStart


    @Override
    protected void onStop() {

        super.onStop();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){

            UpdateUserStatus("офлайн");

        }//if


    }//onStop



    @Override
    protected void onDestroy() {

        super.onDestroy();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){

            UpdateUserStatus("офлайн");

        }//if

    }//onDestroy

    private void VerifyUserExistance() {

        String currentUserID = mAuth.getCurrentUser().getUid();
        RootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.child("name").exists())){
                    Toast.makeText(MainActivity.this, "Добро пожаловать", Toast.LENGTH_SHORT).show();
                }//if
                else {
                    SendUserToSettingsActivity();
                }//else
            }//onDataChange

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }//onCancelled
        });

    }//VerifyUserExistance

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;

    }//onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.main_logout_option){
            UpdateUserStatus("офлайн");
            mAuth.signOut();
            SendUserToLoginActivity();
        }//if

        if(item.getItemId() == R.id.main_settings_option){
            SendUserToSettingsActivity();
        }//if

        if(item.getItemId() == R.id.main_create_group_option){
            SendUserToCreateNewGroupActivity();

            //RequestNewGroup();

        }//if

        if(item.getItemId() == R.id.main_find_friends_option){

            SendUserToFindFriendsActivity();

        }//if

        return true;

    }//onOptionsItemSelected


    private void SendUserToLoginActivity() {

        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);

    }//SendUserToLoginActivity

    private void SendUserToSettingsActivity() {

        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);

    }//SendUserToSettingsActivity

    private void SendUserToCreateNewGroupActivity() {

        Intent createNewGroupIntent = new Intent(MainActivity.this, NewGroupActivity.class);
        startActivity(createNewGroupIntent);

    }//SendUserToCreateNewGroupActivity

    private void SendUserToFindFriendsActivity() {

        Intent findFriendsIntent = new Intent(MainActivity.this, FindFriendsActivity.class);
        startActivity(findFriendsIntent);

    }//SendUserToFindFriendsActivity

    private void UpdateUserStatus(String state){

        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCurrentTime);
        onlineStateMap.put("date", saveCurrentDate);
        onlineStateMap.put("state", state);

        currentUserID = mAuth.getCurrentUser().getUid();

        RootRef.child("Users").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);

    }//UpdateUserStatus

    /*
    private void RequestNewGroup(){

        AlertDialog.Builder builder = new AlertDialog.Builder(NewGroupActivity.this, R.style.AlertDialog);
        builder.setTitle("Введите название группы:");

        final EditText groupNameField = new EditText(NewGroupActivity.this);
        groupNameField.setHint("Название группы");
        builder.setView(groupNameField);

        builder.setPositiveButton("Создать", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String groupName = groupNameField.getText().toString();

                if(TextUtils.isEmpty(groupName)){
                    Toast.makeText(NewGroupActivity.this, "Пожалуйста, введите название группы...", Toast.LENGTH_SHORT).show();
                }//if
                else {
                    CreateNewGroup(groupName);
                }//else

            }//onClick
        });
        builder.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

            }//onClick
        });

        builder.show();

    }//RequestNewGroup

    private void CreateNewGroup(final String groupName) {

        GroupNameRef.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(NewGroupActivity.this, groupName + " создана...", Toast.LENGTH_SHORT).show();
                        }//if

                    }//onComplete
                });

    }//CreateNewGroup
     */

}//MainActivity
