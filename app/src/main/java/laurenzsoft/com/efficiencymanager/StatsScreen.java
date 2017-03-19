package laurenzsoft.com.efficiencymanager;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

public class StatsScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats_screen);
        TextView stats = (TextView) findViewById(R.id.statsTextView);
        Context c = getApplicationContext();
        String content = getResources().getString(R.string.htmlReportDaily, StatisticAlgorithm.minutesWorkedToday(c), StatisticAlgorithm.sessonsWorkedToday(c), StatisticAlgorithm.hoursWorkedWeek(c), StatisticAlgorithm.productiveWeek(c),StatisticAlgorithm.averageToday(c), StatisticAlgorithm.minutesWatchedToday(c), StatisticAlgorithm.workRatioToday(c), StatisticAlgorithm.minutesWatchedWeek(c), StatisticAlgorithm.workRatioWeek(c), StatisticAlgorithm.minutesReadToday(c), StatisticAlgorithm.minutesReadWeek(c),StatisticAlgorithm.readWatchRatioToday(c), StatisticAlgorithm.readWatchRatioWeek(c), StatisticAlgorithm.daysWorkedWeek(c), StatisticAlgorithm.hoursWorkedMonth(c));
        stats.setText(fromHtml(content));
        switch (StatisticAlgorithm.productivityToday(getApplicationContext())) {
            case -1:
                MediaPlayer.create(StatsScreen.this,R.raw.bad).start();
                break;
            case 0:
            case 1:
                MediaPlayer.create(StatsScreen.this,R.raw.medium).start();
                break;
            case 2:
                MediaPlayer.create(StatsScreen.this,R.raw.good).start();
                break;
        }
    }
    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }
}
