package com.kweetz.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.loader.content.AsyncTaskLoader
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.Text
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.google.firebase.FirebaseApp
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import com.kweetz.R
import com.kweetz.database.model.Receipt
import com.kweetz.databinding.ActivityAddReceiptBinding
import com.kweetz.listener.listener
import com.kweetz.model.ModelAsyncResult
import com.kweetz.model.ModelReceiptData
import com.kweetz.utils.*
import java.io.FileNotFoundException


class AddReceiptActivity : BaseActivity(), View.OnClickListener {
    lateinit var binding: ActivityAddReceiptBinding

    var imageUri: Uri? = null
    var totalReceipts = 0
    var receipt = Receipt()
    var model = ModelAsyncResult()

    companion object {
        fun getIntent(context: Context, receipt: Receipt? = Receipt()): Intent {
            var intent = Intent(context, AddReceiptActivity::class.java)
            intent.putExtra(RECEIPT, receipt)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_receipt)
        initialization()
        onClickListener()

        if (intent != null && intent.hasExtra(RECEIPT)) {
            var data = ModelAsyncResult(null, intent!!.getParcelableExtra(RECEIPT) as Receipt, null)
            updateUI(data)
        }
    }

    fun initialization() {
        receipt = intent.getParcelableExtra(RECEIPT) ?: Receipt()
        totalReceipts = roomDB.productsDao().getAllReceipts().size

    }


    fun onClickListener() {
        binding.llAutoFill.setOnClickListener(this)
        binding.llSave.setOnClickListener(this)
    }

    fun firebaseTextDetection(context: Context,  data: Intent?, imageUri: Uri?, reqCode: Int){

        var pointer = 0
        var arrayParent = ArrayList<ArrayList<String>?>()
        lateinit var listParent: HashMap<Int, ArrayList<ModelReceiptData>?>
        var textRecognizer: TextRecognizer = TextRecognizer.Builder(context).build()
         var receipt= Receipt()
        lateinit var bitmap: Bitmap
        lateinit var bitmapTemp: Bitmap
        lateinit var bitmapOverlay: Bitmap

        try {
            arrayParent = ArrayList()
            val frame: Frame
            if (reqCode == REQUEST_CODE_CAMERA) {
                bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
            } else {
                val imageStream = context.contentResolver.openInputStream(data!!.data!!)
                bitmap = BitmapFactory.decodeStream(imageStream)
            }
//                firebaseTextDetection(bitmap)
            bitmapTemp = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)

            if (bitmap.width > bitmap.height) {
                var matrix = Matrix()
                matrix.postRotate(90f)

                var scalled = Bitmap.createScaledBitmap(bitmap, bitmap.width, bitmap.height, true)
                bitmap = Bitmap.createBitmap(scalled, 0, 0, scalled.width, scalled.height, matrix, true)
            }

            bitmap = convevrtToGrayscale(bitmap)
            bitmapOverlay = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)

            frame = Frame.Builder().setBitmap(bitmap).build()
            val items = textRecognizer.detect(frame)

            listParent = HashMap()
            val stringBuilder = StringBuilder()


