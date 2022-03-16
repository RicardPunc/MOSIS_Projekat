package rs.elfak.mosis.projekat;


import com.google.firebase.database.IgnoreExtraProperties;

import org.osmdroid.util.GeoPoint;

import java.io.Serializable;

@IgnoreExtraProperties
public class User implements Serializable {
    private String username;
    private String firstname;
    private String lastname;
    private String password;
    private String phone;
    private int score;
    private GeoPoint location;
    private String photo_str;


    public User(String username, String firstname, String lastname, String password, String phone, String photo) {
        double asda = 0.0;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
        this.phone = phone;
        this.score = 0;
        this.location = new GeoPoint(36.114647, -115.172813, 610.000001);
        this.photo_str = photo;
    }

    public User(String username, String firstname, String lastname, String password, String phone, String photo, GeoPoint location) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
        this.phone = phone;
        this.score = 0;
        this.location = location;
        this.photo_str = photo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoto_str() {
        return photo_str;
    }

    public void setPhoto_str(String photo_str) {
        this.photo_str = photo_str;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
