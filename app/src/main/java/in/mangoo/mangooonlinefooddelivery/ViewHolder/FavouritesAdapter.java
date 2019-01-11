package in.mangoo.mangooonlinefooddelivery.ViewHolder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import in.mangoo.mangooonlinefooddelivery.Common.Common;
import in.mangoo.mangooonlinefooddelivery.Database.Database;
import in.mangoo.mangooonlinefooddelivery.FoodDetail;
import in.mangoo.mangooonlinefooddelivery.Interface.ItemClickListener;
import in.mangoo.mangooonlinefooddelivery.Model.Favourites;
import in.mangoo.mangooonlinefooddelivery.Model.Order;
import in.mangoo.mangooonlinefooddelivery.R;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouriteViewHolder>{

    private android.content.Context context;
    private List<Favourites> favouritesList;

    public FavouritesAdapter(android.content.Context context, List<Favourites> favouritesList) {
        this.context = context;
        this.favouritesList = favouritesList;
    }

    @NonNull
    @Override
    public FavouriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.favourites_item,parent,false);
        return new FavouriteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteViewHolder viewHolder,final int position) {
        viewHolder.food_name.setText(favouritesList.get(position).getFoodName());
        viewHolder.food_price.setText(String.format("₹ %s",favouritesList.get(position).getFoodPrice().toString()));
        Picasso.with(context).load(favouritesList.get(position).getFoodImage())
                .into(viewHolder.food_image);

        viewHolder.quick_cart.setOnClickListener(new View.OnClickListener() {

            final boolean isExists = new Database(context).checkExist(favouritesList.get(position).getFoodId(),Common.currentUser.getPhone());
            @Override
            public void onClick(View v) {
                if(!isExists) {
                    new Database(context).addToCart(new Order(
                            Common.currentUser.getPhone(),
                            favouritesList.get(position).getFoodId(),
                            favouritesList.get(position).getFoodName(),
                            "1",
                            favouritesList.get(position).getFoodPrice(),
                            favouritesList.get(position).getFoodDiscount(),
                            favouritesList.get(position).getFoodImage(),
                            Common.restaurantSelected,
                            "1"
                    ));
                }
                else
                {
                    new Database(context).increaseCart(Common.currentUser.getPhone(),favouritesList.get(position).getFoodId());
                }
                Toast.makeText(context, "Added to Cart !", Toast.LENGTH_SHORT).show();
            }
        });

        final Favourites local =favouritesList.get(position);
        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent foodDetail = new Intent(context,FoodDetail.class);
                foodDetail.putExtra("FoodId",favouritesList.get(position).getFoodId());
                context.startActivity(foodDetail);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favouritesList.size();
    }

    public void removeItem(int position)
    {
        favouritesList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Favourites item,int position)
    {
        favouritesList.add(position,item);
        notifyItemInserted(position);
    }

    public Favourites getItem(int position)
    {
        return favouritesList.get(position);
    }
}
