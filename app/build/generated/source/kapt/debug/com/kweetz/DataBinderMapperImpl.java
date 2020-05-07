package com.kweetz;

import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import androidx.databinding.DataBinderMapper;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.ViewDataBinding;
import com.kweetz.databinding.ActivityAddReceiptBindingImpl;
import com.kweetz.databinding.ActivityFullImageBindingImpl;
import com.kweetz.databinding.ActivityReceiptListBindingImpl;
import com.kweetz.databinding.DialogSelectReceiptBindingImpl;
import com.kweetz.databinding.RowReceiptListBindingImpl;
import java.lang.IllegalArgumentException;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.RuntimeException;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataBinderMapperImpl extends DataBinderMapper {
  private static final int LAYOUT_ACTIVITYADDRECEIPT = 1;

  private static final int LAYOUT_ACTIVITYFULLIMAGE = 2;

  private static final int LAYOUT_ACTIVITYRECEIPTLIST = 3;

  private static final int LAYOUT_DIALOGSELECTRECEIPT = 4;

  private static final int LAYOUT_ROWRECEIPTLIST = 5;

  private static final SparseIntArray INTERNAL_LAYOUT_ID_LOOKUP = new SparseIntArray(5);

  static {
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.kweetz.R.layout.activity_add_receipt, LAYOUT_ACTIVITYADDRECEIPT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.kweetz.R.layout.activity_full_image, LAYOUT_ACTIVITYFULLIMAGE);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.kweetz.R.layout.activity_receipt_list, LAYOUT_ACTIVITYRECEIPTLIST);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.kweetz.R.layout.dialog_select_receipt, LAYOUT_DIALOGSELECTRECEIPT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.kweetz.R.layout.row_receipt_list, LAYOUT_ROWRECEIPTLIST);
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View view, int layoutId) {
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = view.getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
        case  LAYOUT_ACTIVITYADDRECEIPT: {
          if ("layout/activity_add_receipt_0".equals(tag)) {
            return new ActivityAddReceiptBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_add_receipt is invalid. Received: " + tag);
        }
        case  LAYOUT_ACTIVITYFULLIMAGE: {
          if ("layout/activity_full_image_0".equals(tag)) {
            return new ActivityFullImageBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_full_image is invalid. Received: " + tag);
        }
        case  LAYOUT_ACTIVITYRECEIPTLIST: {
          if ("layout/activity_receipt_list_0".equals(tag)) {
            return new ActivityReceiptListBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_receipt_list is invalid. Received: " + tag);
        }
        case  LAYOUT_DIALOGSELECTRECEIPT: {
          if ("layout/dialog_select_receipt_0".equals(tag)) {
            return new DialogSelectReceiptBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for dialog_select_receipt is invalid. Received: " + tag);
        }
        case  LAYOUT_ROWRECEIPTLIST: {
          if ("layout/row_receipt_list_0".equals(tag)) {
            return new RowReceiptListBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for row_receipt_list is invalid. Received: " + tag);
        }
      }
    }
    return null;
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View[] views, int layoutId) {
    if(views == null || views.length == 0) {
      return null;
    }
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = views[0].getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
      }
    }
    return null;
  }

  @Override
  public int getLayoutId(String tag) {
    if (tag == null) {
      return 0;
    }
    Integer tmpVal = InnerLayoutIdLookup.sKeys.get(tag);
    return tmpVal == null ? 0 : tmpVal;
  }

  @Override
  public String convertBrIdToString(int localId) {
    String tmpVal = InnerBrLookup.sKeys.get(localId);
    return tmpVal;
  }

  @Override
  public List<DataBinderMapper> collectDependencies() {
    ArrayList<DataBinderMapper> result = new ArrayList<DataBinderMapper>(1);
    result.add(new androidx.databinding.library.baseAdapters.DataBinderMapperImpl());
    return result;
  }

  private static class InnerBrLookup {
    static final SparseArray<String> sKeys = new SparseArray<String>(2);

    static {
      sKeys.put(0, "_all");
      sKeys.put(1, "model");
    }
  }

  private static class InnerLayoutIdLookup {
    static final HashMap<String, Integer> sKeys = new HashMap<String, Integer>(5);

    static {
      sKeys.put("layout/activity_add_receipt_0", com.kweetz.R.layout.activity_add_receipt);
      sKeys.put("layout/activity_full_image_0", com.kweetz.R.layout.activity_full_image);
      sKeys.put("layout/activity_receipt_list_0", com.kweetz.R.layout.activity_receipt_list);
      sKeys.put("layout/dialog_select_receipt_0", com.kweetz.R.layout.dialog_select_receipt);
      sKeys.put("layout/row_receipt_list_0", com.kweetz.R.layout.row_receipt_list);
    }
  }
}
