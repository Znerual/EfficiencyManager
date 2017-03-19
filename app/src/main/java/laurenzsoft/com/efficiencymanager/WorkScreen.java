package laurenzsoft.com.efficiencymanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class WorkScreen extends AppCompatActivity {
    private int selectedID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_screen);
        selectedID = -1;
        ImageButton startButton = (ImageButton) findViewById(R.id.workStartButton);
        ImageButton randomButton = (ImageButton) findViewById(R.id.workRandomButton);
        ImageButton addProjectButton = (ImageButton) findViewById(R.id.workAddButton);
        final EditText projectName = (EditText) findViewById(R.id.workNewProjectNameEditText);
        Spinner spin = (Spinner) this.findViewById(R.id.workProjectPicker);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WorkScreen.this, WorkingScreen.class);
                intent.putExtra("project", 1); //selected Project
                startActivity(intent);
                finish();
            }
        });
        randomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WorkScreen.this, WorkingScreen.class);
                intent.putExtra("project", 1); //selected Project
                startActivity(intent);
                finish();
            }
        });
        addProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!projectName.getText().toString().isEmpty()) {
                    if (!DataBaseHolder.getInstance(getApplicationContext()).containsProject(projectName.getText().toString())) {
                        int id = (int) DataBaseHolder.getInstance(getApplicationContext()).addProject(projectName.getText().toString());
                        Intent intent = new Intent(WorkScreen.this, WorkingScreen.class);
                        intent.putExtra("project", id); //selected Project
                        startActivity(intent);
                        finish();
                    } else {
                        //play error sound
                        Toast.makeText(WorkScreen.this, getResources().getText(R.string.projectAlreadyEx), Toast.LENGTH_LONG).show();
                    }
                } else {
                    //Play error sound
                    Toast.makeText(WorkScreen.this, getResources().getText(R.string.enterAName), Toast.LENGTH_LONG).show();
                }
            }
        });
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedID != -1) {
                    Intent intent = new Intent(WorkScreen.this, WorkingScreen.class);
                    intent.putExtra("project", selectedID); //selected Project
                    startActivity(intent);
                    finish();
                }

            }
        });
        String[] from = new String[] {DataBaseHolder.TITLE};
        int[] to = new int[] {android.R.id.text1};
        SimpleCursorAdapter sca = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, DataBaseHolder.getInstance(getApplicationContext()).getProjects(), from, to, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        sca.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spin.setAdapter(sca);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                selectedID = (int) id;
            }
            public void onNothingSelected(AdapterView<?> parent) {
                selectedID = -1;
            }

        });
    }
}

