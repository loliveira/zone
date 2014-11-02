package hackaton.geochat;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Igor1201 on 02/11/14.
 */

public class Pong {

    private Coordinate coords;
    @SerializedName("last-seen")
    private String lastSeen;
    private String channel;

    public Pong() {
    }

    public Pong(Coordinate coords, String lastSeen, String channel) {
        this.coords = coords;
        this.lastSeen = lastSeen;
        this.channel = channel;
    }

    public Coordinate getCoords() {
        return coords;
    }

    public void setCoords(Coordinate coords) {
        this.coords = coords;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
