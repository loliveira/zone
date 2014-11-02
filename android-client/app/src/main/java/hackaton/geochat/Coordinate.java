package hackaton.geochat;

/**
 * Created by Igor1201 on 02/11/14.
 */
public class Coordinate {

    private double lat = 0d;
    private double lon = 0d;

    public Coordinate() {
    }

    public Coordinate(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
