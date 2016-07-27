package tokyofarmer.com.uk.tf;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;
import tokyofarmer.com.uk.tf.rest.ApiService;
import tokyofarmer.com.uk.tf.rest.ServiceClient;
import tokyofarmer.com.uk.tf.utils.Common;

public class TxDatabaseActivity extends AppCompatActivity {

    SharedPreferences prefs;
    private RecyclerView mRecyclerView;
    private ApiService service;
    private String credentials64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tx_database);

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

        // get the active doc data online
        // we are not keeping anything here locally, as that goes into the briefcase
        getAllDocsList();
    }

    public final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {

            // if connected with internet

            Toast.makeText(this, " Connected ", Toast.LENGTH_LONG).show();
            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {

            Toast.makeText(this, " Not Connected ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }

    public void getAllDocsList(){
        ////////////////
        // get a list of live docs from the server
        ///////////////

        //String escaped_fragids = fragments.replace("\"", "\\");
        String json_frag_content = "{\"method\": \"findDocuments\",\"params\": {}}";
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

                // do smth with the result
                JSONObject jObjResult = null;
                String fragments_content = "";
                try {
                    jObjResult = new JSONObject(result);
                    fragments_content = jObjResult.getString("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.i("FOUND_DOCS",fragments_content);
                //fillAdapter(fragments_content);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(TxDatabaseActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }
}
