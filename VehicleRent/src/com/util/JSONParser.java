package com.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JSONParser {
	private static final Logger logger = Logger.getLogger(JSONParser.class);
    static InputStream is = null;
    static JsonObject jObj = null;
    static String json = "";

    public JSONParser(){
    }

    @SuppressWarnings("unchecked")
	public JsonObject makeHttpRequest(String url, String method, Object params) {
        try {
                if (method.equalsIgnoreCase("POST")) {
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(url);
                    httpPost.setEntity(new UrlEncodedFormEntity((List<NameValuePair>)params));

                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    is = httpEntity.getContent();
                } else if (method.equalsIgnoreCase("GET")) {
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    String paramString = URLEncodedUtils.format((List<NameValuePair>)params, "utf-8");
                    url += paramString;

                    HttpGet httpGet = new HttpGet(url);
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    is = httpEntity.getContent();
                }else if (method.equalsIgnoreCase("jsonPOST")) {
                    DefaultHttpClient httpClient = new DefaultHttpClient();

                    HttpPost httpPost = new HttpPost(url);
                    httpPost.setHeader("Authorization", "key=AAAAIigj3pU:APA91bHeKvahB6LJX5YmNQcjQWRKw9Lm-rCG7955rw-g3KfrGQhQlQpXB9JM5oXcQi9BSCS-YpiffqZ1fopGFU-57UC-zVFw0omYSyXmvW_LfyKDkWdITC0LCTRpGrL0zXl2Nj4-YWxj6vuInrxac7MEFxA2ipfy4g");
                    httpPost.setEntity(new StringEntity((String)params));
                    httpPost.setHeader("Accept", "application/json");
                    httpPost.setHeader("Content-type", "application/json");
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    is = httpEntity.getContent();
                }else if (method.equalsIgnoreCase("xmlPOST")) {
                    DefaultHttpClient httpClient = new DefaultHttpClient();

                    HttpPost httpPost = new HttpPost(url);
                    httpPost.setEntity(new StringEntity((String)params));
                    httpPost.setHeader("Accept", "application/xml");
                    httpPost.setHeader("Content-type", "application/xml");
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    is = httpEntity.getContent();
                }else{
                    DefaultHttpClient httpclient = new DefaultHttpClient();
                    HttpGet httpget = new HttpGet(url);
                    HttpResponse httpResponse = httpclient.execute(httpget);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    is = httpEntity.getContent();
                }
            

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        jObj = new JsonParser().parse(json).getAsJsonObject();
        return jObj;
    }

    public static String convertStandardJSONString(String data_json) {
        data_json = data_json.replace("\\\\\\\"", "");
        data_json = data_json.replace("}\",", "},");
        data_json = data_json.replace("}\"", "}");
        data_json = data_json.replace("\"{", "{");
        data_json = data_json.replace("{\\\"", "{\"");
        data_json = data_json.replace("\\\":", "\":");
        data_json = data_json.replace(",\\\"", ",\"");
        data_json = data_json.replace(":\\\"", ":\"");
        data_json = data_json.replace("\\\"", "\"");
        data_json = data_json.replaceAll("\\\\", "");
        data_json = data_json.replaceAll("u003cbr/u003e", "\n");
        data_json = data_json.replaceAll("u003c", "<");
        data_json = data_json.replaceAll("u003e", ">");
        data_json = data_json.replaceAll("'null'", "N/A");
        return data_json;
    }
}
