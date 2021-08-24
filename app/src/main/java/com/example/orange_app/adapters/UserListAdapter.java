package com.example.orange_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orange_app.R;
import com.example.orange_app.models.Contacts;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> {

    //private List<Contacts> userContactsList;
    private List<Contacts> userContactsList;

    public int membersCount = 0;
    //public Map<String, Object> members = new HashMap<>();

    private FirebaseAuth mAuth;
    private DatabaseReference ContactsRef, UsersRef, GroupNameRef;
    private String currentUserID;
    public UserListViewHolder userListViewHolder;

    public UserListAdapter(List<Contacts> userContactsList){

        this.userContactsList = userContactsList;

    }//UserListAdapter

    public static class UserListViewHolder extends RecyclerView.ViewHolder {

        //public CheckedTextView checkedTextView;
        public CheckBox checkBox;
        public TextView textView;

        public UserListViewHolder(@NonNull View itemView) {

            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.new_group_user_name_text_view);
            checkBox = (CheckBox) itemView.findViewById(R.id.new_group_checked_box);

            //checkedTextView = (CheckedTextView) itemView.findViewById(R.id.new_group_checked_text_view);

        }//UserListViewHolder()

    }//UserListViewHolder

    @NonNull
    @Override
    public UserListAdapter.UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);

        mAuth = FirebaseAuth.getInstance();

        return new UserListAdapter.UserListViewHolder(view);

    }//onCreateViewHolder

    @Override
    public void onBindViewHolder(@NonNull final UserListAdapter.UserListViewHolder holder, final int position) {

        //holder.checkedTextView.setText(userContactsList.get(position).getName());

        holder.textView.setText((userContactsList.get(position).getName()));

        //userContactsList.get(holder.getAdapterPosition()).setSelected(hasFocus);

        //in some cases, it will prevent unwanted situations
        //holder.checkBox.setOnCheckedChangeListener(null);

        //if true, your checkbox will be selected, else unselected
        //holder.checkBox.setChecked(userContactsList.get(position).getSelected());

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                userContactsList.get(holder.getAdapterPosition()).setSelected(isChecked);

                //members = new HashMap();
                /*
                HashMap<String, Object> members = new HashMap<>();
                //members.put("membersCount",membersCount);
                members.put("name",  userContactsList.toString());

                GroupNameRef.child("Groups").child(currentUserID).child("members").updateChildren(members)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {

                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                //SendUserToMainActivity();

                            }//onComplete

                        });
                GroupNameRef.child("Groups").child("Members")
                        .addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if((dataSnapshot.exists()) && (dataSnapshot.hasChild("members"))){

                                    String nMember = dataSnapshot.child("members").getValue().toString();
                                    userListViewHolder.textView.setText(nMember);

                                }//if

                            }//onDataChange

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }//onCancelled
                        });

                 */

            }//onCheckedChanged



        });



    }//onBindViewHolder

    @Override
    public int getItemCount() {

        return userContactsList.size();

    }//getItemCount

}//UserListAdapter
