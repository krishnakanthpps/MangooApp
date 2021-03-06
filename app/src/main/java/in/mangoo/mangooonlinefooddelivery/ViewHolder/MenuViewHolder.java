package in.mangoo.mangooonlinefooddelivery.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import in.mangoo.mangooonlinefooddelivery.*;

import in.mangoo.mangooonlinefooddelivery.Interface.ItemClickListener;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtMenuName,restName,restAddr;
    public ImageView imageView;

    private ItemClickListener itemClickListener;

    public MenuViewHolder(View itemView){
        super(itemView);

        txtMenuName = (TextView)itemView.findViewById(R.id.menu_name);
        imageView = (ImageView)itemView.findViewById(R.id.menu_image);
        restName = (TextView)itemView.findViewById(R.id.rest_name);
        restAddr = (TextView)itemView.findViewById(R.id.rest_addr);

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
