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
import androidx.loader.content.AsyncTaskLoader
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.Text
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.kweetz.R
import com.kweetz.database.model.Receipt
import com.kweetz.databinding.ActivityAddReceiptBinding
import com.kweetz.listener.listener
import com.kweetz.model.ModelAsyncResult
import com.kweetz.model.ModelReceiptData
import com.kweetz.utils.*
import java.io.FileNotFoundException
import java.text.SimpleDateFormat


class AddReceiptActivity : BaseActivity(), View.OnClickListener {
    lateinit var binding: ActivityAddReceiptBinding

    var imageUri: Uri? = null
    var totalReceipts = 0
    var receipt = Receipt()
    var model = ModelAsyncResult( )

    companion object {
        fun getIntent(context: Context, receipt: Receipt? = Receipt()): Intent {
            var intent = Intent(context, AddReceiptActivity::class.java)
            intent.putExtra(RECEIPT, receipt)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_receipt)
        initialization()
        onClickListener()
        updateUI(model)
    }

    fun initialization() {
        receipt = intent.getParcelableExtra(RECEIPT) ?: Receipt()
        totalReceipts = roomDB.productsDao().getAllReceipts().size

    }


    fun onClickListener() {
        binding.llAutoFill.setOnClickListener(this)
        binding.tvSave.setOnClickListener(this)
    }

    fun updateUI(result: ModelAsyncResult?) {
        binding.model = result?.receipt
        binding.notifyChange()
        if (result?.bitmap?.width ?: 0 > 0) {

            var halfLength = result!!.bitmap?.width ?: 0 / 2
            var listParent: HashMap<Int, ArrayList<ModelReceiptData>?>? = result.listParent

            if (!result.listParent.isNullOrEmpty()) {

                for (i in result.listParent!!) {
                    Log.d("", "")

                }
            }
        }

    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.tvSave -> {
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
                    showToast(this, "Please enter Receipt title")
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

            data?.putExtra(getString(R.string.request_code), reqCode)
            data?.putExtra(getString(R.string.image_uri), imageUri)
            binding.indeterminateBar.visibility = View.VISIBLE
            var mLoader = MyAsyncTask(this, data, imageUri)
            mLoader.registerListener(0) { loader, receipt ->

                Log.d("#run", "over")
                binding.indeterminateBar.visibility = View.GONE

                //binding.rlBackground.background = BitmapDrawable(this.resources, receipt!!.bitmap)
                updateUI(receipt)
            }
            mLoader.startLoading()

        } else {
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show()
        }
    }

    class MyAsyncTask(context: Context, var data: Intent?, var imageUri: Uri?) : AsyncTaskLoader<ModelAsyncResult>(context) {

        var pointer = 0
        var arrayParent = ArrayList<ArrayList<String>?>()
        lateinit var listParent: HashMap<Int, ArrayList<ModelReceiptData>?>
        var textRecognizer: TextRecognizer = TextRecognizer.Builder(context).build()
        lateinit var receipt: Receipt
        lateinit var bitmap: Bitmap
        lateinit var bitmapTemp: Bitmap
        lateinit var bitmapOverlay: Bitmap
        var reqCode = data?.getIntExtra(context.getString(R.string.request_code), 0)
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

                bitmapTemp = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
                bitmapOverlay = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)

                frame = Frame.Builder().setBitmap(bitmap).build()
                val items = textRecognizer.detect(frame)



                items.valueAt(0).components
                var listLength = items.valueAt(items.size() - 1).boundingBox.top
                listParent = HashMap(listLength)
                val stringBuilder = StringBuilder()

                var half = bitmap.width / 2

                for (i in 0 until items.size()) {
                    val item = items.valueAt(i)
                    item.components.size
                    for (j in item.components) {
                        var posTop = j.boundingBox.top

                        var array = ArrayList<ModelReceiptData>()
                        if (listParent.containsKey(posTop)) {
                            array = listParent[posTop] as ArrayList<ModelReceiptData>
                        }
                        var t_left = j.boundingBox.left
                        var t_top = j.boundingBox.top
                        var t_right = j.boundingBox.right
                        var t_bottom = j.boundingBox.bottom
                        array.add(ModelReceiptData(t_left, t_top, t_right, t_bottom, j.value))
                        listParent[posTop] = array
                        Log.d("HEIGHT__", "" + (t_bottom - t_top) + "__" + j.value.replace("/n", ""))
                    }

                    stringBuilder.append(item.value)
                    Log.d("#Detected $i", " = " + item.value + " _left:" + item.boundingBox.left + " _right:" + item.boundingBox.right + " _top:" + item.boundingBox.top + " _bottom:" + item.boundingBox.bottom)

                    if (receipt.receiptNo.isEmpty()) {
                        /****set receipt number****/
                        receipt.receiptNo = getReceiptNumber(item.value).trim()
                    }
                    if (isReceiptTotal(item.value)) {
                        /****set receipt total****/

                        var strPrevious: String? = items.valueAt(i - 1).value.trim()
                        var strNext: String? = items.valueAt(i + 1).value.trim()

                        if (isStrNumber(strPrevious)) {
                            if (isStrNumber(strNext)) {
                                var numPrevious: Float? = strToNumber(strPrevious)?.toFloat()
                                        ?: 0.00f
                                var numNext: Float? = strToNumber(strNext)?.toFloat() ?: 0.00f
                                if (numPrevious!! > numNext!!) {
                                    receipt.receiptTotal = strPrevious ?: ""
                                } else {
                                    receipt.receiptTotal = strNext ?: ""
                                }
                            } else {
                                receipt.receiptTotal = strPrevious ?: ""
                            }
                        } else if (i < (items.size() - 1) && isStrNumber(strNext)) {
                            receipt.receiptTotal = strNext ?: ""
                        }
                    }
                    if (isReceiptDate(item.value)) {
                        receipt.receiptDate = item.value.trim()
                    }
                    stringBuilder.append("\n")
                    createBoundingBox(item)
                }
                /****set receipt full text****/
                receipt.receiptFullText = stringBuilder.toString().trim()
                Log.d("Receipt", "Full Text : " + receipt.receiptFullText)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                Log.d("###", "Something went wrong")
            }
            Log.d("#run", "stop")
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

        fun getReceiptNumber(str: String): String {

            var isReceipt = false
            var patternNumber = Regex("-?\\d+(\\.\\d+)?")

            var arry = arrayOf("Ceks", "CEKS#", "Ceks nr.", "Ceka nr.", "Dok Nr", "Dok. Nr", "DOK. #", "Dokuments:", "Kvits", "Kvits Nr", "Kvits Nr.")
            var receiptNo = ""
            var strLower = str.toLowerCase()
            arry.forEach {
                if (strLower.contains(it.toString().toLowerCase())) {
                    var newStr = str
                    if (isAlphaNumerical(strLower.replace(it.toLowerCase().toString(), ""))) {
                        if (str.contains("\n")) {
                            newStr = str.substring(0, str.indexOf("\n"))
                        }
                        receiptNo = newStr.replace(it, "")
                    }
                }
            }
            return receiptNo
        }


        fun isReceiptTotal(str: String): Boolean {
            var arry = arrayOf("Samaksai EUR", "Kopa apmaksai", "Samaksa EUR", "Kopeja summa apmaksai", "Kopsumma EUR", "KOPA", "KOPA SUMMA", "Kopa EUR")
            var isTotal = false
            arry.forEach {
                if (str.toLowerCase().contains(it.toString().toLowerCase())) {
                    isTotal = true
                }
            }
            return isTotal
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
}