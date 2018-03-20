package cz.foxiczek.mfolomouc;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private Spinner spinner_kolo, spinner_obdobi, spinner_liga, spinner_type;
    private Button button_submit;
    WWWData data = new WWWData();
    TableLayout table01;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //MobileAds.initialize(getApplicationContext(), "ca-app-pub-9048698866822116~6058827589");
        SharedPrefs sp = new SharedPrefs(getApplicationContext());
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        addItemsOnSpinner_liga();
        addItemsOnSpinner_obdobi();
        addItemsOnSpinner_kolo();
        addItemsOnSpinner_type();

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activity_main);
        if (sp.readConfigString("theme").equals("light")) {
            relativeLayout.setBackgroundColor(getResources().getColor(R.color.theme_light));
            logEvent("light","theme_light","style");
        } else {
            relativeLayout.setBackgroundColor(getResources().getColor(R.color.theme_dark));
            logEvent("dark","theme_dark","style");
        }

        if (sp.readConfigString("favTeam") != "") {
            logEvent(sp.readConfigString("favTeam"),sp.readConfigString("favTeam"),"favTeam");
        }

        if (sp.readConfigFloat("fontSize") == 0f) {
            logEvent("default","default","fontSize");
        }else{
            logEvent(String.valueOf(sp.readConfigFloat("fontSize")),String.valueOf(sp.readConfigFloat("fontSize")),"fontSize");
        }


        addListenerOnButton();
        TextView text01 = (TextView) findViewById(R.id.TextView01);
        text01.setText("");

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    private void logEvent(String menuItem, String id, String contentType){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, menuItem);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                if (isNetworkAvailable()) {
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    logEvent("Menu Settings", "SETTINGS", "menu");
                    return true;
                } else {
                    throwError();
                    return false;
                }

            case R.id.action_adresar:
                if (isNetworkAvailable()) {
                    Intent adresar = new Intent(MainActivity.this, AdresarActivity.class);
                    startActivity(adresar);
                    logEvent("Menu Adresar", "ADRESAR","menu");
                    return true;
                } else {
                    throwError();
                    return false;
                }

            case R.id.action_home:
                return true;

            case R.id.action_vypisy:
                if (isNetworkAvailable()) {
                    Intent intent2 = new Intent(MainActivity.this, VypisyActivity.class);
                    startActivity(intent2);
                    logEvent("Menu Vypis", "VYPISVV","menu");
                    return true;
                } else {
                    throwError();
                    return false;
                }

            case R.id.action_myTeam:
                Intent myTeam = new Intent(MainActivity.this, MyTeamActivity.class);
                startActivity(myTeam);
                logEvent("Menu MyTeam", "MYTEAM","menu");
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    public void addItemsOnSpinner_kolo() {
        spinner_kolo = (Spinner) findViewById(R.id.spinner_kolo);
        String[] kolo = getResources().getStringArray(R.array.kolo);
        List<String> list2 = new ArrayList<String>();
        for (String kol : kolo) {
            list2.add(kol);
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list2);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_kolo.setAdapter(dataAdapter);


    }

    public void addItemsOnSpinner_obdobi() {
        DateFormat dateFormat = new SimpleDateFormat("MM");
        Date date = new Date();
        spinner_obdobi = (Spinner) findViewById(R.id.spinner_obdobi);
        String[] obdobi = getResources().getStringArray(R.array.obdobi);
        List<String> list1 = new ArrayList<String>();
        for (String obd : obdobi) {
            list1.add(obd);
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list1);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_obdobi.setAdapter(dataAdapter);
        if (Integer.valueOf(dateFormat.format(date)) > 8) {
            spinner_obdobi.setSelection(dataAdapter.getPosition("Podzim"));
        } else {
            spinner_obdobi.setSelection(dataAdapter.getPosition("Jaro"));
        }
    }

    public void addItemsOnSpinner_liga() {
        final SharedPrefs sp = new SharedPrefs(getApplicationContext());
        spinner_liga = (Spinner) findViewById(R.id.spinner_liga);
        String[] liga = getResources().getStringArray(R.array.liga);
        List<String> list3 = new ArrayList<String>();
        for (String lig : liga) {
            list3.add(lig);
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list3);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_liga.setAdapter(dataAdapter);
        if (sp.readConfigString("selectLiga") != "") {
            spinner_liga.setSelection(dataAdapter.getPosition(sp.readConfigString("selectLiga")));
        }

    }

    public void addItemsOnSpinner_type() {
        spinner_type = (Spinner) findViewById(R.id.spinner_type);
        String[] select = getResources().getStringArray(R.array.selection);
        List<String> list4 = new ArrayList<String>();
        for (String sel : select) {
            list4.add(sel);
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list4);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_type.setAdapter(dataAdapter);
    }


    public void addListenerOnButton() {
        spinner_liga = (Spinner) findViewById(R.id.spinner_liga);
        spinner_obdobi = (Spinner) findViewById(R.id.spinner_obdobi);
        spinner_kolo = (Spinner) findViewById(R.id.spinner_kolo);
        spinner_type = (Spinner) findViewById(R.id.spinner_type);

        button_submit = (Button) findViewById(R.id.button);

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    TextView text01 = (TextView) findViewById(R.id.TextView01);
                    Toast.makeText(MainActivity.this,
                            "obdobi : " + String.valueOf(spinner_obdobi.getSelectedItem()) +
                                    "\nkolo : " + String.valueOf(spinner_kolo.getSelectedItem()),
                            Toast.LENGTH_SHORT).show();
                    String obdobi = String.valueOf(spinner_obdobi.getSelectedItem());
                    obdobi = obdobi.substring(0, 1);
                    String kolo = String.valueOf(spinner_kolo.getSelectedItem());
                    int koloIndex = kolo.indexOf(".");
                    kolo = kolo.substring(0, koloIndex);
                    if (obdobi.equalsIgnoreCase("j")) {
                        int temp;
                        temp = Integer.parseInt(kolo) + 13;
                        kolo = String.valueOf(temp);
                        System.err.println("OBDOBI : " + obdobi + " KOLO : " + kolo + "TEMP : " + temp);
                        String liga = String.valueOf(spinner_liga.getSelectedItem());
                        String type = String.valueOf(spinner_type.getSelectedItem());

                        //text01.setText(data.getWWWData(type +"/?kolo=" + kolo + "&obdobi=" + obdobi, liga));

                        //generateTable(data.getWWWData(type +"/?kolo=" + kolo + "&obdobi=" + obdobi, liga));
                        if (type.equalsIgnoreCase("rozlosovani")) {
                            int temp2 = Integer.parseInt(kolo) - 13;
                            kolo = String.valueOf(temp2);
                            generateRozlosovani(data.getWWWData(type + "/?kolo=" + kolo + "&obdobi=" + obdobi, liga));
                        } else if (type.equalsIgnoreCase("tabulky")) {
                            generateTabulku(data.getWWWData(type + "/?kolo=" + kolo + "&obdobi=" + obdobi, liga));

                        } else {
                            generateVysledky(data.getWWWData(type + "/?kolo=" + kolo + "&obdobi=" + obdobi, liga));

                        }
                        //generateTabulku(data.getWWWData(type +"/?kolo=" + kolo + "&obdobi=" + obdobi, liga));
                    } else {
                        System.err.println("OBDOBI : " + obdobi + " KOLO : " + kolo);
                        String liga = String.valueOf(spinner_liga.getSelectedItem());
                        String type = String.valueOf(spinner_type.getSelectedItem());
                        if (type.equalsIgnoreCase("rozlosovani")) {
                            generateRozlosovani(data.getWWWData(type + "/?kolo=" + kolo + "&obdobi=" + obdobi, liga));
                        } else if (type.equalsIgnoreCase("tabulky")) {
                            generateTabulku(data.getWWWData(type + "/?kolo=" + kolo + "&obdobi=" + obdobi, liga));

                        } else {
                            generateVysledky(data.getWWWData(type + "/?kolo=" + kolo + "&obdobi=" + obdobi, liga));

                        }
                        //text01.setText(data.getWWWData( type + "/?kolo=" + kolo + "&obdobi=" + obdobi, liga));
                        //generateTable(data.getWWWData( type + "/?kolo=" + kolo + "&obdobi=" + obdobi, liga));
                        //generateTabulku(data.getWWWData(type +"/?kolo=" + kolo + "&obdobi=" + obdobi, liga));
                    }
                } else {
                    throwError();
                }
            }


        });
    }

    //rozlosovani
    private void generateRozlosovani(String input) {
        final SharedPrefs sp = new SharedPrefs(getApplicationContext());
        table01 = (TableLayout) findViewById(R.id.Table01);
        int count = table01.getChildCount();
        if (count > 0) {
            for (int a = 0; a < count; a++) {
                View child = table01.getChildAt(a);
                if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
            }
        }
        String[] lines = input.split("\n");

        for (int a = 0; a < lines.length; a++) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            row.setLayoutParams(lp);
            if (a == 0) {
                TextView tv = new TextView(this);
                lines[a] = lines[a].replace(";", "");
                tv.setText(lines[a]);
                tv.setTextColor(Color.WHITE);
                row.addView(tv);
                table01.addView(row, a);
            } else {
                String[] temp = lines[a].split(";");
                float fontSize;
                if (sp.readConfigFloat("fontSize") == 0f) {
                    fontSize = 10f;
                } else {
                    fontSize = sp.readConfigFloat("fontSize");
                }
                TextView tv0 = new TextView(this);
                TextView tv1 = new TextView(this);
                TextView tv2 = new TextView(this);
                TextView tv3 = new TextView(this);
                TextView tv4 = new TextView(this);
                TextView tv5 = new TextView(this);
                tv0.setTextSize(fontSize);
                tv1.setTextSize(fontSize);
                tv2.setTextSize(fontSize);
                tv3.setTextSize(fontSize);
                tv4.setTextSize(fontSize);
                tv5.setTextSize(fontSize);
                tv0.setText(temp[0] + "  ");
                tv1.setText(temp[1] + "  ");
                tv2.setText(temp[2] + "  ");
                tv3.setText(temp[3] + "  ");
                if (sp.readConfigString("favTeam") != "") {
                    if (temp[3].contains(sp.readConfigString("favTeam"))) {
                        row.setBackgroundColor(Color.LTGRAY);
                    }
                }
                tv4.setText(temp[4] + "  ");
                if (temp.length < 6) {
                    tv5.setText("");
                } else {
                    tv5.setText(temp[5]);
                }

                if (sp.readConfigString("theme").equals("light")) {
                    tv0.setTextColor(Color.BLACK);
                    tv1.setTextColor(Color.BLACK);
                    tv2.setTextColor(Color.BLACK);
                    tv3.setTextColor(Color.BLACK);
                    tv4.setTextColor(Color.BLACK);
                    tv5.setTextColor(Color.BLACK);
                } else {
                    tv0.setTextColor(Color.WHITE);
                    tv1.setTextColor(Color.WHITE);
                    tv2.setTextColor(Color.WHITE);
                    tv3.setTextColor(Color.WHITE);
                    tv4.setTextColor(Color.WHITE);
                    tv5.setTextColor(Color.WHITE);
                }
                row.addView(tv0);
                row.addView(tv1);
                row.addView(tv2);
                row.addView(tv3);
                row.addView(tv4);
                row.addView(tv5);
                table01.addView(row, a);
            }
        }
    }

    //tabulkyzl
    private void generateTabulku(String input) {
        final SharedPrefs sp = new SharedPrefs(getApplicationContext());
        float fontSize;
        if (sp.readConfigFloat("fontSize") == 0f) {
            fontSize = 10.0f;
        } else {
            fontSize = sp.readConfigFloat("fontSize");
        }

        table01 = (TableLayout) findViewById(R.id.Table01);


        int count = table01.getChildCount();
        if (count > 0) {
            for (int a = 0; a < count; a++) {
                View child = table01.getChildAt(a);
                if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
            }
        }


        String[] lines = input.split("\n");
        boolean stats = sp.readConfigBoolean("stats");
        if(stats) {
            for (int a = 0; a < lines.length; a++) {
                TableRow row = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
                row.setLayoutParams(lp);
                if (a == 0) {
                    TextView tv = new TextView(this);
                    lines[a] = lines[a].replace(";", "");
                    tv.setText(lines[a]);
                    tv.setTextSize(fontSize);
                    tv.setTextColor(Color.WHITE);
                    row.addView(tv);
                    table01.addView(row, a);

                    TableRow row_popis = new TableRow(this);
                    TextView tv_0 = new TextView(this);
                    tv_0.setTextSize(fontSize);
                    TextView tv_1 = new TextView(this);
                    tv_1.setTextSize(fontSize);
                    TextView tv_2 = new TextView(this);
                    tv_2.setTextSize(fontSize);
                    TextView tv_3 = new TextView(this);
                    tv_3.setTextSize(fontSize);
                    TextView tv_4 = new TextView(this);
                    tv_4.setTextSize(fontSize);

                    TextView tv_5 = new TextView(this);
                    tv_5.setTextSize(fontSize);
                    TextView tv_6 = new TextView(this);
                    tv_6.setTextSize(fontSize);
                    tv_5.setText(" +/- ");
                    tv_6.setText("\u2300");


                    tv_0.setText("místo");
                    tv_1.setText("team");
                    tv_2.setText("záp.");
                    tv_3.setText("skóre");
                    tv_4.setText(" bodů  ");

                    row_popis.addView(tv_0);
                    row_popis.addView(tv_1);
                    row_popis.addView(tv_2);
                    row_popis.addView(tv_3);
                    row_popis.addView(tv_4);

                    row_popis.addView(tv_5);
                    row_popis.addView(tv_6);

                    if (sp.readConfigString("theme").equals("light")) {
                        tv_0.setTextColor(Color.BLACK);
                        tv_1.setTextColor(Color.BLACK);
                        tv_2.setTextColor(Color.BLACK);
                        tv_3.setTextColor(Color.BLACK);
                        tv_4.setTextColor(Color.BLACK);

                        tv_5.setTextColor(Color.BLACK);
                        tv_6.setTextColor(Color.BLACK);

                    } else {
                        tv_0.setTextColor(Color.WHITE);
                        tv_1.setTextColor(Color.WHITE);
                        tv_2.setTextColor(Color.WHITE);
                        tv_3.setTextColor(Color.WHITE);
                        tv_4.setTextColor(Color.WHITE);

                        tv_5.setTextColor(Color.WHITE);
                        tv_6.setTextColor(Color.WHITE);

                    }
                    table01.addView(row_popis, 1);
                } else {
                    String[] temp = lines[a].split(";");

                    TextView tv0 = new TextView(this);
                    tv0.setTextSize(fontSize);
                    TextView tv1 = new TextView(this);
                    tv1.setTextSize(fontSize);
                    TextView tv2 = new TextView(this);
                    tv2.setTextSize(fontSize);
                    TextView tv3 = new TextView(this);
                    tv3.setTextSize(fontSize);
                    TextView tv4 = new TextView(this);
                    tv4.setTextSize(fontSize);
                    TextView tv5 = new TextView(this);
                    tv5.setTextSize(fontSize);
                    TextView tv6 = new TextView(this);
                    tv6.setTextSize(fontSize);
                    tv0.setText(temp[0]);
                    if (sp.readConfigString("favTeam") != "") {
                        if (temp[0].contains(sp.readConfigString("favTeam"))) {
                            row.setBackgroundColor(Color.LTGRAY);
                        }
                    }
                    tv1.setText(temp[1] + "   ");
                    if (sp.readConfigString("favTeam") != "") {
                        if (temp[1].contains(sp.readConfigString("favTeam"))) {
                            row.setBackgroundColor(Color.LTGRAY);
                        }
                    }
                    tv2.setText(temp[2] + "    ");
                    tv3.setText(temp[3] + "    ");
                    String[] plusMinus = temp[3].split(":");
                    String kolo = temp[2];
                    int pmindex = Integer.valueOf(plusMinus[0]) - Integer.valueOf(plusMinus[1]);
                    float golyNaZapas = Float.valueOf(plusMinus[0]) / Float.valueOf(kolo);
                    DecimalFormat df = new DecimalFormat("##.#");
                    String prumer = df.format(golyNaZapas);
                    tv5.setText(String.valueOf(pmindex) + "    ");
                    if (prumer.equals("NaN")) {
                        prumer = "0";
                    }
                    tv6.setText(String.valueOf(prumer) + "  ");
                    System.err.println("DEBUG : pmindex = " + pmindex + " - " + temp[1] + " - \u2300" + prumer);

                    tv4.setText(temp[4]);
                    if (sp.readConfigString("theme").equals("light")) {
                        tv0.setTextColor(Color.BLACK);
                        tv1.setTextColor(Color.BLACK);
                        tv2.setTextColor(Color.BLACK);
                        tv3.setTextColor(Color.BLACK);
                        tv4.setTextColor(Color.BLACK);
                        tv5.setTextColor(Color.BLACK);
                        tv6.setTextColor(Color.BLACK);
                    } else {
                        tv0.setTextColor(Color.WHITE);
                        tv1.setTextColor(Color.WHITE);
                        tv2.setTextColor(Color.WHITE);
                        tv3.setTextColor(Color.WHITE);
                        tv4.setTextColor(Color.WHITE);
                        tv5.setTextColor(Color.WHITE);
                        tv6.setTextColor(Color.WHITE);
                    }
                    row.addView(tv0);
                    row.addView(tv1);
                    row.addView(tv2);
                    row.addView(tv3);
                    row.addView(tv4);
                    row.addView(tv5);
                    row.addView(tv6);
                    table01.addView(row, a + 1);
                }
            }
        }else{
            for (int a = 0; a < lines.length; a++) {
                TableRow row = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
                row.setLayoutParams(lp);
                if (a == 0) {
                    TextView tv = new TextView(this);
                    lines[a] = lines[a].replace(";", "");
                    tv.setText(lines[a]);
                    tv.setTextSize(fontSize);
                    tv.setTextColor(Color.WHITE);
                    row.addView(tv);
                    table01.addView(row, a);

                    TableRow row_popis = new TableRow(this);
                    TextView tv_0 = new TextView(this);
                    tv_0.setTextSize(fontSize);
                    TextView tv_1 = new TextView(this);
                    tv_1.setTextSize(fontSize);
                    TextView tv_2 = new TextView(this);
                    tv_2.setTextSize(fontSize);
                    TextView tv_3 = new TextView(this);
                    tv_3.setTextSize(fontSize);
                    TextView tv_4 = new TextView(this);
                    tv_4.setTextSize(fontSize);

                    tv_0.setText("místo");
                    tv_1.setText("team");
                    tv_2.setText("záp.");
                    tv_3.setText("skóre");
                    tv_4.setText(" bodů  ");

                    row_popis.addView(tv_0);
                    row_popis.addView(tv_1);
                    row_popis.addView(tv_2);
                    row_popis.addView(tv_3);
                    row_popis.addView(tv_4);


                    if (sp.readConfigString("theme").equals("light")) {
                        tv_0.setTextColor(Color.BLACK);
                        tv_1.setTextColor(Color.BLACK);
                        tv_2.setTextColor(Color.BLACK);
                        tv_3.setTextColor(Color.BLACK);
                        tv_4.setTextColor(Color.BLACK);

                    } else {
                        tv_0.setTextColor(Color.WHITE);
                        tv_1.setTextColor(Color.WHITE);
                        tv_2.setTextColor(Color.WHITE);
                        tv_3.setTextColor(Color.WHITE);
                        tv_4.setTextColor(Color.WHITE);


                    }
                    table01.addView(row_popis, 1);
                } else {
                    String[] temp = lines[a].split(";");

                    TextView tv0 = new TextView(this);
                    tv0.setTextSize(fontSize);
                    TextView tv1 = new TextView(this);
                    tv1.setTextSize(fontSize);
                    TextView tv2 = new TextView(this);
                    tv2.setTextSize(fontSize);
                    TextView tv3 = new TextView(this);
                    tv3.setTextSize(fontSize);
                    TextView tv4 = new TextView(this);
                    tv4.setTextSize(fontSize);
                    tv0.setText(temp[0]);
                    if (sp.readConfigString("favTeam") != "") {
                        if (temp[0].contains(sp.readConfigString("favTeam"))) {
                            row.setBackgroundColor(Color.LTGRAY);
                        }
                    }
                    tv1.setText(temp[1] + "   ");
                    if (sp.readConfigString("favTeam") != "") {
                        if (temp[1].contains(sp.readConfigString("favTeam"))) {
                            row.setBackgroundColor(Color.LTGRAY);
                        }
                    }
                    tv2.setText(temp[2] + "    ");
                    tv3.setText(temp[3] + "    ");
                    tv4.setText(temp[4]);
                    if (sp.readConfigString("theme").equals("light")) {
                        tv0.setTextColor(Color.BLACK);
                        tv1.setTextColor(Color.BLACK);
                        tv2.setTextColor(Color.BLACK);
                        tv3.setTextColor(Color.BLACK);
                        tv4.setTextColor(Color.BLACK);

                    } else {
                        tv0.setTextColor(Color.WHITE);
                        tv1.setTextColor(Color.WHITE);
                        tv2.setTextColor(Color.WHITE);
                        tv3.setTextColor(Color.WHITE);
                        tv4.setTextColor(Color.WHITE);

                    }
                    row.addView(tv0);
                    row.addView(tv1);
                    row.addView(tv2);
                    row.addView(tv3);
                    row.addView(tv4);

                    table01.addView(row, a + 1);
                }
            }
        }

    }

    //vysledky
    private void generateVysledky(String input) {

        final SharedPrefs sp = new SharedPrefs(getApplicationContext());
        float fontSize;
        if (sp.readConfigFloat("fontSize") == 0f) {
            fontSize = 10f;
        } else {
            fontSize = sp.readConfigFloat("fontSize");
        }
        table01 = (TableLayout) findViewById(R.id.Table01);
        int count = table01.getChildCount();
        if (count > 0) {
            for (int a = 0; a < count; a++) {
                View child = table01.getChildAt(a);
                if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
            }
        }
        String[] lines = input.split("\n");

        for (int a = 0; a < lines.length; a++) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            row.setLayoutParams(lp);
            if (a == 0) {
                TextView tv = new TextView(this);
                lines[a] = lines[a].replace(";", "");
                tv.setText(lines[a]);
                tv.setTextColor(Color.WHITE);
                row.addView(tv);
                table01.addView(row, a);
            } else {
                //todo : pridat statistiky do vypisu [+/- a goly na zapas ]

                String[] temp = lines[a].split(";");

                TextView tv0 = new TextView(this);
                TextView tv1 = new TextView(this);
                tv0.setTextSize(fontSize);
                tv1.setTextSize(fontSize);
                tv0.setText(temp[0] + "  ");
                if (sp.readConfigString("favTeam") != "") {
                    if (temp[0].contains(sp.readConfigString("favTeam"))) {
                        row.setBackgroundColor(Color.LTGRAY);
                    }
                }
                tv1.setText(temp[1]);
                if (sp.readConfigString("theme").equals("light")) {
                    tv0.setTextColor(Color.BLACK);
                    tv1.setTextColor(Color.BLACK);
                } else {
                    tv0.setTextColor(Color.WHITE);
                    tv1.setTextColor(Color.WHITE);
                }
                row.addView(tv0);
                row.addView(tv1);
                table01.addView(row, a);
            }
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void throwError() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("nenalezeno internetove spojeni");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "UKONČIT",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                });
        alertDialog.show();


    }


}
