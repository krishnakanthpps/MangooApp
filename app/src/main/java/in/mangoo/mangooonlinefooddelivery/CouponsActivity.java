package in.mangoo.mangooonlinefooddelivery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import in.mangoo.mangooonlinefooddelivery.Common.Common;
import in.mangoo.mangooonlinefooddelivery.Model.Coupon;
import in.mangoo.mangooonlinefooddelivery.Model.Request;
import in.mangoo.mangooonlinefooddelivery.ViewHolder.CouponViewHolder;


public class CouponsActivity extends AppCompatActivity {

    boolean show_apply = false;
    boolean used=false;
    RecyclerView recyclerView;
    int payAmount = 0;

    FirebaseRecyclerOptions<Coupon> options = new FirebaseRecyclerOptions.Builder<Coupon>()
            .setQuery(FirebaseDatabase.getInstance()
                            .getReference()
                            .child("coupons")
                    ,Coupon.class)
            .build();

    FirebaseRecyclerAdapter<Coupon,CouponViewHolder> adapter = new FirebaseRecyclerAdapter<Coupon, CouponViewHolder>(options) {
        @Override
        public CouponViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_coupon_item, parent, false);
            return new CouponViewHolder(itemView);

        }

        @Override
        protected void onBindViewHolder(@NonNull final CouponViewHolder viewHolder, int position,
                                        @NonNull final Coupon model) {
            viewHolder.code.setText(model.getCode());
            viewHolder.date.setText(model.getExp());
            viewHolder.desc.setText(model.getDesc());
            if (show_apply){
                viewHolder.apply.setVisibility(View.VISIBLE);
            }
            viewHolder.apply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    used=false;
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Requests");
                    Query query = databaseReference.orderByChild("phone").equalTo(Common.currentUser.getPhone());
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot data:dataSnapshot.getChildren())
                            {
                                Request request = data.getValue(Request.class);
                                String code = model.getCode();
                                String coup = request.getCoupon();
                                if(code.equals(coup)) {
                                    used = true;
                                }
                            }
                            if(used) {
                                Toast.makeText(CouponsActivity.this, "Coupon Already Used!", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                if (payAmount < Integer.parseInt(model.getMaxprice()) && payAmount > Integer.parseInt(model.getMinprice())) {
                                    Intent returnIntent = new Intent();
                                    returnIntent.putExtra("coupon", model);
                                    setResult(Activity.RESULT_OK, returnIntent);
                                    finish();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupons);

        show_apply = getIntent().getBooleanExtra("apply",false);

        recyclerView = findViewById(R.id.recycler_coupons);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        payAmount = getIntent().getIntExtra("total",0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }
}
