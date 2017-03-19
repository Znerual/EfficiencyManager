package laurenzsoft.com.efficiencymanager;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.Date;

public class BreakScreen extends AppCompatActivity {
    private final int TIMERFINISHED = 145;
    private volatile long tickCount;
    private Date startTime;
    private TextView timer;
    private Handler mHandler;
    private int projectID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_break_screen);
        Intent intent = getIntent();
        projectID = intent.getIntExtra("project", -1);

        timer = (TextView) findViewById(R.id.breakTime);
        ImageButton start = (ImageButton) findViewById(R.id.breakStartButton);
        ImageButton addComment = (ImageButton) findViewById(R.id.breakAddCommentButton);
        final EditText title = (EditText) findViewById(R.id.breakCommenTitleEditText);
        final EditText text = (EditText) findViewById(R.id.breakCommentEditText);
        final RatingBar conc = (RatingBar) findViewById(R.id.breakConecntrationRatingBar);
        final RadioButton continueB = (RadioButton) findViewById(R.id.breakContinueRB);
        final RadioButton otherProj = (RadioButton) findViewById(R.id.breakOtherProjectRB);
        RadioButton leisureTimeB = (RadioButton) findViewById(R.id.breakLeisureTimeRB);

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                if (inputMessage.what == TIMERFINISHED) {
                    MediaPlayer.create(BreakScreen.this, R.raw.ready).start();
                }
            }
        };
        tickCount = 0;
        new Thread() {
            @Override
            public void run() {

                while (tickCount < TimeManager.getInstance().getBreakTime()) {
                    try {
                        final long time = TimeManager.getInstance().getBreakTime() - tickCount;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                timer.setText(TimeManager.format(time / 60) + ":" + TimeManager.format(time % 60));
                                //Maybe change color of text in the last two minutes or so
                            }
                        });
                        Thread.sleep(1000);
                        tickCount++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Message message = Message.obtain(mHandler);
                message.what = TIMERFINISHED;
                message.sendToTarget();
                //maybe let the counter continue to count after the minutes
            }
        }.start();

        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataBaseHolder.getInstance(getApplicationContext()).addComment(title.getText().toString(), text.getText().toString(),new Date().getTime(),(int) conc.getRating(), projectID );
                TimeManager.getInstance().setConcentration((int) conc.getRating());
                title.setText("");
                text.setText("");
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if (continueB.isChecked()) {
                    intent = new Intent(BreakScreen.this, WorkingScreen.class);
                    intent.putExtra("project", projectID);
                } else if(otherProj.isChecked()) {
                    intent = new Intent(BreakScreen.this, WorkScreen.class);
                } else {
                    intent = new Intent(BreakScreen.this, LeisureTimeScreen.class);
                }
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (startTime != null) {
            tickCount = ((new Date().getTime() - startTime.getTime()) / 1000);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong("start", startTime.getTime());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        startTime.setTime(savedInstanceState.getLong("start"));
        tickCount = ((new Date().getTime() - startTime.getTime()) / 1000);
    }
}
