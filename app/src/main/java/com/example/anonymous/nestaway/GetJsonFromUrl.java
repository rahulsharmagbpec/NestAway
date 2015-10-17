package com.example.anonymous.nestaway;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.List;

@SuppressWarnings("ALL")
public class GetJsonFromUrl {
    static String response = null;
    public final static int GET = 1;
    public final static int POST = 2;

    public String getJSONFromUrl(String url, int method,
                                 List<NameValuePair> params) {
        // Making the HTTP request
        try
        {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            if (method == POST)
            {
                HttpPost httpPost = new HttpPost(url);
                // adding post params
                if (params != null)
                {
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                }

                httpResponse = httpClient.execute(httpPost);

            }
            else if (method == GET)
            {
                if (params != null)
                {
                    String paramString = URLEncodedUtils
                            .format(params, "utf-8");
                    url += "?" + paramString;
                }
                HttpGet httpGet = new HttpGet(url);

                httpResponse = httpClient.execute(httpGet);

            }
            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;

    }
}
