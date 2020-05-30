package com.sustbus.imageuploaddemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private Button chooser;
    private Button setter;
    private int REQUEST_CODE = 1001;
    private Uri uri;
    private String encodedImage;

    /**
     * stores the image in string form
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chooser = findViewById(R.id.choose);
        setter = findViewById(R.id.set_image);
        imageView = findViewById(R.id.imageView);
        chooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "select image"), REQUEST_CODE);
            }
        });
        setter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    byte[] bytes = Base64.decode(encodedImage.getBytes(), Base64.DEFAULT);
                    imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                } catch (IllegalArgumentException | NullPointerException ignored) {

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            new BackgroundImageUpload().execute(uri);
        }
    }

    public class BackgroundImageUpload extends AsyncTask<Uri, Integer, byte[]> {
        private static final String TAG = "BackgroundImageUpload";
        Bitmap bitmap;

        BackgroundImageUpload() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "onPreExecute: ");
        }

        @Override
        protected byte[] doInBackground(Uri... uris) {
            Log.d(TAG, "doInBackground: started");

            if (bitmap == null) {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uris[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            byte[] bytes = getBytesFromBitmap(bitmap, 20);
            return bytes;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            Log.d(TAG, "onPostExecute: ");
            /**
             * this encoded image is the string form of the image.
             * */
            encodedImage = Base64.encodeToString(bytes, Base64.NO_WRAP);

            Log.d(TAG, "onPostExecute: done " + encodedImage);
        }

        public byte[] getBytesFromBitmap(Bitmap bitmap, int quality) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
            return stream.toByteArray();
        }
    }
}