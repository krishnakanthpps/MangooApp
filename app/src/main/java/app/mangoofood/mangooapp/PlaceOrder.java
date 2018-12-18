package app.mangoofood.mangooapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import org.w3c.dom.Text;

import java.io.IOError;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class PlaceOrder extends AppCompatActivity {

    CardView onlineCard,walletCard,codCard;
    ImageView check1,check2,check3;

    TextView currentAddress,edtComment,pay;

    Geocoder geocoder;
    List<Address> addressList;

    FusedLocationProviderClient client;
    GPSTracker gps;

    RequestQueue requestQueue;

    String lati,longi;
    Context mContext;

    double latitude,longitude;

    Place shippingAddress;
    PlaceAutocompleteFragment edtAddress;


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

        geocoder = new Geocoder(this, Locale.getDefault());

        client = LocationServices.getFusedLocationProviderClient(this);

        onlineCard = (CardView)findViewById(R.id.onlineCard);
        walletCard = (CardView)findViewById(R.id.walletCard);
        codCard = (CardView)findViewById(R.id.codCard);

        check1 = (ImageView)findViewById(R.id.check1);
        check2 = (ImageView)findViewById(R.id.check2);
        check3 = (ImageView)findViewById(R.id.check3);

        currentAddress = (TextView)findViewById(R.id.curAdd);
        //edtAddress = (TextView)findViewById(R.id.edtAddress);
        edtComment = (TextView)findViewById(R.id.edtComment);
        pay = (TextView)findViewById(R.id.pay);

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

        edtAddress = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        edtAddress.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);

        ((EditText)edtAddress.getView().findViewById(R.id.place_autocomplete_search_input)).setHint("Enter your address");
        ((EditText)edtAddress.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(18);

        edtAddress.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                shippingAddress = place;
            }

            @Override
            public void onError(Status status) {
                Log.e("ERROR",status.getStatusMessage());
            }
        });

        currentAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        });

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LatLng latLng = edtAddress.getLatLng();

                if (TextUtils.isEmpty(edtAddress))
                {
                    Toast.makeText(PlaceOrder.this, "Enter an Address", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override    public void onRequestPermissionsResult(int requestCode, String[] permissions,

                                                        int[] grantResults) {
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


}