            val image: FirebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap)
            val detector: FirebaseVisionTextRecognizer = FirebaseVision.getInstance()
                    .onDeviceTextRecognizer
            val result: Task<FirebaseVisionText> = detector.processImage(image)
                    .addOnSuccessListener {
                        val resultText = it.text
                        for (block in it.textBlocks) {
                            val blockText = block.text
                            val blockConfidence = block.confidence
                            val blockLanguages = block.recognizedLanguages
                            val blockCornerPoints = block.cornerPoints
                            val blockFrame = block.boundingBox
                            for (line in block.lines) {
                                val lineText = line.text

                                val lineConfidence = line.confidence
                                val lineLanguages = line.recognizedLanguages
                                val lineCornerPoints = line.cornerPoints
                                val lineFrame = line.boundingBox
//                                for (element in line.elements) {
                                    val item = line

//                                        var item = blockText.components[zz]
                                    var posTop = item.boundingBox!!.top
                                    Log.d("#Firebase: ",""+item.text+"_top:"+item.boundingBox!!.top)
                                    var array = ArrayList<ModelReceiptData>()
                                    if (listParent.containsKey(posTop)) {
                                        array = listParent[posTop] as ArrayList<ModelReceiptData>
                                    }
                                    var tLeft = item.boundingBox!!.left
                                    var tTop = item.boundingBox!!.top
                                    var tRight = item.boundingBox!!.right
                                    var tBottom = item.boundingBox!!.bottom
                                    array.add(ModelReceiptData(tLeft, tTop, tRight, tBottom, item.text))
                                    listParent[posTop] = array
                                    stringBuilder.append(item.text)

                                    if (receipt.receiptIssuer.isEmpty()) {
                                        receipt.receiptIssuer = gerReceiptIssuer(replaceSpecialChar(item.text.trim()))
                                    }

                                    if (isDateTimePattern(item.text)) {
                                        receipt.receiptDate = item.text.trim()
                                    } else if (isDatePattern(item.text)) {
                                        val receiptTime = replaceSpace(receipt.receiptDate)
                                        receipt.receiptDate = item.text.trim() + " " + receiptTime
                                    } else if (isTimePattern(item.text)) {
                                        val receiptdate = receipt.receiptDate
                                        receipt.receiptDate = receiptdate + " " + replaceSpace(item.text)
                                    }
                                    stringBuilder.append("\n")


////                                    Log.d("#Firebase: ",""+elementText)
//                                    val elementConfidence = element.confidence
//                                    val elementLanguages = element.recognizedLanguages
//                                    val elementCornerPoints = element.cornerPoints
//                                    val elementFrame = element.boundingBox
//                                }
                            }
                        }
                        receipt.receiptFullText = stringBuilder.toString().trim()
                        updateUI(ModelAsyncResult(bitmapOverlay, receipt, listParent))
                    }
                    .addOnFailureListener(
                            object : OnFailureListener {
                                override fun onFailure(p0: java.lang.Exception) {
                                    Log.d("#Firebase: ","")
                                }

                            })

