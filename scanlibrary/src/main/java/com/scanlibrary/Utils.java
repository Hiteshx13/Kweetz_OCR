package com.scanlibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jhansi on 05/04/15.
 */
public class Utils {
    static String IMAGES = "image_list";
    public static final String INTENT_DATA_NOTEGROUP_ID = "intent_data_notegroup_id";

    private Utils() {

    }

    public static Uri getUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    private static String createName() {
        String IMG_PREFIX = "IMG_";
        String IMG_POSTFIX = ".jpg";
        String TIME_FORMAT = "yyyyMMdd_HH_mm_ss_SSS";

        String timeStamp = new SimpleDateFormat(TIME_FORMAT).format(new Date());
        return IMG_PREFIX + timeStamp + IMG_POSTFIX;
    }

    private static File getOutputMediaFile() {
        // To be safe, we should check that the SDCard is mounted
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.e("External storage ", "" + Environment.getExternalStorageState());
            return null;
        }

        File dir = new File(Const.FOLDERS.PATH);
        // Create the storage directory if it doesn't exist
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e("", "Failed to create directory");
                return null;
            }
        }

        return new File(dir.getPath() + File.separator + createName());
    }

    private static File getCropMediaFile() {
        // To be safe, we should check that the SDCard is mounted
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.e("External storage ", "" + Environment.getExternalStorageState());
            return null;
        }

        File dir = new File(Const.FOLDERS.CROP_IMAGE_PATH);
        // Create the storage directory if it doesn't exist
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e("", "Failed to create directory");
               // return null;
            }
        }

        return new File(dir.getPath() + File.separator + createName());
    }

    public static Uri getImageUri(Context context, Bitmap bitmap) {

        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);
        File wallpaperDirectory = new File(path,"/KweetzOCR1/");
        wallpaperDirectory.mkdirs();
        File image = null;
        try {
            image = File.createTempFile(
                    "imageFileName", /* prefix */
                    ".jpg", /* suffix */wallpaperDirectory  /* directory */);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        File file = getCropMediaFile();
        try (FileOutputStream out = new FileOutputStream(image)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
 //       String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        //Uri photoURI=FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", image);
        return Uri.fromFile(image);
    }

    public static Bitmap getBitmap(Context context, Uri uri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        return bitmap;
    }
}