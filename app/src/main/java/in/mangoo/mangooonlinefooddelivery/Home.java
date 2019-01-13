package in.mangoo.mangooonlinefooddelivery;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.facebook.accountkit.AccountKit;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.google.firebase.iid.FirebaseInstanceId;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;
import com.squareup.picasso.Picasso;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import in.mangoo.mangooonlinefooddelivery.Common.Common;
import in.mangoo.mangooonlinefooddelivery.Database.Database;
import in.mangoo.mangooonlinefooddelivery.Interface.ItemClickListener;
import in.mangoo.mangooonlinefooddelivery.Model.Banner;
import in.mangoo.mangooonlinefooddelivery.Model.Category;
import in.mangoo.mangooonlinefooddelivery.Model.Food;
import in.mangoo.mangooonlinefooddelivery.Model.Restaurant;
import in.mangoo.mangooonlinefooddelivery.Model.Token;
import in.mangoo.mangooonlinefooddelivery.ViewHolder.BannerViewHolder;
import in.mangoo.mangooonlinefooddelivery.ViewHolder.MenuViewHolder;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database;
    DatabaseReference category,mName,mAddr,mImage;
    TextView txtFullName,restaurantName,restaurantAddr;
    ImageView menuBtn,restaurantImage;


    RecyclerView recyler_menu,bannerList;
    RecyclerView.LayoutManager layoutManager,bannerLayout;
    FirebaseRecyclerAdapter<Banner,BannerViewHolder> bannerAdapter;

    FirebaseRecyclerAdapter<Category,MenuViewHolder>adapter;

    SwipeRefreshLayout swipeRefreshLayout;

    CounterFab fab;
    BottomBar bottomBar;
    BottomBarTab cart_badge;

    HashMap<String, String> image_list;
    SliderLayout mSlider;

    String restName = "",restAddr ="",restImage="";

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
        setContentView(R.layout.activity_home);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);*/

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToInternet(getBaseContext()))
                {
                    loadMenu();
                    loadBanner();
                }
                else
                {
                    Toast.makeText(getBaseContext(), "Check your Internet Connection.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(Common.isConnectedToInternet(getBaseContext()))
                {
                    loadMenu();
                    loadBanner();
                }
                else
                {
                    Toast.makeText(getBaseContext(), "Check your Internet Connection.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

        });

        database = FirebaseDatabase.getInstance();
        category = database.getReference("Restaurants").child(Common.restaurantSelected)
        .child("detail").child("Category");

        mName = database.getReference("Restaurants").child(Common.restaurantSelected);
        mName.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                restName =  (String) snapshot.getValue();
                restaurantName = (TextView)findViewById(R.id.rest_name);
                restaurantName.setText(restName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mImage = database.getReference("Restaurants").child(Common.restaurantSelected);
        mImage.child("image").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                restImage =  (String) snapshot.getValue();
                restaurantImage = (ImageView)findViewById(R.id.rest_image);
                Picasso.with(Home.this).load(restImage).into(restaurantImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mAddr = database.getReference("Restaurants").child(Common.restaurantSelected);
        mAddr.child("addr").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                restAddr =  (String) snapshot.getValue();
                restaurantAddr = (TextView)findViewById(R.id.rest_addr);
                restaurantAddr.setText(restAddr);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        bannerList = (RecyclerView)findViewById(R.id.bannerList);
        bannerList.setHasFixedSize(true);
        bannerLayout = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        bannerList.setLayoutManager(bannerLayout);

        loadBanner();

        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(category,Category.class)
                .build();



        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_item,parent,false);
                return new MenuViewHolder(itemView);

            }

            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder viewHolder, int position, @NonNull Category model) {

                viewHolder.txtMenuName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).resize(300,200).into(viewHolder.imageView);
                final Category clickItem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodList = new Intent(Home.this,FoodList.class);
                        foodList.putExtra("CategoryId",adapter.getRef(position).getKey());
                        startActivity(foodList);
                    }
                });


            }

        };



       // Paper.init(this);

        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();*/

        bottomBar = (BottomBar)findViewById(R.id.bottom_navbar);
        bottomBar.setDefaultTab(R.id.tab_menu);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int id) {
                if (id == R.id.tab_menu) {


                } else if (id == R.id.tab_search) {
                    Intent orderIntent = new Intent(Home.this,FavouritesActivity.class);
                    startActivity(orderIntent);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                } else if (id == R.id.tab_cart) {

                    Intent orderIntent = new Intent(Home.this,Cart.class);
                    startActivity(orderIntent);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);


                } else if (id == R.id.tab_profile) {
                    Intent orderIntent = new Intent(Home.this,Profile.class);
                    startActivity(orderIntent);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                }

            }
        });
        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(int id) {
                if (id == R.id.tab_menu) {
                    Intent intent = new Intent(Home.this,RestaurantList.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                } else if (id == R.id.tab_search) {


                } else if (id == R.id.tab_cart) {


                } else if (id == R.id.tab_profile) {

                }
            }
        });
        cart_badge = bottomBar.getTabWithId(R.id.tab_cart);
        cart_badge.setBadgeCount(new Database(this).getCountCart(Common.currentUser.getPhone()));

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        txtFullName = (TextView)headerView.findViewById(R.id.txtFullName);
        txtFullName.setText(Common.currentUser.getName());

        recyler_menu = (RecyclerView)findViewById(R.id.recyler_menu);
        //recyler_menu.setHasFixedSize(true);
        //layoutManager = new LinearLayoutManager(this);
        //recyler_menu.setLayoutManager(layoutManager);
        recyler_menu.setLayoutManager(new GridLayoutManager(Home.this,2));
        recyler_menu.addItemDecoration(new EqualSpacingItemDecoration(20,EqualSpacingItemDecoration.GRID));

        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recyler_menu.getContext(),
                R.anim.layout_fall_down);
        recyler_menu.setLayoutAnimation(controller);

        updateToken(FirebaseInstanceId.getInstance().getToken());

        //setupslider();

    }

    private void loadBanner() {

        FirebaseRecyclerOptions<Banner> banner = new FirebaseRecyclerOptions.Builder<Banner>()
                .setQuery(FirebaseDatabase.getInstance()
                                .getReference()
                                .child("Restaurants")
                        .child(Common.restaurantSelected)
                        .child("detail")
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
            protected void onBindViewHolder(@NonNull BannerViewHolder viewHolder, int position, @NonNull final Banner model) {
                Picasso.with(getBaseContext()).load(model.getImage())
                        .fit()
                        .into(viewHolder.bannerImage);
                viewHolder.foodName.setText(model.getName());
                //final Banner local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodDetail = new Intent(Home.this,FoodDetail.class);
                        String foodId = model.getId();
                        Log.d("TAG","Food id = "+foodId);
                        foodDetail.putExtra("FoodId",foodId);
                        startActivity(foodDetail);
                    }

                });
            }
        };
        bannerAdapter.startListening();
        bannerList.setAdapter(bannerAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    private void setupslider() {

        //mSlider = (SliderLayout)findViewById(R.id.slider);
        image_list = new HashMap<>();
        final DatabaseReference banners = database.getReference("Restaurants").child(Common.restaurantSelected).child("detail").child("Banner");
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
                                    Intent intent = new Intent(Home.this,FoodDetail.class);
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

    private void updateToken(String token) {
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference tokens=db.getReference("Tokens");
        Token data=new Token(token,false);
        tokens.child(Common.currentUser.getPhone()).setValue(data);
    }

    private void loadMenu(){

        recyler_menu.setAdapter(adapter);
        adapter.startListening();
        swipeRefreshLayout.setRefreshing(false);

        recyler_menu.getAdapter().notifyDataSetChanged();
        recyler_menu.scheduleLayoutAnimation();

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        //mSlider.stopAutoCycle();

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMenu();

    }

        @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(Home.this,RestaurantList.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.menu_search)
            startActivity(new Intent(Home.this,SearchActivity.class));
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            // Handle the camera action
        } else if (id == R.id.nav_cart) {

            Intent cartIntent = new Intent(Home.this,Cart.class);
            startActivity(cartIntent);

        } else if (id == R.id.nav_orders) {

            Intent orderIntent = new Intent(Home.this,OrderStatus.class);
            startActivity(orderIntent);

        } else if (id == R.id.nav_log_out) {

            AccountKit.logOut();

            Intent intent = new Intent(Home.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else if (id == R.id.nav_update_name)
        {
            showChangePasswordDialog();
        }
        else if (id == R.id.nav_home_address)
        {
            showHomeAddressDialog();
        }
        else if (id == R.id.nav_setting)
        {
            showSettingDialog();
        }
        else if (id == R.id.nav_favourites)
        {
            startActivity(new Intent(Home.this,FavouritesActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showSettingDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("SETTINGS");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_setting = inflater.inflate(R.layout.setting_layout,null);

        final CheckBox ckb_subscribe_new = (CheckBox)layout_setting.findViewById(R.id.ckb_sub_new);

        Paper.init(this);
        String isSubscribe = Paper.book().read("sub_new");
        if(isSubscribe ==null || TextUtils.isEmpty(isSubscribe) || isSubscribe.equals("false"))
            ckb_subscribe_new.setChecked(false);
        else
            ckb_subscribe_new.setChecked(true);
        alertDialog.setView(layout_setting);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();

                if(ckb_subscribe_new.isChecked())
                {
                    FirebaseMessaging.getInstance().subscribeToTopic(Common.topicName);
                    Paper.book().write("sub_new","true");
                }
                else
                {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(Common.topicName);
                    Paper.book().write("sub_new","false");
                }
            }
        });

        alertDialog.show();
    }

    private void showHomeAddressDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("CHANGE HOME ADDRESS");
        alertDialog.setMessage("All fields are necessary");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_home = inflater.inflate(R.layout.home_address_layout,null);

        final MaterialEditText edtHomeAddress= (MaterialEditText)layout_home.findViewById(R.id.edtHomeAddress);

        alertDialog.setView(layout_home);

        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();

                Common.currentUser.setHomeAddress(edtHomeAddress.getText().toString());

                FirebaseDatabase.getInstance().getReference("User")
                        .child(Common.currentUser.getPhone())
                        .setValue(Common.currentUser)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(Home.this, "Home Address updated successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        alertDialog.show();

    }

    private void showChangePasswordDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("UPDATE NAME");
        alertDialog.setMessage("All fields are necessary");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_name = inflater.inflate(R.layout.update_name_layout,null);

        final MaterialEditText edtName = (MaterialEditText)layout_name.findViewById(R.id.edtName);

        alertDialog.setView(layout_name);

        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final android.app.AlertDialog waitingDialog = new SpotsDialog(Home.this);
                waitingDialog.show();

                Map<String,Object> update_name = new HashMap<>();
                update_name.put("name",edtName.getText().toString());

                FirebaseDatabase.getInstance()
                        .getReference("User")
                        .child(Common.currentUser.getPhone())
                        .updateChildren(update_name)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                waitingDialog.dismiss();
                                if (task.isSuccessful())
                                    Toast.makeText(Home.this, "Name updated successfully !!", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                dialogInterface.dismiss();

            }
        });

        alertDialog.show();
    }

}
