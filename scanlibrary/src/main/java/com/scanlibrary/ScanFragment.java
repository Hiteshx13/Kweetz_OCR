package com.scanlibrary;

import static com.scanlibrary.UtilsFileKt.convevrtToGrayscale;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by jhansi on 29/03/15.
 */
public class ScanFragment extends Fragment {

    //    private Button scanButton;
    private ImageView sourceImageView;
    private FrameLayout sourceFrame;
    private PolygonView polygonView;
    private View view;
    ProgressBar progressbar;
    private ProgressDialogFragment progressDialogFragment;
    private IScanner scanner;
    //    private Uri finalUri;
    private Bitmap original, scaledBitmap, tempBitmap;
    Map<Integer, PointF> pointFs;
    String imagePath;
    ScanActivity scanActivity;
    List<PointF> listPoints;
    ImageButton btnRotateLeft, btnRotateRight;

    public ScanFragment(Context context) {
        this.scanActivity = (ScanActivity) context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof IScanner)) {
            throw new ClassCastException("Activity must implement IScanner");
        }
        this.scanner = (IScanner) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.scan_fragment_layout, null);

        imagePath = (String) this.getArguments().get(Utils.IMAGES);
        init();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (isValidPoints()) {
            pointFs = polygonView.getPoints();
            listPoints = null;
        } else {
            listPoints = polygonView.invalidPoints;
        }
    }

    private void init() {
        sourceImageView = view.findViewById(R.id.sourceImageView);
        progressbar = view.findViewById(R.id.progressbar);
        btnRotateLeft = view.findViewById(R.id.rotate_left_ib);
        btnRotateRight = view.findViewById(R.id.rotate_right_ib);
//        scanButton = view.findViewById(R.id.scanButton);
        // scanButton.setOnClickListener(new ScanButtonClickListener());
        sourceFrame = view.findViewById(R.id.sourceFrame);
        polygonView = view.findViewById(R.id.polygonView);
        sourceFrame.post(new Runnable() {
            @Override
            public void run() {
                original = getBitmap();
                if (original != null) {
                    setBitmap();
                }
            }
        });

        btnRotateLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotatePhoto(-90);
            }
        });

        btnRotateRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotatePhoto(90);
            }
        });
    }

    private void rotatePhoto(float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);


//        scaledBitmap = Bitmap.createScaledBitmap(original,  original.getWidth(),original.getHeight(), true);

        original = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, true);
        scaledBitmap = null;
//        //original = Bitmap.createBitmap(original, 0, 0, original.getHeight(), original.getWidth(), matrix, true);
//        listPoints = null;
//        pointFs = getOutlinePoints(original);
//        sourceImageView.setImageBitmap(original);
//        polygonView.setPoints(pointFs);
//
//        int padding = (int) getResources().getDimension(R.dimen.scanPadding);
//        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(original.getHeight() + 2 * padding, original.getWidth() + 2 * padding);
//        layoutParams.gravity = Gravity.CENTER;
//        polygonView.setLayoutParams(layoutParams);
//

        setBitmap();
//        sourceImageView.setImageBitmap(original);
//
//        tempBitmap = ((BitmapDrawable) sourceImageView.getDrawable()).getBitmap();
//        pointFs = getEdgePoints(tempBitmap);
//        polygonView.setPoints(pointFs);
//        int padding = (int) getResources().getDimension(R.dimen.scanPadding);
//        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(tempBitmap.getWidth() + 2 * padding, tempBitmap.getHeight() + 2 * padding);
//        layoutParams.gravity = Gravity.CENTER;
//        polygonView.setLayoutParams(layoutParams);
//

