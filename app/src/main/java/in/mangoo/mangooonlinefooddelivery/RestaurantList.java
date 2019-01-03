package in.mangoo.mangooonlinefooddelivery;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import in.mangoo.mangooonlinefooddelivery.Common.Common;
import in.mangoo.mangooonlinefooddelivery.Interface.ItemClickListener;
import in.mangoo.mangooonlinefooddelivery.Model.Banner;
import in.mangoo.mangooonlinefooddelivery.Model.Restaurant;
import in.mangoo.mangooonlinefooddelivery.ViewHolder.BannerViewHolder;
import in.mangoo.mangooonlinefooddelivery.ViewHolder.RestaurantViewHolder;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RestaurantList extends AppCompatActivity {

    AlertDialog waitingDialog;
    RecyclerView recyclerView,bannerList;
    RecyclerView.LayoutManager bannerLayout;
    FirebaseRecyclerAdapter<Banner,BannerViewHolder> bannerAdapter;
    SwipeRefreshLayout swipeRefreshLayout;

    TextView edtHomeAddress,edtRestaurantAddress;
    SliderLayout mSlider;
    HashMap<String, String> image_list;

    FirebaseDatabase database;

    FirebaseRecyclerOptions<Restaurant> options = new FirebaseRecyclerOptions.Builder<Restaurant>()
            .setQuery(FirebaseDatabase.getInstance()
                            .getReference()
                            .child("Restaurants")
                    ,Restaurant.class)
            .build();

    FirebaseRecyclerAdapter<Restaurant,RestaurantViewHolder> adapter = new FirebaseRecyclerAdapter<Restaurant, RestaurantViewHolder>(options) {
        @Override
        public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.restaurant_item, parent, false);
            return new RestaurantViewHolder(itemView);

        }

        @Override
        protected void onBindViewHolder(@NonNull RestaurantViewHolder viewHolder, int position,
                                        @NonNull Restaurant model) {

            viewHolder.txt_restaurant_name.setText(model.getName());
            viewHolder.txt_restaurant_addr.setText(model.getAddr());
            Picasso.with(getBaseContext()).load(model.getImage())
                    .into(viewHolder.img_restaurant);
            final Restaurant clickItem = model;
            viewHolder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    Intent foodList = new Intent(RestaurantList.this,Home.class);
                    Common.restaurantSelected = adapter.getRef(position).getKey();
                    startActivity(foodList);
                }
            });


        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Product-Sans.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_restaurant_list);

        database = FirebaseDatabase.getInstance();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Common.isConnectedToInternet(getBaseContext())) {
                    edtHomeAddress.setText(Common.currentUser.getHomeAddress());
                    loadRestaurant();
                }
                else {
                    Toast.makeText(getBaseContext(), "Check your Internet Connection.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (Common.isConnectedToInternet(getBaseContext())) {
                    edtHomeAddress.setText(Common.currentUser.getHomeAddress());
                    loadRestaurant();
                }
                else {
                    Toast.makeText(getBaseContext(), "Check your Internet Connection.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

        });

        edtRestaurantAddress = (TextView)findViewById(R.id.restaurant_addr);
        edtHomeAddress = (TextView)findViewById(R.id.edtHomeAddress);
        edtHomeAddress.setText(Common.currentUser.getHomeAddress());

        edtHomeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHomeAddressDialog();
                edtHomeAddress.setText(Common.currentUser.getHomeAddress());
            }
        });


        recyclerView = (RecyclerView) findViewById(R.id.recycler_restaurant);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bannerList = (RecyclerView)findViewById(R.id.bannerList);
        bannerList.setHasFixedSize(false);
        bannerLayout = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        bannerList.setLayoutManager(bannerLayout);

        //setupslider();
        loadBanner();

    }

    private void loadBanner() {

        FirebaseRecyclerOptions<Banner> banner = new FirebaseRecyclerOptions.Builder<Banner>()
                .setQuery(FirebaseDatabase.getInstance()
                                .getReference()
                                .child("Banner")
                        ,Banner.class)
                .build();

       bannerAdapter = new FirebaseRecyclerAdapter<Banner, BannerViewHolder>(banner) {
           @NonNull
           @Override
           public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
               View itemView = LayoutInflater.from(parent.getContext())
                       .inflate(R.layout.banner_layout, parent, false);
               return new BannerViewHolder(itemView);
           }

           @Override
           protected void onBindViewHolder(@NonNull BannerViewHolder viewHolder, int position, @NonNull Banner model) {
               Picasso.with(getBaseContext()).load(model.getImage())
                       .into(viewHolder.bannerImage);
               //final Restaurant clickItem = model;
               viewHolder.setItemClickListener(new ItemClickListener() {
                   @Override
                   public void onClick(View view, int position, boolean isLongClick) {
                       Intent foodList = new Intent(RestaurantList.this, Home.class);
                       Common.restaurantSelected = adapter.getRef(position).getKey();
                       startActivity(foodList);
                   }

               });
           }
       };
        bannerAdapter.startListening();
        bannerList.setAdapter(bannerAdapter);
    }


    private void loadRestaurant(){


        adapter.startListening();
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);

        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null)
            adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRestaurant();

    }

    private void showHomeAddressDialog() {

        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(RestaurantList.this);
        alertDialog.setTitle("CHANGE HOME ADDRESS");
        alertDialog.setMessage("All fields are necessary");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_home = inflater.inflate(R.layout.home_address_layout,null);

        final MaterialEditText edtHomeAddress= (MaterialEditText)layout_home.findViewById(R.id.edtHomeAddress);

        alertDialog.setView(layout_home);

        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                Common.currentUser.setHomeAddress(edtHomeAddress.getText().toString());
                edtHomeAddress.setText(Common.currentUser.getHomeAddress());
                dialogInterface.dismiss();

                FirebaseDatabase.getInstance().getReference("User")
                        .child(Common.currentUser.getPhone())
                        .setValue(Common.currentUser)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(RestaurantList.this, "Home Address updated successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });


        alertDialog.show();

    }

    private void setupslider() {

       // mSlider = (SliderLayout)findViewById(R.id.slider);
        image_list = new HashMap<>();
        final DatabaseReference banners = database.getReference("Banner");
        banners.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    Banner banner = postSnapshot.getValue(Banner.class);
                    image_list.put(banner.getName()+"@@@"+banner.getId(),banner.getImage());
                }
                for(String key:image_list.keySet())
                {
                    String[] keySplit = key.split("@@@");
                    String nameofFood = keySplit[0];
                    String idofFood = keySplit[1];
                    final TextSliderView textSliderView = new TextSliderView(getBaseContext());
                    textSliderView
                            .description(nameofFood)
                            .image(image_list.get(key))
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                    Intent intent = new Intent(RestaurantList.this,FoodDetail.class);
                                    intent.putExtras(textSliderView.getBundle());
                                    startActivity(intent);
                                }
                            });
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle().putString("FoodId",idofFood);
                    mSlider.addSlider(textSliderView);
                    banners.removeEventListener(this);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        //mSlider.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
        mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mSlider.setCustomAnimation(new DescriptionAnimation());
        mSlider.setDuration(4000);

    }

    @Override
    public void onBackPressed() {

    }
}
