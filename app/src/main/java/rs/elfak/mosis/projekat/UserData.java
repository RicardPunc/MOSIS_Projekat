package rs.elfak.mosis.projekat;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class UserData {
    private List<String> usernames;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private static final String USERS_CHILD = "users";
    private static final String LOCATIONS_CHILD = "locations";
    private static final String SESSIONS_CHILD = "sessions";


    private UserData(){
        usernames = new ArrayList<String>();
        database = FirebaseDatabase.getInstance("https://mosis-projekat-4c877-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();

        myRef.child(USERS_CHILD).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Get map of users in datasnapshot
                getAllUsernames((Map<String,Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //handle databaseError
            }
        });

    }

    private void getAllUsernames(Map<String, Object> value) {
        ArrayList<String> allUsernames = new ArrayList<>();

        if (value != null) {
            //iterate through each user, ignoring their UID
            for (Map.Entry<String, Object> entry : value.entrySet()){

                //Get user map
                Map singleUser = (Map) entry.getValue();
                //Get username field and append to list
                allUsernames.add((String) singleUser.get("username"));
            }

            this.usernames = allUsernames;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateLocation(User user) {
        byte[] bytes = user.getUsername().getBytes();
        String key = Base64.getEncoder().encodeToString(bytes);

        myRef.child(USERS_CHILD).child(key).setValue(user);
    }

    public void isLoggedIn(Object act) {
        MainActivity activity = (MainActivity) act;
        String id = activity.getID();
        myRef.child(SESSIONS_CHILD).child(id).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String userKey =  (String) dataSnapshot.getValue();

                    myRef.child(USERS_CHILD).child(userKey).get().addOnSuccessListener (new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.getValue() != null) {
                                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                                String username = (String) map.get("username");
                                String firstname = (String) map.get("firstname");
                                String lastname = (String) map.get("lastname");
                                String phone = (String) map.get("phone");
                                String password = (String) map.get("password");
                                String photo = (String) map.get("photo_str");
                                Map<String, Double> location = (Map<String, Double>) map.get("location");
                                GeoPoint locationPoint = new GeoPoint(location.get("latitude"), location.get("longitude"), location.get("altitude"));
                                User user = new User(username, firstname, lastname, password, phone, photo, locationPoint);
                                activity.logInSuccess(user);
                            }
                            else
                            {
                                activity.logInFailure();
                            }
                        }
                    } );
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void logOut(User user) {
        byte[] bytes = user.getUsername().getBytes();
        String userKey = Base64.getEncoder().encodeToString(bytes);

        myRef.child(SESSIONS_CHILD).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    for (String id : map.keySet()) {
                        String key = (String) map.get(id);
                        if (userKey.equals(key)) {
                            myRef.child(SESSIONS_CHILD).child(id).removeValue();
                        }
                    }
                }
            }
        });
    }

    public void getScores(Object act) {
        ScoreboardActivity activity = (ScoreboardActivity) act;
        List<User> users = new ArrayList<>();

        myRef.child(USERS_CHILD).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    for (String key: map.keySet()) {

                        Map<String, Object> userMap = (Map<String, Object>) map.get(key);
                        String username = (String) userMap.get("username");
                        String firstname = (String) userMap.get("firstname");
                        String lastname = (String) userMap.get("lastname");
                        String phone = (String) userMap.get("phone");
                        Long score = (Long) userMap.get("score");
                        String password = (String) userMap.get("password");
                        String photo = (String) userMap.get("photo_str");
                        Map<String, Double> location = (Map<String, Double>) userMap.get("location");
                        GeoPoint locationPoint = new GeoPoint(location.get("latitude"), location.get("longitude"), location.get("altitude"));
                        User user = new User(username, firstname, lastname, password, phone, photo, locationPoint, score);
                        users.add(user);
                        users.add(user);
                        users.add(user);
                        users.add(user);
                        users.add(user);
                    }
                    activity.showScoreboard(users);
                }
            }
        });


    }


    private static class SingletonHolder {
        public static final UserData instance = new UserData();
    }

    public static UserData getInstance() {
        return SingletonHolder.instance;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public int signUp(User user) {
        byte[] bytes = user.getUsername().getBytes();
        String key = Base64.getEncoder().encodeToString(bytes);

        if (usernames.contains(user.getUsername())) return -1;

        myRef.child(USERS_CHILD).child(key).setValue(user);
        usernames.add(user.getUsername());

        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void logIn(Object act, String username, String logInPassword) {

        MainActivity activity = (MainActivity) act;

        String key = Base64.getEncoder().encodeToString(username.getBytes());
        myRef.child(USERS_CHILD).child(key).get().addOnSuccessListener (new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    String username = (String) map.get("username");
                    String firstname = (String) map.get("firstname");
                    String lastname = (String) map.get("lastname");
                    String phone = (String) map.get("phone");
                    String password = (String) map.get("password");
                    String photo = (String) map.get("photo_str");
                    Map<String, Double> location = (Map<String, Double>) map.get("location");
                    GeoPoint locationPoint = new GeoPoint(location.get("latitude"), location.get("longitude"), location.get("altitude"));
                    User user = new User(username, firstname, lastname, password, phone, photo, locationPoint);
                    if (user.getPassword().equals(logInPassword)) {
                        myRef.child(SESSIONS_CHILD).child(activity.getID()).setValue(key);
                        activity.logInSuccess(user);
                    }
                    else activity.logInFailure();
                }
                else
                {
                    activity.logInFailure();
                }
            }
        } );

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getAllUsersLocations(Object act) {

        HomeActivity activity = (HomeActivity) act;

        List<User> users = new ArrayList<User>();

        byte[] bytes = activity.user.getUsername().getBytes();
        String userKey = Base64.getEncoder().encodeToString(bytes);

        myRef.child(USERS_CHILD).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    for (String key: map.keySet()) {
                        if (!userKey.equals(key)) {
                            Map<String, Object> userMap = (Map<String, Object>) map.get(key);
                            String username = (String) userMap.get("username");
                            String firstname = (String) userMap.get("firstname");
                            String lastname = (String) userMap.get("lastname");
                            String phone = (String) userMap.get("phone");
                            String password = (String) userMap.get("password");
                            String photo = (String) userMap.get("photo_str");
                            Map<String, Double> location = (Map<String, Double>) userMap.get("location");
                            GeoPoint locationPoint = new GeoPoint(location.get("latitude"), location.get("longitude"), location.get("altitude"));
                            User user = new User(username, firstname, lastname, password, phone, photo, locationPoint);
                            users.add(user);
                        }
                    }
                    activity.showUsers(users);
                }
            }
        });

    }

    public void addLocation(UserLocation location) {
        String key = myRef.push().getKey();
        myRef.child(LOCATIONS_CHILD).child(key).setValue(location);
    }

    public void getAllLocations(Object act) {
        HomeActivity activity = (HomeActivity) act;

        myRef.child(LOCATIONS_CHILD).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null) {
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    List<UserLocation> locations = new ArrayList<>();

                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        Map singleLocation = (Map) entry.getValue();
                        Map<String, Double> loc = (Map<String, Double>) singleLocation.get("location");
                        GeoPoint pointLocation = new GeoPoint(loc.get("latitude"), loc.get("longitude"), loc.get("altitude"));
                        UserLocation location = new UserLocation(singleLocation.get("name").toString(), singleLocation.get("description").toString(), pointLocation, singleLocation.get("type").toString());
                        locations.add(location);
                    }

                    activity.setLocationsOverlay(locations);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
