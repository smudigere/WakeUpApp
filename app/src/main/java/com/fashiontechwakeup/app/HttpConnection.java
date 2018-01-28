package com.fashiontechwakeup.app;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

public class HttpConnection {

    /**
     * A static method to make all API calls.
     *
     * @param link  <p> The API call that needs to made. </p>
     * @return  <p> The API result. </p>
     * @throws Exception    <p> Multiple Exceptions needed to be handled. </p>
     */
    public static String httpConnection(String link) throws Exception {

        Log.i("allHttpConnections", link);
        String result;

        URL urls = new URL(link);
        HttpURLConnection conn = (HttpURLConnection) urls.openConnection();
        conn.setReadTimeout(150000); //milliseconds
        conn.setConnectTimeout(15000); // milliseconds
        conn.setRequestMethod("GET");

        conn.connect();

        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(), "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null)
                sb.append(line);

            result = String.valueOf(sb);

            return result;
        } else {
            throw new UnknownHostException("Unsuccessful Connection");
        }
    }

}
