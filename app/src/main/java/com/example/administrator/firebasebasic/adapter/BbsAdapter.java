package com.example.administrator.firebasebasic.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.firebasebasic.R;
import com.example.administrator.firebasebasic.model.Bbs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-10-30.
 */
public class BbsAdapter extends RecyclerView.Adapter<BbsAdapter.Holder> {

    List<Bbs> bbsList = new ArrayList<>();


    public void setData(List<Bbs> bbses) {
        this.bbsList = bbses;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_content, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.itemTitle.setText(bbsList.get(position).title);
    }

    @Override
    public int getItemCount() {
        return bbsList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        private TextView itemTitle;

        public Holder(View itemView) {
            super(itemView);
            itemTitle = (TextView) itemView.findViewById(R.id.itemTitle);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

}
