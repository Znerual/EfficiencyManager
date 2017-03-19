package laurenzsoft.com.efficiencymanager;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.AlarmClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Date;

public class ReadingScreen extends AppCompatActivity {
    private final int TIMEROVER = 111;
    private String bookName;
    private int state, bookId;
    private long seconds, timer;
    private Date startTime;
    private Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_screen);

        ImageButton work = (ImageButton) findViewById(R.id.readingWorkButton);
        ImageButton remaind = (ImageButton) findViewById(R.id.readingRemindButton);
        final TextView minutes = (TextView) findViewById(R.id.readingForMinutesTV);
        TextView page = (TextView) findViewById(R.id.readingStoppedPageTV);
        final EditText timeInput = (EditText) findViewById(R.id.readingTimeEditText);
        final TextView timerTV = (TextView) findViewById(R.id.readingTimer);

        timer = -1;
        startTime = new Date();
        work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataBaseHolder.getInstance(getApplicationContext()).addLeisureTime(new Date().getTime(), seconds, state);
                TimeManager.getInstance().tookLeisureTime(seconds);
                seconds = 0;
                NewPageDialog newFragment = new NewPageDialog();
                Bundle bundle = new Bundle();
                bundle.putInt("bookId", bookId);
                newFragment.setArguments(bundle);
                newFragment.show(getSupportFragmentManager(), "Page");

            }
        });
        remaind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlarmClock.ACTION_SET_TIMER);
                intent.putExtra(AlarmClock.EXTRA_MESSAGE, "Times over");
                if (!timeInput.getText().toString().isEmpty()) {
                    intent.putExtra(AlarmClock.EXTRA_LENGTH, 60 * Integer.parseInt(timeInput.getText().toString()));
                    intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
                }
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                timer = (60 * Integer.parseInt(timeInput.getText().toString())) + seconds;
            }
        });
        Intent intent = getIntent();
        state =intent.getIntExtra("state",-1);
        switch (state) {
            case LeisureTimeScreen.BOOKSTATE:
                bookId = intent.getIntExtra("id", -1);
                int pageNum = DataBaseHolder.getInstance(getApplicationContext()).getPage(bookId);
                page.setText(getResources().getString(R.string.stoppedAt, pageNum));
                bookName = DataBaseHolder.getInstance(getApplicationContext()).getBookName(bookId);
                break;
            case LeisureTimeScreen.YOUTUBESTATE:
                page.setText("");
                break;
            case -1:
                page.setText("Something went wrong :(");
                state = LeisureTimeScreen.YOUTUBESTATE;
                break;
        }
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                if (inputMessage.what == TIMEROVER) {
                    MediaPlayer.create(ReadingScreen.this, R.raw.ready).start();
                    timer = -1;
                    timerTV.setText(getResources().getText(R.string.goWorking));
                }
            }

        };
        new Thread() {
            @Override
            public void run() {

                while (true) {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch (state) {
                                    case LeisureTimeScreen.BOOKSTATE:
                                        minutes.setText(getResources().getString(R.string.readingMinutes, bookName, seconds / 60));
                                        break;
                                    case LeisureTimeScreen.YOUTUBESTATE:
                                        minutes.setText(getResources().getString(R.string.atYoutube, seconds / 60));
                                        break;
                                }
                                if (timer != -1) {
                                    if (timer - seconds > 60) {
                                        timerTV.setText(getResources().getString(R.string.timer, TimeManager.format((timer - seconds) / 60)));
                                    } else {
                                        timerTV.setText(getResources().getString(R.string.timer, TimeManager.format(timer - seconds)));
                                    }
                                }
                            }
                        });
                        Thread.sleep(1000);
                        seconds++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (timer <= seconds && timer != -1) {
                        Message message = Message.obtain(mHandler);
                        message.what = TIMEROVER;
                        message.sendToTarget();
                    }
                }

            }
        }.start();
    }
    @Override
    public void onBackPressed() {
        DataBaseHolder.getInstance(getApplicationContext()).addLeisureTime((int) new Date().getTime(), seconds, state);
        TimeManager.getInstance().tookLeisureTime(seconds);
        seconds = 0;
        finish();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (startTime != null) {
             seconds= (int) ((new Date().getTime() - startTime.getTime()) / 1000);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong("start", startTime.getTime());
        savedInstanceState.putLong("timer", timer);
        savedInstanceState.putInt("state", state);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        startTime.setTime(savedInstanceState.getLong("start"));
        seconds =  ((new Date().getTime() - startTime.getTime()) / 1000);
        timer = savedInstanceState.getLong("timer");
        state = savedInstanceState.getInt("state");
    }
}
