package com.kweetz.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.kweetz.R
import com.kweetz.databinding.RowReceiptListBinding
import android.widget.LinearLayout





class ReceiptAdapter(var context: Context, val listRecipt: ArrayList<String>) : RecyclerView.Adapter<ReceiptAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(context)

       var binding = DataBindingUtil.inflate(inflater, R.layout.row_receipt_list,parent,false) as RowReceiptListBinding


        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listRecipt.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.rowBinding.tvReceiptTitle.text = listRecipt.get(position)
    }

    class ViewHolder(val rowBinding: RowReceiptListBinding) : RecyclerView.ViewHolder(rowBinding.root) {
    }
}