package com.kweetz.utils;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.loader.content.CursorLoader;


import com.kweetz.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class RealPathUtil {

    public static String getRealPath(Context context, Uri fileUri) {
        String realPath;
        // SDK < API11
        if (Build.VERSION.SDK_INT < 11) {
            realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(context, fileUri);
        }
        // SDK >= 11 && SDK < 19
        else if (Build.VERSION.SDK_INT < 19) {
            realPath = RealPathUtil.getRealPathFromURI_API11to18(context, fileUri);
        }
        // SDK > 19 (Android 4.4) and up
        else {
            realPath = RealPathUtil.getRealPathFromURI_API19(context, fileUri);
        }
        return realPath;
    }


    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
            cursor.close();
        }
        return result;
    }

    public static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = 0;
        String result = "";
        if (cursor != null) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
            cursor.close();
            return result;
        }
        return result;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API19(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * Create a File for saving an image or video
     */
//    public static File getOutputMediaFile(Context mContext, int type) {
//        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "/."+mContext.getString(R.string.app_name));
//        if (!mediaStorageDir.exists()) {
//            if (!mediaStorageDir.mkdirs()) {
//                Log.d("", "failed to create directory");
//                return null;
//            }
//        }
//
//        // Create a media file name
//        String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HHmmss").format(new Date());
//        File mediaFile;
//        if (type == MEDIA_TYPE_IMAGE) {
//            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
//                    "IMG_" + timeStamp + ".jpg");
//        } else if (type == MEDIA_TYPE_VIDEO) {
//            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
//                    "VID_" + timeStamp + ".mp4");
//        } else {
//            return null;
//        }
//
//        return mediaFile;
//    }

//    public static void saveFile(Context mContext,File file,File fileDest,boolean isDeleteSource) throws IOException {
////        final File fileDest = new File(Environment.getExternalStorageDirectory()  /*"/" + getString(R.string.images) */+ "/" + getString(R.string.images));
//        if (!fileDest.exists()) {
//            fileDest.mkdirs();
//        }
//
//        File newFile = new File(fileDest, file.getName());
//        FileChannel outputChannel = null;
//        FileChannel inputChannel = null;
//        try {
//            outputChannel = new FileOutputStream(newFile).getChannel();
//            inputChannel = new FileInputStream(file).getChannel();
//            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
//            inputChannel.close();
//            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.fromFile(newFile)));
//            if(isDeleteSource){
//                file.delete();
//            }
//            Toast.makeText(mContext, mContext.getString(R.string.story_saved), Toast.LENGTH_SHORT).show();
//        } finally {
//            if (inputChannel != null) inputChannel.close();
//            if (outputChannel != null) outputChannel.close();
//        }
//    }
    public static void deleteFileByPath(String path) {
        File fdelete = new File(path);
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                System.out.println("file Deleted :" + path);
            } else {
                System.out.println("file not Deleted :" + path);
            }
        }
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }
////    public static File getOutputMediaFileTemp(Context mContext, int type) {
////        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/." + mContext.getString(R.string.app_name));
////
////        if (!mediaStorageDir.exists()) {
////            if (!mediaStorageDir.mkdirs()) {
////                Log.d("", "failed to create directory");
////                return null;
////            }
////        }
////
////        // Create a media file name
////        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
////        File mediaFile;
////        if (type == MEDIA_TYPE_IMAGE) {
////            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
////                    "IMG_" + timeStamp + ".jpg");
////        } else if (type == MEDIA_TYPE_VIDEO) {
////            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
////                    "VID_" + timeStamp + ".mp4");
////        } else {
////            return null;
////        }
////
////        return mediaFile;
////    }
//
//    public static File getOutputMediaFileTemp(Context mContext, int type, String foldername) {
//        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/." + mContext.getString(R.string.app_name));
//        if (!foldername.isEmpty()) {
//            mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/." + mContext.getString(R.string.app_name) + "/" + foldername);
//        }
//        if (!mediaStorageDir.exists()) {
//            if (!mediaStorageDir.mkdirs()) {
//                Log.d("", "failed to create directory");
//                return null;
//            }
//        }
//
//        // Create a media file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        File mediaFile;
//        if (type == MEDIA_TYPE_IMAGE) {
//            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
//                    "IMG_" + timeStamp + ".jpg");
//        } else if (type == MEDIA_TYPE_VIDEO) {
//            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
//                    "VID_" + timeStamp + ".mp4");
//        } else {
//            return null;
//        }
//
//        return mediaFile;
//    }
//
    public static File getOutputMediaFileImages(Context mContext, int type) {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/" + mContext.getString(R.string.images));

//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES), mContext.getString(R.string.app_name));
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
//
//    public static ArrayList<MultipartBody.Part> getParamsRequestBodyImage(Context mContext, Uri profileUri) throws URISyntaxException {
//
//        ArrayList<MultipartBody.Part> attachmentName = new ArrayList<MultipartBody.Part>();
//        File file = new File(/*getFilePathFromContentUri(mContext,profileUri)*/"storage/emulated/0/DCIM/Screenshots/IMG_20201020_184737.jpg");
//        okhttp3.RequestBody requestFile =
//                okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/png"), file);
//        /* val surveyBody = RequestBody.create(
//                "image/png".toMediaTypeOrNull(),
//                file)*/
//        MultipartBody.Part body =
//                MultipartBody.Part.createFormData("attachmentName[" + 0 + "]", file.getName(), requestFile);
//        attachmentName.add(body);
//        return attachmentName;
//    }
//
//    public static ArrayList<MultipartBody.Part> getParamsRequestBodyVideo(Context mContext, Uri profileUri) throws URISyntaxException {
//
//        ArrayList<MultipartBody.Part> attachmentName = new ArrayList<MultipartBody.Part>();
//        File file = new File(getFilePathFromContentUri(mContext, profileUri));
//        okhttp3.RequestBody requestFile =
//                okhttp3.RequestBody.create(okhttp3.MediaType.parse("video/*"), file);
//        /* val surveyBody = RequestBody.create(
//                "image/png".toMediaTypeOrNull(),
//                file)*/
//        MultipartBody.Part body =
//                MultipartBody.Part.createFormData("attachmentName[" + 0 + "]", file.getName(), requestFile);
//        attachmentName.add(body);
//        return attachmentName;
//    }

}
