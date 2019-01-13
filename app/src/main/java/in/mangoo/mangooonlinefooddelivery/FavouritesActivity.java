package in.mangoo.mangooonlinefooddelivery;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabSelectListener;

import in.mangoo.mangooonlinefooddelivery.Common.Common;
import in.mangoo.mangooonlinefooddelivery.Database.Database;
import in.mangoo.mangooonlinefooddelivery.Helper.RecyclerItemTouchHelper;
import in.mangoo.mangooonlinefooddelivery.Interface.RecyclerItemTouchHelperListener;
import in.mangoo.mangooonlinefooddelivery.Model.Favourites;
import in.mangoo.mangooonlinefooddelivery.ViewHolder.FavouriteViewHolder;
import in.mangoo.mangooonlinefooddelivery.ViewHolder.FavouritesAdapter;

public class FavouritesActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FavouritesAdapter adapter;
    RelativeLayout rootLayout;

    ScrollView scrollView;
    LinearLayout linearLayout;
    BottomBar bottomBar;
    BottomBarTab cart_badge;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        rootLayout = (RelativeLayout)findViewById(R.id.root_layout);
        scrollView = (ScrollView)findViewById(R.id.scroll);
        linearLayout = (LinearLayout)findViewById(R.id.empty);


        bottomBar = (BottomBar)findViewById(R.id.bottom_navbar);
        bottomBar.setDefaultTab(R.id.tab_search);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int id) {
                if (id == R.id.tab_menu) {

                    Intent intent = new Intent(FavouritesActivity.this,RestaurantList.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                } else if (id == R.id.tab_search) {

                } else if (id == R.id.tab_cart) {
                    Intent intent = new Intent(FavouritesActivity.this,Cart.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                } else if (id == R.id.tab_profile) {
                    Intent intent = new Intent(FavouritesActivity.this,Profile.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                }
            }
        });
        cart_badge = bottomBar.getTabWithId(R.id.tab_cart);
        cart_badge.setBadgeCount(new Database(this).getCountCart(Common.currentUser.getPhone()));

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

        if (adapter.getItemCount() == 0)
        {
            scrollView.setVisibility(View.INVISIBLE);
            linearLayout.setVisibility(View.VISIBLE);

        }
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FavouritesActivity.this,Home.class);
        startActivity(intent);
    }
}
