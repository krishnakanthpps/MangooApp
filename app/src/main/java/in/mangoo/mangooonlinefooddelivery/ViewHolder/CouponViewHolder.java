package in.mangoo.mangooonlinefooddelivery.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import in.mangoo.mangooonlinefooddelivery.Interface.ItemClickListener;
import in.mangoo.mangooonlinefooddelivery.R;
import info.hoang8f.widget.FButton;
import xyz.hanks.library.bang.SmallBangView;

public class CouponViewHolder extends RecyclerView.ViewHolder {

    public TextView code, desc, date, apply;
    public ImageView coupon_image;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public CouponViewHolder(@NonNull View itemView) {
        super(itemView);

        code = (TextView) itemView.findViewById(R.id.coupon_code);
        desc = (TextView) itemView.findViewById(R.id.coupon_description);
        date = (TextView) itemView.findViewById(R.id.coupon_expiry);
        apply = itemView.findViewById(R.id.apply_coupon_btn);

    }

}