//        int padding = (int) getResources().getDimension(R.dimen.scanPadding);
//        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(tempBitmap.getWidth() + 2 * padding, tempBitmap.getHeight() + 2 * padding);
//        layoutParams.gravity = Gravity.CENTER;
//        polygonView.setLayoutParams(layoutParams);
//        progressBar.setVisibility(View.VISIBLE);
//        new RotatePhotoTask(note.getImagePath().getPath(), angle, new PhotoSavedListener() {
//            @Override
//            public void photoSaved(String path, String name) {
//                Bitmap bitmap = BitmapFactory.decodeFile(path);
//                if (bitmap != null) {
//                    progressBar.setVisibility(View.GONE);
//                    pinchImageView.setImageBitmap(bitmap);
//                }
//            }
//
//            @Override
//            public void onNoteGroupSaved(NoteGroup noteGroup) {
//
//            }
//        }).execute();
    }

    public Map<Integer, PointF> getPoints() {
        return polygonView.getPoints();
    }

    public Bitmap getOriginalBitmap() {
        return original;
    }

    public SourceImageRes getSourceImageRes() {
        return new SourceImageRes(sourceImageView.getWidth(), sourceImageView.getHeight());
    }

    public String getOriginalPath() {
        return imagePath;
    }


    public void performOnClick(ArrayList<Bitmap> imageList, ArrayList<Map<Integer, PointF>> listPoints, ArrayList<String> listPath, ArrayList<SourceImageRes> listSourceImage) {
        Log.d("performOnClick", "start...");
        //if (isScanPointsValid(points)) {
        new ScanAsyncTask(imageList, listPoints, listPath, listSourceImage).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        //} else {
        //  showErrorDialog();
//        }
    }

    private Bitmap getBitmap() {
        if (original == null) {

            Uri uri = getUri();
    /*    Glide.with(scanActivity)
                .asBitmap()
                .load(uri)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        return resource;
                    }
                });
*/

            if (imagePath.startsWith("content")) {
                uri = Uri.parse(imagePath);
            }
            try {
                Bitmap bitmap = Utils.getBitmap(getActivity(), uri);
//            getActivity().getContentResolver().delete(uri, null, null);
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        } else {
            return original;
        }
    }


    private Uri getUri() {
        Uri uri = Uri.fromFile(new File(imagePath)); //getArguments().getParcelable(ScanConstants.SELECTED_BITMAP);
        return uri;
    }

    /*private void setBitmap(Bitmap original) {
        Bitmap scaledBitmap = scaledBitmap(original, sourceFrame.getWidth(), sourceFrame.getHeight());
        sourceImageView.setImageBitmap(scaledBitmap);
        Bitmap tempBitmap = ((BitmapDrawable) sourceImageView.getDrawable()).getBitmap();
        Map<Integer, PointF> pointFs = getEdgePoints(tempBitmap);
        polygonView.setPoints(pointFs);
        polygonView.setVisibility(View.VISIBLE);
        int padding = (int) getResources().getDimension(R.dimen.scanPadding);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(tempBitmap.getWidth() + 2 * padding, tempBitmap.getHeight() + 2 * padding);
        layoutParams.gravity = Gravity.CENTER;
        polygonView.setLayoutParams(layoutParams);
    }*/

    public static Bitmap convertToBlackWhite(Bitmap bmp) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);

        int alpha = 0xFF << 24; // ?bitmap?24?
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];

                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);

                grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
                grey = alpha | (grey << 16) | (grey << 8) | grey;
                pixels[width * i + j] = grey;
            }
        }
        Bitmap newBmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        newBmp.setPixels(pixels, 0, width, 0, 0, width, height);
        return newBmp;
    }

    public static Bitmap createBlackAndWhite(Bitmap src) {
        int width = src.getWidth();
        int height = src.getHeight();

        Bitmap bmOut = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        final float redBri = 0.2126f;
        final float greenBri = 0.2126f;
        final float blueBri = 0.0722f;

        int length = width * height;
        int[] inpixels = new int[length];
        int[] oupixels = new int[length];

        src.getPixels(inpixels, 0, width, 0, 0, width, height);

        int point = 0;
        for(int pix: inpixels){
            int R = (pix >> 16) & 0xFF;
            int G = (pix >> 8) & 0xFF;
            int B = pix & 0xFF;
            final float factor = 255f;
            float lum = (redBri * R / factor) + (greenBri * G / factor) + (blueBri * B / factor);

            if (lum > 0.3) {
                oupixels[point] = 0xFFFFFFFF;
            }else{
                oupixels[point] = 0xFF000000;
            }
            point++;
        }
        bmOut.setPixels(oupixels, 0, width, 0, 0, width, height);
        return bmOut;
    }

    private void setBitmap() {

        if (scaledBitmap == null) {
            scaledBitmap = scaledBitmap(original, sourceFrame.getWidth(), sourceFrame.getHeight());
        }
        Bitmap bitmapBW = createBlackAndWhite(scaledBitmap);
        sourceImageView.setImageBitmap(scaledBitmap);

        if (listPoints != null) {
            polygonView.setPointsCoordinates(listPoints);
            int color = getResources().getColor(R.color.orange);
            polygonView.paint.setColor(color);
        } else {
            pointFs = getEdgePoints(bitmapBW);
            polygonView.setPoints(pointFs);
        }
        polygonView.setVisibility(View.VISIBLE);
        int padding = (int) getResources().getDimension(R.dimen.scanPadding);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(bitmapBW.getWidth() + 2 * padding, bitmapBW.getHeight() + 2 * padding);
        layoutParams.gravity = Gravity.CENTER;
        polygonView.setLayoutParams(layoutParams);

    }


    private Map<Integer, PointF> getEdgePoints(Bitmap tempBitmap) {
        List<PointF> pointFs = getContourEdgePoints(tempBitmap);
        Map<Integer, PointF> orderedPoints = orderedValidEdgePoints(tempBitmap, pointFs);
        return orderedPoints;
    }

    private List<PointF> getContourEdgePoints(Bitmap tempBitmap) {
        float[] points = scanActivity.getPoints(tempBitmap);
        float x1 = points[0];
        float x2 = points[1];
        float x3 = points[2];
        float x4 = points[3];

        float y1 = points[4];
        float y2 = points[5];
        float y3 = points[6];
        float y4 = points[7];

        List<PointF> pointFs = new ArrayList<>();
        pointFs.add(new PointF(x1, y1));
        pointFs.add(new PointF(x2, y2));
        pointFs.add(new PointF(x3, y3));
        pointFs.add(new PointF(x4, y4));
        return pointFs;
    }

    private Map<Integer, PointF> getOutlinePoints(Bitmap tempBitmap) {
        Map<Integer, PointF> outlinePoints = new HashMap<>();
        outlinePoints.put(0, new PointF(0, 0));
        outlinePoints.put(1, new PointF(tempBitmap.getWidth(), 0));
        outlinePoints.put(2, new PointF(0, tempBitmap.getHeight()));
        outlinePoints.put(3, new PointF(tempBitmap.getWidth(), tempBitmap.getHeight()));
        return outlinePoints;
    }

    boolean isValidPoints() {
        //List<PointF> pointFs = getContourEdgePoints(original);
        // Map<Integer, PointF> orderedPoints = polygonView.getOrderedPoints(pointFs);
        return polygonView.isValidShape(polygonView.getPoints());
    }

    private Map<Integer, PointF> orderedValidEdgePoints(Bitmap tempBitmap, List<PointF> pointFs) {
        Map<Integer, PointF> orderedPoints = polygonView.getOrderedPoints(pointFs);
        if (!polygonView.isValidShape(orderedPoints)) {
            orderedPoints = getOutlinePoints(tempBitmap);
        }
        return orderedPoints;
    }

