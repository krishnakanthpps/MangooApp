package app.mangoofood.mangooapp;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import app.mangoofood.mangooapp.Common.Common;
import app.mangoofood.mangooapp.Database.Database;
import app.mangoofood.mangooapp.Helper.RecyclerItemTouchHelper;
import app.mangoofood.mangooapp.Interface.RecyclerItemTouchHelperListener;
import app.mangoofood.mangooapp.Model.Favourites;
import app.mangoofood.mangooapp.Model.Order;
import app.mangoofood.mangooapp.ViewHolder.FavouriteViewHolder;
import app.mangoofood.mangooapp.ViewHolder.FavouritesAdapter;

public class FavouritesActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FavouritesAdapter adapter;
    RelativeLayout rootLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        rootLayout = (RelativeLayout)findViewById(R.id.root_layout);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_fav);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        loadFavourites();
    }

    private void loadFavourites() {
        adapter = new FavouritesAdapter(this,new Database(this).getAllFavourites(Common.currentUser.getPhone()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof FavouriteViewHolder)
        {
            String name=((FavouritesAdapter)recyclerView.getAdapter()).getItem(position).getFoodName();

            final Favourites deleteItem = ((FavouritesAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());
            final int deleteIndex = viewHolder.getAdapterPosition();

            adapter.removeItem(viewHolder.getAdapterPosition());
            new Database(getBaseContext()).removeFromFavourites(deleteItem.getFoodId(),Common.currentUser.getPhone());

            Snackbar snackBar =Snackbar.make(rootLayout,name + " removed from cart.",Snackbar.LENGTH_LONG);
            snackBar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.restoreItem(deleteItem,deleteIndex);
                    new Database(getBaseContext()).addToFavourites(deleteItem);

                }
            });
            snackBar.setActionTextColor(Color.YELLOW);
            snackBar.show();
        }
    }
}
