package com.grassrootsreporting;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MultipartBody;


/**
 * Serves as an interface for HTTP traffic
 */
public class PostDataToServer extends AsyncTask<String, Void, String> {
    static final String TAG = "HttpInterface";
    private List<Bitmap> photoList;  // bitmaps
    private Map<String, String> reportDetails;

    public PostDataToServer(Map<String, String> reportDetails, List<Bitmap> photoList) {
        this.photoList = photoList;

        // ensure the right keys are present
        assert reportDetails.containsKey("reportTitle") && null != reportDetails.get("reportTitle");
        assert reportDetails.containsKey("summaryStr") && null != reportDetails.get("summaryStr");
        assert reportDetails.containsKey("locationStr") && null != reportDetails.get("locationStr");
        assert reportDetails.containsKey("timestampStr") && null != reportDetails.get("timestampStr");
        assert reportDetails.containsKey("gpsCoordinates") && null != reportDetails.get("gpsCoordinates");
        this.reportDetails = reportDetails;
    }

    @Override
    protected String doInBackground(String... databaseURL) {
        assert 1 == databaseURL.length;
        OkHttpClient client = new OkHttpClient();
        MultipartBody.Builder mPartBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("reportTitle", reportDetails.get("reportTitle"))
                .addFormDataPart("summaryStr", reportDetails.get("summaryStr"))
                .addFormDataPart("locationStr", reportDetails.get("locationStr"))
                .addFormDataPart("timestampStr", reportDetails.get("timestampStr"))
                .addFormDataPart("gpsCoordinates", reportDetails.get("gpsCoordinates"));

        for (int i=0; i<photoList.size(); i++) {
            // add each photo as an array of bytes
            Bitmap bmp = photoList.get(i);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] byteArr = baos.toByteArray();
            String imageEncoded = Base64.encodeToString(byteArr, Base64.DEFAULT);
            String photoName = "photo_" + Integer.toString(i);
            MultipartBody.Builder image = mPartBuilder.addFormDataPart(
                    "photo_from_camera",
                    photoName,
                    RequestBody.create(MediaType.parse("image/*bmp"), imageEncoded) //bytes)
            );
        }
        RequestBody reqBody = mPartBuilder.build();
        Request request = new Request.Builder()
                .url(databaseURL[0])
                .post(reqBody)
                .build();

        String msg = "Uninitialized";
        try {
            Response responseObj = client.newCall(request).execute();
            String responseStr = responseObj.body().string();
            Log.i(TAG, "responseStr = " + responseStr);
        } catch (IOException e) {
            e.printStackTrace();
            return "FAILURE";
        }
        return "SUCCESS";
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i(TAG, "onPostExecute()");
    }

    @Override
    protected void onPreExecute() {
        Log.i(TAG, "onPreExecute()");
    }

    @Override
    protected void onProgressUpdate(Void... values) {}
}
