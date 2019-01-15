package in.mangoo.mangooonlinefooddelivery;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import in.mangoo.mangooonlinefooddelivery.Common.Common;
import in.mangoo.mangooonlinefooddelivery.Database.Database;
import in.mangoo.mangooonlinefooddelivery.Interface.ItemClickListener;
import in.mangoo.mangooonlinefooddelivery.Model.Favourites;
import in.mangoo.mangooonlinefooddelivery.Model.Food;
import in.mangoo.mangooonlinefooddelivery.Model.Order;
import in.mangoo.mangooonlinefooddelivery.ViewHolder.FoodViewHolder;
import xyz.hanks.library.bang.SmallBangView;

public class SearchActivity extends AppCompatActivity {

    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;
    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    String queryText = "";
    String searchtext = "";

    FirebaseDatabase database;
    DatabaseReference foodList;

    CounterFab fab;
    BottomNavigationViewEx bnve;
    SmallBangView smallBangView;
    BottomBar bottomBar;
    BottomBarTab cart;

    Database localDB;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Restaurants").child(Common.restaurantSelected).child("detail").child("Foods");

        localDB = new Database(this);



        materialSearchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        materialSearchBar.setHint("Search Food..");
//        loadSuggest();
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                List<String> suggest = new ArrayList<String>();
                for (String search : suggestList) {
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);
                startSearch(s);


            }

            @Override
            public void afterTextChanged(Editable s) {



            }
        });

        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

                if (!enabled)
                    recyclerView.setAdapter(adapter);

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {

                startSearch(text);

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }

        });



        recyclerView = (RecyclerView)findViewById(R.id.recycler_search);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        loadAllFoods();


    }

    public static String toTitleCase(String str) {

        if (str == null) {
            return null;
        }

        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }



    private void loadAllFoods() {

        queryText = materialSearchBar.getText().toString();

        Query searchByName = foodList.orderByChild("Name").startAt(queryText).endAt(queryText+"\uf8ff");

        FirebaseRecyclerOptions<Food> foodOptions = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(searchByName,Food.class)
                .build();



        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(foodOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final FoodViewHolder viewHolder,final int position, @NonNull final Food model) {

                viewHolder.food_name.setText(model.getName());
                viewHolder.food_price.setText(String.format("₹ %s",model.getPrice().toString()));
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.food_image);

                final boolean isExists = new Database(getBaseContext()).checkExist(adapter.getRef(position).getKey(),Common.currentUser.getPhone());
                viewHolder.quick_cart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!isExists) {
                            new Database(getBaseContext()).addToCart(new Order(
                                    Common.currentUser.getPhone(),
                                    adapter.getRef(position).getKey(),
                                    model.getName(),
                                    "1",
                                    model.getPrice(),
                                    model.getDiscount(),
                                    model.getImage(),
                                    Common.restName,
                                    "1"
                            ));
                        }
                        else
                        {
                            new Database(getBaseContext()).increaseCart(Common.currentUser.getPhone(),adapter.getRef(position).getKey());
                        }
                        Toast.makeText(SearchActivity.this, "Added to Cart !", Toast.LENGTH_SHORT).show();
                        cart.setBadgeCount(new Database(SearchActivity.this).getCountCart(Common.currentUser.getPhone()));
                    }
                });


                if(localDB.isFavourite(adapter.getRef(position).getKey(),Common.currentUser.getPhone()))
                    //viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);

                viewHolder.fav_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Favourites favourites = new Favourites();
                        favourites.setFoodId(adapter.getRef(position).getKey());
                        favourites.setFoodName(model.getName());
                        favourites.setFoodDescription(model.getDescription());
                        favourites.setFoodDiscount(model.getDiscount());
                        favourites.setFoodImage(model.getImage());
                        favourites.setFoodMenuId(model.getMenuId());
                        favourites.setUserPhone(Common.currentUser.getPhone());
                        favourites.setFoodPrice(model.getPrice());

                        if(!localDB.isFavourite(adapter.getRef(position).getKey(),Common.currentUser.getPhone()))
                        {
                            localDB.addToFavourites(favourites);
                            //viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(SearchActivity.this, ""+model.getName()+" added to Favourites", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            localDB.removeFromFavourites(adapter.getRef(position).getKey(),Common.currentUser.getPhone());
                            //viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(SearchActivity.this, ""+model.getName()+" removed from Favourites", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodDetail = new Intent(SearchActivity.this,FoodDetail.class);
                        foodDetail.putExtra("FoodId",adapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });

            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item,parent,false);
                return new FoodViewHolder(itemView);

            }
        };

        adapter.startListening();

        recyclerView.setAdapter(adapter);

    }


    private void startSearch(CharSequence text) {

        queryText = materialSearchBar.getText();

        searchtext =  toTitleCase(queryText);


        Query searchByName = foodList.orderByChild("Name").startAt(searchtext).endAt(searchtext+"\uf8ff");


        FirebaseRecyclerOptions<Food> foodOptions = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(searchByName,Food.class)
                .build();

        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(foodOptions) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder viewHolder, int position, @NonNull Food model) {

                viewHolder.food_name.setText(model.getName());
                viewHolder.food_price.setText(String.format("₹ %s",model.getPrice().toString()));
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.food_image);

                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodDetail = new Intent(SearchActivity.this,FoodDetail.class);
                        foodDetail.putExtra("FoodId",searchAdapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });

            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item,parent,false);
                return new FoodViewHolder(itemView);
            }
        };

        searchAdapter.startListening();
        recyclerView.setAdapter(searchAdapter);
    }

//    private void loadSuggest() {
//        foodList.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                        for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
//                        {
//                            Food item = postSnapshot.getValue(Food.class);
//                            suggestList.add(item.getName());
//                        }
//                        materialSearchBar.setLastSuggestions(suggestList);
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter != null) {
            adapter.stopListening();
        }
        if(searchAdapter != null) {
            searchAdapter.stopListening();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllFoods();

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SearchActivity.this,Home.class);
        startActivity(intent);
    }
}
