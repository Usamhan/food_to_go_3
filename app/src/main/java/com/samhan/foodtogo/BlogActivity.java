package com.samhan.foodtogo;

import static com.google.android.material.internal.ContextUtils.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlogActivity extends AppCompatActivity {

    FloatingActionButton fab;
    ImageView popupPostImage,popupAddBtn;
    TextView popupTitle,popupDescription;

    RecyclerView postRecyclerView;
    PostAdapter postAdapter;

    List<Post>postList;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private Uri pickedImgUri = null;

    private static final int PReqCode = 2 ;
    private static final int REQUESCODE = 2 ;

    //for pagination
    Integer pageNumber=1;
    Integer postStartNum=1,postEndNum=3;
    Button btnPreviousPage,btnNextPage;
    TextView tv_pageNo;
    Post post;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);

        //for pagination
        btnPreviousPage=findViewById(R.id.previousButton);
        btnNextPage=findViewById(R.id.nextButton);
        tv_pageNo=findViewById(R.id.pageNo);
        pageNumber = Integer.parseInt(tv_pageNo.getText().toString()) ;

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        postRecyclerView=findViewById(R.id.postRecyclerView);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        makeList();

        btnPreviousPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pageNumber==1) {
                    Toast.makeText(getApplicationContext(),"No previous Page Found !",Toast.LENGTH_SHORT).show();
                }
                else {
                    pageNumber-- ;
                    String s = Integer.toString(pageNumber) ;
                    tv_pageNo.setText(s);
                    postEndNum = pageNumber*5 ;
                    postStartNum = pageNumber - 4 ;
                    makeList();
                }
            }
        });

        btnNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageNumber ++ ;
                String s = Integer.toString(pageNumber) ;
                postEndNum = pageNumber*5 ;
                postStartNum = postEndNum - 4 ;
                tv_pageNo.setText(s);
                makeList() ;
            }
        });
        

//        databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                postList =new ArrayList<>();
//
//                for(DataSnapshot postsnap: snapshot.getChildren()){
//
//                    Post post=postsnap.getValue(Post.class);
////                    post.setPostKey(snapshot.getKey());
//                    postList.add(post);
//                }
//
//                Collections.reverse(postList);
//
//                postAdapter=new PostAdapter(getApplicationContext(),postList);
//                postRecyclerView.setAdapter(postAdapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        fab=findViewById(R.id.btn_addPost);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iniPopup();
                setupPopupImageClick();
            }
        });



    }

    private void makeList() {

        postRecyclerView.setHasFixedSize(true);

        databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList =new ArrayList<>();
                ArrayList<Post>  initial = new ArrayList<Post>();
                for(DataSnapshot postsnap: snapshot.getChildren()){

                    Post post=postsnap.getValue(Post.class);
//                    post.setPostKey(snapshot.getKey());
                    initial.add(post);
                }

                Collections.reverse(initial);

                int cnt = 0 ;
                for (Post postsnap: initial) {
                    Post post = postsnap ;
                    if(cnt>=postStartNum-1 && cnt<postEndNum) {
                        postList.add(post) ;
                    }
                    cnt ++ ;
                }

                postAdapter=new PostAdapter(getApplicationContext(),postList);
                postRecyclerView.setAdapter(postAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void setupPopupImageClick() {


        popupPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // here when image clicked we need to open the gallery
                // before we open the gallery we need to check if our app have the access to user files
                // we did this before in register activity I'm just going to copy the code to save time ...

                checkAndRequestForPermission();


            }
        });

    }

    private void checkAndRequestForPermission() {


        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(getApplicationContext(),"Please accept for required permission",Toast.LENGTH_SHORT).show();

            }

            else
            {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }

        }
        else
            // everything goes well : we have permission to access user gallery
            openGallery();

    }

    private void openGallery() {
        //TODO: open gallery intent and wait for user to pick an image !

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null ) {

            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pickedImgUri = data.getData() ;
            popupPostImage.setImageURI(pickedImgUri);

        }


    }


    private void iniPopup() {
        Dialog popAddPost = new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        popAddPost.setContentView(R.layout.popup_add_post);
        popAddPost.show();

        popupPostImage=popAddPost.findViewById(R.id.popup_img);
        popupTitle=popAddPost.findViewById(R.id.popup_title);
        popupDescription=popAddPost.findViewById(R.id.popup_description);
        popupAddBtn=popAddPost.findViewById(R.id.popup_btn_addPost);

        popupAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!popupTitle.getText().toString().isEmpty()
                        && !popupDescription.getText().toString().isEmpty()
                        && pickedImgUri != null )
                {

                    StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("blog_images");
                    final StorageReference imageFilePath=mStorage.child(pickedImgUri.getLastPathSegment());
                    imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageDownloadLink=uri.toString();
                                    Post post=new Post(popupTitle.getText().toString(),
                                            popupDescription.getText().toString(),
                                            imageDownloadLink,
                                            currentUser.getUid()
                                            );

                                    addPost(post);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "failed to upload the picture", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
                else {
                    Toast.makeText(BlogActivity.this, "fill up all the fields", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void addPost(Post post) {

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference myRef= database.getReference("Posts").push();

        String key= myRef.getKey();
        post.setPostKey(key);

        myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Post added", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}