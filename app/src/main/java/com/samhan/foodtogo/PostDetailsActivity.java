package com.samhan.foodtogo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PostDetailsActivity extends AppCompatActivity {

    ImageView imgPost,imgUserPost,like;
    TextView txtPostDesc,txtPostDateName , txtPostTitle;
    EditText editTextComment;
    Button btnAddComment;

    String postKey;

    RecyclerView RvComment;
    CommentAdapter commentAdapter;
    List<Comment> listComment;

    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    FirebaseDatabase firebaseDatabase;


    TextView likeCnt;

    Boolean testClick;
    List<Post> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        imgPost=findViewById(R.id.post_detail_img);
        imgUserPost=findViewById(R.id.post_detail_user_img);

        RvComment=findViewById(R.id.rv_comment);

        txtPostTitle=findViewById(R.id.post_detail_title);
        txtPostDesc=findViewById(R.id.post_detail_desc);
        txtPostDateName=findViewById(R.id.post_detail_date_name);

        editTextComment=findViewById(R.id.post_detail_comment);
        btnAddComment=findViewById(R.id.post_detail_add_comment_btn);

        like=findViewById(R.id.btn_like);
        likeCnt=findViewById(R.id.tv_likeCnt);

        firebaseAuth=FirebaseAuth.getInstance();
        currentUser=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();

        postKey=getIntent().getExtras().getString("postKey");
        String userid = firebaseAuth.getCurrentUser().getUid() ;

        GetLikeCount(postKey,userid);
        AlreadyLiked(postKey,userid);

        Post model=new Post();

        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference("LIKE");
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testClick = true;

                likeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(testClick == true){
                            if(snapshot.child(postKey).hasChild(userid)){
                                likeRef.child(postKey).child(userid).removeValue();
                                testClick = false;
                            }
                            else{
                                likeRef.child(postKey).child(userid).setValue(true);
                                testClick = false;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });



        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference commentReference=firebaseDatabase.getReference("Comment").child(postKey).push();
                String comment_content=editTextComment.getText().toString();
                String uid=currentUser.getUid();
                String uname= currentUser.getDisplayName();


                Comment comment=new Comment(comment_content,uid,uname);

                commentReference.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(PostDetailsActivity.this," comment added", Toast.LENGTH_SHORT).show();
                        editTextComment.setText("");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PostDetailsActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });


        String postImage=getIntent().getExtras().getString("postImage");
        Glide.with(this).load(postImage).into(imgPost);

        String postTitle=getIntent().getExtras().getString("title");
        txtPostTitle.setText(postTitle);

        String userImage=getIntent().getExtras().getString("userPhoto");
        Glide.with(this).load(userImage).into(imgUserPost);

        String postDescription=getIntent().getExtras().getString("description");
        txtPostDesc.setText(postDescription);






        String date=timestampToString(getIntent().getExtras().getLong("postDate"));
        txtPostDateName.setText(date);

        intRvComment();
    }

    private void intRvComment() {

        RvComment.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference commentRef=firebaseDatabase.getReference("Comment").child(postKey);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listComment=new ArrayList<>();

                for(DataSnapshot snap: dataSnapshot.getChildren()){
                    Comment comment=snap.getValue(Comment.class);
                    listComment.add(comment);
                }

                Collections.reverse(listComment);

                commentAdapter=new CommentAdapter(getApplicationContext(),listComment);
                RvComment.setAdapter(commentAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    private String timestampToString(long time) {
        Calendar calendar=Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date= DateFormat.format("dd-MM-yyyy",calendar).toString();
        return date;
    }

    private void AlreadyLiked(final String postkey, final String userid){
        DatabaseReference databaseReference = firebaseDatabase.getReference("LIKE");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(postkey).hasChild(userid)){
                    int likecnt = (int)snapshot.child(postkey).getChildrenCount();
                    likeCnt.setText(String.valueOf(likecnt));
                    like.setImageResource(R.drawable.afterlike);

                }
                else{
                    int cnt = (int)snapshot.child(postkey).getChildrenCount();
                    likeCnt.setText(String.valueOf(cnt));
                    like.setImageResource(R.drawable.beforelike);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private  void GetLikeCount(final String postkey, final String userid) {

        DatabaseReference databaseReference = firebaseDatabase.getReference("LIKE");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer cnt = 0 ;
                for (DataSnapshot postsnap: dataSnapshot.getChildren()) {
                    Like l1 = postsnap.getValue(Like.class);
                    //Toast.makeText(PostDetails_Page.this,postID,Toast.LENGTH_SHORT).show();
                    if(Objects.equals(postKey,l1.getPostID()) ) {
                        cnt ++ ;
                        Toast.makeText(PostDetailsActivity.this," " + " hahaha " + " " ,Toast.LENGTH_SHORT).show() ;
                    }
                }
                likeCnt.setText(Integer.toString(cnt));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}