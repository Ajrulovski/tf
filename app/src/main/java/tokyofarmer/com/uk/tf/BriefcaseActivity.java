package tokyofarmer.com.uk.tf;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;
import tokyofarmer.com.uk.tf.rest.ApiService;
import tokyofarmer.com.uk.tf.rest.ServiceClient;

public class BriefcaseActivity extends AppCompatActivity {
    private ApiService service;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_briefcase);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        service = ServiceClient.getInstance().getClient(this, ApiService.class);
        getDocFromFragmentIds();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void getDocFromFragmentIds(){
        ////////////////
        // get the text from the fragment list
        ///////////////

        String json_frag_content = "{\"method\": \"projects.list\",\"params\": {}}";
        Log.i("FRAG_CONTENT", json_frag_content);
        byte[] bcont=null;
        try {
            bcont = json_frag_content.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        TypedInput in_fragcontent = new TypedByteArray("application/json", bcont);

        service.getAllDocIds(in_fragcontent, new Callback<Response>() {

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
                TextView tv = (TextView) findViewById(R.id.sometext);
                tv.setText(result);
                Log.v("BRIEFCASE_RESULT", result);
                Toast.makeText(BriefcaseActivity.this, result, Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(BriefcaseActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

}
