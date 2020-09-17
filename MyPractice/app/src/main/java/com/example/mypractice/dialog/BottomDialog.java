package com.example.mypractice.dialog;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;

/**
 * Created by yujintao on 2017/3/21.
 */

public class BottomDialog extends Dialog {

    public BottomDialog(@NonNull Context context) {
        super(context);
    }

    public BottomDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }
}