//                    }
//                }


        } catch (e: FileNotFoundException) {
            e.printStackTrace()

        }

        }
    fun updateUI(result: ModelAsyncResult?) {

        var listTemp = HashMap<Int, ArrayList<ModelReceiptData>?>()
        binding.model = result?.receipt
        binding.notifyChange()
        if (result?.bitmap?.width ?: 0 > 0) {

            var halfLength = (result!!.bitmap?.width ?: 0) / 2
            var thirdLength = (result.bitmap?.width ?: 0) / 3

            if (!result.listParent.isNullOrEmpty()) {
                for (i in result.listParent!!) {
                    var arrayList: ArrayList<ModelReceiptData>? = i.value
                    arrayList?.forEach { data ->

                        var height: Int = (data.bottom - data.top)
                        var min = data.top - (height / 2)
                        var max = data.bottom + (height / 2)

                        Log.d("#Data_", "" + data.text + "_height" + height + "_top_" + data.top + "_min_" + min + "_max_" + max)
                        if (data.left > thirdLength) {
                            var array = ArrayList<ModelReceiptData>()


                            var isNew = true
                            var isAdded = false
                            for (j in min..max) {

                                if (result.listParent!!.containsKey(j) /*&& j != data.top*/) {
                                    array = result.listParent!![j] as ArrayList<ModelReceiptData>

                                    if (isAlphaNumerical(data.text)) {
                                        array.forEach { childItem ->
                                            if (childItem.text.equals(data.text, true)) {
                                                isNew = false
                                            }
                                        }
                                        if (isNew) {
                                            array.add(data)
                                            listTemp[j] = array
                                            isAdded = true
                                            break
                                        } else {
                                            listTemp[j] = array
                                            isAdded = true
                                        }

                                    }
                                } else {
//                                    array.add(data)
                                    listTemp[data.top] = array
                                    isAdded = true
                                }
                            }

                            if (!isAdded) {
                                array.add(data)
                                listTemp[data.top] = array
                            }

                        } else {
                            listTemp[i.key] = i.value
                        }
                    }
                }
            }
        }


//        var listOpt = HashMap<Int, ArrayList<ModelReceiptData>?>()
//        for (i in listTemp) {
//            if (i.value?.size ?: 0 > 1) {
//                listOpt[i.key] = i.value
//            }
//        }

        for (i in listTemp) {
            var array = i.value as ArrayList<ModelReceiptData>
            for (j in 0 until array.size) {
                var model = array[j]
//7,12_height63_top_4005_min_3974_max_4099
// Samaksai EUR_height70_top_3795_min_3760_max_3900
                // array.forEach { model ->
                if (result?.receipt?.receiptNo?.isEmpty() == true) {
                    var receiptNo = ""
                    var receiptNumber = getReceiptNumber(model.text)
                    if (receiptNumber.isNotEmpty()) {

                        var trimmed = model.text.replace(receiptNumber, "", true).replace("o", "0", true).trim()

                        if (!trimmed.isEmpty()) {
                            // receiptNo = array[j + 1].text
//                        } else {
                            if (isContainNumerical(trimmed)) {
                                // if (isContainNumerical(trimmed)) {
                                receiptNo = trimmed
                                if (trimmed.contains("\n")) {
                                    receiptNo = trimmed.substring(0, trimmed.indexOf("\n"))
                                }
                                //}
                            }
                        }

                    }
                    result.receipt?.receiptNo = receiptNo
                }
//3,96_height55_top_3311_min_3293_max_3384
//Sanaksai EUR_height_67_top_3113_min_3091_max_3202
                if (isReceiptTotal(model.text)) {
                    var receipt = result?.receipt
                    try {
                        receipt?.receiptTotal = array[j + 1].text.toString()
                    } catch (e: Exception) {

                    }
                    binding.model = receipt
                    binding.notifyChange()
                }
            }
        }

        var receipt = result?.receipt
        //receipt?.receiptTotal = array[j + 1].text.toString()
        binding.model = receipt
        binding.notifyChange()
        binding.indeterminateBar.visibility = View.GONE
    }


    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.llSave -> {
                var receiptNumber = binding.etReceiptNumber.text.toString()
                var receiptDate = binding.etReceiptDate.text.toString()
                var receiptIssuer = binding.etReceiptIssuer.text.toString()
                var receiptTotal = binding.etReceiptTotal.text.toString()
                // var receiptDesc = binding.etReceiptDescription.text.toString()
                var receiptFullText = binding.etFullReceipt.text.toString()

                if (receiptNumber.trim().isNotEmpty()) {
                    var receipt = Receipt(0, receiptNumber, receiptDate, receiptIssuer, receiptTotal, "", receiptFullText)
                    roomDB.productsDao().insertAll(receipt)
                    finish()
                } else {
                    showToast(this, "Please enter receipt number.")
                }
            }
            R.id.llAutoFill -> {

                showReceiptDialog(this, object : listener.onSelectReceiptListener {
                    @RequiresApi(Build.VERSION_CODES.M)
                    override fun onRecentCameraSelected() {
                        super.onRecentCameraSelected()
                        if (ContextCompat.checkSelfPermission(
                                        this@AddReceiptActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(arrayOf(Manifest.permission.CAMERA), PERMISSIONS_REQUEST_CAMERA)
                        } else {
                            getImageFromCamera()
                        }
                    }

                    override fun onRecentGallerySelected() {
                        super.onRecentGallerySelected()
                        if (ContextCompat.checkSelfPermission(this@AddReceiptActivity,
                                        Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {

                            if (ActivityCompat.shouldShowRequestPermissionRationale(this@AddReceiptActivity,
                                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                Toast.makeText(this@AddReceiptActivity, "Please grant all required permissions from application settings", Toast.LENGTH_LONG).show()

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
                        var receipt: Receipt = roomDB.productsDao().getReceipt(roomDB.productsDao().getCount())
                        binding.model = receipt
                        binding.notifyChange()
                    }
                }, totalReceipts)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getImageFromAlbum()
                } else {
                    showToast(this, this@AddReceiptActivity.getString(R.string.grant_all_permission))
                }
                return
            }
            PERMISSIONS_REQUEST_CAMERA -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getImageFromCamera()
                } else {
                    showToast(this, this@AddReceiptActivity.getString(R.string.grant_all_permission))
                }
                return
            }

            else -> {

            }
        }
    }

    private fun getImageFromAlbum() {
        imageUri = null
        try {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY)
        } catch (exp: Exception) {
            Log.i("Error", exp.toString())
        }

    }

    private fun getImageFromCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        imageUri = getCameraUri(this)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA)
        }

    }


    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            var bitmap: Bitmap
            if (reqCode == REQUEST_CODE_CAMERA) {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            } else {
                val imageStream = contentResolver.openInputStream(data!!.data!!)
                bitmap = BitmapFactory.decodeStream(imageStream)
            }


            //  binding.rlBackground.background = BitmapDrawable(toGrayscale(bitmap) )
            data?.putExtra(getString(R.string.request_code), reqCode)
            data?.putExtra(getString(R.string.image_uri), imageUri)
            binding.indeterminateBar.visibility = View.VISIBLE
