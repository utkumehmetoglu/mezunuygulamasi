package com.utku.odev;
import android.graphics.BitmapFactory;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.utku.odev.entities.News;
import com.utku.odev.entities.Post;
import com.utku.odev.entities.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder>  {
    DatabaseReference usersRef = FirebaseDatabase.getInstance("https://utkuodev-c408d-default-rtdb.europe-west1.firebasedatabase.app").getReference("users");
    private List<Post> postList;

    @NonNull
    @Override
    public FeedAdapter.FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed, parent, false);
        return new FeedAdapter.FeedViewHolder(view);
    }
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private StorageReference storageReference = firebaseStorage.getReference();




    public void setPostList(List<Post> postList) {
        this.postList = postList;
        notifyDataSetChanged();
        }

    @Override
    public void onBindViewHolder(@NonNull FeedAdapter.FeedViewHolder holder, int position) {
        Post post = postList.get(position);

        holder.feedTextView.setText(post.getComment());


        usersRef.child(post.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {

                }
                else {
                    User result = task.getResult().getValue(User.class);
                    Date date = new Date(post.getCreated_Millis());
                    DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
                    String formatted = format.format(date);
                    System.out.println(formatted);
                    format.setTimeZone(TimeZone.getTimeZone("Australia/Sydney"));
                    formatted = format.format(date);
                    holder.feedTextView.setText(formatted + "\n" +result.getName() + " " + result.getLastname() + " : " + post.getComment());
                    StorageReference uploadRef = storageReference.child("posts").child(post.getUid()).child(post.getId() + ".jpg");
                    uploadRef.getBytes(1024 * 1024 * 512).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            // Data for "images/island.jpg" is returns, use this as needed
                            holder.feedImageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes,0,bytes.length));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });
                }
            }});




      /*  if(post.getUserID() != null)
            usersRef.child(news.getUserID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {

                    }
                    else {

                    }
                }});*/


    }
    @Override
    public int getItemCount() {
        return postList != null ? postList.size() : 0;
    }

    static class FeedViewHolder extends RecyclerView.ViewHolder {
        TextView feedTextView;
        ImageView feedImageView;
        FeedViewHolder(View itemView) {
            super(itemView);
            feedTextView = itemView.findViewById(R.id.post_textview);
            feedImageView = itemView.findViewById(R.id.post_imageview);
        }
    }


}
