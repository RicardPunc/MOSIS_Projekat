package rs.elfak.mosis.projekat;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.MeasureFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ScoreboardActivity extends AppCompatActivity {

    private LinearLayout scoreboard_ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        scoreboard_ll = findViewById(R.id.scoreboard_ll);

        UserData.getInstance().getScores(this);
    }

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void showScoreboard(List<User> users) {
        if (users != null) {

            users.sort(new Comparator<User>() {
                @Override
                public int compare(User user, User t1) {
                    return user.getScore() > t1.getScore() ? -1 : 1;
                }
            });

            for (User user : users) {
                String photo = user.getPhoto_str();
                byte [] encodeByte= Base64.decode(photo, Base64.URL_SAFE) ;
                Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

                LinearLayout layout = new LinearLayout(this);
                layout.setGravity(Gravity.CENTER_VERTICAL);
                CircleImageView circleImageView = new CircleImageView(this);
                circleImageView.setImageBitmap(bitmap);
                circleImageView.setForegroundGravity(Gravity.CENTER);
                layout.addView(circleImageView);

                TextView textView = new TextView(this);
                textView.setText(user.getUsername());
                textView.setPadding(20, 0, 0, 0);
                textView.setTextSize(20.0f);
                layout.addView(textView);

                TextView textView2 = new TextView(this);
                textView2.setText(user.getScore().toString());
                textView2.setTextSize(20.0f);
                textView2.setPadding(600,0,0,0);
                //textView2.setPadding(500, 0, 0, 0);
                layout.addView(textView2);

                scoreboard_ll.addView(layout);
            }
        }
    }
}