//            var mLoader = MyAsyncTask(this, data, imageUri, reqCode)
             firebaseTextDetection(this, data, imageUri, reqCode)
//            mLoader.registerListener(0) { loader, receipt ->
//
//                Log.d("#run", "over")
//                binding.indeterminateBar.visibility = View.GONE
//
//                //binding.rlBackground.background = BitmapDrawable(this.resources, receipt!!.bitmap)
//                updateUI(receipt)
//            }
//            mLoader.startLoading()

        } else {
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show()
        }
    }

    class MyAsyncTask(context: Context, var data: Intent?, var imageUri: Uri?, var reqCode: Int) : AsyncTaskLoader<ModelAsyncResult>(context) {

        var pointer = 0
        var arrayParent = ArrayList<ArrayList<String>?>()
        lateinit var listParent: HashMap<Int, ArrayList<ModelReceiptData>?>
        var textRecognizer: TextRecognizer = TextRecognizer.Builder(context).build()
        lateinit var receipt: Receipt
        lateinit var bitmap: Bitmap
        lateinit var bitmapTemp: Bitmap
        lateinit var bitmapOverlay: Bitmap

        //var reqCode = data?.getIntExtra(context.getString(R.string.request_code), 0)
        var paint = Paint()

        override fun onStartLoading() {
            forceLoad()
            Log.d("#run", "onStartLoading")
        }

        override fun loadInBackground(): ModelAsyncResult? {
            receipt = Receipt()
            textRecognizer.setProcessor(object : Detector.Processor<TextBlock> {
                override fun release() {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                /**
                 * Detect all the text from camera using TextBlock and the values into a stringBuilder
                 * which will then be set to the textView.
                 */
                override fun receiveDetections(detections: Detector.Detections<TextBlock>) {
                    val items = detections.detectedItems

                    if (items.size() != 0) {

                        val stringBuilder = StringBuilder()
                        for (i in 0 until items.size()) {
                            val item = items.valueAt(i)

                            stringBuilder.append(item.value)
                            Log.d("#Detected $i", " = " + item.value)
                            stringBuilder.append("\n")
                        }
                    }
                }
            })

            try {
                arrayParent = ArrayList()
                val frame: Frame
                if (reqCode == REQUEST_CODE_CAMERA) {
                    bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
                } else {
                    val imageStream = context.contentResolver.openInputStream(data!!.data!!)
                    bitmap = BitmapFactory.decodeStream(imageStream)
                }
//                firebaseTextDetection(bitmap)
                bitmapTemp = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)

                if (bitmap.width > bitmap.height) {
                    var matrix = Matrix()
                    matrix.postRotate(90f)

                    var scalled = Bitmap.createScaledBitmap(bitmap, bitmap.width, bitmap.height, true)
                    bitmap = Bitmap.createBitmap(scalled, 0, 0, scalled.width, scalled.height, matrix, true)
                }

                bitmap = convevrtToGrayscale(bitmap)
                bitmapOverlay = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)

                frame = Frame.Builder().setBitmap(bitmap).build()
                val items = textRecognizer.detect(frame)

                listParent = HashMap()
                val stringBuilder = StringBuilder()


                val image: FirebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap)
                val detector: FirebaseVisionTextRecognizer = FirebaseVision.getInstance()
                        .onDeviceTextRecognizer
                val result: Task<FirebaseVisionText> = detector.processImage(image)
                        .addOnSuccessListener {
                            val resultText = it.text
                            for (block in it.textBlocks) {
                                val blockText = block.text
                                val blockConfidence = block.confidence
                                val blockLanguages = block.recognizedLanguages
                                val blockCornerPoints = block.cornerPoints
                                val blockFrame = block.boundingBox
                                for (line in block.lines) {
                                    val lineText = line.text

                                    val lineConfidence = line.confidence
                                    val lineLanguages = line.recognizedLanguages
                                    val lineCornerPoints = line.cornerPoints
                                    val lineFrame = line.boundingBox
                                    for (element in line.elements) {
                                        val item = element

//                                        var item = blockText.components[zz]
                                        var posTop = item.boundingBox!!.top
                                        Log.d("#Firebase: ",""+item.text+"_top:"+item.boundingBox!!.top)
                                        var array = ArrayList<ModelReceiptData>()
                                        if (listParent.containsKey(posTop)) {
                                            array = listParent[posTop] as ArrayList<ModelReceiptData>
                                        }
                                        var tLeft = item.boundingBox!!.left
                                        var tTop = item.boundingBox!!.top
                                        var tRight = item.boundingBox!!.right
                                        var tBottom = item.boundingBox!!.bottom
                                        array.add(ModelReceiptData(tLeft, tTop, tRight, tBottom, item.text))
                                        listParent[posTop] = array
                                        stringBuilder.append(item.text)

                                        if (receipt.receiptIssuer.isEmpty()) {
                                            receipt.receiptIssuer = gerReceiptIssuer(replaceSpecialChar(item.text.trim()))
                                        }

                                        if (isDateTimePattern(item.text)) {
                                            receipt.receiptDate = item.text.trim()
                                        } else if (isDatePattern(item.text)) {
                                            val receiptTime = replaceSpace(receipt.receiptDate)
                                            receipt.receiptDate = item.text.trim() + " " + receiptTime
                                        } else if (isTimePattern(item.text)) {
                                            val receiptdate = receipt.receiptDate
                                            receipt.receiptDate = receiptdate + " " + replaceSpace(item.text)
                                        }
                                        stringBuilder.append("\n")


//                                    Log.d("#Firebase: ",""+elementText)
                                        val elementConfidence = element.confidence
                                        val elementLanguages = element.recognizedLanguages
                                        val elementCornerPoints = element.cornerPoints
                                        val elementFrame = element.boundingBox
                                    }
                                }
                            }
                            receipt.receiptFullText = stringBuilder.toString().trim()
                        }
                        .addOnFailureListener(
                                object : OnFailureListener {
                                    override fun onFailure(p0: java.lang.Exception) {
                                        Log.d("#Firebase: ","")
                                    }

                                })

//                    }
//                }


            } catch (e: FileNotFoundException) {
                e.printStackTrace()

            }

            return ModelAsyncResult(bitmapOverlay, receipt, listParent)
        }



        fun createBoundingBox(item: Text) {
            paint.color = Color.RED
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 4f

            paint.textSize = 60f//(item.boundingBox.bottom - item.boundingBox.top).toFloat();

            Log.d("#SIZE : " + item.value, "" + (item.boundingBox.bottom - item.boundingBox.top))
            var canvas = Canvas(bitmapTemp)
            var canvasOverlay = Canvas(bitmapOverlay)

            canvas.drawRect(item.boundingBox, paint)
            canvas.drawText(item.value, item.boundingBox.left.toFloat(), item.boundingBox.top.toFloat(), paint)
            canvasOverlay.drawBitmap(bitmapTemp, Matrix(), null)
        }
