package in.mangoo.mangooonlinefooddelivery;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import in.mangoo.mangooonlinefooddelivery.Model.Coupon;
import in.mangoo.mangooonlinefooddelivery.Model.DataMessage;
import in.mangoo.mangooonlinefooddelivery.Model.MyResponse;
import in.mangoo.mangooonlinefooddelivery.Model.Order;
import in.mangoo.mangooonlinefooddelivery.Model.Request;
import in.mangoo.mangooonlinefooddelivery.Model.Restaurant;
import in.mangoo.mangooonlinefooddelivery.Model.RestaurantID;
import in.mangoo.mangooonlinefooddelivery.Model.Token;
import in.mangoo.mangooonlinefooddelivery.Model.User;
import in.mangoo.mangooonlinefooddelivery.Remote.APIService;
import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static in.mangoo.mangooonlinefooddelivery.Notification.CHANNEL_1_ID;

public class PlaceOrder extends AppCompatActivity implements PaymentResultListener {

    CardView onlineCard, walletCard, codCard;
    ImageView check1, check2, check3, coupon_delete, coupon_select;
    RelativeLayout apply_coupon_rlyt;

    TextView currentAddress, edtComment, txtTotalPrice, edtAmount, edtDelivery, edtDiscount,coupon_detail;
    FButton pay;

    Geocoder geocoder;
    List<Address> addressList;

    FusedLocationProviderClient client;
    GPSTracker gps;

    RequestQueue requestQueue;
    APIService mService;

    String coupon_delivery="";
    String lati, longi;
    boolean coupon_applied = false;
    Context mContext;
    double latitude, longitude;

    Place shippingAddress;
    PlaceAutocompleteFragment edtAddress;

    List<Order> cart = new ArrayList<>();
    FirebaseDatabase database;
    DatabaseReference requests;

    NotificationManagerCompat notificationManager;

    ImageView backBtn;
    String coordinates,addr;

