package tokyofarmer.com.uk.tf;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;
import tokyofarmer.com.uk.tf.adapters.DocumentAdapter;
import tokyofarmer.com.uk.tf.models.FragmentModel;
import tokyofarmer.com.uk.tf.rest.ApiService;
import tokyofarmer.com.uk.tf.rest.ServiceClient;
import tokyofarmer.com.uk.tf.utils.Common;

public class DocViewerActivity extends AppCompatActivity {
    private ApiService service;
    private String credentials64;
    String fragments = "";
    private List<FragmentModel> mDataList = new ArrayList<>();
    private DocumentAdapter mDocAdapter;
    private RecyclerView mRecyclerView;
    public String[] strArr;
    String docId="fbe1cccd-9bf8-4729-9fbd-a384e402751b";
    String docText="";
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_viewer);

        prefs = getSharedPreferences("tf.uk.com.tokyofarmer", Context.MODE_PRIVATE);


        String credentials = "tokyo:tokyo";
        credentials64 = "Basic " + Common.stringToBase64String(credentials);

        HashMap<String, String> loginhash=new HashMap<String, String>();
        loginhash.put("email","test@vanilr.com");
        loginhash.put("password","test");

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        service = ServiceClient.getInstance().getClient(this, ApiService.class);

        if(savedInstanceState == null || !savedInstanceState.containsKey("doctext")) {
            //savedInstanceState.putString("docid", docId);
            //savedInstanceState.putString("doctext",docText);

            // if we already have the value for that id in sharedprefs read that doc from there
            // else fetch the doc, load it, and save it to shared prefs
            String savedDoc = prefs.getString(docId, null);
            String fragmentIdArray = prefs.getString("fragmentIdArray", null);

            if(savedDoc != null)
            {
                Log.i("FOUND_IN_PREFS", fragmentIdArray);
                String[] fragmentArrayFromPrefs = fragmentIdArray.split(",");
                strArr = fragmentArrayFromPrefs;
                docText = savedDoc;
                fillAdapter(docText);
            }
            else
            {
                getFragmentIds(docId);
            }
        }
        else {
            //// if we have an instance state then read that and not call the API
            //// read the data from parcelable
            //docId = savedInstanceState.getString("docid");
            strArr = savedInstanceState.getStringArray("fragmentsArray");
            docText = savedInstanceState.getString("doctext");
            fillAdapter(docText);
        }

        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager,this, mDataList) {
//            @Override
//            public void onLoadMore(int current_page) {
//                // do somthing...
//
//                //loadMoreData(current_page);
//
//            }

        });


        //getFragmentIds("ded28338-51ee-41c0-a754-d7c851d68abd");
    }

    public void getFragmentIds(String docid){

        // First, get the document and parse the fragmentIds from the returned data
        String json = "{\"method\": \"getDocument\",\"params\": {\"id\": \""+docid+"\"}}";
        byte[] b=null;
        try {
            b = json.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        TypedInput in = new TypedByteArray("application/json", b);

        //////////////////////
        //get fragment id list
        /////////////////////

        service.getDocument(credentials64, in, new Callback<Response>() {

            @Override
            public void success(Response cb, Response response) {

                //Try to get response body
                BufferedReader reader = null;
                StringBuilder sb = new StringBuilder();
                try {

                    reader = new BufferedReader(new InputStreamReader(cb.getBody().in()));

                    String line;

                    try {
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


                String result = sb.toString();
                String resfinal = "";

                JSONObject jObjResult=null;
                try {
                    jObjResult = new JSONObject(result);
                    fragments = jObjResult.getString("result");
                    JSONObject jObjRevision = new JSONObject(fragments);
                    fragments = jObjRevision.getString("revision");
                    JSONObject jObjFinal = new JSONObject(fragments);
                    fragments = jObjFinal.getString("fragmentIds");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //for(int i=0;i<jObj.length();i++) {
                //    resfinal = resfinal+jObj.
                //}

                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(fragments);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                strArr = new String[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        strArr[i] = jsonArray.getString(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("RESPONSE", fragments);

                // get the doc
                //getDocFromFragmentIds(fragments,savedInstanceState);

                ////////////////
                // get the text from the fragment list
                ///////////////

                String escaped_fragids = fragments.replace("\"", "\\");
                Log.i("ESCAPED", escaped_fragids);
                String json_frag_content = "{\"method\": \"getFragments\",\"params\": {\"fragmentIds\":"+fragments+" }}";
                Log.i("FRAG_CONTENT", json_frag_content);
                byte[] bcont=null;
                try {
                    bcont = json_frag_content.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                TypedInput in_fragcontent = new TypedByteArray("application/json", bcont);

                service.getDocument(credentials64, in_fragcontent, new Callback<Response>() {

                    @Override
                    public void success(Response cb, Response response) {

                        //Try to get response body
                        BufferedReader reader = null;
                        StringBuilder sb = new StringBuilder();
                        try {

                            reader = new BufferedReader(new InputStreamReader(cb.getBody().in()));

                            String line;

                            try {
                                while ((line = reader.readLine()) != null) {
                                    sb.append(line);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        String result = sb.toString();

                        // fill the recycler view
                        JSONObject jObjResult = null;
                        String fragments_content = "";
                        try {
                            jObjResult = new JSONObject(result);
                            fragments_content = jObjResult.getString("result");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                        fillAdapter(fragments_content);

                        //if (result) {
                        //Toast.makeText(Messages.this, "Got the messages.", Toast.LENGTH_LONG).show();
                        //Log.i("RESPONSEresult", result);
                        //TextView tv = (TextView) findViewById(R.id.testing);
                        //tv.setText(result);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(DocViewerActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(DocViewerActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getDocFromFragmentIds(String fragments, Bundle savedInstanceState){
        ////////////////
        // get the text from the fragment list
        ///////////////

        String escaped_fragids = fragments.replace("\"", "\\");
        Log.i("ESCAPED", escaped_fragids);
        String json_frag_content = "{\"method\": \"getFragments\",\"params\": {\"fragmentIds\":"+fragments+" }}";
        Log.i("FRAG_CONTENT", json_frag_content);
        byte[] bcont=null;
        try {
            bcont = json_frag_content.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        TypedInput in_fragcontent = new TypedByteArray("application/json", bcont);

        service.getDocument(credentials64, in_fragcontent, new Callback<Response>() {

            @Override
            public void success(Response cb, Response response) {

                //Try to get response body
                BufferedReader reader = null;
                StringBuilder sb = new StringBuilder();
                try {

                    reader = new BufferedReader(new InputStreamReader(cb.getBody().in()));

                    String line;

                    try {
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


                String result = sb.toString();

                // fill the recycler view
                JSONObject jObjResult = null;
                String fragments_content = "";
                try {
                    jObjResult = new JSONObject(result);
                    fragments_content = jObjResult.getString("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                fillAdapter(fragments_content);

                //if (result) {
                //Toast.makeText(Messages.this, "Got the messages.", Toast.LENGTH_LONG).show();
                //Log.i("RESPONSEresult", result);
                //TextView tv = (TextView) findViewById(R.id.testing);
                //tv.setText(result);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(DocViewerActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

    public void fillAdapter(String fragments_content){
        // pass the value to the global variable so that the savedInstanceState gets fed
        docText = fragments_content;
        Boolean handlingBullets = false;
        String bullethtml="";

        // save the value in shared prefs so that it is read at next startup
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(docId, fragments_content);
        editor.commit();

        JSONObject jFragContent = null;
        //JSONArray jsonArray = null;
        try {
            Log.i("FRAG_CONTENT",fragments_content);
            jFragContent = new JSONObject(fragments_content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mDataList.clear();

        // save the fragment index - strArr to shared prefs
        StringBuilder sb = new StringBuilder();
        int currentlevel = 1;
        int innerSectionCounter = 0;
        int oldSectionCounter = 0;
        String currentSectionIdPrefix="1";
        String oldSectionIdPrefix = "";
        for (int i = 0; i < strArr.length; i++) {
            sb.append(strArr[i]).append(",");
            try {
                //strArr[i] = jsonArray.getString(i);
                FragmentModel model = new FragmentModel();

                //get each fragment values
                JSONObject obj = new JSONObject(jFragContent.getString(strArr[i]));

                String id= obj.getString("id");
                String type= obj.getString("type");
                String text= obj.getString("text");

                int level= obj.getInt("level");
                String bullet= obj.getString("bullet");
                String style= obj.getString("style");
                String markup= obj.getString("markup");

                String lastid="", lasttype="", laststyle="", lastbullet="";
                int lastlevel=0;

                model.setId(id);
                model.setType(type);
                model.setText(text);
                model.setLevel(level);
                model.setBullet(bullet);
                model.setStyle(style);
                model.setMarkup(markup);
                model.setVisible(true);

                if (type.equals("h")){
                    if(level>currentlevel)
                    {

                        oldSectionCounter = innerSectionCounter;
                        innerSectionCounter = 1;
                        oldSectionIdPrefix = currentSectionIdPrefix;
                        currentSectionIdPrefix = currentSectionIdPrefix+"."+String.valueOf(innerSectionCounter);
                        currentlevel = level;
                        //Log.i("HEADER_ENTER_ONE",currentSectionIdPrefix);
                    }
                    else if (level == currentlevel)
                    {
                        innerSectionCounter = innerSectionCounter +1;
                        //oldSectionIdPrefix = currentSectionIdPrefix;
                        if(oldSectionIdPrefix.length()>0) {
                            currentSectionIdPrefix = oldSectionIdPrefix + "." + String.valueOf(innerSectionCounter);
                        }
                        else
                        {
                            currentSectionIdPrefix = String.valueOf(innerSectionCounter);
                        }
                        //Log.i("HEADER_ENTER_TWO",currentSectionIdPrefix);
                    }
                    else
                    {
                        // CHECK THIS PART FOR LARGER LEAPS E.G. FROMLEVEL 7 TO LEVEL 1
                        innerSectionCounter = oldSectionCounter +1;
                        int lastIndexOfDot = currentSectionIdPrefix.lastIndexOf(".");
                        if(lastIndexOfDot > -1) {
                            oldSectionIdPrefix = currentSectionIdPrefix.substring(0, lastIndexOfDot);
                            int secondLastIndexOfDot = oldSectionIdPrefix.lastIndexOf(".");
                            if(secondLastIndexOfDot>-1) {
                                // if the section ID is complex e.g. 1.3.4.4
                                // we need to take the last available integer and use that as the last counter position
                                // at which we left off
                                oldSectionCounter = Integer.valueOf(oldSectionIdPrefix.substring(secondLastIndexOfDot+1));
                                oldSectionIdPrefix = oldSectionIdPrefix.substring(0, secondLastIndexOfDot);
                            }
                            else
                            {
                                // if there are no dots left in the prefix, this means that we have a valid number value to use as a counter
                                oldSectionCounter = Integer.valueOf(oldSectionIdPrefix);
                            }
                            oldSectionCounter = oldSectionCounter +1;
                        }
                        else
                        {
                            Log.i("STRANGE_SITUATION","LOWER_LEVEL_BUT_NO_DOTS");
                        }
                        currentSectionIdPrefix = oldSectionIdPrefix+"."+String.valueOf(oldSectionCounter);
                        currentlevel = level;
                        //Log.i("HEADER_ENTER_THREE",currentSectionIdPrefix);
                    }
                    //model.setSectionId(currentSectionIdPrefix+"-"+String.valueOf(level));
                    model.setSectionId(currentSectionIdPrefix);
                }
                else
                {
                    model.setSectionId("None");
                }
                //Log.i("RESPONSE_OUT", usermessagetypeid + " " + latitude + " " + longitude + " " + time + " " + messagetext);
                mDataList.add(model);

//                // handle bulleted lists
//                // first handle markup
//                JSONArray jsonMarkupArray = null;
//                try {
//                    jsonMarkupArray = new JSONArray(markup);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                if(bullet.equals("u"))
//                {
//
//                    // apply markup
//                    try {
//                        String target;
//                        for (int j = 0; j < jsonMarkupArray.length(); j++) {
//                            JSONObject markupObj = new JSONObject(jsonMarkupArray.getString(j));
//                            String temptext = text.substring(markupObj.getInt("start"),markupObj.getInt("end"));
//                            String tag = markupObj.getString("tag");
//                            switch(tag) {
//                                case "strong":
//                                    text = text.replace(temptext, "<b>"+temptext+"</b>");
//                                    break;
//                                case "a":
//                                    target = markupObj.getString("target");
//                                    //Log.i("FOUND_LINK_WITH ",target.substring(0,1));
//
//                                    if(target.substring(0,1).equals("/"))
//                                    {
//                                        target = "https://demo.tokyofarmer.com" + target;
//                                        //Log.i("FOUND_LINK", target);
//                                    }
//                                    text = text.replace(temptext, "<a href=\"" + target + "\">" + temptext + "</a>");
//                                    break;
////                                case "ilink":
////                                    target = markupObj.getString("target");
////                                    final String finalizedTarget = target;
////                                    clickify(et, temptext ,new ClickSpan.OnClickListener()
////                                    {
////
////                                        @Override
////                                        public void onClick() {
////                                            // do something
////                                            int fragmentIndex = Arrays.asList(fragmentArrayFromPrefs).indexOf(finalizedTarget);
////                                            //Scroll item 2 to 20 pixels from the top
////                                            LinearLayoutManager layoutManager = (LinearLayoutManager) ((RecyclerView)mParent).getLayoutManager();
////                                            layoutManager.scrollToPositionWithOffset(fragmentIndex, 20);
////                                            Toast.makeText(cntx, finalizedTarget, Toast.LENGTH_SHORT).show();
////                                        }
////                                    });
////                                    break;
//                            }
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//
//                    // check if we are starting with building the bullets or are in the middle of building them
//                    if(handlingBullets)
//                    {
//                        bullethtml = bullethtml + "<li>"+text+"</li>";
//                    }
//                    else
//                    {
//                        handlingBullets = true;
//                        bullethtml = "<ul><li>"+text+"</li>";
//                    }
//
//                    lastid=id;
//                    lasttype=type;
//                    lastlevel=level;
//                    laststyle=style;
//                    lastbullet=bullet;
//                }
//                else {
//                    if(handlingBullets)
//                    {
//                        bullethtml = bullethtml + "</ul>";
//                        //text = bullethtml;
//
//
//                        model.setId(lastid);
//                        model.setType(lasttype);
//                        model.setText(bullethtml);
//                        model.setLevel(lastlevel);
//                        model.setBullet(lastbullet);
//                        model.setStyle(laststyle);
//                        model.setMarkup(markup);
//
//                        //Log.i("RESPONSE_OUT", usermessagetypeid + " " + latitude + " " + longitude + " " + time + " " + messagetext);
//                        //mDataList.add(model);
//                        Log.i("BULLET_HTML", bullethtml);
//                        bullethtml = "";
//                        handlingBullets = false;
//                    }
//                    else {
//                        model.setId(id);
//                        model.setType(type);
//                        model.setText(text);
//                        model.setLevel(level);
//                        model.setBullet(bullet);
//                        model.setStyle(style);
//                        model.setMarkup(markup);
//                    }
//                    //Log.i("RESPONSE_OUT", usermessagetypeid + " " + latitude + " " + longitude + " " + time + " " + messagetext);
//                    mDataList.add(model);
//                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mDocAdapter = new DocumentAdapter(mDataList);
            mRecyclerView.setAdapter(mDocAdapter);
        }
        editor.putString("fragmentIdArray", sb.toString());
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_doc_viewer, menu);
        return true;
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putString("docid", docId);
        outState.putString("doctext", docText);
        outState.putStringArray("fragmentsArray", strArr);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