//    private class ScanButtonClickListener implements View.OnClickListener {
//        @Override
//        public void onClick(View v) {
//            final Map<Integer, PointF> points = polygonView.getPoints();
//
//
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    if (isScanPointsValid(points)) {
//                        new ScanAsyncTask(points).execute();
//                    } else {
//                        showErrorDialog();
//                    }
//                }
//            }).start();
//
//        }
//    }

    private void showErrorDialog() {
        SingleButtonDialogFragment fragment = new SingleButtonDialogFragment(R.string.ok, getString(R.string.cantCrop), "Error", true);
        FragmentManager fm = getActivity().getFragmentManager();
        fragment.show(fm, SingleButtonDialogFragment.class.toString());
    }

    private boolean isScanPointsValid(Map<Integer, PointF> points) {
        return points.size() == 4;
    }

    private Bitmap scaledBitmap(Bitmap bitmap, int width, int height) {
        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), new RectF(0, 0, width, height), Matrix.ScaleToFit.CENTER);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
    }

    private Bitmap getScannedBitmap(Bitmap original, Map<Integer, PointF> points, SourceImageRes source) {
        int width = original.getWidth();
        int height = original.getHeight();
        float xRatio = (float) original.getWidth() / source.width;
        float yRatio = (float) original.getHeight() / source.height;

        float x1 = (points.get(0).x) * xRatio;
        float x2 = (points.get(1).x) * xRatio;
        float x3 = (points.get(2).x) * xRatio;
        float x4 = (points.get(3).x) * xRatio;
        float y1 = (points.get(0).y) * yRatio;
        float y2 = (points.get(1).y) * yRatio;
        float y3 = (points.get(2).y) * yRatio;
        float y4 = (points.get(3).y) * yRatio;
        Log.d("", "POints(" + x1 + "," + y1 + ")(" + x2 + "," + y2 + ")(" + x3 + "," + y3 + ")(" + x4 + "," + y4 + ")");
        Bitmap _bitmap = scanActivity.getScannedBitmap(original, x1, y1, x2, y2, x3, y3, x4, y4);
        return _bitmap;
    }

    private class ScanAsyncTask extends AsyncTask<Void, Void, ArrayList<Uri>> {

        private ArrayList<Bitmap> imageList;
        private ArrayList<Map<Integer, PointF>> listPoints;
        ArrayList<Uri> listUri = new ArrayList<>();
        private ArrayList<String> listPath;
        private ArrayList<SourceImageRes> listSourceImage;

        public ScanAsyncTask(ArrayList<Bitmap> imageList, ArrayList<Map<Integer, PointF>> listPoints, ArrayList<String> listPath, ArrayList<SourceImageRes> listSourceImage) {
            this.imageList = imageList;
            this.listPoints = listPoints;
            this.listPath = listPath;
            this.listSourceImage = listSourceImage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Uri> doInBackground(Void... params) {
            // showProgressDialog(getString(R.string.scanning));

            for (int i = 0; i < imageList.size(); i++) {
                Bitmap bitmap = getScannedBitmap(imageList.get(i), listPoints.get(i),listSourceImage.get(i));
//FileOutputStream fos;
//                try{
//
//                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
//                        ContentResolver resolver=scanActivity.getContentResolver();
//                        ContentValues contentValues=new ContentValues();
//                        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,"Image_"+".jpg");
//                        contentValues.put(MediaStore.MediaColumns.MIME_TYPE,"image/jpeg");
//                        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES+File.separator+"OCR");
//                        Uri uri=resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
//                        fos=(FileOutputStream)resolver.openOutputStream(Objects.requireNonNull(uri));
//
//                    }
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }

                Uri uri = Utils.getImageUri(getActivity(), bitmap);

                listUri.add(uri);

//                File file = new File(listPath.get(i));
//                if (file.exists()) {
//                    file.delete();
//                }
                bitmap.recycle();
            }

            return listUri;
        }

        @Override
        protected void onPostExecute(ArrayList<Uri> listBitmap) {
            super.onPostExecute(listBitmap);
            //bitmap.recycle();
//            scanActivity.sendResults(listUri);
            scanner.onScanFinish(listBitmap);
            dismissDialog();
        }
    }

    protected void showProgressDialog(String message) {
        progressDialogFragment = new ProgressDialogFragment(message);
        FragmentManager fm = scanActivity.getFragmentManager();
        progressDialogFragment.show(fm, ProgressDialogFragment.class.toString());
    }

    protected void dismissDialog() {
        progressbar.setVisibility(View.GONE);
//        progressDialogFragment.dismissAllowingStateLoss();
    }

}