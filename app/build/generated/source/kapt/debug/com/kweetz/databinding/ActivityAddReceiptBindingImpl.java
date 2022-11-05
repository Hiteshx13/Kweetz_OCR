package com.kweetz.databinding;
import com.kweetz.R;
import com.kweetz.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class ActivityAddReceiptBindingImpl extends ActivityAddReceiptBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.llAutoFill, 6);
        sViewsWithIds.put(R.id.indeterminateBar, 7);
        sViewsWithIds.put(R.id.llSave, 8);
        sViewsWithIds.put(R.id.tvSave, 9);
        sViewsWithIds.put(R.id.icRect, 10);
    }
    // views
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ActivityAddReceiptBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 11, sIncludes, sViewsWithIds));
    }
    private ActivityAddReceiptBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (androidx.appcompat.widget.AppCompatEditText) bindings[5]
            , (androidx.appcompat.widget.AppCompatEditText) bindings[2]
            , (androidx.appcompat.widget.AppCompatEditText) bindings[3]
            , (androidx.appcompat.widget.AppCompatEditText) bindings[1]
            , (androidx.appcompat.widget.AppCompatEditText) bindings[4]
            , (androidx.appcompat.widget.AppCompatImageView) bindings[10]
            , (android.widget.ProgressBar) bindings[7]
            , (android.widget.LinearLayout) bindings[6]
            , (android.widget.LinearLayout) bindings[8]
            , (android.widget.RelativeLayout) bindings[0]
            , (androidx.appcompat.widget.AppCompatTextView) bindings[9]
            );
        this.etFullReceipt.setTag(null);
        this.etReceiptDate.setTag(null);
        this.etReceiptIssuer.setTag(null);
        this.etReceiptNumber.setTag(null);
        this.etReceiptTotal.setTag(null);
        this.rlBackground.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x2L;
        }
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
        if (BR.model == variableId) {
            setModel((com.kweetz.database.model.Receipt) variable);
        }
        else {
            variableSet = false;
        }
            return variableSet;
    }

    public void setModel(@Nullable com.kweetz.database.model.Receipt Model) {
        this.mModel = Model;
        synchronized(this) {
            mDirtyFlags |= 0x1L;
        }
        notifyPropertyChanged(BR.model);
        super.requestRebind();
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        com.kweetz.database.model.Receipt model = mModel;
        java.lang.String modelReceiptFullText = null;
        java.lang.String modelReceiptNo = null;
        java.lang.String modelReceiptIssuer = null;
        java.lang.String modelReceiptDate = null;
        java.lang.String modelReceiptTotal = null;

        if ((dirtyFlags & 0x3L) != 0) {



                if (model != null) {
                    // read model.receiptFullText
                    modelReceiptFullText = model.getReceiptFullText();
                    // read model.receiptNo
                    modelReceiptNo = model.getReceiptNo();
                    // read model.receiptIssuer
                    modelReceiptIssuer = model.getReceiptIssuer();
                    // read model.receiptDate
                    modelReceiptDate = model.getReceiptDate();
                    // read model.receiptTotal
                    modelReceiptTotal = model.getReceiptTotal();
                }
        }
        // batch finished
        if ((dirtyFlags & 0x3L) != 0) {
            // api target 1

            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.etFullReceipt, modelReceiptFullText);
            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.etReceiptDate, modelReceiptDate);
            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.etReceiptIssuer, modelReceiptIssuer);
            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.etReceiptNumber, modelReceiptNo);
            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.etReceiptTotal, modelReceiptTotal);
        }
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): model
        flag 1 (0x2L): null
    flag mapping end*/
    //end
}