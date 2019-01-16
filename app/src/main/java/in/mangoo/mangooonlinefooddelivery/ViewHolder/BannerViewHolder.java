package in.mangoo.mangooonlinefooddelivery.ViewHolder;

import android.graphics.ImageDecoder;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import in.mangoo.mangooonlinefooddelivery.Interface.ItemClickListener;
import in.mangoo.mangooonlinefooddelivery.R;
import xyz.hanks.library.bang.SmallBangView;

public class BannerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView bannerImage;
    public TextView foodName;
    /*public Button quickCart;
    public SmallBangView buttonBang;*/
    private ItemClickListener itemClickListener;

    public BannerViewHolder(@NonNull View itemView) {
        super(itemView);

        bannerImage = (ImageView)itemView.findViewById(R.id.bannerImage);
        foodName = (TextView)itemView.findViewById(R.id.food_name);
        /*quickCart = (Button)itemView.findViewById(R.id.btn_quick_cart);
        buttonBang = (SmallBangView) itemView.findViewById(R.id.button_bang);*/
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
