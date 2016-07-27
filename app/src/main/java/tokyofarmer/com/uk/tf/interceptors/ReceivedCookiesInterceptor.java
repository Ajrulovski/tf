package tokyofarmer.com.uk.tf.interceptors;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * This Interceptor adds all received Cookies to the app DefaultPreferences.
 * <p>
 * Created by Gazmend on 6/20/2016.
 */

public class ReceivedCookiesInterceptor implements Interceptor {
    SharedPreferences prefs;
    @NonNull
    private final Context mContext;
    @NonNull
    private final String mPrefFile;

    protected ReceivedCookiesInterceptor(@NonNull final Context context) {
        this(context, null);
    }

    public ReceivedCookiesInterceptor(@NonNull final Context context, @Nullable final String prefFile) {
        mContext = context.getApplicationContext();
        mPrefFile = prefFile != null ? prefFile : getClass().getSimpleName();
    }

    protected final SharedPreferences getPrefs() {
        return mContext.getSharedPreferences(mPrefFile, Context.MODE_PRIVATE);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        //prefs = getPrefs("tf.uk.com.tokyofarmer", Context.MODE_PRIVATE);
        prefs = getPrefs();

        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            HashSet<String> cookies = new HashSet<>();

            for (String header : originalResponse.headers("Set-Cookie")) {
                cookies.add(header);
            }

            SharedPreferences.Editor editor = prefs.edit();
            editor.putStringSet("PREF_COOKIES", cookies);
            editor.commit();
        }

        return originalResponse;
    }
}
