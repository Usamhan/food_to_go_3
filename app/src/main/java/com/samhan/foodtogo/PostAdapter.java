package com.samhan.foodtogo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
    Context mContext;
    List<Post> mData;





    public PostAdapter(Context mContext, List<Post> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row= LayoutInflater.from(mContext).inflate(R.layout.row_post,parent,false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tvTitle.setText(mData.get(position).getTitle());
        Glide.with(mContext).load(mData.get(position).getPicture()).into(holder.imgPost);


        Post model=mData.get(position);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent postDetailActivity= new Intent(mContext, PostDetailsActivity.class);
                postDetailActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                int position= holder.getAdapterPosition();

                postDetailActivity.putExtra("title",mData.get(position).getTitle());
                postDetailActivity.putExtra("postImage",mData.get(position).getPicture());
                postDetailActivity.putExtra("description",mData.get(position).getDescription());
                postDetailActivity.putExtra("postKey",mData.get(position).getPostKey());

                postDetailActivity.putExtra("postLike",mData.get(position).getPostLike());

                long timestamp  = (long) mData.get(position).getTimeStamp();
                postDetailActivity.putExtra("postDate",timestamp) ;

                mContext.startActivity(postDetailActivity);
            }
        });



//        for like

//        holder.like.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseDatabase.getInstance().getReference()
//                        .child("Posts")
//                        .child(model.getPostKey())
//                        .child("Likes")
//                        .child(FirebaseAuth.getInstance().getUid())
//                        .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void unused) {
//                                FirebaseDatabase.getInstance().getReference()
//                                        .child("Posts")
//                                        .child(model.getPostKey())
//                                        .child("PostLike")
//                                        .setValue(model.getPostLike()+1).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                            @Override
//                                            public void onSuccess(Void unused) {
//                                                holder.like.setTextColor(Color.RED);
//                                            }
//                                        });
//
//                            }
//                        });
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        ImageView imgPost;




        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle=itemView.findViewById(R.id.row_post_title);
//            like=itemView.findViewById(R.id.btn_like);
//            likeCnt=itemView.findViewById(R.id.tv_likeCnt);


            imgPost=itemView.findViewById(R.id.row_post_img);
        }
    }
}
