package in.mangoo.mangooonlinefooddelivery;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import in.mangoo.mangooonlinefooddelivery.Model.Order;
import in.mangoo.mangooonlinefooddelivery.Model.Request;
import in.mangoo.mangooonlinefooddelivery.ViewHolder.OrderViewHolder;
import info.hoang8f.widget.FButton;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OrderDetail extends AppCompatActivity {

    public RecyclerView itemView,qtyView,priceView;
    public RecyclerView.LayoutManager itemLm,qtyLm,priceLm;

    TextView paid,order,payMethod;
    FButton homeBtn;
    ImageView backBtn;

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
        backBtn = (ImageView)findViewById(R.id.backBtn);

        Intent intent = getIntent();
        String total = intent.getStringExtra("Total");
        String orderid = intent.getStringExtra("OrderId");
        String method  = intent.getStringExtra("Method");
        paid.setText(total);
        order.setText(orderid);
        payMethod.setText(method);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetail.this,Home.class);
                startActivity(intent);
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetail.this,Home.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
    }
}
