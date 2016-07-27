package tokyofarmer.com.uk.tf.adapters;

/**
 * Created by Gazmend on 1/13/2016.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tokyofarmer.com.uk.tf.ClickSpan;
import tokyofarmer.com.uk.tf.R;
import tokyofarmer.com.uk.tf.models.FragmentModel;
import tokyofarmer.com.uk.tf.utils.Common;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentViewHolder> implements View.OnClickListener{

    private List<FragmentModel> mFeedList;
    private Context cntx;
    private ViewGroup mParent;
    FragmentModel timeLineModel;
    ImageView iv,ci;

    public DocumentAdapter(List<FragmentModel> feedList) {
        mFeedList = feedList;
    }

    @Override
    public DocumentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType == 1)
        {
            view = View.inflate(parent.getContext(), R.layout.item_doc_center_text, null);
        }
        else if(viewType == 2)
        {
            view = View.inflate(parent.getContext(), R.layout.item_doc_empty, null);
        }
        else if(viewType == 3)
        {
            view = View.inflate(parent.getContext(), R.layout.item_doc_header, null);
        }
        else
        {
            view = View.inflate(parent.getContext(), R.layout.item_doc, null);
        }

        mParent = parent;
        return new DocumentViewHolder(view, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        timeLineModel = mFeedList.get(position);
        int retvalue=0;
        // check for type of style
        String style = timeLineModel.getStyle();
        String type = timeLineModel.getType();
        Boolean vis = timeLineModel.isVisible();

        // apply style
        if(style.length() > 3) {
            JSONObject styleObj = null;
            try {
                styleObj = new JSONObject(style);
                switch (type) {
                    case "p":
                        String textalign = styleObj.getString("text-align");
                        Log.i("FOUND_TEXTALIGN", textalign);
                        if (textalign.equals("center"))
                        {
                            //View view = View.inflate(mParent.getContext(), R.layout.item_doc_center_text, null);
                            retvalue = 1;
                        }

                        break;
                    case "h":
                        retvalue = 3;
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (vis != true) retvalue = 2;
        return retvalue;
    }

    @Override
    public void onBindViewHolder(DocumentViewHolder holder, int position) {
        //FragmentModel timeLineModel = mFeedList.get(position);

        String style = timeLineModel.getStyle();
        String type = timeLineModel.getType();

        EditText et = (EditText) holder.fragment;
        cntx = et.getContext();

        SharedPreferences prefs = cntx.getSharedPreferences("tf.uk.com.tokyofarmer", Context.MODE_PRIVATE);
        String fragmentIdArray = prefs.getString("fragmentIdArray", null);
        final String[] fragmentArrayFromPrefs = fragmentIdArray.split(",");

        final EditText et2 = (EditText) holder.fragment;
        WebView vw = (WebView) holder.htmlcont;
        iv = (ImageView) holder.imagecontent;
        ci = (ImageView) holder.collapseicon;

        ci.setOnClickListener(this);


        String text = timeLineModel.getText();

        et.setClickable(true);
        et.setMovementMethod(LinkMovementMethod.getInstance());

        // set proper identation
        if (timeLineModel.getLevel()!=0){
            text = Common.setIdent(timeLineModel.getLevel(),text,cntx);
            Log.i("IDENTED",text);
            holder.fragment.setText(Html.fromHtml(text));
        }

        et.setVisibility(View.GONE);
        vw.setVisibility(View.GONE);
        iv.setVisibility(View.GONE);
        //ci.setVisibility(View.GONE);


        ci.setImageResource(R.drawable.empty);
        switch(type) {
            case "html":
                String html = timeLineModel.getText();
                //String html = text;
                String mime = "text/html";
                String encoding = "utf-8";
                vw.setVisibility(View.VISIBLE);
                vw.getSettings().setJavaScriptEnabled(true);
                String finalhtml = "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />" + html.toString();
                Log.i("FOUND_HTML", finalhtml);
                vw.loadDataWithBaseURL("file:///android_asset/", finalhtml, mime, encoding, null);
                break;
            case "img":
                String fulluri ="https://demo.tokyofarmer.com"+timeLineModel.getText();
                Log.i("FOUND_URI", fulluri);
                iv.setVisibility(View.VISIBLE);
                Context cntx = holder.imagecontent.getContext();

                //build the login
                String credentials = "tokyo:tokyo";
                final String credentials64 = "Basic " + Common.stringToBase64String(credentials);

                OkHttpClient okClient = new OkHttpClient.Builder()
                        .addInterceptor(
                                new Interceptor() {
                                    @Override
                                    public Response intercept(Interceptor.Chain chain) throws IOException {
                                        Request original = chain.request();

                                        // Request customization: add request headers
                                        Request.Builder requestBuilder = original.newBuilder()
                                                .header("Authorization", credentials64)
                                                .method(original.method(), original.body());

                                        Request request = requestBuilder.build();
                                        return chain.proceed(request);
                                    }
                                })
                        .build();

                Picasso picasso= new Picasso.Builder(cntx).downloader(new OkHttp3Downloader(okClient)).build();
                picasso.load(fulluri).into(holder.imagecontent);
                break;
            case "h":
                ci.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                String headingText = "<h"+ String.valueOf(timeLineModel.getLevel())+">"+text+"</h"+String.valueOf(timeLineModel.getLevel())+">";
                holder.fragment.setText(Html.fromHtml(headingText));
                text = headingText;
                et.setVisibility(View.VISIBLE);
                break;
            default:
                et.setVisibility(View.VISIBLE);
                holder.fragment.setText(Html.fromHtml(text));
                break;
        }


        // handle markup
        String markup = timeLineModel.getMarkup();

        JSONArray jsonMarkupArray = null;
        try {
            jsonMarkupArray = new JSONArray(markup);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // apply markup
        try {
            String target;
            for (int j = 0; j < jsonMarkupArray.length(); j++) {
                JSONObject markupObj = new JSONObject(jsonMarkupArray.getString(j));
                String temptext = text.substring(markupObj.getInt("start"),markupObj.getInt("end"));
                String tag = markupObj.getString("tag");
                switch(tag) {
                    case "strong":
                        text = text.replace(temptext, "<b>"+temptext+"</b>");
                        // set the transformed text to the EditText
                        holder.fragment.setText(Html.fromHtml(text));
                        break;
                    case "a":
                        target = markupObj.getString("target");

                        if(target.substring(0,1).equals("/"))
                        {
                            target = "https://demo.tokyofarmer.com" + target;
                        }
                        text = text.replace(temptext, "<a href=\"" + target + "\">" + temptext + "</a>");
                        // set the transformed text to the EditText
                        holder.fragment.setText(Html.fromHtml(text));
                        break;
                    case "ilink":
                        target = markupObj.getString("target");
                        final String finalizedTarget = target;
                        clickify(et, temptext ,new ClickSpan.OnClickListener()
                        {

                            @Override
                            public void onClick() {
                                // do something
                                int fragmentIndex = Arrays.asList(fragmentArrayFromPrefs).indexOf(finalizedTarget);
                                //Scroll item 2 to 20 pixels from the top
                                LinearLayoutManager layoutManager = (LinearLayoutManager) ((RecyclerView)mParent).getLayoutManager();
                                layoutManager.scrollToPositionWithOffset(fragmentIndex, 20);
                                //Toast.makeText(cntx, finalizedTarget, Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // if there is a bullet apply it
        if(timeLineModel.getBullet().equals("u")) {
            text = "&#8226; "+text+"<br/>";
            holder.fragment.setText(Html.fromHtml(text));
        }

    }


    // handle clicks of internal links
    public static void clickify(EditText view, final String clickableText,
                                final ClickSpan.OnClickListener listener) {

        CharSequence text = view.getText();
        String string = text.toString();
        ClickSpan span = new ClickSpan(listener);

        Log.i("CLICKIFY_CLICKABLE", clickableText);

        int start = string.indexOf(clickableText);
        Log.i("CHECK_CLICKIFY", string);
        Log.i("CLICKIFY_INDEX", String.valueOf(start));
        int end = start + clickableText.length();
        if (start == -1) return;

        Log.i("MAKING_CLICKIFY", clickableText);
        Log.i("CHECK_CLICKIFY2", string);

        if (text instanceof Spannable) {
            ((Spannable)text).setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            Log.i("CLICKIFY_FOUND","IS_SPANNABLE");
        } else {
            SpannableString s = SpannableString.valueOf(text);
            Log.i("CLICKIFY_FOUND", "IS_NOT_SPANNABLE");
            s.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            view.setText(s);
        }

        MovementMethod m = view.getMovementMethod();
        if ((m == null) || !(m instanceof LinkMovementMethod)) {
            view.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }


    @Override
    public int getItemCount() {
        return (mFeedList!=null? mFeedList.size():0);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == ci.getId()) {
            Toast.makeText(cntx, "ImageView Clicked!", Toast.LENGTH_LONG).show();
            //mFeedList.get(8).setVisible(false);
            mFeedList.remove(8);
            notifyItemRemoved(8);
            //notifyDataSetChanged();
        }
    }
}