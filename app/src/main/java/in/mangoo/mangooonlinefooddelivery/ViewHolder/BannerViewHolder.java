package in.mangoo.mangooonlinefooddelivery.ViewHolder;

import android.graphics.ImageDecoder;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import in.mangoo.mangooonlinefooddelivery.Interface.ItemClickListener;
import in.mangoo.mangooonlinefooddelivery.R;

public class BannerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView bannerImage;
    private ItemClickListener itemClickListener;

    public BannerViewHolder(@NonNull View itemView) {
        super(itemView);

        bannerImage = (ImageView)itemView.findViewById(R.id.bannerImage);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }
}
