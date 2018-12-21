package app.mangoofood.mangooapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.SnackBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.Inflater;

import app.mangoofood.mangooapp.Common.Common;
import app.mangoofood.mangooapp.Database.Database;
import app.mangoofood.mangooapp.Helper.RecyclerItemTouchHelper;
import app.mangoofood.mangooapp.Interface.RecyclerItemTouchHelperListener;
import app.mangoofood.mangooapp.Model.MyResponse;
import app.mangoofood.mangooapp.Model.Order;
import app.mangoofood.mangooapp.Model.Request;
import app.mangoofood.mangooapp.Model.Sender;
import app.mangoofood.mangooapp.Model.Token;
import app.mangoofood.mangooapp.Model.User;
import app.mangoofood.mangooapp.Remote.APIService;
import app.mangoofood.mangooapp.Remote.IGoogleService;
import app.mangoofood.mangooapp.ViewHolder.CartAdapter;
import app.mangoofood.mangooapp.ViewHolder.CartViewHolder;
import info.hoang8f.widget.FButton;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
