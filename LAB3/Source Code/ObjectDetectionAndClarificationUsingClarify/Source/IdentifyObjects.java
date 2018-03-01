package com.example.khushbukolhe.predictions;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import  android.graphics.Color;
import android.graphics.PorterDuff;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//Created By Khushbu Kolhe

public class IdentifyObjects extends AppCompatActivity {

    String base64EncodedImageToProcess;
    TableLayout tblLayout;
    ListView listView;
    ImageView imageView;
    Uri imageUri;
    Button btnSearch;
    String searchString;
    JSONArray itemsArray;
    CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify_objects);

        listView = (ListView) findViewById(R.id.listView);
        itemsArray = new JSONArray();
        imageView = (ImageView) findViewById(R.id.imageView);
        TextView textView = (TextView) findViewById(R.id.textView2);
        textView.setClickable(false);

        customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);

        Bundle extras = getIntent().getExtras();
        String imageURIString = extras.getString("imageURI");
        imageUri = Uri.parse(imageURIString);


        try {

            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            imageView.setImageBitmap(bitmap);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] byteArrayImage = baos.toByteArray();
            base64EncodedImageToProcess = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        if (savedInstanceState == null) {
            String baseUrl = "https://api.clarifai.com/v2/models/aaa03c23b3724a16a56b629203edc62c/outputs";
            OkHttpClient client = new OkHttpClient();
            try {
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                JSONObject jsonRequestBody = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                JSONObject jsonData = new JSONObject();
                JSONObject jsonImage = new JSONObject();
                JSONObject jsonImageURL = new JSONObject();

                jsonImageURL.put("base64", base64EncodedImageToProcess);
                jsonImage.put("image",jsonImageURL);
                jsonData.put("data",jsonImage);
                jsonArray.put(jsonData);
                jsonRequestBody.put("inputs",jsonArray);

                RequestBody requestBody = RequestBody.create(JSON, jsonRequestBody.toString());
                Request request = new Request.Builder()
                        .url(baseUrl)
                        .addHeader("Content-Type","application/json")
                        .addHeader("Authorization", "Key e4e46f1bf8e144adbe9ef204407059e9")
                        .post(requestBody)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        System.out.println(e.getMessage());
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final JSONObject jsonResult;
                        final String result = response.body().string();
                        try {
                            jsonResult = new JSONObject(result);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    processAndDisplayResults(jsonResult);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception ex) {
            }
        }


        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToWalmartSearch();
            }
        });
        btnSearch.setEnabled(false);
        btnSearch.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_OVER);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                searchString = ((TextView) view.findViewById(R.id.textViewType)).getText().toString();
                btnSearch.setEnabled(true);
                btnSearch.getBackground().setColorFilter(null);
            }
        });
    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return itemsArray.length();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.prediction_result_layout, null);

            TextView txtViewType = (TextView) view.findViewById(R.id.textViewType);
            TextView txtViewProbability = (TextView) view.findViewById(R.id.textViewProbability);

            try {
                txtViewType.setText(itemsArray.getJSONObject(i).getString("name"));
                txtViewProbability.setText(itemsArray.getJSONObject(i).getString("value"));
            } catch (JSONException ex) {

            }
            return view;
        }
    }

    //private  void navigateToWalmartSearch() {
       // Intent intent = new Intent(this, WalmartSearch.class);

       // Bundle extras = new Bundle();
      //  extras.putString("searchTerms",searchString);
     //   intent.putExtras(extras);

     //   startActivity(intent);
    //}

    private void processAndDisplayResults(JSONObject jsonResult) {
        try {
            JSONArray arr = jsonResult.getJSONArray("outputs");
            JSONObject firstObj = arr.getJSONObject(0);
            JSONObject objData = firstObj.getJSONObject("data");
            itemsArray = objData.getJSONArray("concepts");
            customAdapter.notifyDataSetChanged();
            searchString = itemsArray.getJSONObject(0).getString("name");

        } catch (JSONException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
