package com.mtdev.musicbox.application.fragments.MenuFragment;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mtdev.musicbox.AppConfig;
import com.mtdev.musicbox.Client.Utils.ImageLoader;
import com.mtdev.musicbox.R;
import com.mtdev.musicbox.application.entities.ProductType;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by PC on 22/01/2018.
 */

public class ProductTypesRecyclerAdapter extends RecyclerView.Adapter<ProductTypesRecyclerAdapter.MyViewHolder> {

    List<ProductType> albumList;
    Context ctx;
    ImageLoader imgLoader;

    public ProductTypesRecyclerAdapter(List<ProductType> albumList, Context ctx) {
        this.albumList = albumList;
        this.ctx = ctx;
        imgLoader = new ImageLoader(ctx);
    }

    @Override
    public ProductTypesRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card_layout3, parent, false);

        return new ProductTypesRecyclerAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProductTypesRecyclerAdapter.MyViewHolder holder, int position) {
        ProductType ab = albumList.get(position);
        holder.title.setText(ab.getName());
        holder.title.setTextColor(Color.parseColor("#DDDDDD"));
        holder.artist.setText("Menu  Of " + ab.getName() +"s." );
        holder.artist.setTextColor(Color.parseColor("#BBBBBB"));
        Picasso.with(ctx).load(AppConfig.URL_GETIMG_PREFIX+ab.getImgUrl()).into(holder.art);

        //imgLoader.DisplayImage(ab.getAlbumSongs().get(0).getPath(), holder.art);
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView art;
        TextView title, artist;
        RelativeLayout bottomHolder;

        public MyViewHolder(View view) {
            super(view);
            art = (ImageView) view.findViewById(R.id.backImage);
            title = (TextView) view.findViewById(R.id.card_title);
            artist = (TextView) view.findViewById(R.id.card_artist);
            bottomHolder = (RelativeLayout) view.findViewById(R.id.bottomHolder);
        }
    }

}
