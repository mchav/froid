package froid.widget;

import android.widget.TextView;
import android.text.TextWatcher;
import android.text.Editable;
import frege.run8.Func;
import frege.run8.Thunk;
import frege.prelude.PreludeBase;

public class WidgetHelpers {
    public static void onTextChanged(TextView view, final Func.U<String, Func.U<frege.runtime.Phantom.RealWorld, Short>> handler) {
        view.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                PreludeBase.TST.performUnsafe(
                    handler.apply(Thunk.lazy(s.toString())).call()
                ).call();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    public static void setSelection(android.widget.EditText view) {
        view.setSelection(view.getText().length());
    }
}
