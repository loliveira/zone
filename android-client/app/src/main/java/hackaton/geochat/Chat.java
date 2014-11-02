package hackaton.geochat;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpPost;
import com.koushikdutta.async.http.AsyncHttpResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Chat extends ListActivity implements LocationListener, WebsocketService.WebSocketListener {

    public static final String IP = "http://feynman.buzzlabs.com.br:4000";

    private List<Message> messages = new ArrayList<Message>();
    private MessageAdapter adapter;
    private EditText txtMessage;
    private double lastLatitude = 0d;
    private double lastLongitude = 0d;
    private Gson gson = new GsonBuilder().create();
    public static String nickname = null;
    private String clientsPong = "";

    private WebsocketService websocket = null;
    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,
                                       IBinder binder) {
            WebsocketService.MyBinder b = (WebsocketService.MyBinder) binder;
            websocket = b.getService();
            b.setWebSocketListener(Chat.this);
            Log.d("SERVICE", "Connected.");
        }

        public void onServiceDisconnected(ComponentName className) {
            websocket = null;
            Log.d("SERVICE", "Disconnected.");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        adapter = new MessageAdapter(this, messages);
        setListAdapter(adapter);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // set events for sending messages
        txtMessage = (EditText) findViewById(R.id.message);
        txtMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendMessage(v);
                    return true;
                }
                return false;
            }
        });
    }

    public void sendMessage(View view) {
        if (txtMessage.getText().toString().trim().isEmpty()) {
            return;
        }

        if (websocket != null && websocket.isConnected()) {
            Log.d("CHAT", "Sending message.");
            websocket.sendMessage(gson.toJson(new Message(txtMessage.getText().toString(), nickname)));
            txtMessage.getText().clear();

            // hide keyboard
            /*InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
            return;*/
        } else {
            Log.e("SERVICE", "Not connected, calling connectToWebsocket().");
            register();
        }

    }

    public void openMap(View view) {
        Intent iMap = new Intent(this, Map.class);
        iMap.putExtra("latitude", lastLatitude);
        iMap.putExtra("longitude", lastLongitude);
        iMap.putExtra("clients", clientsPong);
        startActivity(iMap);
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLatitude = location.getLatitude();
        lastLongitude = location.getLongitude();

        /*Log.i("LOCATION", "Latitude: " + location.getLatitude());
        Log.i("LOCATION", "Longitude: " + location.getLongitude());
        Log.i("LOCATION", "Accuracy: " + location.getAccuracy());*/

        if (nickname != null) {
            /*MultipartFormDataBody body = new MultipartFormDataBody();
            body.addStringPart("a", "1");
            post.setBody(body);*/
            if (websocket != null && websocket.isConnected()) {
                ping();
            } else {
                register();
            }
        }
    }

    public void register() {
        Log.d("REGISTER", "Register sent: " + "/register/" + nickname);

        if (nickname != null) {
            AsyncHttpPost post = new AsyncHttpPost(IP + "/register/" + nickname);
            try {
                AsyncHttpClient.getDefaultInstance().execute(post, new AsyncHttpClient.StringCallback() {
                    @Override
                    public void onCompleted(Exception e, AsyncHttpResponse asyncHttpResponse, String s) {
                        if (e != null) {
                            Log.e("REGISTER", e.toString());
                            return;
                        }
                        Log.i("REGISTER", s);

                        ping();

                        if (websocket != null) {
                            websocket.connectWebsocket();
                        }
                    }
                });
            } catch (Exception e) {
                Log.e("REGISTER", e.toString());
            }
        }
    }

    public void ping() {
        Log.d("PING", "Ping sent: " + "/ping/" + nickname + "?lat=" + lastLatitude + "&lon=" + lastLongitude);

        AsyncHttpPost post = new AsyncHttpPost(IP + "/ping/" + nickname + "?lat=" + lastLatitude + "&lon=" + lastLongitude);
        try {
            AsyncHttpClient.getDefaultInstance().execute(post, new AsyncHttpClient.StringCallback() {
                @Override
                public void onCompleted(Exception e, AsyncHttpResponse asyncHttpResponse, String s) {
                    if (e != null) {
                        Log.e("PING", e.toString());
                        return;
                    }
                    Log.i("PING", "Ping received: " + s);

                    clientsPong = s;
                }
            });
        } catch (Exception e) {
            Log.e("PING", e.toString());
        }
    }

    @Override
    public void onOpen() {
        ping();
    }

    @Override
    public void onTextMessage(String payload) {
        /*Message m = null;
        try {
            m = gson.fromJson(payload, Message.class);
        } catch (Exception e) {
            Log.e("JSON", e.toString());
        }*/

        String jsonConversation = getSharedPreferences("geochat-v1", 0).getString("conversation", "[]");
        Message[] msgs = gson.fromJson(jsonConversation, Message[].class);
        messages.clear();
        messages.addAll(Arrays.asList(msgs));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClose(int code, String reason) {
        try {
            websocket.disconnect();
        } catch (Exception e) {
            Log.e("WEBSOCKET", e.toString());
        }
        register();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // binding to service
        Intent intent = new Intent(this, WebsocketService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        //startService(intent);

        // get old chat
        String jsonConversation = getSharedPreferences("geochat-v1", 0).getString("conversation", "[]");
        lastLatitude = getSharedPreferences("geochat-v1", 0).getFloat("latitude", 0f);
        lastLongitude = getSharedPreferences("geochat-v1", 0).getFloat("longitude", 0f);

        Message[] msgs = gson.fromJson(jsonConversation, Message[].class);
        messages.clear();
        messages.addAll(Arrays.asList(msgs));

        /*if (getIntent().getStringExtra("nickname") != null) {
            messages.add(new Message(getIntent().getStringExtra("message"), getIntent().getStringExtra("nickname")));
        }*/

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });

        // start location poller
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 15000, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 2f, this);

        // open preferences for nickname
        nickname = getSharedPreferences("geochat-v1", 0).getString("nickname", null);

        if (websocket == null || !websocket.isConnected()) {
            // request nickname if not set previously
            if (nickname == null) {
                changeNicknameDialog();
            } else {
                register();
            }
        }
    }

    public void changeNicknameDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Nickname");
        alert.setMessage("Input your nickname");
        final EditText input = new EditText(this);
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                nickname = input.getText().toString().replaceAll(" ", "_");

                if (nickname.trim().isEmpty()) {
                    changeNicknameDialog();
                } else {
                    getSharedPreferences("geochat-v1", 0).edit().putString("nickname", nickname).commit();
                    register();
                }
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("APPLICATION", "Stopping application.");
        getSharedPreferences("geochat-v1", 0).edit().putString("conversation", gson.toJson(messages)).commit();
        getSharedPreferences("geochat-v1", 0).edit().putFloat("latitude", (float) lastLatitude).commit();
        getSharedPreferences("geochat-v1", 0).edit().putFloat("longitude", (float) lastLongitude).commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("APPLICATION", "Pausing application.");
        unbindService(mConnection);
    }

    /*
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.chat, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle item selection
            switch (item.getItemId()) {
                case R.id.change_nickname:
                    websocket.disconnect();

                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle("Nickname");
                    alert.setMessage("Input your nickname");
                    final EditText input = new EditText(this);
                    alert.setView(input);
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            nickname = input.getText().toString();
                            getSharedPreferences("geochat-v1", 0).edit().putString("nickname", nickname).commit();

                            register();
                        }
                    });
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });

                    alert.show();

                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
    */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
