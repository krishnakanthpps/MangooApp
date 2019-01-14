package in.mangoo.mangooonlinefooddelivery;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.accountkit.AccountKit;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabSelectListener;

import in.mangoo.mangooonlinefooddelivery.Common.Common;
import in.mangoo.mangooonlinefooddelivery.Database.Database;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Profile extends AppCompatActivity {

    BottomBar bottomBar;
    BottomBarTab cart_badge;
    LinearLayout editProfile,myOrders,myWallet,Offer,About,logOut;
    TextView userName,userPhone;

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

        setContentView(R.layout.activity_profile);

        editProfile = (LinearLayout)findViewById(R.id.editProfile);
        myOrders = (LinearLayout)findViewById(R.id.myOrders);
        myWallet = (LinearLayout)findViewById(R.id.wallet);
        Offer = (LinearLayout)findViewById(R.id.Offers);
        About = (LinearLayout)findViewById(R.id.Terms);
        logOut = (LinearLayout)findViewById(R.id.logOut);
        userName = (TextView)findViewById(R.id.userName);
        userPhone = (TextView)findViewById(R.id.userPhone);

        userPhone.setText(Common.currentUser.getPhone());
        userName.setText(Common.currentUser.getName());

        myOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this,OrderStatus.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this,EditProfile.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountKit.logOut();

                Intent intent = new Intent(Profile.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        Offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this,CouponsActivity.class);
                startActivity(intent);
            }
        });

        bottomBar = (BottomBar)findViewById(R.id.bottom_navbar);
        bottomBar.setDefaultTab(R.id.tab_profile);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int id) {
                if (id == R.id.tab_menu) {

                    Intent intent = new Intent(Profile.this,RestaurantList.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);


                } else if (id == R.id.tab_search) {


                } else if (id == R.id.tab_cart) {

                    Intent intent = new Intent(Profile.this,Cart.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);


                } else if (id == R.id.tab_profile) {

                }

            }
        });
        cart_badge = bottomBar.getTabWithId(R.id.tab_cart);
        cart_badge.setBadgeCount(new Database(this).getCountCart(Common.currentUser.getPhone()));
    }
}
