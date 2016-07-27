package tokyofarmer.com.uk.tf.interceptors;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This interceptor puts all the Cookies in Shared Preferences into the Request.
 * <p>
 * Created by Gazmend on 6/20/2016.
 */

public class AddCookiesInterceptor implements Interceptor {
    SharedPreferences prefs;
    @NonNull
    private final Context mContext;
    @NonNull
    private final String mPrefFile;

    protected AddCookiesInterceptor(@NonNull final Context context) {
        this(context, null);
    }

    public AddCookiesInterceptor(@NonNull final Context context, @Nullable final String prefFile) {
        mContext = context.getApplicationContext();
        mPrefFile = prefFile != null ? prefFile : getClass().getSimpleName();
    }

    protected final SharedPreferences getPrefs() {
        return mContext.getSharedPreferences(mPrefFile, Context.MODE_PRIVATE);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        HashSet<String> preferences = (HashSet) getPrefs().getStringSet("PREF_COOKIES", new HashSet<String>());
        for (String cookie : preferences) {
            builder.addHeader("Cookie", cookie);
            Log.v("OkHttp", "Adding Header: " + cookie); // This is done so I know which headers are being added; this interceptor is used after the normal logging of OkHttp
        }

        return chain.proceed(builder.build());
    }
}
