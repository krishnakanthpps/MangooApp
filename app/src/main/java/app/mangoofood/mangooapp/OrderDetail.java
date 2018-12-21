package app.mangoofood.mangooapp;

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
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import app.mangoofood.mangooapp.Common.Common;
import app.mangoofood.mangooapp.Database.Database;
import app.mangoofood.mangooapp.Interface.ItemClickListener;
import app.mangoofood.mangooapp.Model.Food;
import app.mangoofood.mangooapp.Model.Order;
import app.mangoofood.mangooapp.Model.Request;
import app.mangoofood.mangooapp.ViewHolder.CartAdapter;
import app.mangoofood.mangooapp.ViewHolder.FoodViewHolder;
import app.mangoofood.mangooapp.ViewHolder.OrderViewHolder;
import info.hoang8f.widget.FButton;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OrderDetail extends AppCompatActivity {

    public RecyclerView itemView,qtyView,priceView;
    public RecyclerView.LayoutManager itemLm,qtyLm,priceLm;

    TextView paid,order,payMethod;
    FButton homeBtn;

    FirebaseRecyclerAdapter<Request,OrderViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference requests;
    List<Order> cart = new ArrayList<>();

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

        setContentView(R.layout.activity_order_detail);

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        paid = (TextView)findViewById(R.id.paid);
        order = (TextView)findViewById(R.id.order);
        payMethod = (TextView)findViewById(R.id.paymentMethod);
        homeBtn = (FButton) findViewById(R.id.home);

        Intent intent = getIntent();
        String total = intent.getStringExtra("Total");
        String orderid = intent.getStringExtra("OrderId");
        String method  = intent.getStringExtra("Method");
        paid.setText(total);
        order.setText(orderid);
        payMethod.setText(method);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetail.this,Home.class);
                startActivity(intent);
            }
        });

    }

}