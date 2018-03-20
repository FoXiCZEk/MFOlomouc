package cz.foxiczek.mfolomouc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    Spinner spinner_team, spinner_liga;
    CallbackManager callbackManager;
    LoginManager loginManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        generateSettingsTable();
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithPublishPermissions(SettingsActivity.this,Arrays.asList("publish_actions"));
        final LoginButton loginButton = (LoginButton) findViewById(R.id.fb_login_button);
        TextView text_fb_info = (TextView) findViewById(R.id.textView_fb_info_popis);
        TextView text_fb_name = (TextView) findViewById(R.id.textView_fb_info_name);
        if(Profile.getCurrentProfile() != null){
            text_fb_name.setText(Profile.getCurrentProfile().getFirstName() + " " + Profile.getCurrentProfile().getLastName());
        }else{
            text_fb_info.setVisibility(View.INVISIBLE);
            text_fb_name.setVisibility(View.INVISIBLE);
        }

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        TextView text_fb_info = (TextView) findViewById(R.id.textView_fb_info_name);

                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {

                    }
                });

                Button button_about = (Button) findViewById(R.id.button_about);
        button_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(),0);
                    String version = pInfo.versionName;
                    int build = pInfo.versionCode;
                    long info = pInfo.lastUpdateTime;
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(info);
                    TextView link = new TextView(SettingsActivity.this);
                    link.setText(Html.fromHtml("<a href=\"mailto:foxiczek@hotmail.com\">poslat email</a>"));
                    link.setMovementMethod(LinkMovementMethod.getInstance());
                    link.setPadding(250,0,0,0);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.M.yyyy HH:mm");
                    android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(SettingsActivity.this).create();
                    alertDialog.setTitle("Info");
                    alertDialog.setMessage("verze aplikace : " + version + "\nvytvořeno :"  + sdf.format(calendar.getTime())+ "\nautor : foxiczek@hotmail.com ");
                    alertDialog.setView(link);
                    alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void generateSettingsTable() {
        int settingsCount = 4;
        TableLayout table = (TableLayout) findViewById(R.id.table_settings);
        SharedPrefs prefs = new SharedPrefs(getApplicationContext());

        //TableRow tr = new TableRow(this);
        TableRow tr = (TableRow) findViewById(R.id.tableRow_team);
        //TextView popis = new TextView(this);
        TextView popis = (TextView) findViewById(R.id.textView_team_popis);
        //popis.setText("Oblibeny tym");
        //popis.setPadding(0, 0, 50, 0);
        //final TextView data = new TextView(this);
        final TextView data = (TextView) findViewById(R.id.textView_team_data);
        if (prefs.readConfigString("favTeam").equals("")) {
            data.setText("NEBYL ZADAN");
        } else {
            data.setText(prefs.readConfigString("favTeam"));
        }
        //Button change = new Button(this);
        Button change = (Button) findViewById(R.id.button_team_change);
        //change.setGravity(Gravity.RIGHT);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPrefs sf = new SharedPrefs(getApplicationContext());
                LayoutInflater li = LayoutInflater.from(SettingsActivity.this);
                final View promptsView = li.inflate(R.layout.alert_myteam, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
                alertDialogBuilder.setView(promptsView);
                alertDialogBuilder.setTitle("vyber oblibeny team");

                final AlertDialog alertDialog = alertDialogBuilder.create();
                spinner_liga = (Spinner) promptsView.findViewById(R.id.spinner_liga);
                String[] ligy = getResources().getStringArray(R.array.ligy);
                List<String> list_ligy = new ArrayList<String>();
                for (String liga : ligy) {
                    list_ligy.add(liga);
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(SettingsActivity.this, android.R.layout.simple_spinner_item, list_ligy);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_liga.setAdapter(dataAdapter);
                TextView text = (TextView) promptsView.findViewById(R.id.textView_test);
                spinner_team = (Spinner) promptsView.findViewById(R.id.spinner_team);
                String oznac;
                if (sf.readConfigString("favLiga") != "") {
                    //System.err.println("DEBUG : favLiga = " + sf.readConfigString("favLiga"));
                    if (sf.readConfigString("favLiga").contains("1")) {
                        oznac = "1. liga";
                        //System.err.println("DEBUG : oznac = " + oznac);
                    } else {
                        oznac = sf.readConfigString("favLiga");
                        String tmp = oznac.replaceAll("liga_", "");
                        //System.err.println("DEBUG : tmp = " + tmp);
                        oznac = tmp.substring(0, 1) + ". " + tmp.substring(1, 2);
                        //System.err.println("DEBUG : oznac = " + oznac);
                    }
                    //System.err.println("DEBUG : oznac = " + oznac);
                    spinner_liga.setSelection(dataAdapter.getPosition(oznac));
                }
                spinner_liga.setOnItemSelectedListener(new SpinnerSelect(spinner_liga, text, spinner_team, getApplicationContext()));
                //TextView text_text = (TextView) promptsView.findViewById(R.id.textView_test);
                Button save = (Button) promptsView.findViewById(R.id.button_alert);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Spinner spinner_team = promptsView.findViewById(R.id.spinner_team);
                        Spinner spinner_liga = promptsView.findViewById(R.id.spinner_liga);
                        SharedPrefs sf = new SharedPrefs(getApplicationContext());
                        sf.setConfig("favTeam", spinner_team.getSelectedItem().toString());
                        String liga = spinner_liga.getSelectedItem().toString();
                        if(liga.contains("1")){
                            sf.setConfig("selectLiga", "1.liga");
                            sf.setConfig("favLiga", "1.liga");
                        }else {
                            String tmp = liga.replaceAll("liga", "");
                            String tmp2 = tmp.replaceAll("\\.", "");
                            tmp = tmp2.replaceAll(" ", "");
                            liga = "liga_" + tmp;
                            tmp2 = tmp.substring(0, 1) + ".liga " + tmp.substring(1, 2);
                            System.err.println("DEBUG : " + liga);
                            sf.setConfig("selectLiga", tmp2);
                            sf.setConfig("favLiga", liga);
                        }
                        alertDialog.dismiss();
                        data.setText(spinner_team.getSelectedItem().toString());
                    }
                });

                alertDialog.show();
                alertDialog.setCanceledOnTouchOutside(false);

            }


        });
        change.setText("Změnit");


        // font settings
        final Button button_font_small = (Button) findViewById(R.id.button_font_small);
        final Button button_font_medium = (Button) findViewById(R.id.button_font_medium);
        final Button button_font_large = (Button) findViewById(R.id.button_font_large);
        final SharedPrefs sp = new SharedPrefs(getApplicationContext());
        if(sp.readConfigFloat("fontSize") == 10.0f){
            button_font_small.setEnabled(false);
        }else if(sp.readConfigFloat("fontSize") == 15.0f){
            button_font_medium.setEnabled(false);
        }else if(sp.readConfigFloat("fontSize") == 20.0f){
            button_font_large.setEnabled(false);
        }else{
            sp.setConfig("fontSize", 15.0f);
            button_font_medium.setEnabled(false);
        }


        button_font_small.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sp.readConfigFloat("fontSize") == 0 || sp.readConfigFloat("fontSize") != 10.0f){
                    sp.setConfig("fontSize", 10.0f);
                    button_font_small.setEnabled(false);
                    button_font_medium.setEnabled(true);
                    button_font_large.setEnabled(true);
                }
            }
        });

        button_font_medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sp.readConfigFloat("fontSize") == 0 || sp.readConfigFloat("fontSize") != 15.0f){
                    sp.setConfig("fontSize", 15.0f);
                    button_font_small.setEnabled(true);
                    button_font_medium.setEnabled(false);
                    button_font_large.setEnabled(true);
                }
            }
        });

        button_font_large.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sp.readConfigFloat("fontSize") == 0 || sp.readConfigFloat("fontSize") != 20.0f){
                    sp.setConfig("fontSize", 20.0f);
                    button_font_small.setEnabled(true);
                    button_font_medium.setEnabled(true);
                    button_font_large.setEnabled(false);
                }
            }
        });


        final ToggleButton toggle_theme = (ToggleButton) findViewById(R.id.toggleButton_theme);
        if(sp.readConfigString("theme").equals("light")){
            toggle_theme.setChecked(true);
        }else{
            toggle_theme.setChecked(false);
        }
        toggle_theme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(toggle_theme.isChecked()){
                    sp.setConfig("theme", "light");
                }else{
                    sp.setConfig("theme", "dark");
                }
            }
        });


        final ToggleButton toggle_stats = (ToggleButton) findViewById(R.id.toggleButton_stats);
        if(sp.readConfigBoolean("stats")){
            toggle_stats.setChecked(true);
        }else{
            toggle_stats.setChecked(false);
        }
        toggle_stats.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(toggle_stats.isChecked()){
                    sp.setConfig("stats",true);
                }else{
                    sp.setConfig("stats",false);
                }
            }
        });

        TextView text_stats_popis = (TextView) findViewById(R.id.textView_stats_popis);
        text_stats_popis.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(SettingsActivity.this,"Zobrazí statistiky u tabulek\n +/- rozdíl ve skóre\n\u2300 průměr gólů na zápas ",Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        TextView text_font_popis = (TextView) findViewById(R.id.textView_font_popis);
        text_font_popis.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(SettingsActivity.this,"Změna velikosti písma\nmale / střední / velké",Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        TextView text_team_popis = (TextView) findViewById(R.id.textView_team_popis);
        text_team_popis.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(SettingsActivity.this,"Nastavení oblíbeného týmu\ntento bude zvýrazněn ve výpisech",Toast.LENGTH_SHORT).show();
                return true;
            }
        });




    }





}

