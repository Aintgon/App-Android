package edu.uw.covidsafe;

import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

public class AppStatusManager {

    public Snackbar getmSnackBar() {
        return mSnackBar;
    }

    Snackbar mSnackBar;
    View.OnClickListener onClickListener;
    String actionText;

    public AppStatusManager() {
        mSnackBar = null;
        onClickListener = null;
        actionText = null;
    }

    public AppStatusManager makeSnackBar(View view,
                                 SpannableStringBuilder stringBuilder,
                                 int snackbarDuration) {
        if (view != null && stringBuilder != null) {
            mSnackBar = Snackbar.make(view, stringBuilder, snackbarDuration);
        }
        return this;
    }

    public AppStatusManager setAction(String actionText, View.OnClickListener onClickListener) {
        mSnackBar.setAction(actionText, onClickListener);
        return this;
    }

    public void show() {
        if (mSnackBar != null) {
            View snackbarView = mSnackBar.getView();
            TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setMaxLines(5);

            mSnackBar.show();
        }
    }


}
