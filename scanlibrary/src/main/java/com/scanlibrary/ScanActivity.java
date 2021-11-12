package com.scanlibrary;

import android.app.AlertDialog;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import static com.scanlibrary.Utils.IMAGES;
import static com.scanlibrary.Utils.INTENT_DATA_NOTEGROUP_ID;

/**
 * Created by jhansi on 28/03/15.
 */
public class ScanActivity extends FragmentActivity implements IScanner, ComponentCallbacks2 {


    ArrayList<String> imageList;
    ScreenSlidePagerAdapter pagerAdapter;
    FloatingActionButton btnDone;
    ArrayList<ScanFragment> listFragments;
    int counter = 0;
//    int mNoteGroupID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_layout);
        btnDone = findViewById(R.id.btnDone);
//        mNoteGroupID = getIntent().getIntExtra(INTENT_DATA_NOTEGROUP_ID, 0);

        init();
    }

    public static Intent getActivityIntent(Context context, ArrayList<String> listNote) {
        Intent intent = new Intent(context, ScanActivity.class);
//        intent.putExtra(INTENT_DATA_NOTEGROUP_ID, mNoteGroupID);
        intent.putExtra(IMAGES, listNote);
        return intent;
    }

    private void init() {

        imageList = (ArrayList<String>) getIntent().getSerializableExtra(IMAGES);
        //ScanFragment fragment = new ScanFragment();
        listFragments = new ArrayList<>();

        for (int i = 0; i < imageList.size(); i++) {
            ScanFragment fragment = new ScanFragment(this);
            Bundle bundle = new Bundle();
            bundle.putString(IMAGES, imageList.get(i));
            fragment.setArguments(bundle);
            listFragments.add(fragment);
        }

        final ViewPager mPager = findViewById(R.id.pager);

        pagerAdapter = new ScreenSlidePagerAdapter(this, imageList, listFragments, getSupportFragmentManager());
        mPager.setAdapter(pagerAdapter);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pagerAdapter.onDoneClicked(new OnValidateView() {
                            @Override
                            public void isValid(boolean isValid, int pos) {
                                if (!isValid) {
                                    Toast.makeText(ScanActivity.this, "Please select valid points", Toast.LENGTH_LONG).show();
                                    mPager.setCurrentItem(pos,true);
                                }
                            }
                        });
                    }
                });
            }
        });

    }

    protected int getPreferenceContent() {
        return getIntent().getIntExtra(ScanConstants.OPEN_INTENT_PREFERENCE, 0);
    }


    @Override
    public void onBitmapSelect(Uri uri) {

        Log.d("#URI Cropped Uri", "" + uri);


//        ScanFragment fragment = new ScanFragment(this);
//        Bundle bundle = new Bundle();
//        bundle.putParcelable(ScanConstants.SELECTED_BITMAP, uri);
//        fragment.setArguments(bundle);
//        android.app.FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.add(R.id.content, fragment);
//        fragmentTransaction.addToBackStack(ScanFragment.class.toString());
//        fragmentTransaction.commit();
    }

    @Override
    public void onScanFinish(ArrayList<Uri> listUri) {
        Intent intent = new Intent();
        intent.putExtra(IMAGES, listUri);
//        intent.putExtra(INTENT_DATA_NOTEGROUP_ID, mNoteGroupID);
        setResult(RESULT_OK, intent);
        finish();

//        Log.d("#URI onScanFinish :", "" + uri);
//        if (counter < imageList.size()) {
//            pagerAdapter.onDoneClicked(counter);
//        } else if (counter == imageList.size()) {
//            Log.d("#URI onScanFinish :", "Go");
//
//        }
//        ResultFragment fragment = new ResultFragment();
//        Bundle bundle = new Bundle();
//        bundle.putParcelable(ScanConstants.SCANNED_RESULT, uri);
//        fragment.setArguments(bundle);
//        android.app.FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.add(R.id.content, fragment);
//        fragmentTransaction.addToBackStack(ResultFragment.class.toString());
//        fragmentTransaction.commit();
    }


    @Override
    public void onTrimMemory(int level) {
        switch (level) {
            case ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN:
                /*
                   Release any UI objects that currently hold memory.

                   The user interface has moved to the background.
                */
                break;
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE:
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW:
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL:
                /*
                   Release any memory that your app doesn't need to run.

                   The device is running low on memory while the app is running.
                   The event raised indicates the severity of the memory-related event.
                   If the event is TRIM_MEMORY_RUNNING_CRITICAL, then the system will
                   begin killing background processes.
                */
                break;
            case ComponentCallbacks2.TRIM_MEMORY_BACKGROUND:
            case ComponentCallbacks2.TRIM_MEMORY_MODERATE:
            case ComponentCallbacks2.TRIM_MEMORY_COMPLETE:
                /*
                   Release as much memory as the process can.

                   The app is on the LRU list and the system is running low on memory.
                   The event raised indicates where the app sits within the LRU list.
                   If the event is TRIM_MEMORY_COMPLETE, the process will be one of
                   the first to be terminated.
                */
                new AlertDialog.Builder(this)
                        .setTitle(R.string.low_memory)
                        .setMessage(R.string.low_memory_message)
                        .create()
                        .show();
                break;
            default:
                /*
                  Release any non-critical data structures.

                  The app received an unrecognized memory level value
                  from the system. Treat this as a generic low-memory message.
                */
                break;
        }
    }

    public native Bitmap getScannedBitmap(Bitmap bitmap, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4);

    public native Bitmap getGrayBitmap(Bitmap bitmap);

    public native Bitmap getMagicColorBitmap(Bitmap bitmap);

    public native Bitmap getBWBitmap(Bitmap bitmap);

    public native float[] getPoints(Bitmap bitmap);

    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("Scanner");
    }
}