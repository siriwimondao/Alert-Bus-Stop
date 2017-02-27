package rsu.siriwimon.pakdeeporn.alertbusstop;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    // Explicit
    private ListView listView;
    private Button button;
    private MyManage myManage;
    private LocationManager locationManager;
    private Criteria criteria;
    private Double userLatADouble = 13.964987, userLngADouble = 100.585154;
    private boolean aBoolean = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myManage = new MyManage(MainActivity.this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);

        //Bind Widget การผูกตัวแปร
        listView = (ListView) findViewById(R.id.livBusStop);
        button = (Button) findViewById(R.id.button);

        //Create ListView
        createListView();

        //Button controller
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               mySoundEfect(R.raw.add_bus1);
            }// onClick
        });
        // Long Click Button Controller
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d("31octV1","You Click Long"); //ควบคุมการคลิก

                startActivity(new Intent(MainActivity.this,AddBusStop.class));//เคลื่อนย้ายการทำงาน

                return true;

            } // onLongClick
        });

        //My Loop
        myLoop();


    }// Main Medthod

    //นี่คือ เมทอด ที่หาระยะ ระหว่างจุด
    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1.609344 * 1000;


        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    private void myLoop() {

        //Doing
        afterResume();

        calculateAllDistance();


        //Post
        if (aBoolean) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    myLoop();
                }
            }, 1000);
        }


    }   // myLoop

    private void calculateAllDistance() {

        try {

            SQLiteDatabase sqLiteDatabase = openOrCreateDatabase(MyOpenHelper.database_name,
                    MODE_PRIVATE, null);
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM busTABLE", null);
            int intCursor = cursor.getCount();
            double[] destinationLatDoubles = new double[intCursor];
            double[] destinationLngDoubles = new double[intCursor];
            double[] distanceDoubles = new double[intCursor];

            for (int i=0;i<intCursor;i++) {

                destinationLatDoubles[i] = Double.parseDouble(cursor.getString(3));
                destinationLngDoubles[i] = Double.parseDouble(cursor.getString(4));
                distanceDoubles[i] = distance(userLatADouble, userLngADouble,
                        destinationLatDoubles[i], destinationLngDoubles[i]);

                Log.d("27febV4", "ระยะห่างจากจุดที่ (" + i + ") ==> " + distanceDoubles[i]);

            }   //for


                    cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }   // calculate

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);
    }

    public Location myFindLocation(String strProvicer) {

        Location location = null;
        if (locationManager.isProviderEnabled(strProvicer)) {
            locationManager.requestLocationUpdates(strProvicer, 1000, 10, locationListener);
            location = locationManager.getLastKnownLocation(strProvicer);
        }

        return location;
    }

    public LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            userLatADouble = location.getLatitude();
            userLngADouble = location.getLongitude();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };



    @Override
    protected void onResume() {
        super.onResume();
        createListView();
        afterResume();
    }

    private void afterResume() {

        locationManager.removeUpdates(locationListener);

        Location networkLocation = myFindLocation(LocationManager.NETWORK_PROVIDER);
        if (networkLocation != null) {
            userLatADouble = networkLocation.getLatitude();
            userLngADouble = networkLocation.getLongitude();
        }

        Location gpsLocation = myFindLocation(LocationManager.GPS_PROVIDER);
        if (gpsLocation != null) {
            userLatADouble = gpsLocation.getLatitude();
            userLngADouble = gpsLocation.getLongitude();
        }

        Log.d("27febV3", "Lat ==> " + userLatADouble);
        Log.d("27febV3", "Lng ==> " + userLngADouble);

    }   // afterResume

    private void createListView() {

        try {

            //Read All SQLite
            SQLiteDatabase sqLiteDatabase = openOrCreateDatabase(MyOpenHelper.database_name,
                    MODE_PRIVATE, null);
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM busTABLE WHERE Destination = 1" , null);
            cursor.moveToFirst();
            int intCursor = cursor.getCount();
            Log.d("27febV2", "intCursor ==> " + intCursor);

            String[] nameStrings = new String[intCursor];
            for (int i=0;i<intCursor;i++) {

                nameStrings[i] = cursor.getString(1);
                cursor.moveToNext();

            }   // for

            ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(MainActivity.this,
                    android.R.layout.simple_list_item_1, nameStrings);
            listView.setAdapter(stringArrayAdapter);

            cursor.close();


        } catch (Exception e) {
            e.printStackTrace();
        }


    }   // createListView

    private void mySoundEfect(int intSound) {
        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(),intSound);
        mediaPlayer.start(); //ทำการร้อง

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.release(); // คืนหน่วยความจำ
            }
        });
    } // mySoundEfect
}// Main class
