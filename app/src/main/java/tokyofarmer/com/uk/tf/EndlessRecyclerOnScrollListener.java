package tokyofarmer.com.uk.tf;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.List;

import tokyofarmer.com.uk.tf.models.FragmentModel;

/**
 * Created by Gazmend on 4/29/2016.
 */
public abstract class EndlessRecyclerOnScrollListener extends
        RecyclerView.OnScrollListener {
    public static String TAG = EndlessRecyclerOnScrollListener.class
            .getSimpleName();
    private int currentLevel = 1;
    private String sectionNumber = "1";
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    Activity mCaller;
    List<FragmentModel> mFullDataList;

    private int current_page = 1;

    private LinearLayoutManager mLinearLayoutManager;

    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager, Activity callerActivity, List<FragmentModel> fullDataList) {
        this.mLinearLayoutManager = linearLayoutManager;
        this.mCaller = callerActivity;
        this.mFullDataList = fullDataList;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
        TextView secid = (TextView) mCaller.findViewById(R.id.section_id);

        // change the sectionId in the sticky header to it's proper value
        String foundType = mFullDataList.get(firstVisibleItem).getType();
        if(foundType.equals("h"))
        {
            secid.setText(mFullDataList.get(firstVisibleItem).getSectionId());
        }


        //Toast.makeText(recyclerView.getContext(), "Scrolled at "+String.valueOf(firstVisibleItem), Toast.LENGTH_SHORT).show();
//        if (loading) {
//            if (totalItemCount > previousTotal) {
//                loading = false;
//                previousTotal = totalItemCount;
//            }
//        }
//        if (!loading
//                && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
//            // End has been reached
//
//            // Do something
//            current_page++;
//
//            onLoadMore(current_page);
//
//            loading = true;
//        }
    }

    //public abstract void onLoadMore(int current_page);
}
