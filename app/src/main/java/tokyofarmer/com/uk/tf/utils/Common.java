package tokyofarmer.com.uk.tf.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tokyofarmer.com.uk.tf.R;

/**
 * Created by INSDare on 2/11/2015.
 */
public class Common {

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static String setIdent(int times, String text, Context cntx){
        for(int i=0;i<times;i++){
            text = cntx.getString(R.string.tab)+text;
        }
        return text;
    }

    public static int handleLevel(int currentlevel, int foundlevel){
        if (foundlevel!=currentlevel){
            return foundlevel;
        }
        else
        {
            return currentlevel;
        }
    }

    public static boolean isUsernameValid(String username) {
        boolean isValid = false;

        //Match characters and symbols in the list, a-z, A-Z, 0-9, underscore, hyphen
        //Length at least 6 characters and maximum length of 15
        String expression = "^[a-zA-Z0-9_-]{6,15}$";
        CharSequence inputStr = username;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }
    public static boolean isPhoneValid(String phone) {
        if(phone.length()==11&&phone.startsWith("3897")) {
            return true;
        }
        return false;
    }

    public static boolean isPasswordValid(String password) {
        boolean isValid = false;

        //Match characters and symbols in the list, a-z, A-Z, 0-9, underscore, hyphen
        //Length at least 6 characters and maximum length of 20
        String expression = "^[a-zA-Z0-9_-]{6,20}$";
        CharSequence inputStr = password;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean hasMinimumLength(String text, int minLength) {
        if(text.length()>=minLength) {
            return true;
        }
        return false;
    }

    public static boolean hasConnection(Context curContext) {
        ConnectivityManager cm = (ConnectivityManager) curContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiNetwork = cm
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }

        NetworkInfo mobileNetwork = cm
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }
        return false;
    }

    public static String stringToBase64String(String text) {
        byte[] data = null;
        try {
            data = text.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        return Base64.encodeToString(data, Base64.NO_WRAP);
    }

    public static String encodeTobase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        return imageEncoded;
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return decodeToBitmap(decodedByte);
    }

    public static Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public static String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }

}
