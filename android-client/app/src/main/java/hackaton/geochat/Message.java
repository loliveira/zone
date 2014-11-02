package hackaton.geochat;

public class Message {

    private String message;
    private String nickname;
    private double distance = 0d;

    public Message() {
    }

    public Message(String message, String nickname) {
        this.message = message;
        this.nickname = nickname;
    }

    public Message(String message, String nickname, double distance) {
        this.message = message;
        this.nickname = nickname;
        this.distance = distance;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

}