    int payamount = 0,discount=0;
    int couponRequestCode = 1234;
    Locale locale = new Locale("en", "IN");
    NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);


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

        setContentView(R.layout.activity_place_order);

        mContext = this;
        mService = Common.getFCMService();

        geocoder = new Geocoder(this, Locale.getDefault());

        notificationManager = NotificationManagerCompat.from(this);

        client = LocationServices.getFusedLocationProviderClient(this);

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        onlineCard = (CardView) findViewById(R.id.onlineCard);
        walletCard = (CardView) findViewById(R.id.walletCard);
        codCard = (CardView) findViewById(R.id.codCard);

        check1 = (ImageView) findViewById(R.id.check1);
        check2 = (ImageView) findViewById(R.id.check2);
        check3 = (ImageView) findViewById(R.id.check3);

        coupon_select = findViewById(R.id.coupon_next_img);
        coupon_delete = findViewById(R.id.cancel_coupon_img);
        coupon_detail = findViewById(R.id.coupon_detail);

        apply_coupon_rlyt = findViewById(R.id.apply_coupon_rlyt);

        currentAddress = (TextView) findViewById(R.id.curAdd);
        //edtAddress = (TextView)findViewById(R.id.edtAddress);
        edtComment = (TextView) findViewById(R.id.edtComment);
        pay = (FButton) findViewById(R.id.pay);
        txtTotalPrice = (TextView) findViewById(R.id.total);
        edtAmount = (TextView) findViewById(R.id.edtAmount);
        edtDelivery = (TextView) findViewById(R.id.edtDelivery);
        edtDiscount = (TextView) findViewById(R.id.edtDiscount);

        backBtn = (ImageView) findViewById(R.id.backBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlaceOrder.this, Cart.class);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        final String total = intent.getStringExtra("Total");
        edtAmount.setText(total);
        coupon_applied = intent.getBooleanExtra("Status",false);
        if(coupon_applied) {
            coupon_delete.setVisibility(View.VISIBLE);
            coupon_select.setVisibility(View.GONE);
            coupon_delivery = intent.getStringExtra("Delivery_Coupon");
            coupon_detail.setText("Coupon Applied for Free Delivery :"+coupon_delivery);
        }

        Locale locale = new Locale("en", "IN");
        final NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        onlineCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check1.setVisibility(View.VISIBLE);
                check2.setVisibility(View.INVISIBLE);
                check3.setVisibility(View.INVISIBLE);
            }
        });

        walletCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check2.setVisibility(View.VISIBLE);
                check1.setVisibility(View.INVISIBLE);
                check3.setVisibility(View.INVISIBLE);
            }
        });

        codCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check3.setVisibility(View.VISIBLE);
                check1.setVisibility(View.INVISIBLE);
                check2.setVisibility(View.INVISIBLE);
            }
        });

        apply_coupon_rlyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!coupon_applied) {
                    Intent intent1 = new Intent(PlaceOrder.this,CouponsActivity.class);
                    intent1.putExtra("apply",true);
                    intent1.putExtra("total",payamount);
                    startActivityForResult(intent1,couponRequestCode);
                }
            }
        });

        coupon_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coupon_detail.setText("Apply Coupon");
                txtTotalPrice.setText(fmt.format(payamount));
                edtDiscount.setText(fmt.format(discount));
                pay.setText("PAY ₹ " + payamount);
                coupon_delete.setVisibility(View.GONE);
                coupon_select.setVisibility(View.VISIBLE);
                coupon_applied = false;
            }
        });

        edtAddress = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        edtAddress.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);

        ((EditText) edtAddress.getView().findViewById(R.id.place_autocomplete_search_input)).setHint("Enter your address");
        ((EditText) edtAddress.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(18);
        ((EditText) edtAddress.getView().findViewById(R.id.place_autocomplete_search_input)).setText(Common.currentUser.getHomeAddress());

        edtAddress.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                shippingAddress = place;
            }

            @Override
            public void onError(Status status) {
                Log.e("ERROR", status.getStatusMessage());
            }
        });


        currentAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });

        addr = Common.currentUser.getHomeAddress() + " Karaikal ";

        int deliveryCharge = 0,b=0;
        deliveryCharge = calculateDelivery();
        edtDelivery.setText(fmt.format(deliveryCharge));
        edtDiscount.setText(fmt.format(b));

        try {
            payamount += fmt.parse(edtAmount.getText().toString()).intValue() + fmt.parse(edtDelivery.getText().toString()).intValue() + fmt.parse(edtDiscount.getText().toString()).intValue();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        txtTotalPrice.setText(fmt.format(payamount));
        pay.setText("PAY ₹ " + payamount);

        pay.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick (View v){

                if (((EditText) edtAddress.getView().findViewById(R.id.place_autocomplete_search_input)).getText().toString().isEmpty()) {
                    Toast.makeText(PlaceOrder.this, "Please enter an Address", Toast.LENGTH_SHORT).show();
                }
                if ((check1.getVisibility() == View.INVISIBLE) && (check2.getVisibility() == View.INVISIBLE) && (check3.getVisibility() == View.INVISIBLE)) {
                    Toast.makeText(PlaceOrder.this, "Please choose a payment method", Toast.LENGTH_SHORT).show();
                }
                //Payment through RazorPay
                else if (check1.getVisibility() == View.VISIBLE &&
                        !((EditText) edtAddress.getView().findViewById(R.id.place_autocomplete_search_input)).getText().toString().isEmpty()) {
                    // startPayment();
                    Toast.makeText(PlaceOrder.this, "This Payment method is currently unavailable", Toast.LENGTH_SHORT).show();
                }

                // COD Payment
                else if (check3.getVisibility() == View.VISIBLE &&
                        !((EditText) edtAddress.getView().findViewById(R.id.place_autocomplete_search_input)).getText().toString().isEmpty()) {

                    cart = new Database(PlaceOrder.this).getCarts(Common.currentUser.getPhone());

                    Geocoder geocoder = new Geocoder(PlaceOrder.this, Locale.getDefault());
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocationName(addr, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addresses.get(0);
                    double longitude = address.getLongitude();
                    double latitude = address.getLatitude();
                    getAddress(latitude,longitude);

                    Request request = new Request(
                            Common.currentUser.getPhone(),
                            Common.currentUser.getName(),
                            ((EditText) edtAddress.getView().findViewById(R.id.place_autocomplete_search_input)).getText().toString(),
                            "COD",
                            txtTotalPrice.getText().toString(),
                            "1",
                            edtComment.getText().toString(),
                            String.format("%s, %s",latitude,longitude),
                            "Not assigned",
                            cart,
                            coupon_delivery
                    );

                    String order_number = String.valueOf(System.currentTimeMillis());
                    requests.child(order_number).setValue(request);
                    //sendNotificationOrder(order_number);
                    notifyUser(v,order_number);

                    new Database(getBaseContext()).cleanCart(Common.currentUser.getPhone());


                    Toast.makeText(PlaceOrder.this, "Order Placed Successful", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(PlaceOrder.this, OrderDetail.class);
                    intent.putExtra("Total", txtTotalPrice.getText().toString());
                    //intent.putExtra("OrderId", order_number);
                    intent.putExtra("Method", "COD");
                    startActivity(intent);
                }

                // Mangoo Wallet Payment
                else if (check2.getVisibility() == View.VISIBLE &&
                        !((EditText) edtAddress.getView().findViewById(R.id.place_autocomplete_search_input)).getText().toString().isEmpty()) {
                    cart = new Database(PlaceOrder.this).getCarts(Common.currentUser.getPhone());
                    double amount = 0;
                    try {
                        Locale locale = new Locale("en", "IN");
                        amount = Common.formatCurrrency(txtTotalPrice.getText().toString(), locale).doubleValue();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (Double.parseDouble(Common.currentUser.getBalance().toString()) >= amount) {
                        Request request = new Request(
                                Common.currentUser.getPhone(),
                                Common.currentUser.getName(),
                                ((EditText) edtAddress.getView().findViewById(R.id.place_autocomplete_search_input)).getText().toString(),
                                "Mangoo Wallet",
                                txtTotalPrice.getText().toString(),
                                "1",
                                edtComment.getText().toString(),
                                coordinates,
                                "Not assigned",
                                cart,
                                coupon_delivery
                        );

                        final String order_number = String.valueOf(System.currentTimeMillis());
                        requests.child(order_number).setValue(request);

                        new Database(getBaseContext()).cleanCart(Common.currentUser.getPhone());

                        double balance = Double.parseDouble(Common.currentUser.getBalance().toString()) - amount;
                        Map<String, Object> update_balance = new HashMap<>();
                        update_balance.put("balance", balance);

                        FirebaseDatabase.getInstance()
                                .getReference("User")
                                .child(Common.currentUser.getPhone())
                                .updateChildren(update_balance)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseDatabase.getInstance()
                                                    .getReference("User")
                                                    .child(Common.currentUser.getPhone())
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            Common.currentUser = dataSnapshot.getValue(User.class);
                                                            //sendNotificationOrder(order_number);
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                        }
                                    }
                                });

                        Intent intent = new Intent(PlaceOrder.this, OrderDetail.class);
                        intent.putExtra("Total", txtTotalPrice.getText().toString());
                        intent.putExtra("OrderId", order_number);
                        intent.putExtra("Method", "Mangoo Wallet");
                        Toast.makeText(PlaceOrder.this, "Payment Successful", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PlaceOrder.this, "You have insufficient wallet balance. Please choose another payment method", Toast.LENGTH_SHORT).show();
                    }
                }
            }

    });
    }

    public void notifyUser(View v,String order_number) {

        String title = "Mangoo";
        String message = "Your order #"+order_number+" has been successfully placed ";

        Intent activityIntent = new Intent(this,OrderStatus.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,activityIntent,0);

        android.app.Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle(title)
                .setContentText(message)
                .setColor(Color.RED)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message)
                        .setBigContentTitle(title))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(1, notification);
    }

    public int calculateDelivery() {

        Geocoder geocoder = new Geocoder(PlaceOrder.this, Locale.getDefault());
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

        delCharge = ( (new Database(PlaceOrder.this).getRestaurantCount(Common.currentUser.getPhone()) - 1) * 10) + rate;

        return delCharge;

    }

    public void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(PlaceOrder.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(1);
            Log.v("TAG", "Address" + add);
            Toast.makeText(this, "Address=>" + add, Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0

                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    gps = new GPSTracker(mContext, PlaceOrder.this);

                    // Check if GPS enabled

                    if (gps.canGetLocation()) {

                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();

                        // \n is for new line

                        Toast.makeText(getApplicationContext(),
                                "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                    } else {
                        // Can't get location.
                        // GPS or network is not enabled.
                        // Ask user to enable GPS/network in settings
                        gps.showSettingsAlert();
                    }

                } else {

                    // permission denied, boo! Disable the

                    // functionality that depends on this permission.
                    Toast.makeText(mContext, "You need to grant permission",

                            Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == couponRequestCode){
            if (resultCode == Activity.RESULT_OK){
                coupon_applied = true;
                coupon_delete.setVisibility(View.VISIBLE);
                coupon_select.setVisibility(View.GONE);
                if (data != null) {
                    int tot = 0,disc=0;
                    Coupon coupon = (Coupon) data.getSerializableExtra("coupon");
                    if(coupon.getType().equals("Price cut")) {
                        tot = payamount - Integer.parseInt(coupon.getOff());
                        disc = Integer.parseInt(coupon.getOff());
                    }
                    else if(coupon.getType().equals("Percentage off"))
                    {
                        tot = payamount -((payamount*Integer.parseInt(coupon.getOff()))/100);
                        disc = Integer.parseInt(coupon.getOff());
                    }
                    else if(coupon.getType().equals("Free Delivery"))
                    {
                        tot=payamount;
                        disc=0;
                    }
                    coupon_detail.setText("Coupon Used :"+coupon.getCode());
                    coupon_delivery=coupon.getCode();
                    txtTotalPrice.setText(fmt.format(tot));
                    edtDiscount.setText(fmt.format(disc));
                    pay.setText("PAY ₹ " + tot);
                }
            }
        }

    }

    private void startPayment() {

        Checkout checkout = new Checkout();
        checkout.setImage(R.mipmap.ic_launcher);

        final Activity activity = this;

        try {
            JSONObject options = new JSONObject();
            options.put("Description", "Order #123455");
            options.put("currency", "INR");
            options.put("amount", payamount * 100);
            checkout.open(activity, options);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String s) {

        cart = new Database(this).getCarts(Common.currentUser.getPhone());

        Request request = new Request(
                Common.currentUser.getPhone(),
                Common.currentUser.getName(),
                ((EditText) edtAddress.getView().findViewById(R.id.place_autocomplete_search_input)).getText().toString(),
                "RazorPay",
                txtTotalPrice.getText().toString(),
                "1",
                edtComment.getText().toString(),
                coordinates,
                "Not assigned",
                cart,
                coupon_delivery
        );

        String order_number = String.valueOf(System.currentTimeMillis());
        requests.child(order_number).setValue(request);

        new Database(getBaseContext()).cleanCart(Common.currentUser.getPhone());
        //sendNotificationOrder(order_number);

        Toast.makeText(PlaceOrder.this, "Payment Successful", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(PlaceOrder.this, OrderDetail.class);
        intent.putExtra("Total", txtTotalPrice.getText().toString());
        intent.putExtra("OrderId", order_number);
        intent.putExtra("Method", "RazorPay");
        startActivity(intent);

    }

    @Override
    public void onPaymentError(int i, String s) {

        Toast.makeText(PlaceOrder.this, "Payment Failed", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(PlaceOrder.this, OrderFailed.class);
        intent.putExtra("Method", "RazorPay");
        startActivity(intent);

    }

    private void sendNotificationOrder(final String order_number) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query data = tokens.orderByChild("isServerToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    Token serverToken =postSnapshot.getValue(Token.class);
                    Map<String,String> dataSend = new HashMap<>();
                    dataSend.put("title","Mangoo");
                    dataSend.put("message","You have new order"+order_number);
                    DataMessage dataMessage = new DataMessage(serverToken.getToken(),dataSend);

                    String test = new Gson().toJson(dataMessage);
                    Log.d("Context",test);

                    mService.sendNotification(dataMessage)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {

                                        if (response.body().success == 1) {
                                            Toast.makeText(PlaceOrder.this, "Thank you, Order Placed", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(PlaceOrder.this, "Failed !!", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.e("ERROR",t.getMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PlaceOrder.this, Cart.class);
        startActivity(intent);

    }

    public void getCurrentLocation() {

        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION)

                != PackageManager.PERMISSION_GRANTED

                && ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION)

                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PlaceOrder.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {

            gps = new GPSTracker(mContext, PlaceOrder.this);
            // Check if GPS enabled
            if (gps.canGetLocation()) {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                // \n is for new line
                Toast.makeText(getApplicationContext(),
                        "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                lati = Double.toString(latitude);
                longi = Double.toString(longitude);

                try {
                    addressList = geocoder.getFromLocation(latitude, longitude, 1);

                    String addressStr = addressList.get(0).getAddressLine(0);
                    String areaStr = addressList.get(0).getLocality();
                    String cityStr = addressList.get(0).getAdminArea();
                    String countryStr = addressList.get(0).getCountryName();
                    String postalcodeStr = addressList.get(0).getPostalCode();

                    String fullAddress = addressStr + ", " + areaStr + ", " + cityStr + ", " + countryStr + ", " + postalcodeStr;

                    edtAddress.setText(fullAddress);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // Can't get location.
                gps.showSettingsAlert();

            }
        }
    }

}






