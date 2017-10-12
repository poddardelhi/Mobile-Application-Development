package com.example.nishantpoddar.homeassignment_3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class CloudClass {

    private final String boundary =  "*****";
    private final String lineEnd = "\r\n";
    private final String twoHyphens = "--";

    private HttpURLConnection connection;
    private DataOutputStream outputStream;
    private String fileName;
    private String fieldName;
    private String targetUrl;


    public CloudClass(String targetUrl, String fileName, String fieldName){
        this.targetUrl = targetUrl;
        this.fileName = fileName;
        this.fieldName = fieldName;
    }

    public void addImage(Bitmap bitmap) throws IOException {
        URL url = new URL(targetUrl);

        connection = (HttpURLConnection)url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("ENCTYPE", "multipart/form-data");
        connection.setRequestProperty("Cache-Control", "no-cache");
        connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + this.boundary);
        connection.setRequestProperty(fieldName, fileName);

        outputStream = new DataOutputStream(connection.getOutputStream());

        outputStream.writeBytes(twoHyphens+boundary+ lineEnd);
        outputStream.writeBytes("Content-Disposition: form-data; name=\"" + fieldName+"\";filename=\""+fileName+"\""+ lineEnd);
        outputStream.writeBytes(lineEnd);

        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, arrayOutputStream);
        byte[] bytes = arrayOutputStream.toByteArray();

        outputStream.write(bytes);
    }

    public Bitmap finish() throws IOException {
        Bitmap bitmap = null;

        outputStream.writeBytes(lineEnd);
        //outputStream.writeBytes(twoHyphens+boundary+ lineEnd);
        outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

        outputStream.flush();
        outputStream.close();

        int responseCode = connection.getResponseCode();
        Log.i("HTTP_RESP_CODE", "Got response code: "+responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream responseStream = new BufferedInputStream(connection.getInputStream());
            bitmap = BitmapFactory.decodeStream(responseStream);

            connection.disconnect();
        }

        return bitmap;
    }
}
