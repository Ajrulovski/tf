package tokyofarmer.com.uk.tf;

import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Created by Gazmend on 4/6/2016.
 */
public class ClickSpan extends ClickableSpan {

    private OnClickListener mListener;

    public ClickSpan(OnClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View widget) {
        if (mListener != null) mListener.onClick();
    }

    public interface OnClickListener {
        void onClick();
    }
}
