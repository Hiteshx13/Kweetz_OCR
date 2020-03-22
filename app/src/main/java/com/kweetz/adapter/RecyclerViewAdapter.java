package com.kweetz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kweetz.R;
import com.kweetz.database.model.Receipt;
import com.kweetz.listener.OnItemClickListener;

import java.util.ArrayList;

/**
 * Created by JUNED on 6/10/2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    ArrayList<Receipt> arrayTitle;
    Context context;
    View view1;
    TextView textView;
    OnItemClickListener listener;

    public RecyclerViewAdapter(Context context1, ArrayList<Receipt> arrayTitle, OnItemClickListener listener) {
        this.arrayTitle = arrayTitle;
        this.context = context1;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        private LinearLayout llRoot;

        public ViewHolder(View v) {

            super(v);

            textView = (TextView) v.findViewById(R.id.tvReceiptTitle);
            llRoot = (LinearLayout) v.findViewById(R.id.llRoot);
        }
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view1 = LayoutInflater.from(context).inflate(R.layout.row_receipt_list, parent, false);
        return new ViewHolder(view1);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.textView.setText(arrayTitle.get(position).getReceiptNo());
        holder.llRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {

        return arrayTitle.size();
    }
}