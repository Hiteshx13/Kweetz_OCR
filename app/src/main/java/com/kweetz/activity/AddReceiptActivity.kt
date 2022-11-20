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
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.vision.text.TextRecognizer
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.kweetz.R
import com.kweetz.database.model.Receipt
import com.kweetz.listener.listener
import com.kweetz.model.ModelAsyncResult
import com.kweetz.model.ModelReceiptData
import com.kweetz.utils.*
import com.scanlibrary.ScanActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.*


class AddReceiptActivity : BaseActivity(), View.OnClickListener {

    val PATH = "path"
    lateinit var binding: com.kweetz.databinding.ActivityAddReceiptBinding

    var imageUri: Uri? = null
    var totalReceipts = 0
    var receipt = Receipt()
    var paint = Paint()
    lateinit var bitmapOriginal: Bitmap
    lateinit var bitmapTemp: Bitmap
    lateinit var bitmapOverlay: Bitmap

    companion object {
        fun getIntent(context: Context, receipt: Receipt? = Receipt()): Intent {
            var intent = Intent(context, AddReceiptActivity::class.java)
            intent.putExtra(RECEIPT, receipt)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        FirebaseApp.initializeApp(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_receipt)
        initialization()
        onClickListener()

        if (intent != null && intent.hasExtra(RECEIPT)) {
            // var data = ModelAsyncResult(null, intent!!.getParcelableExtra(RECEIPT) as Receipt, null)
            //updateUI(data)
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

    fun firebaseTextDetection(context: Context, imageUri: Uri?, reqCode: Int) {

        var pointer = 0
        var arrayParent = ArrayList<ArrayList<String>?>()
        var arrayOriginalData = ArrayList<ModelReceiptData>()
        var textRecognizer: TextRecognizer = TextRecognizer.Builder(context).build()
        var receipt = Receipt()
        lateinit var bitmap: Bitmap




        try {
            arrayParent = ArrayList()
//            val frame: Frame

            val contentResolver = contentResolver
//            try {
//                bitmap = if (Build.VERSION.SDK_INT < 28) {
//                    MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
//                } else {
//                    var file= File(imageUri?.getPath())
//                    file.absolutePath
//                    val source = ImageDecoder.createSource(contentResolver, imageUri!!)
//                    ImageDecoder.decodeBitmap(source)
//                }
//            } catch (e: java.lang.Exception) {
//                e.printStackTrace()
//            }
            if (reqCode == REQUEST_CODE_CAMERA) {
                bitmapOriginal =
                    MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
            } else {
                bitmapOriginal = MediaStore.Images.Media.getBitmap(
                    this.contentResolver,
                    Uri.parse("file://$imageUri")
                )
//                val imageStream = context.contentResolver.openInputStream(data!!.data!!)
//                bitmap = BitmapFactory.decodeStream(imageStream)
            }
//                firebaseTextDetection(bitmap)
            bitmapTemp = Bitmap.createBitmap(
                bitmapOriginal.width,
                bitmapOriginal.height,
                Bitmap.Config.ARGB_8888
            )

            if (bitmapOriginal.width > bitmapOriginal.height) {
                var matrix = Matrix()
                matrix.postRotate(90f)

                var scalled = Bitmap.createScaledBitmap(
                    bitmapOriginal,
                    bitmapOriginal.width,
                    bitmapOriginal.height,
                    true
                )
                bitmapOriginal =
                    Bitmap.createBitmap(scalled, 0, 0, scalled.width, scalled.height, matrix, true)
            }

            bitmap = convevrtToGrayscale(bitmapOriginal)
            bitmapOverlay =
                Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)

//            frame = Frame.Builder().setBitmap(bitmap).build()
//            val items = textRecognizer.detect(frame)

            val stringBuilder = StringBuilder()

            /** Text recognition from image**/
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val image = InputImage.fromBitmap(bitmap, 0)
            val result = recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val resultText = visionText.text
                    val arrayLeft = HashMap<Int, ModelReceiptData>()
                    val arrayRight = HashMap<Int, ModelReceiptData>()
                    var splitLength = bitmapOriginal.width / 2


                    for (block in visionText.textBlocks) {
                        val blockText = block.text
                        val blockCornerPoints = block.cornerPoints
                        val blockFrame = block.boundingBox
                        //createBoundingBox(blockFrame!!,1)


                        for (line in block.lines) {

                            var tLeft = line.boundingBox!!.left
                            var tTop = line.boundingBox!!.top
                            var tRight = line.boundingBox!!.right
                            var tBottom = line.boundingBox!!.bottom


                            val symbolicString = getSymbolicString(line.text)
                            val model = ModelReceiptData(
                                0,
                                tLeft,
                                tTop,
                                tRight,
                                tBottom,
                                line.text,
                                symbolicString
                            )


                            if (tLeft <= splitLength) {

                                arrayLeft[tTop] = model
                                Log.d("#DATA_LEFT", "" + line.text)

                            } else {
                                arrayRight[tTop] = model
                                Log.d("#DATA_RIGHT", "" + line.text)
                            }
                            // Log.d("#DATA_SYMMBOLIC", "" + symbolicString)


//                            listParent.add()
                            createBoundingBox(line.boundingBox!!, 1)


//                            val lineText = line.text
//                            val lineCornerPoints = line.cornerPoints
//                            val lineFrame = line.boundingBox
//                            createBoundingBox(lineFrame!!,1)
//
//                            var posTop = line.boundingBox!!.top
//                            Log.d("#Firebase: ", "" + line.text + "_top:" + line.boundingBox!!.top)
//                            var array = ArrayList<ModelReceiptData>()
//                            if (listParent.containsKey(posTop)) {
//                                array = listParent[posTop] as ArrayList<ModelReceiptData>
//                            }
//                            var tLeft = line.boundingBox!!.left
//                            var tTop = line.boundingBox!!.top
//                            var tRight = line.boundingBox!!.right
//                            var tBottom = line.boundingBox!!.bottom
//                            array.add(ModelReceiptData(tLeft, tTop, tRight, tBottom, line.text))
//                            listParent[posTop] = array
//                            stringBuilder.append(line.text)
//
//                            if (receipt.receiptIssuer.isEmpty()) {
//                                receipt.receiptIssuer =
//                                    gerReceiptIssuer(replaceSpecialChar(line.text.trim()))
//                            }
//
//                            if (isDateTimePattern(line.text)) {
//                                receipt.receiptDate = line.text.trim()
//                            } else if (isDatePattern(line.text)) {
//                                val receiptTime = replaceSpace(receipt.receiptDate)
//                                receipt.receiptDate = line.text.trim() + " " + receiptTime
//                            } else if (isTimePattern(line.text)) {
//                                val receiptdate = receipt.receiptDate
//                                receipt.receiptDate = receiptdate + " " + replaceSpace(line.text)
//                            }
//                            stringBuilder.append("\n")

                        }
                    }



                    receipt.receiptFullText = stringBuilder.toString().trim()
                    updateUI(ModelAsyncResult(bitmapOriginal, receipt, arrayLeft, arrayRight))

                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                    // ...
                    Toast.makeText(context, "" + e.message, Toast.LENGTH_LONG).show()
                }


//            val image1: FirebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap)
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
//
//                                val lineConfidence = line.confidence
//                                val lineLanguages = line.recognizedLanguages
//                                val lineCornerPoints = line.cornerPoints
//                                val lineFrame = line.boundingBox
//                                val line = line
//
//                                var posTop = line.boundingBox!!.top
//                                Log.d("#Firebase: ", "" + line.text + "_top:" + line.boundingBox!!.top)
//                                var array = ArrayList<ModelReceiptData>()
//                                if (listParent.containsKey(posTop)) {
//                                    array = listParent[posTop] as ArrayList<ModelReceiptData>
//                                }
//                                var tLeft = line.boundingBox!!.left
//                                var tTop = line.boundingBox!!.top
//                                var tRight = line.boundingBox!!.right
//                                var tBottom = line.boundingBox!!.bottom
//                                array.add(ModelReceiptData(tLeft, tTop, tRight, tBottom, line.text))
//                                listParent[posTop] = array
//                                stringBuilder.append(line.text)
//
//                                if (receipt.receiptIssuer.isEmpty()) {
//                                    receipt.receiptIssuer = gerReceiptIssuer(replaceSpecialChar(line.text.trim()))
//                                }
//
//                                if (isDateTimePattern(line.text)) {
//                                    receipt.receiptDate = line.text.trim()
//                                } else if (isDatePattern(line.text)) {
//                                    val receiptTime = replaceSpace(receipt.receiptDate)
//                                    receipt.receiptDate = line.text.trim() + " " + receiptTime
//                                } else if (isTimePattern(line.text)) {
//                                    val receiptdate = receipt.receiptDate
//                                    receipt.receiptDate = receiptdate + " " + replaceSpace(line.text)
//                                }
//                                stringBuilder.append("\n")
//
//                            }
//                        }
//                        receipt.receiptFullText = stringBuilder.toString().trim()
//                        updateUI(ModelAsyncResult(bitmapOverlay, receipt, listParent))
//                    }
//                    .addOnFailureListener(
//                            object : OnFailureListener {
//                                override fun onFailure(p0: java.lang.Exception) {
//                                    Log.d("#Firebase: ", "")
//                                }
//
//                            })

        } catch (e: FileNotFoundException) {
            e.printStackTrace()

        }

    }

    fun createBoundingBox(item: Rect, type: Int) {

        when (type) {
            1 -> {
                paint.color = Color.RED
                paint.strokeWidth = 10f
            }
            2 -> {
                paint.color = Color.BLUE
                paint.strokeWidth = 6f
            }
            3 -> {
                paint.color = Color.GREEN
                paint.strokeWidth = 4f
            }
        }

        paint.style = Paint.Style.STROKE


        var canvas = Canvas(bitmapTemp)
        var canvasOverlay = Canvas(bitmapOverlay)

        canvas.drawRect(item, paint)

        canvasOverlay.drawBitmap(bitmapTemp, Matrix(), null)
    }


    fun updateUI(result: ModelAsyncResult?) {
        //var arrayCombined = HashMap<Int, ModelReceiptData>()
        binding.model = result?.receipt
        //binding.notifyChange()
        if (result!!.bitmap?.width ?: 0 > 0) {


            result.arrayRight.forEach {
                val modelRight = it.value
                var height: Int = (modelRight.bottom!! - modelRight.top!!)
                var min = modelRight.top!! - (height / 2)
                var max = modelRight.bottom!! + (height / 2)
                Log.d("#DATA_RIGHT", "" + modelRight.text)
                Log.d("MERGING", "_START...")
                var isMerged = false
                var modelMerge = ModelReceiptData()
                for (j in min..max) {

                    if (result.arrayLeft.containsKey(j)) {
                        val modelJ = result.arrayLeft[j]

                        if (modelMerge.left == null || modelMerge.left ?: 0 > modelJ?.left ?: 0) {
                            modelMerge.left = modelJ?.left
                            modelMerge.top = modelJ?.top
                        }

                        modelMerge.right = modelJ?.right
                        modelMerge.bottom = modelJ?.bottom
                        modelMerge.text = modelMerge.text + " " + modelJ?.text
                        modelMerge.symbols = modelMerge.symbols + " " + modelJ?.symbols
                        //result.arrayLeft.remove(j)
                        // val modelOld: ModelReceiptData = result.arrayLeft[j]!!

                        isMerged = true
                    }
                }

                if (isMerged) {
                    val modelNew = ModelReceiptData(
                        0,
                        modelMerge.left, modelMerge.top, modelRight.right, modelRight.bottom,
                        modelMerge.text + " " + modelRight.text,
                        modelMerge.symbols + " " + modelRight.symbols
                    )

                    result.arrayLeft[modelMerge.top!!] = modelNew

                    createBoundingBox(
                        Rect(
                            modelNew.left!!,
                            modelNew.top!!,
                            modelNew.right!!,
                            modelNew.bottom!!
                        ), 2
                    )
                    Log.d("MERGING", "" + modelNew.text)
                }
                if (!isMerged) {
                    result.arrayLeft[modelRight.top!!] = modelRight

                }
                Log.d("MERGING", "_end....")
            }

            calculatePercentage(result.arrayLeft)
        }


        var receipt = result.receipt
        binding.model = receipt
        binding.notifyChange()
        binding.indeterminateBar.visibility = View.GONE
    }

    private fun calculatePercentage(data: HashMap<Int, ModelReceiptData>) {
        var map = HashMap<Int, ModelReceiptData>()
        map = data
        runBlocking {
            launch(Dispatchers.IO) {

//        for ((i, value) in data) {
//            val modelI = value
//            Log.d("#LEFT_DATA", "" + modelI.text)
//
//            val arrReversedI = modelI.symbols.trim().split(" ").toTypedArray()
//
//            // iterating string array
//            var wordsI = ""
//            var pointer = 0
//            for (j in arrReversedI.size - 1 downTo 0) {
//                wordsI += arrReversedI[j] + " "
//
//                //comparing symbols in other string
//                for ((k, value) in data) {
//                    if (k != i) {
//                        val modelK = data[k]
//                        val arrReversedK = modelK!!.symbols.trim().split(" ").toTypedArray()
//
//                        var wordsK = ""
//                        for (kr in arrReversedK.size - 1 downTo 0) {
//                            wordsK += arrReversedK[kr] + " "
//                            if (wordsI == wordsK) {
//                                // map[i]= wordsK
//                                modelI.percentageOfMatch =
//                                    (((arrReversedK.size - kr) * 100) / arrReversedK.size)
//                                map[i] = modelI
//                                //println("Matched: "+k+"_"+wordsI+"___"+wordsK)
//
//                            } else {
//                                pointer = k + 1
//                                // println("Not Matched: "+k+"_"+wordsI+"___"+wordsK)
//
//                            }
//
//                        }
//
//                    }
//                }
//
//            }
//
//        }

                val arrayPatternTotal = arrayOf("STR", "TOTAL", "CURRENCY", "NUMSFSNUM", "NUMSCNUM")
                val arrayPatternItems = arrayOf("STR", "NUM", "SC", "CURRENCY", "QSS")
                val arrayPatternAddress = arrayOf("STR", "SC", "SPS", "SPE", "SH")
                val arrayPatternReceiptNumber = arrayOf("STR", "SCO", "NUM")


                map.forEach {
                    val model = it.value
                    var pTotal  = (100 / arrayPatternTotal.size)
                    var pItem= (100 / arrayPatternItems.size)
                    var pAddress = (100 / arrayPatternAddress.size)
                    val pReceiptNo = (100 / arrayPatternReceiptNumber.size)

                    arrayPatternTotal.forEach { str ->
                        if (model.symbols.contains(str, true)
                        ) {
                            val old: Int = map[it.key]?.percentageTotal ?: 0
                            map[it.key]?.percentageTotal = (old + pTotal)
                        }
                    }

                    arrayPatternItems.forEach { str ->
                        if (model.symbols.contains(str, true)
                            && !model.symbols.contains("TOTAL", true) &&
                            (
                                    model.symbols.contains("CURRENCY", true)
                                            || model.symbols.contains("NUMSFSNUM", true)
                                            || model.symbols.contains("NUMSCNUM", true))
                        ) {
                            val old: Int = map[it.key]?.percentageItem ?: 0
                            map[it.key]?.percentageItem = (old + pItem)
                        }
                    }
                    arrayPatternAddress.forEach { str ->
                        if (model.symbols.contains(str, true)
                            && !model.symbols.contains("TOTAL", true)
                            && !model.symbols.contains("CURRENCY", true)
                        ) {
                            val old: Int = map[it.key]?.percentageAddress ?: 0
                            map[it.key]?.percentageAddress = (old + pAddress)
                        }
                    }
                    arrayPatternReceiptNumber.forEach { str ->
                        if (model.symbols.contains(str, true)
                            && !model.symbols.contains("TOTAL", true)
                            && model.symbols.contains("NUM", true)
                            && !model.symbols.contains("CURRENCY", true)
                            && !model.symbols.contains("SC", true)
                        ) {

                            val old: Int = map[it.key]?.percentageReceiptNumber ?: 0
                            map[it.key]?.percentageReceiptNumber = (old + pReceiptNo)
                        }
                    }
                }
            }
            map.forEach {
                val model = it.value
                Log.d("#Predicted Again: ", "" + " Size: " + model.getHigherPercentage().tagCorrected)
            }
        }

    }

    fun readAssetsFile() {
        var pointer = 0
        var data = HashMap<Int, ModelReceiptData>()
        val arraySymbols = ArrayList<String>()
        val inputStream: InputStream = assets.open("testtext.txt")
        var reader: BufferedReader? = null
        val sb = StringBuilder()
        try {
            reader = BufferedReader(
                InputStreamReader(assets.open("testtext.txt"))
            )

            // do reading, usually loop until end of file reading
            val mLine = ""

            reader.readLines().forEach {
                if (it.trim().isNotEmpty() && it != ",") {
                    pointer += 1
                    val symbols = getSymbolicString(it)
                    arraySymbols.add(symbols)
                    sb.append("$symbols, \n")
                   // data[pointer] = ModelReceiptData(symbols = sb.toString())
                }

            }
            //  data.put(pointer,ModelReceiptData(symbols=sb.toString()))
            // Log.d("###LINES", "" + sb.toString().replace(",", "\n"))
            Log.d("###LINES", "" + arraySymbols.size)

        } catch (e: IOException) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                    //calculatePercentage(data)
                } catch (e: IOException) {
                    //log the exception
                }
            }
        }


    }

    private fun postProcess() {

        val bmpMerged = Bitmap.createBitmap(
            bitmapOriginal.width,
            bitmapOriginal.height,
            bitmapOriginal.config
        )
        val canvas = Canvas(bmpMerged)
        canvas.drawBitmap(bitmapOriginal, Matrix(), null)
        canvas.drawBitmap(bitmapOverlay, Matrix(), null)

        val bmp = Bitmap.createScaledBitmap(bmpMerged, 300, 600, false)
        binding.icRect.setImageBitmap(bmp)
        Toast.makeText(this, "Done..!", Toast.LENGTH_LONG).show()
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
                    var receipt = Receipt(
                        0,
                        receiptNumber,
                        receiptDate,
                        receiptIssuer,
                        receiptTotal,
                        "",
                        receiptFullText
                    )
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
                        readAssetsFile()

//                        binding.model = Receipt()
//                        binding.notifyChange()
//                        super.onRecentCameraSelected()
//                        if (ContextCompat.checkSelfPermission(
//                                this@AddReceiptActivity, Manifest.permission.CAMERA
//                            ) != PackageManager.PERMISSION_GRANTED
//                        ) {
//                            requestPermissions(
//                                arrayOf(Manifest.permission.CAMERA),
//                                PERMISSIONS_REQUEST_CAMERA
//                            )
//                        } else {
//                            getImageFromCamera()
//                        }
                    }

                    override fun onRecentGallerySelected() {
                        super.onRecentGallerySelected()
                        binding.model = Receipt()
                        binding.notifyChange()
                        if (ContextCompat.checkSelfPermission(
                                this@AddReceiptActivity,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                            != PackageManager.PERMISSION_GRANTED
                        ) {

                            if (ActivityCompat.shouldShowRequestPermissionRationale(
                                    this@AddReceiptActivity,
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                )
                            ) {
                                Toast.makeText(
                                    this@AddReceiptActivity,
                                    "Please grant all required permissions from application settings",
                                    Toast.LENGTH_LONG
                                ).show()

                            } else {
                                ActivityCompat.requestPermissions(
                                    this@AddReceiptActivity,
                                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                    PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                                )
                            }
                        } else {
                            getImageFromAlbum()
                        }
                    }

                    override fun onRecentSelected() {
                        super.onRecentSelected()
                        binding.model = Receipt()
                        binding.notifyChange()
                        var receipt: Receipt =
                            roomDB.productsDao().getReceipt(roomDB.productsDao().getCount())
                        binding.model = receipt
                        binding.notifyChange()
                    }
                }, totalReceipts)
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getImageFromAlbum()
                } else {
                    showToast(
                        this,
                        this@AddReceiptActivity.getString(R.string.grant_all_permission)
                    )
                }
                return
            }
            PERMISSIONS_REQUEST_CAMERA -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getImageFromCamera()
                } else {
                    showToast(
                        this,
                        this@AddReceiptActivity.getString(R.string.grant_all_permission)
                    )
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
        //Uri.fromFile(RealPathUtil.getOutputMediaFileImages(this, FileColumns.MEDIA_TYPE_IMAGE))+
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA)
        }
    }


    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {


            var bitmap: Bitmap

            if (reqCode == REQUEST_PHOTO_EDIT) {

                if (resultCode == RESULT_DELETED) {
                    val path = data!!.getStringExtra(PATH)
                    PhotoUtil.deletePhoto(path)
                } else if (resultCode == RESULT_CANCELED) {

                } else if (resultCode == RESULT_OK) {
                    //mNoteGroup = null;

                    val list = data!!.getSerializableExtra("image_list") as java.util.ArrayList<Uri>
                    firebaseTextDetection(this, list[0], reqCode)
                }
            } else {

                var list = ArrayList<String>()
                if (reqCode == REQUEST_CODE_CAMERA) {

//                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)

                    list.add(imageUri.toString())
                } else {

                    val imageStream = contentResolver.openInputStream(data!!.data!!)
//                bitmap = BitmapFactory.decodeStream(imageStream)
                    list.add(RealPathUtil.getRealPath(this, data.data!!))
                }

                data?.putExtra(getString(R.string.request_code), reqCode)
                data?.putExtra(getString(R.string.image_uri), imageUri)
                binding.indeterminateBar.visibility = View.VISIBLE

                val intent = ScanActivity.getActivityIntent(this, list)
                startActivityForResult(intent, REQUEST_PHOTO_EDIT)
            }
        } else {
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show()
        }
    }

}