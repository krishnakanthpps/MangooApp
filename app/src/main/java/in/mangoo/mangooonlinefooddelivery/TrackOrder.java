package in.mangoo.mangooonlinefooddelivery;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabSelectListener;
import com.squareup.picasso.Picasso;
import com.transferwise.sequencelayout.SequenceStep;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import in.mangoo.mangooonlinefooddelivery.Common.Common;
import in.mangoo.mangooonlinefooddelivery.Database.Database;
import in.mangoo.mangooonlinefooddelivery.Interface.ItemClickListener;
import in.mangoo.mangooonlinefooddelivery.Model.Category;
import in.mangoo.mangooonlinefooddelivery.Model.DeliveryBoy;
import in.mangoo.mangooonlinefooddelivery.Model.Food;
import in.mangoo.mangooonlinefooddelivery.Model.Order;
import in.mangoo.mangooonlinefooddelivery.Model.Request;
import in.mangoo.mangooonlinefooddelivery.ViewHolder.CartAdapter;
import in.mangoo.mangooonlinefooddelivery.ViewHolder.FoodViewHolder;
import in.mangoo.mangooonlinefooddelivery.ViewHolder.MenuViewHolder;
import info.hoang8f.widget.FButton;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TrackOrder extends AppCompatActivity {

    SequenceStep first,second,third,fourth,fifth;

    TextView orderNumber,deliveryName,deliveryPhone,userName,homeAddr;

    FirebaseDatabase db;
    DatabaseReference order,delivery;

    FirebaseRecyclerAdapter<Food,FoodViewHolder> adapter;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    ImageView backBtn;
    BottomBar bottomBar;
    BottomBarTab cart_badge;
    FButton Call;

    //Request request;

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

        setContentView(R.layout.activity_track_order);

        db = FirebaseDatabase.getInstance();
        order = db.getReference("Requests");

        //request = new Request();

        first = (SequenceStep)findViewById(R.id.firstStep);
        second = (SequenceStep)findViewById(R.id.secondStep);
        third = (SequenceStep)findViewById(R.id.thirdStep);
        fourth = (SequenceStep)findViewById(R.id.fourthStep);
        fifth = (SequenceStep)findViewById(R.id.fifthStep);
        orderNumber = (TextView)findViewById(R.id.orderNumber);
        deliveryName = (TextView)findViewById(R.id.deliveryName);
        deliveryPhone = (TextView)findViewById(R.id.deliveryPhone);
        userName = (TextView)findViewById(R.id.userName);
        homeAddr = (TextView)findViewById(R.id.Address);
        Call = (FButton)findViewById(R.id.Call);
        backBtn = (ImageView)findViewById(R.id.backBtn);

        bottomBar = (BottomBar)findViewById(R.id.bottom_navbar);
        bottomBar.setDefaultTab(R.id.tab_profile);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int id) {
                if (id == R.id.tab_menu) {

                    Intent intent = new Intent(TrackOrder.this,RestaurantList.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                } else if (id == R.id.tab_search) {
                    Intent intent = new Intent(TrackOrder.this,FavouritesActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                } else if (id == R.id.tab_cart) {
                    Intent intent = new Intent(TrackOrder.this,Cart.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                } else if (id == R.id.tab_profile) {

                }
            }
        });
        cart_badge = bottomBar.getTabWithId(R.id.tab_cart);
        cart_badge.setBadgeCount(new Database(this).getCountCart(Common.currentUser.getPhone()));

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrackOrder.this,OrderStatus.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.foodeView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        userName.setText(Common.currentUser.getName());

        Intent intent = getIntent();
        final String order_number = intent.getStringExtra("OrderNo");
        homeAddr.setText(intent.getStringExtra("Address"));
        orderNumber.setText("Order #" + order_number);
        deliveryName.setText(intent.getStringExtra("DeliveryBoy"));
        final String status_code = intent.getStringExtra("Status");

        order.child(order_number).child("status").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String code = (String) dataSnapshot.getValue();
                setStatus(code);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        delivery = db.getReference("Driverpool");
        if (deliveryName.getText().toString().equals("Not Assigned"))
        {
            deliveryPhone.setVisibility(View.GONE);
        }
        else {
            Query query = delivery.orderByChild("name").equalTo(intent.getStringExtra("DeliveryBoy"));
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        DeliveryBoy deliveryBoy = postSnapshot.getValue(DeliveryBoy.class);
                        deliveryPhone.setText(deliveryBoy.getPhone().toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        Call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deliveryPhone.getVisibility() == View.VISIBLE) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + deliveryPhone.getText().toString()));
                    startActivity(callIntent);
                }
                else
                {
                    Toast.makeText(TrackOrder.this, "Delivery boy has not been assigned yet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setStatus(status_code);
        loadFoodList(order_number);

    }

    private void setStatus(String status_code) {

        if (status_code.equals("1"))
        {
            first.setActive(true);
            first.setSubtitle("You order has been successfully placed");
        }
        else if (status_code.equals("2"))
        {
            first.setActive(false);
            first.setSubtitle("");
            second.setActive(true);
            second.setSubtitle("Your order has been confirmed by the restaurant");
        }
        else if (status_code.equals("3"))
        {
            first.setActive(false);
            first.setSubtitle("");
            second.setActive(false);
            second.setSubtitle("");
            third.setActive(true);
            third.setSubtitle("Your order is waiting for pickup by delivery boy");
        }
        else if (status_code.equals("4"))
        {
            first.setActive(false);
            first.setSubtitle("");
            second.setActive(false);
            second.setSubtitle("");
            third.setActive(false);
            third.setSubtitle("");
            fourth.setActive(true);
            fourth.setSubtitle("Your order has been picked up and is out for delivery");
        }
        else
        {
            first.setActive(false);
            first.setSubtitle("");
            second.setActive(false);
            second.setSubtitle("");
            third.setActive(false);
            third.setSubtitle("");
            fourth.setActive(false);
            fourth.setSubtitle("");
            fifth.setActive(true);
            fifth.setSubtitle("Your order has been delivered successfully");
        }

    }

    private void loadFoodList(String order_number) {

        Query query = order.equalTo(order_number);

        FirebaseRecyclerOptions<Food> options = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(query,Food.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder viewHolder, int position, @NonNull Food model) {
                viewHolder.food_name.setText(model.getName());
                viewHolder.food_price.setText(String.format("₹ %s",model.getPrice().toString()));
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.food_image);
            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item,parent,false);
                return new FoodViewHolder(itemView);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }
}
