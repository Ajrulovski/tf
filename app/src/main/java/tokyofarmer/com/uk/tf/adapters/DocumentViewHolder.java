package tokyofarmer.com.uk.tf.adapters;

/**
 * Created by Gazmend on 1/13/2016.
 */

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;

import tokyofarmer.com.uk.tf.R;

public class DocumentViewHolder extends RecyclerView.ViewHolder {
    public EditText fragment;
    public WebView htmlcont;
    public ImageView imagecontent;
    public ImageView collapseicon;

    public DocumentViewHolder(View itemView, int viewType) {
        super(itemView);
        fragment = (EditText) itemView.findViewById(R.id.fragment);
        htmlcont = (WebView) itemView.findViewById(R.id.htmlcont);
        imagecontent = (ImageView) itemView.findViewById(R.id.imagecontent);
        collapseicon = (ImageView) itemView.findViewById(R.id.collapseicon);
        //mTimelineView = (TimelineView) itemView.findViewById(R.id.time_marker);
        //mTimelineView.initLine(viewType);
    }

}
