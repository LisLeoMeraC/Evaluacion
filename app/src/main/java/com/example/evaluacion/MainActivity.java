package com.example.evaluacion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button btnCapturarImagen, btnDetectarTexto;
    private ImageView imageView;
    private TextView textView;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Bitmap imageBitmap;
    GoogleMap mapa;
    private static final String url="http://www.geognos.com/api/en/countries/info/all.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCapturarImagen=findViewById(R.id.captura_Imagen);
        btnDetectarTexto=findViewById(R.id.detectar_Texto);
        imageView=findViewById(R.id.image_view);
        textView=findViewById(R.id.text_display);

        btnCapturarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dispatchTakePictureIntent();
                textView.setText("");
            }
        });
        btnDetectarTexto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                detectarTextodeImagen();

            }
        });
    }

    public void onMapReady(GoogleMap googleMap) {
        mapa=googleMap;
        mapa.getUiSettings().setZoomControlsEnabled(true);

        // mapa.setMapType(GoogleMap.MAP_TYPE_HYBRID);

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
       if(takePictureIntent.resolveActivity(getPackageManager())!=null)
       {
           startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
       }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }

    private void detectarTextodeImagen()
    {
        FirebaseVisionImage firebaseVisionImage= FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextDetector firebaseVisionTextDetector= FirebaseVision.getInstance().getVisionTextDetector();
        firebaseVisionTextDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                displayTextFromImage(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(MainActivity.this,"Error: "+ e.getMessage(), Toast.LENGTH_SHORT);

                Log.d("Error: ",e.getMessage());
            }
        });
    }

    private void displayTextFromImage(FirebaseVisionText firebaseVisionText)
    {
        List<FirebaseVisionText.Block>blockList=firebaseVisionText.getBlocks();
        if(blockList.size()==0){
            Toast.makeText(this,"no text Found in image",Toast.LENGTH_SHORT);
        }
        else{
            for(FirebaseVisionText.Block block: firebaseVisionText.getBlocks()){
                String text=block.getText();
                textView.setText(text);
            }
        }

    }
}