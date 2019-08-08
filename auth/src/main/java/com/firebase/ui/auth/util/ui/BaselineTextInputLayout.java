package com.firebase.ui.auth.util.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import androidx.annotation.RestrictTo;

import com.google.android.material.textfield.TextInputLayout;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class BaselineTextInputLayout extends TextInputLayout {
    public BaselineTextInputLayout(Context context) {
        super(context);
    }

    public BaselineTextInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaselineTextInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int getBaseline() {
        EditText text = getEditText();
        return text == null ? super.getBaseline() : text.getPaddingTop() + text.getBaseline();
    }
}
