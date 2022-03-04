package rs.elfak.mosis.projekat;

import android.os.Build;
import android.provider.ContactsContract;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class UserData {
    private List<String> usernames;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private static final String FIREBASE_CHILD = "users";


    private UserData(){
        usernames = new ArrayList<String>();
        database = FirebaseDatabase.getInstance("https://mosis-projekat-4c877-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();


        myRef.child(FIREBASE_CHILD).addListenerForSingleValueEvent(
                new ValueEventListener() {
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


//        myRef.child(FIREBASE_CHILD).addListenerForSingleValueEvent(parentEventListener);

//        myRef.child(FIREBASE_CHILD).orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                User user = snapshot.getValue(User.class);
//                System.out.println(snapshot.getKey() + " is named " + user.getFirstName());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }

    private void getAllUsernames(Map<String, Object> value) {
        ArrayList<String> allUsernames = new ArrayList<>();

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : value.entrySet()){

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list
            allUsernames.add((String) singleUser.get("username"));
        }

        this.usernames = allUsernames;
    }


//    ValueEventListener parentEventListener = new ValueEventListener() {
//        @Override
//        public void onDataChange(@NonNull DataSnapshot snapshot) {
//            User user = snapshot.getValue(User.class);
//            System.out.println(snapshot.getKey() + " is named " + user.getFirstName());
//        }
//
//        @Override
//        public void onCancelled(@NonNull DatabaseError error) {
//
//        }
//    };

//    ChildEventListener childEventListener = new ChildEventListener() {
//        @Override
//        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//        }
//
//        @Override
//        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//        }
//
//        @Override
//        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//        }
//
//        @Override
//        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//        }
//
//        @Override
//        public void onCancelled(@NonNull DatabaseError error) {
//
//        }
//    };


    private static class SingletonHolder {
        public static final UserData instance = new UserData();
    }

    public static UserData getInstance() {
        return SingletonHolder.instance;
    }

//    public boolean checkUsername(String username) {
//        if (!this.usernames.contains(username)) {
//            return true;
//        }
//        return false;
//    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int signUp(User user) {
        byte[] bytes = user.getUsername().getBytes();
        String key = Base64.getEncoder().encodeToString(bytes);

        if (usernames.contains(user.getUsername())) return -1;

        myRef.child(FIREBASE_CHILD).child(key).setValue(user);
        usernames.add(user.getUsername());

        return 0;
    }

    static boolean success = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean logIn(String username, String logInPassword) {

        String key = Base64.getEncoder().encodeToString(username.getBytes());
        myRef.child(FIREBASE_CHILD).child(key).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                                                              @Override
                                                                              public void onSuccess(DataSnapshot dataSnapshot) {
                                                                                  Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
//                                                                                  String savedPass = (String)map.get("password");
                                                                                  String username = (String) map.get("username");
                                                                                  String firstname = (String) map.get("firstname");
                                                                                  String lastname = (String) map.get("lastname");
                                                                                  String phone = (String) map.get("phone");
                                                                                  String password = (String) map.get("password");
                                                                                  String photo =(String) map.get("photo");
                                                                                  User user = new User(username, firstname, lastname, password, phone, photo);
                                                                                  if (user.getPassword().equals(logInPassword)) success = true;
                                                                              }
                                                                          });

        return success;

    }
}
