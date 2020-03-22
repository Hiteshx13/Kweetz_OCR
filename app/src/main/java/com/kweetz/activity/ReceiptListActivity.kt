package com.kweetz.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.kweetz.R
import com.kweetz.adapter.RecyclerViewAdapter
import com.kweetz.database.model.Receipt
import com.kweetz.databinding.ActivityReceiptListBinding
import com.kweetz.listener.OnItemClickListener
import com.kweetz.utils.lounchActivity
import java.text.SimpleDateFormat


public class ReceiptListActivity : BaseActivity(), View.OnClickListener {

    lateinit var listReceipts: ArrayList<Receipt>
    lateinit var binding: ActivityReceiptListBinding
    lateinit var adapter: RecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_receipt_list)
        initialization()

    }


    fun initialization() {

        listReceipts = ArrayList()
        listReceipts.addAll(roomDB.productsDao().getAllReceipts() as ArrayList<Receipt>)

        /**set view visiblity based on data**/
        binding.tvNoData.visibility = if (listReceipts.size == 0) View.VISIBLE else View.GONE
        binding.rvReceipts.visibility = if (listReceipts.size == 0) View.GONE else View.VISIBLE


        binding.btnNewReceipt.setOnClickListener(this)
        adapter = RecyclerViewAdapter(this, listReceipts, object : OnItemClickListener {
            override fun onClick(pos: Int) {
                lounchActivity(applicationContext, AddReceiptActivity.getIntent(applicationContext,listReceipts.get(pos)))
            }
        })
        binding.rvReceipts.layoutManager = LinearLayoutManager(this)
        binding.rvReceipts.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        initialization()
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.btnNewReceipt -> {

                lounchActivity(this, AddReceiptActivity())
                /*var pattern = Regex("-?\\d+(\\.\\d+)?")
                pattern.matches("954")*/
            }

        }
    }

    fun isReceiptDate(strDate: String): Boolean {
        var isDate = false
        var dateFormat1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var dateFormat2 = SimpleDateFormat("yyyy-MM-dd")

        try {
            var date = dateFormat1.parse(strDate)
            isDate = true
        } catch (e: Exception) {
            Log.d("ParseException", "" + e.message)
        }

        try {
            var date = dateFormat2.parse(strDate)
            isDate = true
        } catch (e: Exception) {
            Log.d("ParseException", "" + e.message)
        }
        return isDate
    }


}