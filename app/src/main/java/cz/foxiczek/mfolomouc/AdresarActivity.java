package cz.foxiczek.mfolomouc;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
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


public class AdresarActivity extends AppCompatActivity {
    TableLayout table02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adresar);

        SharedPrefs sp = new SharedPrefs(getApplicationContext());
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activity_adresar);
        if(sp.readConfigString("theme").equals("light")){
            relativeLayout.setBackgroundColor(getResources().getColor(R.color.theme_light));
        }else{
            relativeLayout.setBackgroundColor(getResources().getColor(R.color.theme_dark));
        }

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Adresář hřišť");
        WWWData www = new WWWData();
        TextView text_adresar = (TextView) findViewById(R.id.textView_Adresar);

        //text_adresar.setText(www.getAdresar());
        //text_adresar.setMovementMethod(new ScrollingMovementMethod());
        text_adresar.setText("");
        generateTable(www.getAdresar());

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                Intent intent = new Intent(AdresarActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;

            case R.id.action_settings:

                Intent intent_settings = new Intent(AdresarActivity.this, SettingsActivity.class);
                intent_settings.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent_settings);
                return true;


            case R.id.action_adresar:
                return true;

            case R.id.action_vypisy:
                Intent intent1 = new Intent(AdresarActivity.this, VypisyActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
                return true;

            case R.id.action_myTeam:
                Intent myTeam = new Intent(AdresarActivity.this, MyTeamActivity.class);
                startActivity(myTeam);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
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
        table02 = (TableLayout) findViewById(R.id.table02);
        int count = table02.getChildCount();
        if (count > 0) {
            for (int a = 0; a < count; a++) {
                View child = table02.getChildAt(a);
                if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
            }
        }


        String[] lines = input.split("\n");
        for (int a = 0; a < lines.length - 1; a++) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            row.setLayoutParams(lp);
            lines[a] = lines[a].replaceFirst("\\s+", "-");
            lines[a] = lines[a].replaceFirst("\\s+", "-");
            lines[a] = lines[a].replaceFirst("\\s+", "-");

            String temp1 = lines[a];
            String[] temp = temp1.split("-");

            TextView tv1 = new TextView(this);
            TextView tv2 = new TextView(this);
            TextView tv3 = new TextView(this);
            TextView tv4 = new TextView(this);
            tv1.setTextSize(fontSize);
            tv2.setTextSize(fontSize);
            tv3.setTextSize(fontSize);
            tv4.setTextSize(fontSize);
            if(temp[0].contains("=")) {
                String[] hriste = temp[0].split("=");
                tv1.setText(Html.fromHtml("<a href='" + hriste[0] + "'>" + hriste[1] + "</a>"));
                tv1.setMovementMethod(LinkMovementMethod.getInstance());
            }
            else{
                tv1.setText(temp[0] + " ");
            }
            tv2.setText(temp[1] + " ");
            tv3.setText(temp[2] + " ");
            if (temp.length < 4) {
                tv4.setText("");
            } else {
                tv4.setText(temp[3]);
            }

            tv4.setMaxWidth((int) (getScreenWidth() / 2 + getScreenWidth() / 4));
            if(sp.readConfigString("theme").equals("light")){
                tv1.setTextColor(Color.BLACK);
                tv2.setTextColor(Color.BLACK);
                tv3.setTextColor(Color.BLACK);
                tv4.setTextColor(Color.BLACK);
            }else {
                tv1.setTextColor(Color.WHITE);
                tv2.setTextColor(Color.WHITE);
                tv3.setTextColor(Color.WHITE);
                tv4.setTextColor(Color.WHITE);
            }
            row.addView(tv1);
            row.addView(tv2);
            row.addView(tv3);
            row.addView(tv4);
            table02.addView(row, a);

        }

    }

    public int getScreenWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        return width;
    }

}