package in.mangoo.mangooonlinefooddelivery;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabSelectListener;

import java.io.Serializable;
import java.util.ArrayList;

import in.mangoo.mangooonlinefooddelivery.Common.Common;
import in.mangoo.mangooonlinefooddelivery.Database.Database;
import in.mangoo.mangooonlinefooddelivery.Interface.ItemClickListener;
import in.mangoo.mangooonlinefooddelivery.Model.Order;
import in.mangoo.mangooonlinefooddelivery.Model.Request;
import in.mangoo.mangooonlinefooddelivery.ViewHolder.OrderViewHolder;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OrderStatus extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request,OrderViewHolder> adapter;

    ImageView backBtn;

    FirebaseDatabase database;
    DatabaseReference requests;

    BottomBar bottomBar;
    BottomBarTab cart_badge;

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

        setContentView(R.layout.activity_order_status);

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        recyclerView = (RecyclerView)findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        backBtn = (ImageView)findViewById(R.id.backBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderStatus.this,Profile.class);
                startActivity(intent);
            }
        });

        bottomBar = (BottomBar)findViewById(R.id.bottom_navbar);
        bottomBar.setDefaultTab(R.id.tab_profile);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int id) {
                if (id == R.id.tab_menu) {

                    Intent intent = new Intent(OrderStatus.this,RestaurantList.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                } else if (id == R.id.tab_search) {
                    Intent intent = new Intent(OrderStatus.this,FavouritesActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                } else if (id == R.id.tab_cart) {
                    Intent intent = new Intent(OrderStatus.this,Cart.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                } else if (id == R.id.tab_profile) {

                }
            }
        });
        cart_badge = bottomBar.getTabWithId(R.id.tab_cart);
        cart_badge.setBadgeCount(new Database(this).getCountCart(Common.currentUser.getPhone()));

        loadOrders(Common.currentUser.getPhone());

    }

    private void loadOrders(final String phone) {

        Query getOrderByUser = requests.orderByChild("phone")
                .equalTo(phone);

        FirebaseRecyclerOptions<Request> orderOptions = new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(getOrderByUser,Request.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(orderOptions) {

            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder viewHolder,final int position, @NonNull final Request model) {

                viewHolder.txtOrderId.setText("Order #" + adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(convertCodetoStatus(model.getStatus()));
                viewHolder.txtOrderTotal.setText(model.getTotal());
                viewHolder.txtOrderDate.setText(Common.getDate(Long.parseLong(adapter.getRef(position).getKey())));
                viewHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(adapter.getItem(position).getStatus().equals("0"))
                            deleteOrder(adapter.getRef(position).getKey());
                        else if (adapter.getItemCount() == 0)
                        {
                            Intent intent = new Intent(OrderStatus.this,RestaurantList.class);
                            startActivity(intent);
                        }
                        else
                            Toast.makeText(OrderStatus.this, "This order cannot be deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent intent = new Intent(OrderStatus.this,TrackOrder.class);
                        intent.putExtra("OrderNo",adapter.getRef(position).getKey());
                        intent.putExtra("Address",model.getAddress());
                        intent.putExtra("DeliveryBoy",model.getDeliverBoy());
                        intent.putExtra("Total",model.getTotal());
                        intent.putExtra("Status",model.getStatus());
                        intent.putExtra("Payment",model.getPaymentMethod());
                        //Bundle bundle = new Bundle();
                        //bundle.putSerializable("Foods",(Serializable)model.getFoods());
                        //intent.putExtra("Foods",bundle);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_layout,parent,false);
                return new OrderViewHolder(itemView);

            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private void deleteOrder(final String key) {
        requests.child(key)
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(OrderStatus.this, new StringBuilder("Order")
                        .append(key)
                        .append(" has been deleted").toString(), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OrderStatus.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter!=null)
            adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders(Common.currentUser.getPhone());
    }

    private String convertCodetoStatus(String status) {

        if(status.equals("1"))
            return "Placed";
        else if(status.equals("2"))
            return "Confirmed";
        else if(status.equals("3"))
            return "Cooking";
        else if(status.equals("4"))
            return "On the Way";
        else
            return "Shipped";

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(OrderStatus.this,Profile.class);
        startActivity(intent);
    }
}
