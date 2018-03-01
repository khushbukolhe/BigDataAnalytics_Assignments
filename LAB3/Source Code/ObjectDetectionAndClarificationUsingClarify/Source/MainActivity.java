package com.example.khushbukolhe.predictions;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.provider.MediaStore;
import java.io.IOException;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

// Created by Khushbu Kolhe.

public class MainActivity extends AppCompatActivity {
    public static final int PICK_IMAGE_REQUEST = 1;

    ImageView imageView;
    TextView textView;
    Button btnGetGallery;
    Button btnPredict;
    Uri imageFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);
        textView = (TextView) findViewById(R.id.textView);

        btnGetGallery = (Button) findViewById(R.id.btnGetGallery);
        btnPredict = (Button) findViewById(R.id.btnPredict);

        btnGetGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, PICK_IMAGE_REQUEST);
            }
        });

        btnPredict.setVisibility(View.INVISIBLE);

        btnPredict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToIdentifyObjects();
            }
        });

    }

    private  void navigateToIdentifyObjects() {

        Intent intent = new Intent(this, IdentifyObjects.class);

        Bundle extras = new Bundle();
        extras.putString("imageURI",imageFileUri.toString());
        intent.putExtras(extras);

        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageFileUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageFileUri);
                ImageView myImageView = (ImageView) findViewById(R.id.imageView);
                myImageView.setImageBitmap(bitmap);
                btnPredict.setVisibility(View.VISIBLE);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
