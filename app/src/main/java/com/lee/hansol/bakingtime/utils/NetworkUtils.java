package com.lee.hansol.bakingtime.utils;

import android.content.Context;

import com.lee.hansol.bakingtime.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

public class NetworkUtils {

    public static JSONArray getJSONArrayFromUrl(Context context, String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        String encoding = context.getString(R.string.encoding_backing_recipe_webpage);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName(encoding)));
            String jsonArrayString = readAllFrom(reader);
            return new JSONArray(jsonArrayString);
        } finally {
            is.close();
        }
    }

    private static String readAllFrom(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = reader.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

}
