package com.example.orange_app.adapters;

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
import com.example.orange_app.R;
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

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> userMessagesList;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;

    public MessageAdapter(List<Messages> userMessagesList){

        this.userMessagesList = userMessagesList;

    }//MessageAdapter

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView senderMessageText, receiverMessageText;
        public CircleImageView receiverProfileImage;
        public ImageView messageSenderPicture, messageReceiverPicture;


        public MessageViewHolder(@NonNull View itemView) {

            super(itemView);

            senderMessageText = (TextView) itemView.findViewById(R.id.sender_message_text);
            receiverMessageText = (TextView) itemView.findViewById(R.id.receiver_message_text);
            receiverProfileImage = (CircleImageView) itemView.findViewById(R.id.message_profile_image);
            messageSenderPicture = itemView.findViewById(R.id.message_sender_image_view);
            messageReceiverPicture = itemView.findViewById(R.id.message_receiver_image_view);

        }//MessageViewHolder

    }//MessageViewHolder

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_messages_layout, parent, false);

        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);

    }//onCreateViewHolder

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position) {

        String messageSenderID = mAuth.getCurrentUser().getUid();
        Messages messages = userMessagesList.get(position);

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
                            .into(holder.receiverProfileImage);

                }//if

            }//onDataChange

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }//onCancelled

        });

        holder.receiverMessageText.setVisibility(View.GONE);
        holder.receiverProfileImage.setVisibility(View.GONE);
        holder.senderMessageText.setVisibility(View.GONE);
        holder.messageSenderPicture.setVisibility(View.GONE);
        holder.messageReceiverPicture.setVisibility(View.GONE);

        if(fromMessageType.equals("text")){

            if(fromUserID.equals(messageSenderID)){

                holder.senderMessageText.setVisibility(View.VISIBLE);

                holder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
                holder.senderMessageText.setText(messages.getMessage() + "\n \n" + messages.getTime() + " - " + messages.getDate());

            }//if
            else {

                holder.receiverProfileImage.setVisibility(View.VISIBLE);
                holder.receiverMessageText.setVisibility(View.VISIBLE);

                holder.receiverMessageText.setBackgroundResource(R.drawable.receiver_messages_layout);
                holder.receiverMessageText.setText(messages.getMessage() + "\n \n" + messages.getTime() + " - " + messages.getDate());

            }//else

        }//if
        else if(fromMessageType.equals("image")){

            if(fromUserID.equals(messageSenderID)){

                holder.messageSenderPicture.setVisibility(View.VISIBLE);

                Picasso.get().load(messages.getMessage()).into(holder.messageSenderPicture);

            }//if
            else {

                holder.receiverProfileImage.setVisibility(View.VISIBLE);
                holder.messageReceiverPicture.setVisibility(View.VISIBLE);

                Picasso.get().load(messages.getMessage()).into(holder.messageReceiverPicture);

            }//else

        }//else if
        else if(fromMessageType.equals("pdf") || fromMessageType.equals("docx")) {

            if(fromUserID.equals(messageSenderID)){

                holder.messageSenderPicture.setVisibility(View.VISIBLE);

                Picasso.get()
                        .load("https://firebasestorage.googleapis.com/v0/b/orangeapp-6d5cb.appspot.com/o/Image%20Files%2Ffile.png?alt=media&token=c7bd88c5-958d-4b91-8a31-5c08bf3e1a26")
                        .into(holder.messageSenderPicture);


            }//if
            else {

                holder.receiverProfileImage.setVisibility(View.VISIBLE);
                holder.messageReceiverPicture.setVisibility(View.VISIBLE);

                Picasso.get()
                        .load("https://firebasestorage.googleapis.com/v0/b/orangeapp-6d5cb.appspot.com/o/Image%20Files%2Ffile.png?alt=media&token=c7bd88c5-958d-4b91-8a31-5c08bf3e1a26")
                        .into(holder.messageReceiverPicture);


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

    private void deleteSentMessage(final int position, final MessageViewHolder holder){

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
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

    private void deleteReceiveMessage(final int position, final MessageViewHolder holder){

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
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

    private void deleteMessageForEveryOne(final int position, final MessageViewHolder holder){

        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    rootRef.child("Messages")
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

}//MessageAdapter
