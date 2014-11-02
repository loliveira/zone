package hackaton.geochat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

/**
 * Created by Igor1201 on 01/11/14.
 */

public class WebsocketService extends Service {

    private final IBinder mBinder = new MyBinder();
    private final WebSocketConnection mConnection = new WebSocketConnection();
    private Gson gson = new GsonBuilder().create();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        connectWebsocket();
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    public class MyBinder extends Binder {

        private WebSocketListener listener = null;

        public WebsocketService getService() {
            return WebsocketService.this;
        }

        public void setWebSocketListener(WebSocketListener listener) {
            this.listener = listener;
        }

        public void onOpen() {
            if (listener != null) {
                listener.onOpen();
            }
        }

        public void onTextMessage(String payload) {
            if (listener != null) {
                listener.onTextMessage(payload);
            }
        }

        public void onClose(int code, String reason) {
            if (listener != null) {
                listener.onClose(code, reason);
            }
        }
    }

    public void sendMessage(String json) {
        if (mConnection.isConnected()) {
            Log.d("CHAT", "Is connected, sending.");
            mConnection.sendTextMessage(json);
        }
    }

    public interface WebSocketListener {
        public void onOpen();

        public void onTextMessage(String payload);

        public void onClose(int code, String reason);
    }

    public boolean isConnected() {
        return mConnection.isConnected();
    }

    public void disconnect() {
        mConnection.disconnect();
    }

    public void connectWebsocket() {
        // start websocket connection
        Log.d("WEBSOCKET", "Trying to start websocket.");

        //AsyncHttpClient.getDefaultInstance().websocket("http://200.235.68.195:4000/ws", "geochat-v1", this);
        if (!mConnection.isConnected() && Chat.nickname != null) {
            try {
                mConnection.connect(Chat.IP.replaceAll("http://", "ws://") + "/ws/" + Chat.nickname, new WebSocketHandler() {
                    @Override
                    public void onOpen() {
                        Log.d("WEBSOCKET", "Status: Connected to " + Chat.IP.replaceAll("http://", "ws://") + "/ws/" + Chat.nickname);
                        ((MyBinder) mBinder).onOpen();
                    }

                    @Override
                    public void onTextMessage(String payload) {
                        Log.d("WEBSOCKET", "Message received: " + payload);

                        Message m = null;
                        try {
                            m = gson.fromJson(payload, Message.class);
                        } catch (Exception e) {
                            Log.e("JSON", e.toString());
                        }

                        if (m != null) {
                            String jsonConversation = getSharedPreferences("geochat-v1", 0).getString("conversation", "[]");
                            Message[] msgs = gson.fromJson(jsonConversation, Message[].class);
                            List<Message> messages = new ArrayList<Message>();
                            messages.addAll(Arrays.asList(msgs));
                            messages.add(m);
                            getSharedPreferences("geochat-v1", 0).edit().putString("conversation", gson.toJson(messages)).commit();

                            // call listener to update UI
                            ((MyBinder) mBinder).onTextMessage(payload);

                            if (!m.getNickname().equals(Chat.nickname)) {
                                // play ringtone and display notification
                                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                                r.play();

                                // display notification
                                Intent resultIntent = new Intent(WebsocketService.this, Chat.class);
                                resultIntent.putExtra("nickname", m.getNickname());
                                resultIntent.putExtra("message", m.getMessage());

                                PendingIntent clickIntent =
                                        PendingIntent.getActivity(
                                                WebsocketService.this,
                                                0,
                                                resultIntent,
                                                PendingIntent.FLAG_UPDATE_CURRENT
                                        );

                                NotificationCompat.Builder mBuilder =
                                        new NotificationCompat.Builder(WebsocketService.this)
                                                .setSmallIcon(R.drawable.ic_launcher)
                                                .setContentTitle("New message from " + m.getNickname())
                                                .setContentText(m.getNickname() + ": " + m.getMessage())
                                                .setContentIntent(clickIntent);

                                NotificationManager mNotifyMgr =
                                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                mNotifyMgr.notify(1, mBuilder.build());
                            }
                        }
                    }

                    @Override
                    public void onClose(int code, String reason) {
                        Log.d("WEBSOCKET", "Connection lost. " + reason);
                        ((MyBinder) mBinder).onClose(code, reason);
                    }
                });
            } catch (WebSocketException e) {
                Log.e("WEBSOCKET", e.toString());
            }
        }
    }

   /* @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getAction().equals(SEND_MESSAGE)) {
            String message = intent.getStringExtra("message");
            Message m = new Message(message, Chat.nickname);

            if (mConnection.isConnected()) {
                mConnection.sendTextMessage(gson.toJson(m));

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                return;
            }

            Log.e("WEBSOCKET", "Not connected.");
        }
    }*/
}
