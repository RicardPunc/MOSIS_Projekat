package rs.elfak.mosis.projekat;


import android.graphics.Bitmap;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    private String username;
    private String firstName;
    private String lastname;
    private String password;
    private String phone;

    private String photo_str;


    public User(String username, String firstName, String lastname, String password, String phone, String photo) {
        this.username = username;
        this.firstName = firstName;
        this.lastname = lastname;
        this.password = password;
        this.phone = phone;
        this.photo_str = photo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
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
}
