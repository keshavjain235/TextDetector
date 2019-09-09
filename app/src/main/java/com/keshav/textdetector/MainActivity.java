package com.keshav.textdetector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageView image;
    Button camera, detect;

    Bitmap image_rec;

    FirebaseVisionTextRecognizer detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init of widgets
        image = findViewById(R.id.image);
        camera = findViewById(R.id.camera);
        detect = findViewById(R.id.detect);
        detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(i,100);

            }
        });

        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    detectText(image_rec);
                }catch (Exception e) {
                    Toast.makeText(MainActivity.this, "No Image Clicked", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }//oncreate

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 100) {
                Bundle extra = data.getExtras();

                image_rec = (Bitmap) extra.get("data");

                image.setImageBitmap(image_rec);
            }
        }catch (Exception e){
            Toast.makeText(this, "No Image Clicked", Toast.LENGTH_SHORT).show();
        }
    }//onActivityResult

    public void detectText(Bitmap b) {

        //firebase image format
        FirebaseVisionImage f_image = FirebaseVisionImage.fromBitmap(b);

        detector.processImage(f_image).addOnCompleteListener(new OnCompleteListener<FirebaseVisionText>() {
            @Override
            public void onComplete(@NonNull Task<FirebaseVisionText> task) {

                List<FirebaseVisionText.TextBlock> blocks = task.getResult().getTextBlocks();

                String d = "";

                for (int i = 0; i < blocks.size(); i++) {
                    List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();

                    for (int j = 0; j < lines.size(); j++) {

                        String k = lines.get(j).getText();

                        d += k + "\n";   //d = d + k+ "\n"

                    }
                    d += "\n";   //d = d + "\n"
                }
                alertDialog("" + d);
            }
        });
    }

    public void alertDialog(String d) {
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setMessage(""+d);
        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }

}//MainActivity
