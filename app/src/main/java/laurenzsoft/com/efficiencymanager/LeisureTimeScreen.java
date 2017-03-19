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

public class LeisureTimeScreen extends AppCompatActivity {
    public final static int BOOKSTATE = 1;
    public final static int YOUTUBESTATE = 2;
    private int selectedID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leisure_time_screen);
        selectedID = -1;
        final ImageButton book =   (ImageButton) findViewById(R.id.leisureBookButton);
        ImageButton youtube = (ImageButton) findViewById(R.id.leisureYoutubeButton);
        ImageButton newBook = (ImageButton) findViewById(R.id.leisureNewBookButton);
        final EditText title = (EditText) findViewById(R.id.leisureTitleEditText);
        final Spinner bookPicker = (Spinner) findViewById(R.id.leisureBookPicker);

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedID != -1) {
                    Intent intent = new Intent(LeisureTimeScreen.this, ReadingScreen.class);
                    intent.putExtra("id", selectedID);
                    intent.putExtra("state",BOOKSTATE);
                    startActivity(intent);
                    finish();
                }

            }
        });
        newBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stitle = title.getText().toString();
                int id;
                if (!stitle.isEmpty()) {
                    if (DataBaseHolder.getInstance(getApplicationContext()).containsBook(stitle)) {
                        id = DataBaseHolder.getInstance(getApplicationContext()).getBookId(stitle);
                    } else {
                        id = (int) DataBaseHolder.getInstance(getApplicationContext()).addBook(stitle);
                    }
                    Intent intent = new Intent(LeisureTimeScreen.this, ReadingScreen.class);
                    intent.putExtra("id", id);
                    intent.putExtra("state",BOOKSTATE);
                    startActivity(intent);
                    finish();
                }
            }
        });
        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LeisureTimeScreen.this, ReadingScreen.class);
                intent.putExtra("state",YOUTUBESTATE);
                startActivity(intent);
                finish();
            }
        });
        String[] from = new String[] {DataBaseHolder.TITLE};
        int[] to = new int[] {android.R.id.text1};
        SimpleCursorAdapter sca = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, DataBaseHolder.getInstance(getApplicationContext()).getBooks(), from, to, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        sca.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        bookPicker.setAdapter(sca);
        bookPicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                selectedID = (int) id;
            }
            public void onNothingSelected(AdapterView<?> parent) {
                selectedID = -1;
            }

        });
    }
}
