package com.scanlibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.Map;

class ScreenSlidePagerAdapter extends FragmentPagerAdapter {

    Context context;
    ArrayList<String> imageList;
    ArrayList<ScanFragment> listFragments;
    ArrayList<Map<Integer, PointF>> points;

    public ScreenSlidePagerAdapter(Context context, ArrayList<String> imageList, ArrayList<ScanFragment> listFragments, FragmentManager fm) {
        super(fm);
        this.imageList = new ArrayList<String>();
        this.imageList.addAll(imageList);
        this.context = context;
        this.listFragments = listFragments;
        listFragments = new ArrayList<>();
    }

    public void onDoneClicked(OnValidateView listener) {

        ArrayList<Map<Integer, PointF>> listPoints = new ArrayList<>();
        ArrayList<Bitmap> listBitmap = new ArrayList<>();
        ArrayList<SourceImageRes> listSourceImage = new ArrayList<>();
        ArrayList<Integer> sourceHeight = new ArrayList<>();
        ArrayList<String> listUri = new ArrayList<>();
        boolean isValid = true;
        for (int i = 0; i < listFragments.size(); i++) {
            //  points.add();
            ScanFragment fragment = listFragments.get(i);
            Log.d("#Frag_" + i, "isValidPoints: " + fragment.isValidPoints());
            if (!fragment.isValidPoints()) {
                isValid = false;
                listener.isValid(false, i);
                break;
            }
            listPoints.add(fragment.getPoints());
            listSourceImage.add(fragment.getSourceImageRes());
            listBitmap.add(fragment.getOriginalBitmap());
            listUri.add(fragment.getOriginalPath());
        }
        if (isValid) {
            ScanFragment fragment = listFragments.get(0);
            fragment.performOnClick(listBitmap, listPoints, listUri,listSourceImage);
        }
    }

    @Override
    public Fragment getItem(int position) {
//        ScanFragment fragment;
//        if (listFragments.size() < imageList.size()) {
//            fragment = new ScanFragment(context);
//            Bundle bundle = new Bundle();
//            bundle.putString(IMAGES, imageList.get(position));
//            fragment.setArguments(bundle);
//            listFragments.add(fragment);
//        } else {
//            fragment = listFragments.get(position);
//        }


//        android.app.FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.add(R.id.content, fragment);


        return listFragments.get(position);
    }

    @Override
    public int getCount() {
        return imageList.size();
    }
}