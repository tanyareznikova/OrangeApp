package com.example.orange_app.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orange_app.ImageViewerActivity;
import com.example.orange_app.MainActivity;
import com.example.orange_app.NewGroupActivity;
import com.example.orange_app.R;
import com.example.orange_app.models.Contacts;
import com.example.orange_app.models.Messages;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private List<Messages> userMessagesList;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, RootRef;

    //private String messageReceiverID, messageReceiverName, messageImageReceiverName;

    public GroupAdapter(List<Messages> userMessagesList){

        this.userMessagesList = userMessagesList;

    }//GroupAdapter

    public class GroupViewHolder extends RecyclerView.ViewHolder {

        public TextView senderGroupMessageText, receiverGroupMessageText, receiverNameMessageText;
        public TextView receiverNameImage;
        public TextView senderDateTimeText, receiverDateTimeText, senderDateTimeImage, receiverDateTimeImage;
        public CircleImageView receiverGroupProfileImage;
        public ImageView groupMessageSenderPicture, groupMessageReceiverPicture;


        public GroupViewHolder(@NonNull View itemView) {

            super(itemView);

            senderGroupMessageText = (TextView) itemView.findViewById(R.id.group_sender_message_text);
            receiverGroupMessageText = (TextView) itemView.findViewById(R.id.group_receiver_message_text);

            receiverNameMessageText = (TextView) itemView.findViewById(R.id.group_receiver_name_text_view);
            receiverNameImage = (TextView) itemView.findViewById(R.id.receiver_image_name_text_view);
            senderDateTimeText = (TextView) itemView.findViewById(R.id.group_sender_date_time_text_view);
            receiverDateTimeText = (TextView) itemView.findViewById(R.id.group_receiver_date_time_text_view);
            senderDateTimeImage = (TextView) itemView.findViewById(R.id.sender_image_date_time_text_view);
            receiverDateTimeImage = (TextView) itemView.findViewById(R.id.receiver_image_date_time_text_view);

            receiverGroupProfileImage = (CircleImageView) itemView.findViewById(R.id.group_message_profile_image);
            groupMessageSenderPicture = itemView.findViewById(R.id.group_message_sender_image_view);
            groupMessageReceiverPicture = itemView.findViewById(R.id.group_message_receiver_image_view);



        }//GroupViewHolder()

    }//GroupViewHolder

    @NonNull
    @Override
    public GroupAdapter.GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_groups_layout, parent, false);

        mAuth = FirebaseAuth.getInstance();

        return new GroupAdapter.GroupViewHolder(view);

    }//onCreateViewHolder

    @Override
    public void onBindViewHolder(@NonNull final GroupViewHolder holder, final int position) {

        String messageSenderID = mAuth.getCurrentUser().getUid();
        Messages messages = userMessagesList.get(position);

        RootRef = FirebaseDatabase.getInstance().getReference();

        String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();

        UsersRef = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(fromUserID);

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("image")){

                    String receiverImage = dataSnapshot.child("image").getValue().toString();

                    Picasso.get().load(receiverImage).placeholder(R.drawable.profile_image)
                            .into(holder.receiverGroupProfileImage);

                }//if
                else if(dataSnapshot.hasChild("name")){

                    String receiverName = dataSnapshot.child("name").getValue().toString();
                    holder.receiverNameMessageText.setText(receiverName);

                }//else if

            }//onDataChange

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }//onCancelled

        });

        holder.receiverGroupMessageText.setVisibility(View.GONE);
        holder.receiverGroupProfileImage.setVisibility(View.GONE);
        holder.senderGroupMessageText.setVisibility(View.GONE);
        holder.groupMessageSenderPicture.setVisibility(View.GONE);
        holder.groupMessageReceiverPicture.setVisibility(View.GONE);

        holder.receiverNameMessageText.setVisibility(View.GONE);
        holder.receiverNameImage.setVisibility(View.GONE);
        holder.senderDateTimeText.setVisibility(View.GONE);
        holder.receiverDateTimeText.setVisibility(View.GONE);
        holder.senderDateTimeImage.setVisibility(View.GONE);
        holder.receiverDateTimeImage.setVisibility(View.GONE);

        if(fromMessageType.equals("text")){

            if(fromUserID.equals(messageSenderID)){

                holder.senderGroupMessageText.setVisibility(View.VISIBLE);
                holder.senderDateTimeText.setVisibility(View.VISIBLE);

                holder.senderGroupMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
                holder.senderGroupMessageText.setText(messages.getMessage());
                holder.senderDateTimeText.setText(messages.getTime() + " - " + messages.getDate());

            }//if
            else {

                holder.receiverNameMessageText.setVisibility(View.VISIBLE);
                holder.receiverGroupProfileImage.setVisibility(View.VISIBLE);
                holder.receiverGroupMessageText.setVisibility(View.VISIBLE);
                holder.receiverDateTimeText.setVisibility(View.VISIBLE);

                holder.receiverNameMessageText.setText(messages.getUserName());
                holder.receiverGroupMessageText.setBackgroundResource(R.drawable.receiver_messages_layout);
                //holder.receiverGroupMessageText.setText(messages.getMessage() + "\n \n" + messages.getTime() + " - " + messages.getDate());
                holder.receiverGroupMessageText.setText(messages.getMessage());
                holder.receiverDateTimeText.setText(messages.getTime() + " - " + messages.getDate());

            }//else

        }//if
        else if(fromMessageType.equals("image")){

            if(fromUserID.equals(messageSenderID)){

                holder.groupMessageSenderPicture.setVisibility(View.VISIBLE);
                holder.senderDateTimeImage.setVisibility(View.VISIBLE);

                Picasso.get().load(messages.getMessage()).into(holder.groupMessageSenderPicture);
                holder.senderDateTimeImage.setText(messages.getTime() + " - " + messages.getDate());

            }//if
            else {

                holder.receiverNameImage.setVisibility(View.VISIBLE);
                //holder.receiverGroupProfileImage.setVisibility(View.VISIBLE);
                holder.groupMessageReceiverPicture.setVisibility(View.VISIBLE);
                holder.receiverDateTimeImage.setVisibility(View.VISIBLE);

                holder.receiverNameImage.setText(messages.getUserName());
                Picasso.get().load(messages.getMessage()).into(holder.groupMessageReceiverPicture);
                holder.receiverDateTimeImage.setText(messages.getTime() + " - " + messages.getDate());

            }//else

        }//else if
        else if(fromMessageType.equals("pdf") || fromMessageType.equals("docx")) {

            if(fromUserID.equals(messageSenderID)){

                holder.groupMessageSenderPicture.setVisibility(View.VISIBLE);
                holder.senderDateTimeImage.setVisibility(View.VISIBLE);

                Picasso.get()
                        .load("https://firebasestorage.googleapis.com/v0/b/orangeapp-6d5cb.appspot.com/o/Image%20Files%2Ffile.png?alt=media&token=c7bd88c5-958d-4b91-8a31-5c08bf3e1a26")
                        .into(holder.groupMessageSenderPicture);

                holder.senderDateTimeImage.setText(messages.getTime() + " - " + messages.getDate());

            }//if
            else {

                holder.receiverNameImage.setVisibility(View.VISIBLE);
                holder.receiverGroupProfileImage.setVisibility(View.VISIBLE);
                holder.groupMessageReceiverPicture.setVisibility(View.VISIBLE);
                holder.receiverDateTimeImage.setVisibility(View.VISIBLE);

                holder.receiverNameImage.setText(messages.getUserName());
                Picasso.get()
                        .load("https://firebasestorage.googleapis.com/v0/b/orangeapp-6d5cb.appspot.com/o/Image%20Files%2Ffile.png?alt=media&token=c7bd88c5-958d-4b91-8a31-5c08bf3e1a26")
                        .into(holder.groupMessageReceiverPicture);

                holder.receiverDateTimeImage.setText(messages.getTime() + " - " + messages.getDate());

            }//else

        }//else

        if(fromUserID.equals(messageSenderID)){

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(userMessagesList.get(position).getType().equals("pdf") || userMessagesList.get(position).getType().equals("docx")){

                        CharSequence options[] = new CharSequence[]{

                                "Удалить у меня в чате",
                                "Загрузить и просмотреть этот документ",
                                "Отменить",
                                "Удалить у всех в чате"

                        };//CharSequence options[]

                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Удалить сообщение?");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if(which == 0){

                                    deleteSentMessage(position, holder);

                                    Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);

                                }//if
                                else if(which == 1){

                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                                    holder.itemView.getContext().startActivity(intent);

                                }//else if
                                else if(which == 3){

                                    deleteMessageForEveryOne(position, holder);

                                    Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);

                                }//else if

                            }//onClick

                        });//builder.setItems

                        builder.show();

                    }//if
                    else if(userMessagesList.get(position).getType().equals("text")){

                        CharSequence options[] = new CharSequence[]{

                                "Удалить у меня в чате",
                                "Отменить",
                                "Удалить у всех в чате"

                        };//CharSequence options[]

                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Удалить сообщение?");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if(which == 0){

                                    deleteSentMessage(position, holder);

                                    Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);

                                }//if
                                else if(which == 2){

                                    deleteMessageForEveryOne(position, holder);

                                    Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);

                                }//else if

                            }//onClick

                        });//builder.setItems

                        builder.show();

                    }//else if
                    else if(userMessagesList.get(position).getType().equals("image")){

                        CharSequence options[] = new CharSequence[]{

                                "Удалить у меня в чате",
                                "Просмотреть это фото",
                                "Отменить",
                                "Удалить у всех в чате"

                        };//CharSequence options[]

                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Удалить сообщение?");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if(which == 0){

                                    deleteSentMessage(position, holder);

                                    Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);

                                }//if
                                else if(which == 1){

                                    Intent intent = new Intent(holder.itemView.getContext(), ImageViewerActivity.class);
                                    intent.putExtra("url", userMessagesList.get(position).getMessage());
                                    holder.itemView.getContext().startActivity(intent);

                                }//else if
                                else if(which == 3){

                                    deleteMessageForEveryOne(position, holder);

                                    Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);

                                }//else if

                            }//onClick

                        });//builder.setItems

                        builder.show();

                    }//else if

                }//onClick

            });

        }//if
        else {

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(userMessagesList.get(position).getType().equals("pdf") || userMessagesList.get(position).getType().equals("docx")){

                        CharSequence options[] = new CharSequence[]{

                                "Удалить у меня в чате",
                                "Загрузить и просмотреть этот документ",
                                "Отменить"

                        };//CharSequence options[]

                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Удалить сообщение?");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if(which == 0){

                                    deleteReceiveMessage(position, holder);

                                    Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);

                                }//if
                                else if(which == 1){

                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                                    holder.itemView.getContext().startActivity(intent);

                                }//else if

                            }//onClick

                        });//builder.setItems

                        builder.show();

                    }//if
                    else if(userMessagesList.get(position).getType().equals("text")){

                        CharSequence options[] = new CharSequence[]{

                                "Удалить у меня в чате",
                                "Отменить"

                        };//CharSequence options[]

                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Удалить сообщение?");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if(which == 0){

                                    deleteReceiveMessage(position, holder);

                                    Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);

                                }//if

                            }//onClick

                        });//builder.setItems

                        builder.show();

                    }//else if
                    else if(userMessagesList.get(position).getType().equals("image")){

                        CharSequence options[] = new CharSequence[]{

                                "Удалить у меня в чате",
                                "Просмотреть это фото",
                                "Отменить"

                        };//CharSequence options[]

                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Удалить сообщение?");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if(which == 0){

                                    deleteReceiveMessage(position, holder);

                                    Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);

                                }//if
                                else if(which == 1){

                                    Intent intent = new Intent(holder.itemView.getContext(), ImageViewerActivity.class);
                                    intent.putExtra("url", userMessagesList.get(position).getMessage());
                                    holder.itemView.getContext().startActivity(intent);

                                }//else if

                            }//onClick

                        });//builder.setItems

                        builder.show();

                    }//else if

                }//onClick

            });

        }//else

    }//onBindViewHolder

    @Override
    public int getItemCount() {

        return userMessagesList.size();

    }//getItemCount

    private void deleteSentMessage(final int position, final GroupAdapter.GroupViewHolder holder){

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Groups")
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    Toast.makeText(holder.itemView.getContext(), "Удалено", Toast.LENGTH_SHORT).show();

                }//if
                else {

                    Toast.makeText(holder.itemView.getContext(), "Ошибка", Toast.LENGTH_SHORT).show();

                }//else

            }//onComplete
        });

    }//deleteSentMessage

    private void deleteReceiveMessage(final int position, final GroupAdapter.GroupViewHolder holder){

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Groups")
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    Toast.makeText(holder.itemView.getContext(), "Удалено", Toast.LENGTH_SHORT).show();

                }//if
                else {

                    Toast.makeText(holder.itemView.getContext(), "Ошибка", Toast.LENGTH_SHORT).show();

                }//else

            }//onComplete
        });

    }//deleteReceiveMessage

    private void deleteMessageForEveryOne(final int position, final GroupAdapter.GroupViewHolder holder){

        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Groups")
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    rootRef.child("Groups")
                            .child(userMessagesList.get(position).getFrom())
                            .child(userMessagesList.get(position).getTo())
                            .child(userMessagesList.get(position).getMessageID())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                Toast.makeText(holder.itemView.getContext(), "Удалено", Toast.LENGTH_SHORT).show();

                            }//if

                        }//onComplete

                    });

                }//if
                else {

                    Toast.makeText(holder.itemView.getContext(), "Ошибка", Toast.LENGTH_SHORT).show();

                }//else

            }//onComplete
        });

    }//deleteMessageForEveryOne

}//GroupAdapter
