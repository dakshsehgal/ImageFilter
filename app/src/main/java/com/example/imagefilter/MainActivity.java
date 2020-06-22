package com.example.imagefilter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.request.RequestOptions;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import java.io.FileDescriptor;
import java.io.IOException;

import jp.wasabeef.glide.transformations.GrayscaleTransformation;
import jp.wasabeef.glide.transformations.gpu.ToonFilterTransformation;


public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private ImageView imageView;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        imageView = findViewById(R.id.imageview);


    }

    public void choose(View v) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                parcelFileDescriptor.close();
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public void apply(Transformation<Bitmap> transformation) {
        Glide.with(this)
                .load(bitmap)
                .apply(RequestOptions.bitmapTransform(transformation))
                .into(imageView);
    }

    public void applyToon(View v) {
        apply(new ToonFilterTransformation());

    }

    public void applyGrayscale(View v) {
        apply(new GrayscaleTransformation());

    }

    public void save(View v) {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap newBitmap = drawable.getBitmap();

        MediaStore.Images.Media.insertImage(getContentResolver(), newBitmap, "filtered_image", "An image with a filter");

    }

}