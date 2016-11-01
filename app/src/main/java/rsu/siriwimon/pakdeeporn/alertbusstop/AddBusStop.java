package rsu.siriwimon.pakdeeporn.alertbusstop;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AddBusStop extends FragmentActivity implements OnMapReadyCallback {
    // Explicit
    private GoogleMap mMap;
    private EditText editText;
    private Button button;
    private String nameBusStopString;
    private ImageView recodImageView, playimImageView;
    private boolean aBoolean = true; // nonrecord sound
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_addbusstop_layout);

        // Bind Widget
        editText = (EditText) findViewById(R.id.editText) ;
        button = (Button) findViewById(R.id.button2);
        recodImageView = (ImageView) findViewById(R.id.imageView);
        playimImageView = (ImageView) findViewById(R.id.imageView2);

        //record controller
        recodImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                startActivityForResult(intent, 0); //ใส่เลข 0 เป็น result
            } // onclick บันทึกเสียง
        });

        // button controller ปุ่มคลิ๊ก
       button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

             // Get Value From EditText รับค่าจาก EditText
               nameBusStopString = editText.getText().toString().trim();

               // Check spece
                   if (nameBusStopString.equals("")) {
                       // Have Space
                        MyAlert myAlert = new MyAlert(AddBusStop.this,
                                R.drawable.doremon48,
                                getResources().getString(R.string.title_have_space),
                                getResources().getString(R.string.massage_have_space));
                       myAlert.myDialog();

                   } // if
           } // onClick
       });
        //play controller
        playimImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //check record
                if (aBoolean) {
                    // non record ไม่มีการบันทึกเสียงให้แจ้ง
                    MyAlert myAlert = new MyAlert(AddBusStop.this, R.drawable.nobita48,
                            getResources().getString(R.string.title_record_sound),
                            getResources().getString(R.string.massage_record_sound));
                    myAlert.myDialog();
                } else {
                    // record ok ให้เสียงร้องเล่นเสียง
                    MediaPlayer mediaPlayer = MediaPlayer.create(AddBusStop.this,uri);
                    mediaPlayer.start();
                }
            } // onclick
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    } // Main Method

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == 0)&&(resultCode == RESULT_OK)){
            Log.d("lnovV1","Result OK");
            aBoolean = false; //record sound ok
            uri = data.getData();

        }//if การบันทึกเสียง ถ้าบันทึกเสร็จให้กลับมาหน้าเดิม


    }   // onActivityresult

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    } // onMapReady
} // Main Class