package me.mgottein;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.MovementMethod;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by mgottein on 3/17/15.
 *
 * Lifted from {@see android.widget.EditText}. Configures a {@see me.mgottein.LineNumberTextView}
 * to be editable
 */
public class LineNumberEditText extends LineNumberTextView {
    public LineNumberEditText(Context context) {
        super(context);
    }

    public LineNumberEditText(Context context, AttributeSet attrs) {
        super(context, attrs, android.R.attr.editTextStyle);
    }

    public LineNumberEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, android.R.attr.editTextStyle);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LineNumberEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, android.R.attr.editTextStyle, defStyleRes);
    }

    @Override
    protected boolean getDefaultEditable() {
        return true;
    }

    @Override
    protected MovementMethod getDefaultMovementMethod() {
        return ArrowKeyMovementMethod.getInstance();
    }

    @Override
    public Editable getText() {
        return (Editable) super.getText();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, BufferType.EDITABLE);
    }

    /**
     * Convenience for {@link android.text.Selection#setSelection(android.text.Spannable, int, int)}.
     */
    public void setSelection(int start, int stop) {
        Selection.setSelection(getText(), start, stop);
    }

    /**
     * Convenience for {@link Selection#setSelection(android.text.Spannable, int)}.
     */
    public void setSelection(int index) {
        Selection.setSelection(getText(), index);
    }

    /**
     * Convenience for {@link Selection#selectAll}.
     */
    public void selectAll() {
        Selection.selectAll(getText());
    }

    /**
     * Convenience for {@link Selection#extendSelection}.
     */
    public void extendSelection(int index) {
        Selection.extendSelection(getText(), index);
    }

    @Override
    public void setEllipsize(TextUtils.TruncateAt ellipsis) {
        if (ellipsis == TextUtils.TruncateAt.MARQUEE) {
            throw new IllegalArgumentException("EditText cannot use the ellipsize mode "
                    + "TextUtils.TruncateAt.MARQUEE");
        }
        super.setEllipsize(ellipsis);
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(LineNumberEditText.class.getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(LineNumberEditText.class.getName());
    }

    @Override
    public boolean performAccessibilityAction(int action, Bundle arguments) {
        switch (action) {
            case AccessibilityNodeInfo.ACTION_SET_TEXT: {
                CharSequence text = (arguments != null) ? arguments.getCharSequence(
                        AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE) : null;
                setText(text);
                if (text != null && text.length() > 0) {
                    setSelection(text.length());
                }
                return true;
            }
            default: {
                return super.performAccessibilityAction(action, arguments);
            }
        }
    }
}
