package com.samhan.foodtogo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button btn_toBlog,btn_toDetection ,btn_toVideo,btn_toMap,btn_toRating,btn_toSass;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_toBlog=findViewById(R.id.btn_toBlog);
        btn_toDetection=findViewById(R.id.btn_toDetection);
        btn_toVideo=findViewById(R.id.btn_toVideo);
        btn_toMap=findViewById(R.id.btn_toMap);
        btn_toRating=findViewById(R.id.btn_toRating);
        btn_toSass=findViewById(R.id.btn_toSass);

        btn_toDetection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), MachineLearningActivity.class);
                startActivity(intent);

                Toast.makeText(MainActivity.this, "works ", Toast.LENGTH_SHORT).show();
            }
        });

        btn_toBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), BlogActivity.class);
                startActivity(intent);


            }
        });

        btn_toVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), VideoActivity.class);
                startActivity(intent);
            }
        });

        btn_toMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
            }
        });

        btn_toRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), RatingActivity.class);
                startActivity(intent);
            }
        });

        btn_toSass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), Gsap_SassActivity.class);
                startActivity(intent);
            }
        });
    }
}