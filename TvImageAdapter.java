package com.example.lenovo.moviereviewapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.moviereviewapp.ModelClass.Result;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

public class TvImageAdapter extends RecyclerView.Adapter<TvImageAdapter.ViewHolder> {
    Context context;
    List<com.example.lenovo.moviereviewapp.TvModelClass.Result> results;
    TextView s;
    public TvImageAdapter(Context context, List<com.example.lenovo.moviereviewapp.TvModelClass.Result> results) {
         this.context = context;
        this.results = results;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.movie_images, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(results!=null) {
            Picasso.with(context).load("http://image.tmdb.org/t/p/w185" + results.get(position).getPosterPath())
                    .into(holder.image);
            // holder.tv.setText(results.get(position).getOriginalTitle());
        }

        else{
            Toast.makeText(context, "no  data" , Toast.LENGTH_SHORT).show();
        }

    }



    @Override
    public int getItemCount() {
        return results.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
        ImageView image;
        //TextView tv;
        public ViewHolder(View itemView) {
            super(itemView);
            image= itemView.findViewById(R.id.image_1);
            // tv=itemView.findViewById(R.id.movieTitle);
            itemView.setOnClickListener((View.OnClickListener) this);

        }

        @Override
        public void onClick(View v) {
            int img_position=getAdapterPosition();
            com.example.lenovo.moviereviewapp.TvModelClass.Result result=results.get(img_position);
            Intent intent=new Intent(context,TvReviewDescription.class);
            Bundle b=new Bundle();
            b.putSerializable(MovieReviewDescription.KEY,  result);
            intent.putExtras(b);
            context.startActivity(intent);
        }
    }
}

