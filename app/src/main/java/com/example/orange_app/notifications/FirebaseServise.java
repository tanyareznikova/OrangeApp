package com.example.orange_app.notifications;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;

public class FirebaseServise extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {

        super.onNewToken(s);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //String tokenRefresh = FirebaseInstanceId.getInstance().getToken();
        //String tokenRefresh = FirebaseInstanceId.getInstance().getInstanceId().getResult().getToken();
        s = FirebaseInstanceId.getInstance().getInstanceId().getResult().getToken();

        if(user != null){

            //updateToken(tokenRefresh);
            updateToken(s);

        }//if

    }//onNewToken

    private void updateToken(String tokenRefresh) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(tokenRefresh);
        ref.child(user.getUid()).setValue(token);

    }//updateToken

}//FirebaseServise
