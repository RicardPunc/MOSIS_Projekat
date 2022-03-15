package rs.elfak.mosis.projekat;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.drawable.DrawableUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.security.Permission;

public class SignupActivity extends AppCompatActivity {

    static final int PERMISSION_CAMERA = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int USERNAME_EXISTS = -1;

    ImageView thumb_iv;
    String photo_str = "";
    UserData userData;

    EditText username_et, firstname_et, lastname_et, password_et, phone_et;
    TextView photo_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        userData = UserData.getInstance();

        username_et = findViewById(R.id.username_et);
        firstname_et = findViewById(R.id.firstname_et);
        lastname_et = findViewById(R.id.lastname_et);
        password_et = findViewById(R.id.password_et);
        phone_et = findViewById(R.id.phone_et);

        photo_tv = findViewById(R.id.photo_tv);

        thumb_iv = findViewById(R.id.thumb_iv);

        thumb_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(SignupActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SignupActivity.this, new String[] {Manifest.permission.CAMERA}, PERMISSION_CAMERA);
                }
                else
                {
                    dispatchTakePictureIntent();
                }
            }
        });

        Button signup_btn = findViewById(R.id.signup_btn);
        signup_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                signup_btn.setEnabled(false);
                String username = username_et.getText().toString();
                String firstname = firstname_et.getText().toString();
                String lastname = lastname_et.getText().toString();
                String password = password_et.getText().toString();
                String phone = phone_et.getText().toString();

                if (validateFields(username, firstname, lastname, password, phone)) {
                    User user = new User(username, firstname, lastname, password, phone, photo_str);
                    int signupRequestCode = userData.signUp(user);

                    if (signupRequestCode == USERNAME_EXISTS) {
                        signup_btn.setEnabled(true);
                        username_et.setError("Username already exists!");
                    }
                    else {
                        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                }
                else {
                    signup_btn.setEnabled(true);
                }

            }
        });

        username_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() < 3){
                    username_et.setError("Username must be at least length of 3!");
                }
            }
        });

        firstname_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() < 3){
                    firstname_et.setError("First name must be at least length of 3!");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        lastname_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() < 3){
                    lastname_et.setError("Last name must be at least length of 3!");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        password_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() < 8){
                    password_et.setError("Password needs to be 8 or more characters long!");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private boolean validateFields(String username, String firstName, String lastname, String password, String phone) {
        boolean valid = true;

        if (username.length() < 3){
            username_et.setError("Username must be at least length of 3!");
            valid = false;
        }

        if (firstName.length() < 3){
            firstname_et.setError("First name must be at least length of 3!");
            valid = false;
        }

        if (lastname.length() < 3){
            lastname_et.setError("Last name must be at least length of 3!");
            valid = false;
        }

        if (password.length() < 8){
            password_et.setError("Password needs to be 8 or more characters long!");
            valid = false;
        }

        if (phone.length() < 1){
            phone_et.setError("You must enter phone number!");
        }

        if (photo_str == "") {
            photo_tv.setError("You must add photo!");
            valid = false;
        }

        return valid;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            thumb_iv.setImageBitmap(imageBitmap);

            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, bao); // bmp is bitmap from user image file
            byte[] byteArray = bao.toByteArray();
            photo_str = Base64.encodeToString(byteArray, Base64.URL_SAFE);

            photo_tv.setError(null);
        }
    }


}