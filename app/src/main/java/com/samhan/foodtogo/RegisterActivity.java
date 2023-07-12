package com.samhan.foodtogo;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    EditText edtTxt_signup_mail,edtTxt_signup_pass,edtTxt_confirmPass,edtTxt_signup_username,edtTxt_datepicker;
    Button btn_SignUp;

    Spinner spinner_district;

    TextView txt_GotoLogInPage,txt_district;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        spinner_district=findViewById(R.id.spinner);
        txt_district=findViewById(R.id.txt_district);

        edtTxt_signup_mail=findViewById(R.id.edtTxt_signup_mail);
        edtTxt_signup_pass=findViewById(R.id.edtTxt_signup_pass);
        edtTxt_signup_username=findViewById(R.id.edtTxt_username);
        edtTxt_confirmPass=findViewById(R.id.edtTxt_confirmPass);
        edtTxt_datepicker=findViewById(R.id.edtTxt_datepicker);
        txt_GotoLogInPage=findViewById(R.id.txt_GotoLogInPage);

        btn_SignUp=findViewById(R.id.btn_SignUp);


        firebaseAuth=FirebaseAuth.getInstance();




        btn_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail=edtTxt_signup_mail.getText().toString();
                String password=edtTxt_signup_pass.getText().toString();
                String confirmPass=edtTxt_confirmPass.getText().toString();
                String username=edtTxt_signup_username.getText().toString();

                if(!mail.equals("") && !password.equals("") && !username.equals("") && password.equals(confirmPass))
                {
                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
                    Query usernameQuery = usersRef.orderByChild("username").equalTo(username);

                    usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                // Username already exists, display an error message
                                Toast.makeText(RegisterActivity.this, "username already taken", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                firebaseAuth.createUserWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            FirebaseUser user=firebaseAuth.getCurrentUser();
                                            if(user!=null){
                                                user.sendEmailVerification();
                                                if(user.sendEmailVerification().isSuccessful()){
                                                    Toast.makeText(RegisterActivity.this, "Successfully added", Toast.LENGTH_SHORT).show();
                                                    Intent intent=new Intent(getApplicationContext(),LogInActivity.class);
                                                    startActivity(intent);
                                                }
                                            }
                                        }
                                    }
                                });
//                                firebaseAuth.createUserWithEmailAndPassword(mail, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//                                    @Override
//                                    public void onSuccess(AuthResult authResult) {
//                                        // Toast.makeText(RegisterActivity.this, "Successfully added", Toast.LENGTH_SHORT).show();
//                                        Toast.makeText(RegisterActivity.this, "Successfully added", Toast.LENGTH_SHORT).show();
//                                    }
//                                }).addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                                    }
//                                });

                                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
                                String userId = firebaseAuth.getCurrentUser().getUid();
                                databaseRef.child("users").child(userId).child("username").setValue(username);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

                else
                {
                    Toast.makeText(RegisterActivity.this, "fill up all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        txt_GotoLogInPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in=new Intent(getApplicationContext(),LogInActivity.class);
                startActivity(in);
            }
        });

        // Example data source as an array of strings
        String[] data = {"Dhaka", "Chittagong", "Sylhet"};

//for the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_district.setAdapter(adapter);


        spinner_district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //datepicker

        edtTxt_datepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance() ;
                int day = calendar.get(Calendar.DAY_OF_MONTH) ;
                int month = calendar.get(Calendar.MONTH) ;
                int year = calendar.get(Calendar.YEAR) ;
                //Date Picker Dialog
                DatePickerDialog picker = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        edtTxt_datepicker.setText(dayOfMonth + "/" +(month+1) + "/" +year);
                    }
                }, year,month,day);
                picker.show();
            }
        });


    }
}