package com.samhan.foodtogo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RatingActivity extends AppCompatActivity {

    RatingBar ratingBar ;
    TextView CurrRating ;
    String ratingValue ;
    Button submit ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        submit = findViewById(R.id.submitRating) ;
        ratingBar = findViewById(R.id.rating) ;
        CurrRating = findViewById(R.id.currRating) ;


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ratingValue = String.valueOf(ratingBar.getRating())  ;
                Float NewRating  = Float.parseFloat(ratingValue) ;
                // Have to change the Rating value of this user in realtime databse .
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                String userid = firebaseAuth.getCurrentUser().getUid() ;
                DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users").child(userid)   ;
                referenceProfile.child("ratingApp").setValue(NewRating).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext()," Rating has been updated ! ",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }) ;
                // for ever click //

                // Rating part with Firebase
                referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users") ;
                Query query = referenceProfile.orderByChild("ratingApp")  ;
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    Float[] totalRating1 = {0.00F};
                    Float[] totalUserGivenRating1 = {0.00F};
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            Float Rating = userSnapshot.child("ratingApp").getValue(Float.class);
                            if(Rating>=0.00F) {
                                totalRating1[0] += Rating ;
                                totalUserGivenRating1[0] += 1.0F ;
                            }
                        }
                        Float RATING = 0.0F ;
                        if(totalUserGivenRating1[0]>0.00F) {
                            RATING += totalRating1[0] / totalUserGivenRating1[0] ;
                        }
                        CurrRating.setText("Current Rating :  " + Float.max(RATING,0.00F) );
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(),"Something Wrong .\n Try again ! ",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Float[] totalRating = {0.00F};
        Float[] totalUserGivenRating = {0.00F};
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users") ;
        referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users") ;
        Query query = referenceProfile.orderByChild("ratingApp")  ;
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    Float Rating = userSnapshot.child("ratingApp").getValue(Float.class);
                    if(Rating>=0.00F) {
                        totalRating[0] += Rating ;
                        totalUserGivenRating[0] += 1.0F ;
                    }
                }
                Float RATING = 0.0F ;
                if(totalUserGivenRating[0]>0.00F) {
                    RATING += totalRating[0] / totalUserGivenRating[0] ;
                }
                CurrRating.setText("Current Rating :  " + RATING );
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Something Wrong .\n Try again ! ",Toast.LENGTH_SHORT).show();
            }
        });

    }
}