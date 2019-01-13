package in.mangoo.mangooonlinefooddelivery.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import in.mangoo.mangooonlinefooddelivery.R;

public class NewsViewHolder extends RecyclerView.ViewHolder {

    public ImageView bannerImage;
    public TextView foodName;

    public NewsViewHolder(@NonNull View itemView) {
        super(itemView);

        bannerImage = (ImageView)itemView.findViewById(R.id.bannerImage);
        foodName = (TextView)itemView.findViewById(R.id.food_name);
    }

}