//        fun firebaseTextDetection(bitmap:Bitmap){
//
//            val image: FirebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap)
//            val detector: FirebaseVisionTextRecognizer = FirebaseVision.getInstance()
//                    .onDeviceTextRecognizer
//            val result: Task<FirebaseVisionText> = detector.processImage(image)
//                    .addOnSuccessListener {
//                        val resultText = it.text
//                        for (block in it.textBlocks) {
//                            val blockText = block.text
//                            val blockConfidence = block.confidence
//                            val blockLanguages = block.recognizedLanguages
//                            val blockCornerPoints = block.cornerPoints
//                            val blockFrame = block.boundingBox
//                            for (line in block.lines) {
//                                val lineText = line.text
//                                Log.d("#Firebase: ",""+lineText)
//                                val lineConfidence = line.confidence
//                                val lineLanguages = line.recognizedLanguages
//                                val lineCornerPoints = line.cornerPoints
//                                val lineFrame = line.boundingBox
//                                for (element in line.elements) {
//                                    val elementText = element.text
////                                    Log.d("#Firebase: ",""+elementText)
//                                    val elementConfidence = element.confidence
//                                    val elementLanguages = element.recognizedLanguages
//                                    val elementCornerPoints = element.cornerPoints
//                                    val elementFrame = element.boundingBox
//                                }
//                            }
//                        }
//                    }
//                    .addOnFailureListener(
//                            object : OnFailureListener {
//                                override fun onFailure(p0: java.lang.Exception) {
//                                    Log.d("#Firebase: ","")
//                                }
//
//                            })
//
//        }

    }




}