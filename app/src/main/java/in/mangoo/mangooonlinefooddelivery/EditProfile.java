package in.mangoo.mangooonlinefooddelivery;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.HashMap;
import java.util.Map;

import in.mangoo.mangooonlinefooddelivery.Common.Common;
import in.mangoo.mangooonlinefooddelivery.Database.Database;
import info.hoang8f.widget.FButton;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EditProfile extends AppCompatActivity {

    FButton save,cancel;
    EditText userName,homeAddr;
    ImageView backBtn;
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

        setContentView(R.layout.activity_edit_profile);

        save = (FButton)findViewById(R.id.Save);
        cancel = (FButton)findViewById(R.id.Cancel);
        userName = (EditText)findViewById(R.id.userName);
        homeAddr = (EditText)findViewById(R.id.homeAddr);
        backBtn = (ImageView)findViewById(R.id.backBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfile.this,Profile.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });
        bottomBar = (BottomBar)findViewById(R.id.bottom_navbar);
        bottomBar.setDefaultTab(R.id.tab_profile);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int id) {
                if (id == R.id.tab_menu) {

                    Intent intent = new Intent(EditProfile.this,RestaurantList.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                } else if (id == R.id.tab_search) {
                    Intent intent = new Intent(EditProfile.this,FavouritesActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                } else if (id == R.id.tab_cart) {
                    Intent intent = new Intent(EditProfile.this,Cart.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                } else if (id == R.id.tab_profile) {

                }
            }
        });
        cart_badge = bottomBar.getTabWithId(R.id.tab_cart);
        cart_badge.setBadgeCount(new Database(this).getCountCart(Common.currentUser.getPhone()));


        userName.setText(Common.currentUser.getName());
        homeAddr.setText(Common.currentUser.getHomeAddress());

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> update_name = new HashMap<>();
                update_name.put("name",userName.getText().toString());

                Common.currentUser.setName(userName.getText().toString());
                Common.currentUser.setHomeAddress(homeAddr.getText().toString());

                FirebaseDatabase.getInstance()
                        .getReference("User")
                        .child(Common.currentUser.getPhone())
                        .setValue(Common.currentUser)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Toast.makeText(EditProfile.this, "Profile updated successfully !!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(EditProfile.this,Profile.class);
                                startActivity(intent);
                            }


                        });
           }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfile.this,Profile.class);
                startActivity(intent);
            }
        });
    }
}
