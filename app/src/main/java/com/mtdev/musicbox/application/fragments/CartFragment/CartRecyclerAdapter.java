package com.mtdev.musicbox.application.fragments.CartFragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtdev.musicbox.R;
import com.mtdev.musicbox.application.entities.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by PC on 22/01/2018.
 */

public class CartRecyclerAdapter extends RecyclerView.Adapter<CartRecyclerAdapter.MyViewHolder> {
    private List<Product> localTracks;
    private Context ctx;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView art;
        TextView title,  price, quantiy;

        public MyViewHolder(View view) {
            super(view);
            art = (ImageView) view.findViewById(R.id.img_2);
            title = (TextView) view.findViewById(R.id.title_2);
            price = (TextView)view.findViewById(R.id.price);
            quantiy = (TextView) view.findViewById(R.id.quantity);
        }
    }

    public CartRecyclerAdapter(ArrayList<Product> localTracks, Context ctx) {
        this.localTracks = localTracks;
        this.ctx = ctx;
    }

    @Override
    public CartRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item, parent, false);

        return new CartRecyclerAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CartRecyclerAdapter.MyViewHolder holder, int position) {
        final Product track = localTracks.get(position);
        holder.title.setText(track.getName());
        holder.price.setText(track.getPrice()+" DT");
        holder.quantiy.setText(track.getQuantity()+"  ");
        Picasso.with(ctx).load(track.getImgUrl()).into(holder.art);

    }

    @Override
    public int getItemCount() {
        return localTracks.size();
    }

    public static Bitmap getAlbumArt(String path) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        Bitmap bitmap = null;

        byte[] data = mmr.getEmbeddedPicture();
        if (data != null) {
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            return bitmap;
        } else {
            return null;
        }
    }
}
