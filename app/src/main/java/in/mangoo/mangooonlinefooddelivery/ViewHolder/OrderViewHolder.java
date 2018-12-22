package in.mangoo.mangooonlinefooddelivery.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import in.mangoo.mangooonlinefooddelivery.Interface.ItemClickListener;
import in.mangoo.mangooonlinefooddelivery.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtOrderId,txtOrderStatus,txtOrderPhone,txtOrderAddress,txtOrderDate;
    private ItemClickListener itemClickListener;

    public ImageView btn_delete;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        txtOrderAddress = (TextView)itemView.findViewById(R.id.order_address);
        txtOrderId = (TextView)itemView.findViewById(R.id.order_id);
        txtOrderPhone = (TextView)itemView.findViewById(R.id.order_phone);
        txtOrderStatus = (TextView)itemView.findViewById(R.id.order_status);
        btn_delete = (ImageView)itemView.findViewById(R.id.btnDelete);
        txtOrderDate = (TextView)itemView.findViewById(R.id.order_date);
        itemView.setOnClickListener(this);



    }

    @Override
    public void onClick(View view) {

        itemClickListener.onClick(view,getAdapterPosition(),false);

    }
}
