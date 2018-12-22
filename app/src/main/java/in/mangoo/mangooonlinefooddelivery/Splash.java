package in.mangoo.mangooonlinefooddelivery;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import in.mangoo.mangooonlinefooddelivery.Common.Common;
import in.mangoo.mangooonlinefooddelivery.Model.User;

public class Splash extends AppCompatActivity {

    private static int SPLASH_TIME = 100;

    FirebaseDatabase database;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        AccountKit.initialize(this);

        database = FirebaseDatabase.getInstance();
        users = database.getReference("User");

        //Code to start timer and take action after the timer ends
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do any action here. Now we are moving to next page
                if (AccountKit.getCurrentAccessToken() != null)
                {

                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(Account account) {
                            users.child(account.getPhoneNumber().toString())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            User localUser = dataSnapshot.getValue(User.class);
                                            Intent homeIntent = new Intent(Splash.this, RestaurantList.class);
                                            Common.currentUser = localUser;
                                            startActivity(homeIntent);
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }

                        @Override
                        public void onError(AccountKitError error) {

                        }
                    });
                }
                else {
                    Intent mainIntent = new Intent(Splash.this, Onboard.class);
                    startActivity(mainIntent);
                }
                /* This 'finish()' is for exiting the app when back button pressed
                 *  from Home page which is ActivityHome
                 */
               // finish();
            }
        }, SPLASH_TIME);


    }

    private void login(final String phone, final String pwd) {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");
        if (Common.isConnectedToInternet(getBaseContext())) {


            final ProgressDialog mDialog = new ProgressDialog(Splash.this);
            mDialog.setMessage("Logging in. .. .");
            mDialog.show();

            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(phone).exists()) {
                        mDialog.dismiss();
                        User user = dataSnapshot.child(phone).getValue(User.class);
                        user.setPhone(phone);
                        if (user.getPassword().equals(pwd)){
                            {
                                Intent homeIntent = new Intent(Splash.this, RestaurantList.class);
                                Common.currentUser = user;
                                startActivity(homeIntent);
                                finish();
                            }
                        } else {
                            Toast.makeText(Splash.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        mDialog.dismiss();
                        Toast.makeText(Splash.this, "User not exist", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else
        {
            Toast.makeText(Splash.this, "Check your Internet Connection.", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
