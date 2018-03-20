package cz.foxiczek.mfolomouc;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class VypisyActivity extends AppCompatActivity {
    TableLayout table03;
    WWWData www = new WWWData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vypisy);
        SharedPrefs sp = new SharedPrefs(getApplicationContext());
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activity_vypisy);
        if(sp.readConfigString("theme").equals("light")){
            relativeLayout.setBackgroundColor(getResources().getColor(R.color.theme_light));
        }else{
            relativeLayout.setBackgroundColor(getResources().getColor(R.color.theme_dark));
        }
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("poslední zápis STDK");
        //TextView vypis = (TextView) findViewById(R.id.textView_vypis);
        //vypis.setMovementMethod(new ScrollingMovementMethod());
        //vypis.setText(www.getVypisy());
        generateTable(www.getVypisy());

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                Intent intent1 = new Intent(VypisyActivity.this, MainActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
                return true;

            case R.id.action_adresar:
                Intent intent = new Intent(VypisyActivity.this, AdresarActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;

            case R.id.action_vypisy:
                return true;

            case R.id.action_settings:
                Intent settings = new Intent(VypisyActivity.this, SettingsActivity.class);
                settings.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(settings);
                return true;

            case R.id.action_myTeam:
                Intent myTeam = new Intent(VypisyActivity.this, MyTeamActivity.class);
                startActivity(myTeam);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    private void generateTable(String input) {
        final SharedPrefs sp = new SharedPrefs(getApplicationContext());
        float fontSize;
        if(sp.readConfigFloat("fontSize") == 0f){
            fontSize = 10f;
        }
        else{
            fontSize = sp.readConfigFloat("fontSize");
        }
        table03 = (TableLayout) findViewById(R.id.table03);
        int count = table03.getChildCount();
        if (count > 0) {
            for (int a = 0; a < count; a++) {
                View child = table03.getChildAt(a);
                if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
            }
        }


        String[] lines = input.split("\n");
        for (int a = 0; a < lines.length - 1; a++) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            row.setLayoutParams(lp);
            TextView tv1 = new TextView(this);
            if (a == 0) {
                tv1.setTextSize(17);
                tv1.setTypeface(null, Typeface.BOLD);
            }

            tv1.setText(lines[a]);
            tv1.setTextSize(fontSize);
            tv1.setMaxWidth((int) (getScreenWidth() + (getScreenWidth() / 2)));
            if(sp.readConfigString("theme").equals("light")){
                tv1.setTextColor(Color.BLACK);
            }else {
                tv1.setTextColor(Color.WHITE);
            }
            row.addView(tv1);
            table03.addView(row, a);
        }
    }

    public int getScreenWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        float temp = (float) width;
        temp = (temp / 2) + (temp / 4);
        width = (int) temp;
        return width;
    }

}