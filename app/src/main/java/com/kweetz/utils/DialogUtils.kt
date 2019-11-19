package com.kweetz.utils

import android.app.Dialog
import android.content.Context
import android.support.v7.widget.AppCompatButton
import android.view.View
import android.view.Window
import android.widget.TextView
import com.kweetz.R
import com.kweetz.listener.listener


     fun ShowReceiptDialog(context: Context,listener: listener.onSelectReceiptListener) {

        val mDialog: Dialog
        mDialog = Dialog(context)
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mDialog.setContentView(R.layout.dialog_select_receipt)
         mDialog.window.setBackgroundDrawableResource(android.R.color.transparent);
        val btnRecent: AppCompatButton
        val btnCamera: AppCompatButton
        val btnGallery: AppCompatButton

        btnRecent = mDialog.findViewById(R.id.btnRecent)
        btnCamera = mDialog.findViewById(R.id.btnCamera)
        btnGallery = mDialog.findViewById(R.id.btnGallery)

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
