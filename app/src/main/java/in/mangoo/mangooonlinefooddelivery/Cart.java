package in.mangoo.mangooonlinefooddelivery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import in.mangoo.mangooonlinefooddelivery.Common.Common;
import in.mangoo.mangooonlinefooddelivery.Database.Database;
import in.mangoo.mangooonlinefooddelivery.Helper.RecyclerItemTouchHelper;
import in.mangoo.mangooonlinefooddelivery.Interface.RecyclerItemTouchHelperListener;
import in.mangoo.mangooonlinefooddelivery.Model.Coupon;
import in.mangoo.mangooonlinefooddelivery.Model.Order;
import in.mangoo.mangooonlinefooddelivery.Model.Request;
import in.mangoo.mangooonlinefooddelivery.Model.RestaurantID;
import in.mangoo.mangooonlinefooddelivery.Remote.APIService;
import in.mangoo.mangooonlinefooddelivery.Remote.IGoogleService;
import in.mangoo.mangooonlinefooddelivery.ViewHolder.CartAdapter;
import in.mangoo.mangooonlinefooddelivery.ViewHolder.CartViewHolder;
import info.hoang8f.widget.FButton;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Cart extends AppCompatActivity implements  RecyclerItemTouchHelperListener {
    
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    
    FirebaseDatabase database;
    DatabaseReference requests;
    
    public TextView txtTotalPrice;
    public String address;
    boolean coupon_applied = false;
    String coupon_code;

    FButton btnPlace;

    public TextView edtAmount,edtDelivery,edtDiscount;
    LinearLayout empty;
    NestedScrollView scroll;
    ImageView backBtn;
    BottomBar bottomBar;
    BottomBarTab cart_badge;

    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;

    IGoogleService mGoogleMapService;
    APIService mService;
    RelativeLayout rootLayout;

    int payamount = 0;
    int total = 0;
    String addr ="";


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

        setContentView(R.layout.activity_cart);

        mGoogleMapService = Common.getGoogleMapAPI();

        rootLayout = (RelativeLayout)findViewById(R.id.rootLayout);

        mService = Common.getFCMService();

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("coupons");
        Query query = mFirebaseDatabaseReference.orderByChild("type").equalTo("Free Delivery");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data:dataSnapshot.getChildren()){
                    coupon_applied = true;
                    Coupon coupon = data.getValue(Coupon.class);
                    coupon_code = coupon.getCode();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Requests");
                    Query query1 = databaseReference.orderByChild("phone").equalTo(Common.currentUser.getPhone());
                    query1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                            for(DataSnapshot data1:dataSnapshot1.getChildren())
                            {
                                Request request = data1.getValue(Request.class);
                                if(request.getCoupon().equals(coupon_code))
                                    coupon_applied = false;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        txtTotalPrice = (TextView)findViewById(R.id.totalpay);
        btnPlace = (FButton) findViewById(R.id.btnPlaceOrder);
        edtAmount = (TextView)findViewById(R.id.edtAmount);
        edtDelivery = (TextView)findViewById(R.id.edtDelivery);
        edtDiscount = (TextView)findViewById(R.id.edtDiscount);
        empty = (LinearLayout) findViewById(R.id.empty);
        scroll = (NestedScrollView)findViewById(R.id.scroll);
        backBtn = (ImageView)findViewById(R.id.backBtn);

        bottomBar = (BottomBar)findViewById(R.id.bottom_navbar);
        cart_badge = bottomBar.getTabWithId(R.id.tab_cart);
        cart_badge.setBadgeCount(new Database(this).getCountCart(Common.currentUser.getPhone()));
        bottomBar.setDefaultTab(R.id.tab_cart);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int id) {
                if (id == R.id.tab_menu) {

                    Intent intent = new Intent(Cart.this,RestaurantList.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                } else if (id == R.id.tab_search) {
                    Intent intent = new Intent(Cart.this,FavouritesActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                } else if (id == R.id.tab_cart) {


                } else if (id == R.id.tab_profile) {
                    Intent intent = new Intent(Cart.this,Profile.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Cart.this,RestaurantList.class);
                startActivity(intent);
            }
        });

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(cart.size()>0) {

                    /*String checkMin = edtAmount.getText().toString();
                    checkMin.replace("₹","");
                    int minvalue = Integer.parseInt(checkMin);*/

                    Number parse = 0;
                    NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("en","IN"));
                    try {
                        parse = numberFormat.parse(edtAmount.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (parse.intValue() < 100)
                    {
                        Toast.makeText(Cart.this, "Minimum Order Value is ₹ 100", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Intent intent = new Intent(Cart.this, PlaceOrder.class);
                        intent.putExtra("Total", edtAmount.getText().toString());
                        intent.putExtra("Status",coupon_applied);
                        if(coupon_applied) {
                            intent.putExtra("Delivery_Coupon",coupon_code);
                        }
                        startActivity(intent);
                    }
                }
                else
                    Toast.makeText(Cart.this, "Your cart is empty.", Toast.LENGTH_SHORT).show();
            }
        });

        Locale locale = new Locale("en","IN");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        addr = Common.currentUser.getHomeAddress() + " Karaikal ";
        edtDelivery.setText(fmt.format(0));
        
        loadListFood();
        
    }

    private void loadListFood() {

        cart = new Database(this).getCarts(Common.currentUser.getPhone());
        adapter = new CartAdapter(cart,Cart.this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        total = 0;
        for(Order order:cart)
            total += (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));

        Locale locale = new Locale("en","IN");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        edtAmount.setText(fmt.format(total));
        int a = 50,b =0;
        if (cart.size() == 0) {
            empty.setVisibility(View.VISIBLE);
            btnPlace.setVisibility(View.INVISIBLE);
            scroll.setVisibility(View.INVISIBLE);
           // edtDelivery.setText(fmt.format(0));
        }
        else
        {
            int deliveryCharge = 0;
            deliveryCharge = calculateDelivery();
            edtDelivery.setText(fmt.format(deliveryCharge));
            edtDiscount.setText(fmt.format(b));
        }
        try {
            payamount += fmt.parse(edtAmount.getText().toString()).intValue() + fmt.parse(edtAmount.getText().toString()).intValue() +  fmt.parse(edtDelivery.getText().toString()).intValue() + fmt.parse(edtDiscount.getText().toString()).intValue();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        txtTotalPrice.setText(fmt.format(payamount));

    }

    private int calculateDelivery() {

        Geocoder geocoder = new Geocoder(Cart.this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocationName(addr, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address address = addresses.get(0);
        double longitude = address.getLongitude();
        double latitude = address.getLatitude();

        double latA = 10.924956,lngA = 79.832238,latB = latitude,lngB = longitude;

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(latB - latA);
        double lonDistance = Math.toRadians(lngB - lngA);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(latA)) * Math.cos(Math.toRadians(latB))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = R * c * 1000; // convert to meters

        dist = Math.sqrt(Math.pow(dist, 2))/1000 + 1;

        int rate = 0,delCharge = 0;

        if (dist>=0.00 && dist<2.00)
            rate = 10;
        else if (dist>=2.00 && dist<4.00)
            rate = 20;
        else if (dist>=4.00 && dist<6.00)
            rate = 30;
        else if (dist>=6.00 && dist<9.00)
            rate = 40;
        else if (dist>=9.00 && dist<11.00)
            rate = 50;
        else if (dist>=11.00 && dist<12.00)
            rate = 60;
        else if (dist>=12.00 && dist<14.00)
            rate = 70;
        else if (dist>=14.00 && dist<23.00)
            rate = 100;
        else
            rate = 150;

        Log.d("TAG","Rate = "+rate);
        Log.d("TAG","Dist = "+dist);

        delCharge = ( (new Database(Cart.this).getRestaurantCount(Common.currentUser.getPhone()) - 1) * 10) + rate;

        return delCharge;


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());
        return true;
    }

    private void deleteCart(int pos) {
        cart.remove(pos);
        new Database(this).cleanCart(Common.currentUser.getPhone());
        /*for(RestaurantID item:cart)
            new Database(this).addToCart(item);*/
        loadListFood();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof CartViewHolder)
        {
            String name = ((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getProductName();
            final Order deleteItem = ((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());

            final int deleteIndex = viewHolder.getAdapterPosition();

            adapter.removeItem(deleteIndex);
            new Database(getBaseContext()).removeFromCart(deleteItem.getProductId(),Common.currentUser.getPhone());

            total = 0;
            List<Order> orders = new Database(getBaseContext()).getCarts(Common.currentUser.getPhone());
            for(Order item:orders)
                total += (Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity()));

            Locale locale = new Locale("en","IN");
            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

            edtAmount.setText(fmt.format(total));

            int deliveryCharge = 0,b=0;
            deliveryCharge = calculateDelivery();
            edtDelivery.setText(fmt.format(deliveryCharge));
            edtDiscount.setText(fmt.format(b));

            payamount = 0;

            try {
                payamount += fmt.parse(edtAmount.getText().toString()).intValue() + fmt.parse(edtDelivery.getText().toString()).intValue() + fmt.parse(edtDiscount.getText().toString()).intValue();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            txtTotalPrice.setText(fmt.format(payamount));
            if ((cart.size() == 0 )&& (edtAmount.getText().toString().equals("₹ 0.00")))
            {
                empty.setVisibility(View.VISIBLE);
                btnPlace.setVisibility(View.INVISIBLE);
                scroll.setVisibility(View.INVISIBLE);
                edtDelivery.setText(fmt.format(0));
            }

            Snackbar snackBar =Snackbar.make(rootLayout,name + " removed from cart.",Snackbar.LENGTH_LONG);
            snackBar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.restoreItem(deleteItem,deleteIndex);
                    new Database(getBaseContext()).addToCart(deleteItem);

                    total = 0;
                    List<Order> orders = new Database(getBaseContext()).getCarts(Common.currentUser.getPhone());
                    for(Order item:orders)
                        total += (Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity()));

                    Locale locale = new Locale("en","IN");
                    NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

                    if ((cart.size() == 0 )&& (edtAmount.getText().toString().equals("₹ 0.00")))
                    {
                        empty.setVisibility(View.VISIBLE);
                        btnPlace.setVisibility(View.INVISIBLE);
                        scroll.setVisibility(View.INVISIBLE);
                        edtDelivery.setText(fmt.format(0));
                    }

                    edtAmount.setText(fmt.format(total));

                    int deliveryCharge = 0,b=0;
                    deliveryCharge = calculateDelivery();
                    edtDelivery.setText(fmt.format(deliveryCharge));
                    edtDiscount.setText(fmt.format(b));

                    payamount = 0;
                    try {
                        payamount += fmt.parse(edtAmount.getText().toString()).intValue() + fmt.parse(edtDelivery.getText().toString()).intValue() +fmt.parse(edtDiscount.getText().toString()).intValue();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    txtTotalPrice.setText(fmt.format(payamount));

                }
            });
            snackBar.setActionTextColor(Color.YELLOW);
            snackBar.show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Cart.this,RestaurantList.class);
        startActivity(intent);
    }
}
