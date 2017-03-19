package laurenzsoft.com.efficiencymanager;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Date;

public class WorkingScreen extends AppCompatActivity {
    private final int TIMERFINISHED = 123;
    private Date startTime;
    private TextView timer;
    volatile long tickCount = 0;
    private Handler mHandler;
    private int projectID, selectedID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_working_screen);

        timer = (TextView) findViewById(R.id.workingTimer);
        TextView currentProject = (TextView) findViewById(R.id.workingProjectTextView);
        ImageButton finishedButton = (ImageButton) findViewById(R.id.workingFinishedButton);
        ExpandableListView comments = (ExpandableListView) findViewById(R.id.workingCommentsListView);

        Intent intent = getIntent();
        projectID = intent.getIntExtra("project", -1);
        currentProject.setText(DataBaseHolder.getInstance(getApplicationContext()).getProjectName(projectID));

        startTime = new Date();
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                if (inputMessage.what == TIMERFINISHED) {
                    MediaPlayer.create(WorkingScreen.this, R.raw.ready).start();
                }
            }

        };
        //start a timer and go to break screen after 25 minutes
        //this timer should be a new thread, which can controll the label and update the time after every second.
        new Thread() {
            @Override
            public void run() {
                int workingTime = TimeManager.getInstance().getWorkingTime();
                while (true) {
                    if (tickCount < workingTime) {
                            final long time = TimeManager.getInstance().getWorkingTime() - tickCount;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    timer.setText(TimeManager.format(time / 60) + ":" + TimeManager.format(time % 60));
                                }
                            });
                        try {
                            Thread.sleep(1000);
                            tickCount++;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else if (tickCount == workingTime){
                        Message message = Message.obtain(mHandler);
                        message.what = TIMERFINISHED;
                        message.sendToTarget();
                    } else {
                        final long time = tickCount - TimeManager.getInstance().getWorkingTime();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                timer.setText("Over: " + (time / 60) + "m");
                            }
                        });
                        try {
                            Thread.sleep(10000);
                            tickCount += 10;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }

                }

                //maybe let the counter continue to count after the minutes
            }
        }.start();

        finishedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataBaseHolder.getInstance(getApplicationContext()).addTimeToProject(projectID, tickCount);
                DataBaseHolder.getInstance(getApplicationContext()).addSession(new Date().getTime(), tickCount, projectID);
                if (tickCount > TimeManager.getInstance().getLastWorkingTime()) {
                    TimeManager.getInstance().completedLesson(tickCount);
                } else {
                    TimeManager.getInstance().abortedLesson(tickCount);
                }

                Intent intent = new Intent(WorkingScreen.this, BreakScreen.class);
                intent.putExtra("project", projectID);
                startActivity(intent);
                finish();
            }
        });
        Cursor cs = DataBaseHolder.getInstance(getApplicationContext()).getComments(projectID);
        WorkingScreen.this.startManagingCursor(cs);
        cs.moveToFirst();
        CommentAdapter c = new CommentAdapter(cs, getApplicationContext(),R.layout.comment_head,                     // Your row layout for a group
                R.layout.comment_item,                 // Your row layout for a child
                new String[] { DataBaseHolder.TITLE, DataBaseHolder.DATE },                      // Field(s) to use from group cursor
                new int[] { R.id.groupName, R.id.groupDescr },                 // Widget ids to put group data into
                new String[] { DataBaseHolder.TEXT, DataBaseHolder.CONCENTRATION },  // Field(s) to use from child cursors
                new int[] { R.id.itemName, R.id.itemProgress });

        comments.setAdapter(c);
        comments.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (startTime != null) {
            tickCount =  ((new Date().getTime() - startTime.getTime()) / 1000);
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

    @Override
    public void onBackPressed() {
        QuitWorkingDialog dialog = new QuitWorkingDialog();
        dialog.show(getFragmentManager(), "Dialog");
    }

    public class CommentAdapter extends SimpleCursorTreeAdapter {
        public CommentAdapter(Cursor cursor, Context context, int groupLayout, int childLayout, String[] groupFrom, int[] groupTo, String[] childrenFrom, int[] childrenTo) {
            super(context, cursor, groupLayout, groupFrom, groupTo, childLayout, childrenFrom, childrenTo);
        }

        @Override
        protected Cursor getChildrenCursor(Cursor groupCursor) {
            String title = groupCursor.getString(groupCursor.getColumnIndex(DataBaseHolder.TITLE));
            long date = groupCursor.getLong(groupCursor.getColumnIndex(DataBaseHolder.DATE));

            Cursor childCursor = DataBaseHolder.getInstance(getApplicationContext()).getCommentText(projectID, title, date);
            WorkingScreen.this.startManagingCursor(childCursor);
            childCursor.moveToFirst();
            return childCursor;
        }
    }


}
