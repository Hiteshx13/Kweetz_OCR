package com.kweetz.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.kweetz.R
import com.kweetz.databinding.ActivityAddReceiptBinding
import com.kweetz.listener.listener
import com.kweetz.utils.PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
import com.kweetz.utils.ShowReceiptDialog
import com.kweetz.utils.lounchActivity
import java.io.FileNotFoundException


lateinit var binding: ActivityAddReceiptBinding

class AddReceiptActivity : AppCompatActivity(), View.OnClickListener {

    val PICK_FROM_GALLERY = 100
    val PICK_FROM_CAMERA = 200
    lateinit var textRecognizer: TextRecognizer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_receipt)
        initialization()
        onClickListener()
    }

    fun initialization() {
        //Create the TextRecognizer
        textRecognizer = TextRecognizer.Builder(applicationContext).build()

        //Set the TextRecognizer's Processor.
        textRecognizer.setProcessor(object : Detector.Processor<TextBlock> {
            override fun release() {}

            /**
             * Detect all the text from camera using TextBlock and the values into a stringBuilder
             * which will then be set to the textView.
             */
            override fun receiveDetections(detections: Detector.Detections<TextBlock>) {
                val items = detections.detectedItems
                if (items.size() != 0) {

                    binding.etFullReceipt.post(Runnable {
                        /*Frame frame =new Frame.Builder().setBitmap(bitmap).build();
                          SparseArray items = txtRecognizer.detect(frame);*/

                        val stringBuilder = StringBuilder()
                        for (i in 0 until items.size()) {
                            val item = items.valueAt(i)
                            stringBuilder.append(item.value)
                            Log.d("#Detected $i", " = " + item.value)
                            stringBuilder.append("\n")
                        }
                        binding.etFullReceipt.setText(stringBuilder.toString())
                    })
                }
            }
        })
    }

    fun onClickListener() {
        binding.llAutoFill.setOnClickListener(this)
        binding.tvSave.setOnClickListener(this)

    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.tvSave -> {
                finish()
            }
            R.id.llAutoFill -> {

                ShowReceiptDialog(this, object : listener.onSelectReceiptListener {
                    override fun onRecentCameraSelected() {
                        super.onRecentCameraSelected()
                        if (ContextCompat.checkSelfPermission(this@AddReceiptActivity,
                                        Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {

                            if (ActivityCompat.shouldShowRequestPermissionRationale(this@AddReceiptActivity,
                                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                Toast.makeText(this@AddReceiptActivity,"Please grant all required permissions from application settings",Toast.LENGTH_LONG).show()
                            } else {
                                ActivityCompat.requestPermissions(this@AddReceiptActivity,
                                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                        PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
                            }
                        } else {
                            getImageFromAlbum()
                        }
                    }

                    override fun onRecentGallerySelected() {
                        super.onRecentGallerySelected()
                        if (ContextCompat.checkSelfPermission(this@AddReceiptActivity,
                                        Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {

                            if (ActivityCompat.shouldShowRequestPermissionRationale(this@AddReceiptActivity,
                                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                Toast.makeText(this@AddReceiptActivity,"Please grant all required permissions from application settings",Toast.LENGTH_LONG).show()

                            } else {
                                ActivityCompat.requestPermissions(this@AddReceiptActivity,
                                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                        PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
                            }
                        } else {
                            getImageFromAlbum()
                        }
                    }

                    override fun onRecentSelected() {
                        super.onRecentSelected()
                    }
                })
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                if(requestCode.equals(PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)){
                    getImageFromAlbum()
                }

                } else {
                }
                return
            }


            else -> {

            }
        }
    }

    private fun getImageFromAlbum() {
        try {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, PICK_FROM_GALLERY)
        } catch (exp: Exception) {
            Log.i("Error", exp.toString())
        }

    }
    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)


        if (resultCode == Activity.RESULT_OK) {
            try {
                val imageUri = data!!.data
                val imageStream = contentResolver.openInputStream(imageUri!!)
                val selectedImage = BitmapFactory.decodeStream(imageStream)


                val frame = Frame.Builder().setBitmap(selectedImage).build()
                val items = textRecognizer.detect(frame)

                val stringBuilder = StringBuilder()
                for (i in 0 until items.size()) {
                    val item = items.valueAt(i) as TextBlock
                    stringBuilder.append(item.value)
                    Log.d("#Detected $i", " = " + item.value)
                    stringBuilder.append("\n")
                }
                binding.etFullReceipt.setText(stringBuilder.toString())


            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
            }

        } else {
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show()
        }
    }

}