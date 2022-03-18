package rs.elfak.mosis.projekat;

import org.osmdroid.util.GeoPoint;

import java.io.Serializable;

public class UserLocation implements Serializable {
    private String name;
    private String description;
    private GeoPoint location;

    public UserLocation(String name, String description, GeoPoint location) {
        this.name = name;
        this.description = description;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }
}
