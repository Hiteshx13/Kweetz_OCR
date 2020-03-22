package com.kweetz.utils

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.kweetz.R
import com.kweetz.listener.listener


fun showReceiptDialog(context: Context, listener: listener.onSelectReceiptListener, totalReceipts: Int) {

    var mDialog = Dialog(context)
    mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    mDialog.setContentView(R.layout.dialog_select_receipt)
    mDialog.window.setBackgroundDrawableResource(android.R.color.transparent);
    val btnRecent: AppCompatButton
    val btnCamera: AppCompatButton
    val btnGallery: AppCompatButton
    var llRecent: LinearLayout

    btnRecent = mDialog.findViewById(R.id.btnRecent)
    btnCamera = mDialog.findViewById(R.id.btnCamera)
    btnGallery = mDialog.findViewById(R.id.btnGallery)
    llRecent = mDialog.findViewById(R.id.llRecent)


    if (totalReceipts > 0) llRecent.visibility = View.VISIBLE else llRecent.visibility = View.GONE

    btnRecent.setOnClickListener(object : View.OnClickListener {
        override fun onClick(view: View?) {
            mDialog.cancel()
            listener.onRecentSelected()
        }

    })
    btnCamera.setOnClickListener(object : View.OnClickListener {
        override fun onClick(view: View?) {
            mDialog.cancel()
            listener.onRecentCameraSelected()
        }

    })
    btnGallery.setOnClickListener(object : View.OnClickListener {
        override fun onClick(view: View?) {
            mDialog.cancel()
            listener.onRecentGallerySelected()
        }

    })

    mDialog.show()
}
