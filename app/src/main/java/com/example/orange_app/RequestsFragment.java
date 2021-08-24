package com.example.orange_app;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.orange_app.models.Contacts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private View RequestsFragmentView;
    private RecyclerView myRequestsList;

    private DatabaseReference ChatRequestRef, UserRef, ContactsRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    public RequestsFragment() {
        // Required empty public constructor
    }//RequestsFragment()


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        RequestsFragmentView = inflater.inflate(R.layout.fragment_requests, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ChatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");

        myRequestsList = (RecyclerView) RequestsFragmentView.findViewById(R.id.chat_requests_list);
        myRequestsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return RequestsFragmentView;

    }//onCreateView

    @Override
    public void onStart() {

        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(ChatRequestRef.child(currentUserID), Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, RequestsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, RequestsViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull final RequestsViewHolder requestsViewHolder, int i, @NonNull Contacts contacts) {

                        requestsViewHolder.itemView.findViewById(R.id.request_accept_btn).setVisibility(View.INVISIBLE);
                        requestsViewHolder.itemView.findViewById(R.id.request_cancel_btn).setVisibility(View.INVISIBLE);

                        final String listUserID = getRef(i).getKey();

                        DatabaseReference getTypeRef = getRef(i).child("request_type").getRef();

                        getTypeRef.addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if(dataSnapshot.exists()){

                                    String type = dataSnapshot.getValue().toString();

                                    if(type.equals("received")){

                                        UserRef.child(listUserID).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                if(dataSnapshot.hasChild("image")){

                                                    final String requestProfileImage = dataSnapshot.child("image").getValue().toString();

                                                    Picasso.get().load(requestProfileImage).into(requestsViewHolder.profileImage);

                                                }//if

                                                final String requestUserName = dataSnapshot.child("name").getValue().toString();
                                                final String requestUserStatus = dataSnapshot.child("status").getValue().toString();

                                                requestsViewHolder.userName.setText(requestUserName);
                                                requestsViewHolder.userStatus.setText("хочет добавить вас в друзья");

                                                requestsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {

                                                    @Override
                                                    public void onClick(View v) {

                                                        CharSequence options[] = new CharSequence[] {

                                                                "Принять",
                                                                "Отклонить"

                                                        };//CharSequence

                                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                        builder.setTitle(" Запрос на переписку от " + requestUserName);

                                                        builder.setItems(options, new DialogInterface.OnClickListener() {

                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {

                                                                if(which == 0) {

                                                                    ContactsRef.child(currentUserID).child(listUserID).child("Contact")
                                                                            .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                            if(task.isSuccessful()){

                                                                                ContactsRef.child(listUserID).child(currentUserID).child("Contact")
                                                                                        .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                        if(task.isSuccessful()){

                                                                                            ChatRequestRef.child(currentUserID).child(listUserID)
                                                                                                    .removeValue()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                                                            if(task.isSuccessful()){

                                                                                                                ChatRequestRef.child(listUserID).child(currentUserID)
                                                                                                                        .removeValue()
                                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                            @Override
                                                                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                                                                if(task.isSuccessful()){

                                                                                                                                    Toast.makeText(getContext(), "Контакт сохранен", Toast.LENGTH_SHORT).show();

                                                                                                                                }//if

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

                                                                }//if
                                                                if(which == 1) {

                                                                    ChatRequestRef.child(currentUserID).child(listUserID)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                    if(task.isSuccessful()){

                                                                                        ChatRequestRef.child(listUserID).child(currentUserID)
                                                                                                .removeValue()
                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                                        if(task.isSuccessful()){

                                                                                                            Toast.makeText(getContext(), "Контакт удален", Toast.LENGTH_SHORT).show();

                                                                                                        }//if

                                                                                                    }//onComplete

                                                                                                });

                                                                                    }//if

                                                                                }//onComplete
                                                                            });

                                                                }//if

                                                            }//onClick

                                                        });

                                                        builder.show();

                                                    }//onClick

                                                });

                                            }//onDataChange

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }//onCancelled

                                        });//UserRef.child

                                    }//if
                                    else if(type.equals("sent")){

                                        Button request_sent_btn = requestsViewHolder.itemView.findViewById(R.id.request_accept_btn);
                                        request_sent_btn.setText("Отправить запрос");

                                        requestsViewHolder.itemView.findViewById(R.id.request_cancel_btn).setVisibility(View.INVISIBLE);

                                        UserRef.child(listUserID).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                if(dataSnapshot.hasChild("image")){

                                                    final String requestProfileImage = dataSnapshot.child("image").getValue().toString();

                                                    Picasso.get().load(requestProfileImage).into(requestsViewHolder.profileImage);

                                                }//if

                                                final String requestUserName = dataSnapshot.child("name").getValue().toString();
                                                final String requestUserStatus = dataSnapshot.child("status").getValue().toString();

                                                requestsViewHolder.userName.setText(requestUserName);
                                                requestsViewHolder.userStatus.setText("Вы уже отправили запрос на переписку");

                                                requestsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {

                                                    @Override
                                                    public void onClick(View v) {

                                                        CharSequence options[] = new CharSequence[] {

                                                                "Отменить запрос"

                                                        };//CharSequence

                                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                        builder.setTitle("Запрос уже был отправлен");

                                                        builder.setItems(options, new DialogInterface.OnClickListener() {

                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {

                                                                if(which == 0) {

                                                                    ChatRequestRef.child(currentUserID).child(listUserID)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                    if(task.isSuccessful()){

                                                                                        ChatRequestRef.child(listUserID).child(currentUserID)
                                                                                                .removeValue()
                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                                        if(task.isSuccessful()){

                                                                                                            Toast.makeText(getContext(), "Вы отменили запрос", Toast.LENGTH_SHORT).show();

                                                                                                        }//if

                                                                                                    }//onComplete

                                                                                                });

                                                                                    }//if

                                                                                }//onComplete
                                                                            });

                                                                }//if

                                                            }//onClick

                                                        });

                                                        builder.show();

                                                    }//onClick

                                                });

                                            }//onDataChange

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }//onCancelled
                                        });//UserRef.child

                                    }//else if

                                }//if

                            }//onDataChange

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }//onCancelled

                        });

                    }//onBindViewHolder

                    @NonNull
                    @Override
                    public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                        RequestsViewHolder requestsViewHolder = new RequestsViewHolder(view);
                        return requestsViewHolder;

                    }//onCreateViewHolder

                };//FirebaseRecyclerAdapter

        myRequestsList.setAdapter(adapter);
        adapter.startListening();

    }//onStart

    public static class RequestsViewHolder extends RecyclerView.ViewHolder {

        TextView userName, userStatus;
        CircleImageView profileImage;
        Button AcceptButton, CancelButton;

        public RequestsViewHolder(@NonNull View itemView) {

            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
            AcceptButton = itemView.findViewById(R.id.request_accept_btn);
            CancelButton = itemView.findViewById(R.id.request_cancel_btn);

        }//RequestsViewHolder

    }//RequestsViewHolder

}//RequestsFragment
