package com.mtdev.musicbox.application.fragments.MenuFragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtdev.musicbox.AppConfig;
import com.mtdev.musicbox.R;
import com.mtdev.musicbox.application.activities.MainActivity;
import com.mtdev.musicbox.application.entities.Product;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by PC on 22/01/2018.
 */

public class ProductRecyclerAdapter extends RecyclerView.Adapter<ProductRecyclerAdapter.MyViewHolder> {

    private List<Product> productList;
    private Context ctx;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView art;
        TextView title, artist , price;
        Button openDiaglog;

        public MyViewHolder(View view) {
            super(view);
            art = (ImageView) view.findViewById(R.id.img_2);
            title = (TextView) view.findViewById(R.id.title_2);
            artist = (TextView) view.findViewById(R.id.url_2);
            price = (TextView)view.findViewById(R.id.price);
            openDiaglog = (Button) view.findViewById(R.id.openDialogAdd);
        }
    }

    public ProductRecyclerAdapter(List<Product> productList, Context ctx) {
        this.productList = productList;
        this.ctx = ctx;
    }

    @Override
    public ProductRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_2, parent, false);

        return new ProductRecyclerAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProductRecyclerAdapter.MyViewHolder holder, int position) {
        final Product track = productList.get(position);
        holder.title.setText(track.getName());
        final String pName = track.getName().toString();
        holder.artist.setText(track.getDetails());
        holder.price.setText(track.getPrice()+" DT");
        Picasso.with(ctx).load(AppConfig.URL_GETIMG_PREFIX+track.getImgUrl()).into(holder.art);
        holder.openDiaglog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(ctx);
                dialog.setContentView(R.layout.add_to_cart_dialog);
                dialog.setTitle("Add to cart...");
                TextView prodName = (TextView) dialog.findViewById(R.id.pName);
                prodName.setText(pName);
                TextView price = (TextView) dialog.findViewById(R.id.pprix);
                price.setText(track.getPrice()+ " DT");
                final TextView totalprice = (TextView) dialog.findViewById(R.id.prixTotal);
                final EditText quantity = (EditText) dialog.findViewById(R.id.quantity);
                Button quantityplus = (Button) dialog.findViewById(R.id.quantityPlus);
                quantityplus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.q = Integer.parseInt(quantity.getText().toString());
                        MainActivity.q=MainActivity.q+1;
                        quantity.setText(""+MainActivity.q);
                        MainActivity.totalp=track.getPrice()*MainActivity.q;
                        totalprice.setText(""+MainActivity.totalp+" DT");
                    }
                });
                Button quantityminus = (Button) dialog.findViewById(R.id.quantityMinus);
                quantityminus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.q = Integer.parseInt(quantity.getText().toString());
                        if(MainActivity.q!=0){
                            MainActivity.q=MainActivity.q-1;
                            quantity.setText(MainActivity.q+" ");
                            MainActivity.totalp=track.getPrice()*MainActivity.q;
                            totalprice.setText(MainActivity.totalp + " DT");
                        }else {
                            MainActivity.q=0;
                            quantity.setText(""+MainActivity.q);
                            MainActivity.totalp=0;
                            totalprice.setText(MainActivity.totalp+ " DT");
                        }

                    }
                });
                final ImageView image = (ImageView) dialog.findViewById(R.id.imageProduct);
                Picasso.with(ctx).load(track.getImgUrl()).into(image);
                Button dialogBtn = (Button) dialog.findViewById(R.id.add_to_cart_button);
                Button quitBtn = (Button) dialog.findViewById(R.id.quit);
                dialogBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // SQLite database handler
                        MainActivity.db.addProduct(pName,track.getImgUrl(),MainActivity.totalp,MainActivity.q);
                        dialog.dismiss();

                    }
                });
                quitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }

        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

}
