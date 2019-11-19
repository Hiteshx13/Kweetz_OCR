package com.kweetz.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.kweetz.R
import com.kweetz.adapter.ReceiptAdapter
import com.kweetz.adapter.RecyclerViewAdapter
import com.kweetz.databinding.ActivityReceiptListBinding
import com.kweetz.utils.lounchActivity

public class ReceiptListActivity : AppCompatActivity() ,View.OnClickListener {

    lateinit var listReceipts: ArrayList<String>
    lateinit var binding: ActivityReceiptListBinding
    lateinit var adapter: RecyclerViewAdapter
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_receipt_list)
        initialization()
    }

    fun initialization() {
        listReceipts = ArrayList()
        for (i in 0..20) {
            listReceipts.add("Receipt " + i)
        }

        binding.btnNewReceipt.setOnClickListener(this)
        adapter = RecyclerViewAdapter(this, listReceipts)
        binding.rvReceipts.layoutManager = LinearLayoutManager(this)
        binding.rvReceipts.adapter = adapter
    }
    override fun onClick(view: View?) {
         when(view){
             binding.btnNewReceipt->{
                 lounchActivity(this, AddReceiptActivity())
             }
         }
    }

}