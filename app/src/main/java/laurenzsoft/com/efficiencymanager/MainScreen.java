package laurenzsoft.com.efficiencymanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainScreen extends AppCompatActivity {
    ImageButton workButton, leisureButton, statsButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        workButton = (ImageButton) findViewById(R.id.mainWorkButton);
        leisureButton = (ImageButton) findViewById(R.id.mainLeisureButton);
        statsButton = (ImageButton) findViewById(R.id.mainStatsButton);
        workButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainScreen.this, WorkScreen.class);
                startActivity(intent);
            }
        });
        leisureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainScreen.this, LeisureTimeScreen.class);
                startActivity(intent);
            }
        });
        statsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainScreen.this, StatsScreen.class);
                startActivity(intent);
            }
        });
    }
}
