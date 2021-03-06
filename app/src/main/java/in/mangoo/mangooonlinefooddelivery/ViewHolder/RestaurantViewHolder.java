package in.mangoo.mangooonlinefooddelivery.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import in.mangoo.mangooonlinefooddelivery.Interface.ItemClickListener;
import in.mangoo.mangooonlinefooddelivery.R;

public class RestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txt_restaurant_name,txt_restaurant_addr,txt_opening_time,txt_closing_time;
    public ImageView img_restaurant;

    private ItemClickListener itemClickListener;

    public RestaurantViewHolder(View itemView){
        super(itemView);

        txt_restaurant_name = (TextView)itemView.findViewById(R.id.restaurant_name);
        img_restaurant = (ImageView)itemView.findViewById(R.id.restaurant_image);
        txt_restaurant_addr = (TextView)itemView.findViewById(R.id.restaurant_addr);
        txt_opening_time = (TextView)itemView.findViewById(R.id.opening_time);
        txt_closing_time = (TextView)itemView.findViewById(R.id.closing_time);

        itemView.setOnClickListener(this);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view){

        itemClickListener.onClick(view,getAdapterPosition(),false);

    }
}
