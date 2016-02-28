package com.jaghory.nutricam;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.wolfram.alpha.WAEngine;
import com.wolfram.alpha.WAException;
import com.wolfram.alpha.WAPlainText;
import com.wolfram.alpha.WAPod;
import com.wolfram.alpha.WAQuery;
import com.wolfram.alpha.WAQueryResult;
import com.wolfram.alpha.WASubpod;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import com.clarifai.api.ClarifaiClient;
import com.clarifai.api.RecognitionRequest;
import com.clarifai.api.RecognitionResult;
import com.clarifai.api.Tag;
import com.clarifai.api.exception.ClarifaiException;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    public ArrayList<String> satan = new ArrayList<>();

    List<RecognitionResult> results = null;

    private File photoFilereally = null;

    private final ClarifaiClient clarifai = new ClarifaiClient(Credentials.CLIENT_ID,
            Credentials.CLIENT_SECRET);

    static final int REQUEST_TAKE_PHOTO = 1;
    String photoPath = "";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Firebase sfireref = new Firebase("https://nutricam.firebaseio.com/users/");

        setContentView(R.layout.activity_main);

        Button picture_btn = (Button) findViewById(R.id.takePicture_btn);
        Button history_btn = (Button) findViewById(R.id.viewHistory_btn);
        ImageButton like_btn = (ImageButton) findViewById(R.id.likeButton);
        ImageButton dislike_btn = (ImageButton) findViewById(R.id.dislikeButton);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        picture_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                satan.clear();
                // Take a picture and save it to the gallery
                takePictureIntent();
            }
        });

        history_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), FoodHistoryActivity.class));
            }
        });

        like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                for (int i = 0; i < satan.size(); i++) {
                    sfireref.child(sfireref.getAuth().getUid()).child(satan.get(i)).child("like").setValue(true);

                }
            }
        });

        dislike_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                for (int i = 0; i < satan.size(); i++) {
                    sfireref.child(sfireref.getAuth().getUid()).child(satan.get(i)).child("like").setValue(false);

                }
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    /**
     * Create an image file
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        photoPath = "file:" + image.getAbsolutePath();

        return image;
    }

    /**
     * Take picture intent
     */
    private void takePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;

            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {

                this.photoFilereally = photoFile;

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    /**
     * Add picture to gallery
     */
    private void galleryAddPic() {

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(photoPath);
        Uri contentUri = Uri.fromFile(f);


        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.jaghory.nutricam/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.jaghory.nutricam/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    private String tags_to_string()throws IOException{
        Firebase brimstone = new Firebase("https://nutricam.firebaseio.com/users/");
        String out = "";
        String keyword = "";

        List<String> food = Arrays.asList("apple", "orange", "coffee", "chips", "banana", "cookie", "pretzel",
                 "egg", "donut", "bagel", "pizza", "granola", "chocolate", "water", "lettuce", "tomato", "cheese", "cake",
                "olive oil");

        try {
            int found =0;
            for (Tag tag : results.get(0).getTags()) {

                String tg = tag.getName().toLowerCase();
                System.out.println(tg);
                for(int i = 0; i < food.size(); i++){
                    if(food.get(i).equals(tg)){
                        found=1;
                        keyword = food.get(i);
                        brimstone.child(brimstone.getAuth().getUid()).child(keyword).child("date").setValue(new Date().toString());
                        out += keyword.toUpperCase() + "\n";
                        satan.add(keyword);

                    }
                }

            }
            if(found ==0)
            {
                out = "Did not find any food in the picture :[";
                return out;
            }
        }catch (Exception e){

            out = "Error";
        }


        WAQueryResult queryResult = null;
        WAEngine engine = new WAEngine();

        // These properties will be set in all the WAQuery objects created from this WAEngine.
        engine.setAppID("AJ5QRY-Y8GWWL4HGH");
        engine.addFormat("plaintext");

        // Create the query.
        WAQuery query = engine.createQuery();

        // Set properties of the query.
        query.setInput(keyword + " food");

        // This sends the URL to the Wolfram|Alpha server, gets the XML result
        // and parses it into an object hierarchy held by the WAQueryResult object.
        try {
            queryResult = engine.performQuery(query);
            queryResult.getXML();
        }
        catch (WAException e) {
            e.printStackTrace();
        }

        String xmlString = queryResult.getXML().toString();
        char[] xml = queryResult.getXML().toString().toCharArray();

        //getting info

        String calories="", protein="", carbohydrates="", sugar="", calcium="", sodium="", cholesterol = "";
        int index = xmlString.indexOf("total calories ");
            for( int i = index+16; i<xmlString.length();i++)
            {
                if(xml[i] != ' ')
                {
                    calories+=xml[i];
                }
                else{ break; }
            }
        index = xmlString.indexOf("cholesterol ");
        for( int i = index+13; i<xmlString.length();i++)
        {
            if(xml[i] != ' ')
            {
                cholesterol+=xml[i];
            }
            else{ break; }
        }
        index = xmlString.indexOf("protein ");
        for( int i = index+9; i<xmlString.length();i++)
        {
            if(xml[i] != ' ')
            {
                protein+=xml[i];
            }
            else{ break; }
        }
        index = xmlString.indexOf("total carbohydrates ");
        for( int i = index+21; i<xmlString.length();i++)
        {
            if(xml[i] != ' ')
            {
                carbohydrates+=xml[i];
            }
            else{ break; }
        }
        index = xmlString.indexOf("sugar ");
        for( int i = index+7; i<xmlString.length();i++)
        {
            if(xml[i] != ' ')
            {
                sugar+=xml[i];
            }
            else{ break; }
        }
        String grams="";
        int index2 = xmlString.indexOf("(");
        index = xmlString.indexOf("calcium ");
        for( int i = index2+1; i<xmlString.length();i++)
        {
            if(xml[i] != ' ')
            {
                grams+=xml[i];
            }
            else{ break; }
        }
        for( int i = index+9; i<xmlString.length();i++)
        {
            if(xml[i] != ' ')
            {
                calcium+=xml[i];
            }
            else{ break; }
        }


        try {
            calcium = calcium.substring(0, calcium.length() - 1);
            double calciumDouble = Double.parseDouble(calcium) / 100;
            calciumDouble = (calciumDouble * Double.parseDouble(grams));
            calcium = Double.toString(Math.round(calciumDouble));
        }
        catch(Exception e){
            calcium = "0";
        }



        index = xmlString.indexOf("sodium ");
        for( int i = index+8; i<xmlString.length();i++)
        {
            if(xml[i] != ' ')
            {
                sodium+=xml[i];
            }
            else{ break; }
        }
    out+= "\nCalories: "+ calories + "\nCholestrol: " + cholesterol + "mg\nProtein: " + protein;
        out+="mg\nCarbs: "+ carbohydrates + "g\nSugar: "+sugar + "g\nCalcium: "+calcium + "mg\nSodium: "+ sodium + "ug";


        double calcalories = Double.parseDouble(calories);
        if(calcalories <= 150)
        {
            out+="HEALTHY";
        }else{out+="UNHEALTHY";}


/*
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(e"
// Request parameters and other properties.
        List<String> params = new ArrayList<String>();

        params.add('[' + calories+','+ protein+','+ carbohydrates+
                ',' +sugar+',' + calcium+','+ sodium+','+ cholesterol+ ']');

        httpPostRequest.setHeader("Authorization:Bearer", "zu4L/sbq+yYXkTP+tJm608UKB2dF20KsxuHqUMvojBYBQNfkOEnMkOnRKZiDyKs6ll8d/W1COuObVcFk11islg=="); //API Key Here
        httpPostRequest.setHeader("Content-Type:application", "json"); //required forsubmitting in json format
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

//Execute and get the response.
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            InputStream instream = entity.getContent();
            try {
                //Stuff
            } finally {
                instream.close();
            }
        }
*/
/*
        System.out.println("AZURE");
        URL url2 = new URL("\"https://ussouthcentral.services.azureml.net/workspaces/3eb1c7b1c750459d9b0cf36dee153f2f/services/3646018e338d4b16aa8688a3b38687e0/execute?api-version=2.0&details=true");
        HttpsURLConnection urC = (HttpsURLConnection) url2.openConnection();
        urC.setRequestProperty("Authorization","Bearer \"zu4L/sbq+yYXkTP+tJm608UKB2dF20KsxuHqUMvojBYBQNfkOEnMkOnRKZiDyKs6ll8d/W1COuObVcFk11islg==");
        urC.setRequestProperty("Content-Type","application/json");
        urC.setRequestProperty("Accept","application/json");


        String inputstuff = "{ Inputs: { input1: { ColumnNames: [ healthy, Energ_Kcal, Protein, Carbohydrt, Sugar_Tot, Calcium, " +
                "Sodium, Cholestrl ], Values: [ [" + calories+','+ protein+','+ carbohydrates+ ',' +sugar+',' + calcium+','+ sodium+','
                + cholesterol+ "], [ '0', '0', '0', '0', '0', '0', '0', '0' ] ] } }, GlobalParameters: {} }";
        InputStream in = new BufferedInputStream( urC.getInputStream());

        try{
          Object data = urC.getContent();
            System.out.println("DataTYpe "+ data.getClass());
        }
        catch(Exception e){
            System.out.println("ERROR is called --'"+ e.getMessage());
        }
*/
        return out;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_TAKE_PHOTO) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                galleryAddPic();

                System.out.println(data);

                try {
                    File photoFile = this.photoFilereally;
                    //System.out.println(photoFile.toString());
                    results = clarifai.recognize(new RecognitionRequest(photoFile));
                    TextView t = (TextView) findViewById(R.id.textView);
                    t.setText(tags_to_string());

                    System.out.println("Clarifai error");

                }catch (ClarifaiException e)
                {
                    System.out.println("Clarifai error");
                }catch(IOException a)
                {
                    System.out.println("Error" + a.getMessage());
                }

                // Do something with the contact here (bigger example below)
            }
        }
    }

}
