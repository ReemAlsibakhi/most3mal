package com.reemsib.mst3jl.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import com.reemsib.mst3jl.R;
import com.reemsib.mst3jl.model.Image;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class ImageTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<Image> images;
    OnItemClickListener mListener;
   public interface OnItemClickListener {
        void onClicked(Integer position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public ImageTypeAdapter(Context context,ArrayList<Image> images){
        this.context = context;
        this.images=images;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_advert_item, parent, false);
        return new ImageViewHolder(itemView);

   }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position){
        if(viewHolder instanceof ImageViewHolder){
            ((ImageViewHolder) viewHolder).populate(images.get(position),position);
        }
   }



    @Override
    public int getItemCount(){
      return images.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        ImageView btnDelete;
        public ImageViewHolder(View itemView){
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.img_advert);
            btnDelete = (ImageView) itemView.findViewById(R.id.btn_delete);

        }
           public void populate(Image imageDataWrapper,Integer position){
               Picasso.get().load(imageDataWrapper.getImage()).into(image);
               btnDelete.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {

                       Toast.makeText(context,position+"",Toast.LENGTH_LONG).show();
                       if (mListener!=null){
                           mListener.onClicked(position);
                       }
                   }
               });
            }
    }

}