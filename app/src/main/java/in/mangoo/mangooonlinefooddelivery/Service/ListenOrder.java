package in.mangoo.mangooonlinefooddelivery.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import in.mangoo.mangooonlinefooddelivery.Common.Common;
import in.mangoo.mangooonlinefooddelivery.Model.Request;
import in.mangoo.mangooonlinefooddelivery.OrderStatus;
import in.mangoo.mangooonlinefooddelivery.R;
import in.mangoo.mangooonlinefooddelivery.TrackOrder;

import static in.mangoo.mangooonlinefooddelivery.Notification.CHANNEL_1_ID;

public class ListenOrder extends Service implements ChildEventListener {

    FirebaseDatabase db;
    DatabaseReference requests;

    NotificationManagerCompat notificationManager;

    public ListenOrder() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        db = FirebaseDatabase.getInstance();
        requests = db.getReference("Requests");
        notificationManager = NotificationManagerCompat.from(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        requests.addChildEventListener(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        Request request = dataSnapshot.getValue(Request.class);

        showNotification(dataSnapshot.getKey(),request);

    }

    private void showNotification(String key, Request request) {

        String title = "Mangoo";
        String message = convertCodetoStatus(key,request);

        Intent intent = new Intent(this, OrderStatus.class);
        intent.putExtra("userPhone",request.getPhone());
        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

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

        //NotificationManager notificationManager = (NotificationManager)getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1,notification);

    }

    public String convertCodetoStatus(String key,Request request) {

        String res ="";
        /*requests.child("status").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Common.statusCode = (String) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        String status_code = request.getStatus();

        Log.d("TAG","Status code : " + status_code);

        if(status_code.equals("1"))
            res = "Your order #"+ key +" has been successfully placed";
        else if(status_code.equals("2"))
            res = "Your order #"+ key +" has been confirmed by the Restaurant";
        else if(status_code.equals("3"))
            res = "Your order #"+ key +" is waiting for pickup by delivery boy";
        else if(status_code.equals("4"))
            res = "Your order #"+ key +" is on the way";
        else
            res = "Your order #"+ key +" has been Delivered successfully";

        return res;

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
