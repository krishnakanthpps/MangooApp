package in.mangoo.mangooonlinefooddelivery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import in.mangoo.mangooonlinefooddelivery.Common.Common;
import in.mangoo.mangooonlinefooddelivery.Database.Database;
import in.mangoo.mangooonlinefooddelivery.Helper.RecyclerItemTouchHelper;
import in.mangoo.mangooonlinefooddelivery.Interface.RecyclerItemTouchHelperListener;
import in.mangoo.mangooonlinefooddelivery.Model.Order;
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

    FButton btnPlace;

    public TextView edtAmount,edtDelivery,edtDiscount;
    LinearLayout empty;
    ScrollView scroll;
    ImageView backBtn;

    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;

    IGoogleService mGoogleMapService;
    APIService mService;
    RelativeLayout rootLayout;

    int payamount = 0;
    public int total = 0;


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
        scroll = (ScrollView)findViewById(R.id.scroll);
        backBtn = (ImageView)findViewById(R.id.backBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Cart.this,Home.class);
                startActivity(intent);
            }
        });

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(cart.size()>0) {
                    Intent intent = new Intent(Cart.this, PlaceOrder.class);
                    intent.putExtra("Total", edtAmount.getText().toString());
                    startActivity(intent);
                }
                else
                    Toast.makeText(Cart.this, "Your cart is empty.", Toast.LENGTH_SHORT).show();
            }
        });
        
        loadListFood();
        
    }

    private void loadListFood() {

        cart = new Database(this).getCarts(Common.currentUser.getPhone());
        adapter = new CartAdapter(cart,this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        //int total = 0;
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
            edtDelivery.setText(fmt.format(0));
        }
        else
        {
            edtDelivery.setText(fmt.format(50));
        }

        try {
            payamount += fmt.parse(edtAmount.getText().toString()).intValue() + fmt.parse(edtDiscount.getText().toString()).intValue()
                    + fmt.parse(edtDelivery.getText().toString()).intValue();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        txtTotalPrice.setText(fmt.format(payamount));

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
        for(Order item:cart)
            new Database(this).addToCart(item);
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

            int a = 50,b =0,payamount = 0;
            edtDelivery.setText(fmt.format(a));
            edtDiscount.setText(fmt.format(b));

            try {
                payamount += fmt.parse(edtAmount.getText().toString()).intValue() + fmt.parse(edtDiscount.getText().toString()).intValue()
                        + fmt.parse(edtDelivery.getText().toString()).intValue();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            txtTotalPrice.setText(fmt.format(payamount));
            if ((cart.size() == 0 )&& (edtAmount.getText().toString().equals("₹ 0.00")))
        {
            edtDelivery.setText(fmt.format("0"));
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

                    edtAmount.setText(fmt.format(total));

                    int a = 50,b =0, payamount = 0;
                    edtDelivery.setText(fmt.format(a));
                    edtDiscount.setText(fmt.format(b));

                    try {
                        payamount += fmt.parse(edtAmount.getText().toString()).intValue() + fmt.parse(edtDiscount.getText().toString()).intValue()
                                + fmt.parse(edtDelivery.getText().toString()).intValue();
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
}