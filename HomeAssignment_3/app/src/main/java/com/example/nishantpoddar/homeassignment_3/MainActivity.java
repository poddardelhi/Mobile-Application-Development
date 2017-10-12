package com.example.nishantpoddar.homeassignment_3;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jabistudio.androidjhlabs.filter.BoxBlurFilter;
import com.jabistudio.androidjhlabs.filter.util.AndroidUtils;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String HEROKU_URL = "http://aisjk.herokuapp.com/upload";
    private static final int PERMISSIONS_REQUEST = 1;
    private static final int IMAGE_SELECT_REQUEST = 2;
    BitmapFactory.Options bitimg;
    private String imagePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bitimg = new BitmapFactory.Options();
        requestPermission();
        bitimg.inPreferredConfig = Bitmap.Config.ARGB_8888;
    }

    public void localButtonClicked(View v){

        if (imagePath != null && !imagePath.isEmpty()){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bitimg);

            Toast.makeText(this, "Blurring image...", Toast.LENGTH_LONG).show();
            new LocalTask(bitmap).execute();
        }else{
//            Toast.makeText(this, "Please select an image to blur", Toast.LENGTH_LONG).show();
        }
    }
    public void cloudButtonClicked(View v){
        if (imagePath != null && !imagePath.isEmpty()){

            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bitimg);
            Toast.makeText(this, "Blurring image...", Toast.LENGTH_LONG).show();
            new CloudTask(bitmap, imagePath).execute();
        }else{
//            Toast.makeText(this, "Please select an image to blur", Toast.LENGTH_LONG).show();
        }
    }

    public void selectImage(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, IMAGE_SELECT_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_SELECT_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                imagePath = getImagePathFromUri(uri);
//                Toast.makeText(this, "Selected Image: " + imagePath, Toast.LENGTH_LONG).show();
            }
        }
    }

    /** Our abstract AsyncTask for image processing.
     *  It will display the processing result in a UI dialog.
     *  Implementation must define the processImage() method
     */
    abstract class ImageProcessingAsyncTask extends AsyncTask<Void, Void, Bitmap> {

        protected abstract Bitmap processImage();

        protected Bitmap doInBackground(Void... voids) {
            return processImage();
        }

        @Override
        protected void onPostExecute(Bitmap bmp) {
            super.onPostExecute(bmp);
            if (bmp != null) {
                showImage(bmp);
            }
        }
    }

    /** Send file to Heroku app and display the response image in UI */
    private class CloudTask extends ImageProcessingAsyncTask {
        private final Bitmap bmp;
        private final String imageName;

        public CloudTask(Bitmap bitmap, String imageName) {
            this.bmp = bitmap;
            this.imageName = imageName;
        }

        @Override
        protected Bitmap processImage() {
            Bitmap responseBitmap = null;
            try {
                CloudClass uploader = new CloudClass(HEROKU_URL, this.imageName, "uploaded_file");
                uploader.addImage(this.bmp);
                responseBitmap = uploader.finish();
            } catch (IOException e) {
                Log.e("CloudTaskProcessError", e.getMessage());
            }
            return responseBitmap;
        }
    }

    private class LocalTask extends ImageProcessingAsyncTask {
        private final Bitmap bmp;
        public LocalTask(Bitmap bmp) {
            this.bmp = bmp;
        }

        @Override
        protected Bitmap processImage() {
            int width = bmp.getWidth();
            int height = bmp.getHeight();
            int[] pixels = AndroidUtils.bitmapToIntArray(bmp);

            BoxBlurFilter boxBlurFilter = new BoxBlurFilter();
            boxBlurFilter.setRadius(10);
            boxBlurFilter.setIterations(10);
            int[] result = boxBlurFilter.filter(pixels, width, height);

            return Bitmap.createBitmap(result, width, height, Bitmap.Config.ARGB_8888);
        }
    }


    /** Opens a pop-up dialog displaying the argument Bitmap image */
    public void showImage(Bitmap bmp) {
        Dialog builder = new Dialog(this);
        ImageView imageView = new ImageView(this);

        //Scale down the bitmap to avoid bitmap memory errors, set it to the imageview.
        //This code scales it such that the width is 1280
        int nh = (int) ( bmp.getHeight() * (1280.0 / bmp.getWidth()) );
        Bitmap scaled = Bitmap.createScaledBitmap(bmp, 1280, nh, true);
        imageView.setImageBitmap(scaled);

        builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        builder.show();
    }


    /**
     * Request user permission to read contacts
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                )
        {
            requestPermissions(new String[]{Manifest.permission.INTERNET,
                                            Manifest.permission.READ_EXTERNAL_STORAGE},
                                PERMISSIONS_REQUEST);
        }
    }

    /**
     * Get the selected image path from the content uri
     * @param uri image
     * @return the image path and name
     */
    public String getImagePathFromUri(Uri uri) {
        String path = null;
        String[] projection = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if(cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            path = cursor.getString(column_index);
        }

        cursor.close();
        return path;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("IMAGE_PATH", imagePath);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            imagePath = savedInstanceState.getString("IMAGE_PATH");
        }
    }
}