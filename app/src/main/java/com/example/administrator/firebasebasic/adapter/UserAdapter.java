package com.example.administrator.firebasebasic.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.firebasebasic.R;
import com.example.administrator.firebasebasic.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-10-30.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.Holder> {

    List<User> users = new ArrayList<>();
    Callback callback;

    public UserAdapter(Callback callback) {
        this.callback = callback;
    }

    public void setData(List<User> users) {
        this.users = users;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.itemName.setText(users.get(position).username);
        holder.itemAge.setText(users.get(position).age+"");
        holder.itemEmail.setText(users.get(position).email);
        holder.curUserId = users.get(position).user_id;
        holder.position = position;
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        private TextView itemName;
        private TextView itemEmail;
        private TextView itemAge;
        private String curUserId;
        private int position;

        public Holder(View itemView) {
            super(itemView);
            itemName = (TextView) itemView.findViewById(R.id.itemName);
            itemEmail = (TextView) itemView.findViewById(R.id.itemEmail);
            itemAge = (TextView) itemView.findViewById(R.id.itemAge);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.setCurrentUser(curUserId, position);
                }
            });
        }
    }

    public interface Callback {
        void setCurrentUser(String user, int position);
    }

}
