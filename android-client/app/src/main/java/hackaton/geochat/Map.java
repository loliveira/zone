package hackaton.geochat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Created by Igor1201 on 01/11/14.
 */

public class Map extends Activity implements GoogleMap.OnCameraChangeListener {

    private GoogleMap map;
    private LatLng myLocation;
    private HashMap<String, Pong> clients = new HashMap<String, Pong>();
    private Gson gson = new GsonBuilder().create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Get a handle to the Map Fragment
        map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map)).getMap();

        Intent main = getIntent();
        myLocation = new LatLng(main.getDoubleExtra("latitude", 0d), main.getDoubleExtra("longitude", 0d));

        Type type = new TypeToken<java.util.HashMap<String, Pong>>() {
        }.getType();
        clients = gson.fromJson(main.getStringExtra("clients"), type);

        map.getUiSettings().setScrollGesturesEnabled(false);
        map.setOnCameraChangeListener(this);

        map.addCircle(new CircleOptions().center(myLocation).radius(1000d).strokeWidth(2f).strokeColor(Color.argb(127, 0, 0, 255)).fillColor(Color.argb(50, 0, 0, 255)));
        map.addMarker(new MarkerOptions().position(myLocation));

        for (java.util.Map.Entry<String, Pong> client : clients.entrySet()) {
            Pong p = client.getValue();
            if (p != null && p.getCoords() != null) {
                LatLng location = new LatLng(p.getCoords().getLat(), p.getCoords().getLon());
                map.addMarker(new MarkerOptions().position(location).title(client.getKey()));
            }
        }

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 14.5f));
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        map.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        Log.d("MAP", "Zoom: " + map.getCameraPosition().zoom);
    }
}
