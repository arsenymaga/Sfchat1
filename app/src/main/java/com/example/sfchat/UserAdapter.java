package com.example.sfchat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
/*показ RecyclerView с помощью карточек с аватаркой и именем*/
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private OnUserClickListener listener;
    private ArrayList<User> users;

    public UserAdapter(ArrayList<User> users) {
        this.users = users;
    }

    public interface OnUserClickListener {
        void onUserClick(int position);
    }

    public void setOnUserClickListener(OnUserClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.useritem, parent, false);

        UserViewHolder viewHolder = new UserViewHolder(view, listener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User currentUser = users.get(position);
        holder.avatarImageView.setImageResource(currentUser.getAvatarImageResource());
        holder.userNameText.setText(currentUser.getName());

    }

    @Override
    public int getItemCount() {

        return users.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        public ImageView avatarImageView;
        public TextView userNameText;


        public UserViewHolder(@NonNull View itemView, final OnUserClickListener listener) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.avatarUser);
            userNameText = itemView.findViewById(R.id.userNameText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onUserClick(position);
                        }
                    }
                }
            });
        }
    }
}
