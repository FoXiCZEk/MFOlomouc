package cz.foxiczek.mfolomouc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;


public class MyTeamActivity extends AppCompatActivity {
    //todo: pridat notifikace pred zapasem
    private CallbackManager callbackManager;
    private static String TAG = MyTeamActivity.class.getName();
    private LoginManager loginManager;
    ShareDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_team);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        //shareDialog.registerCallback(callbackManager);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        System.err.println("DEBUG : " + result.toString() + result.getPostId());
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {
                        System.err.println("DEBUG : " + error.getMessage());
                    }
                });

                DBOpenHelper dbo = new DBOpenHelper(getApplicationContext());
        SharedPrefs sp = new SharedPrefs(getApplicationContext());
        Button button_zapasy = (Button) findViewById(R.id.button_zapasy);
        Button button_nactiZapasy = (Button) findViewById(R.id.button_loadZapasy);
        Button show_hraci = (Button) findViewById(R.id.button_show_hraci);
        final ProgressBar progress = (ProgressBar) findViewById(R.id.progressBar_loading);


        if (sp.readConfigString("favTeam").equals("")) {
            FavNotSet();
            button_zapasy.setVisibility(View.INVISIBLE);
            button_nactiZapasy.setVisibility(View.INVISIBLE);
            show_hraci.setVisibility(View.INVISIBLE);
        } else {
            progress.setVisibility(View.GONE);
            generateTable(dbo.getAllPlayers());
            System.err.println(dbo.checkFavTeam(sp.readConfigString("favTeam")));
            DateFormat dateFormat = new SimpleDateFormat("MM");
            Date date = new Date();
            int count;
            if (Integer.valueOf(dateFormat.format(date)) > 8 && Integer.valueOf(dateFormat.format(date)) <= 12) {
                count = 13;
            } else if (Integer.valueOf(dateFormat.format(date)) > 2 && Integer.valueOf(dateFormat.format(date)) <= 8) {
                count = 26;
            } else if (Integer.valueOf(dateFormat.format(date)) <= 2) {
                count = 13;
            } else {
                count = 13;
            }


            if (dbo.checkFavTeam(sp.readConfigString("favTeam")) && dbo.getCountZapasy() == count) {

                button_zapasy.setVisibility(View.VISIBLE);
                button_nactiZapasy.setVisibility(View.INVISIBLE);
            } else {
                button_zapasy.setVisibility(View.INVISIBLE);
                button_nactiZapasy.setVisibility(View.VISIBLE);
            }


            button_zapasy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DBOpenHelper dbo = new DBOpenHelper(getApplicationContext());
                    generateZapasy(dbo.getZapasy());
                }
            });

            button_nactiZapasy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar_loading);


                    if (isNetworkAvailable()) {
                        SharedPrefs sf = new SharedPrefs(getApplicationContext());
                        new AsyncWWW().execute();
                    } else {
                        throwError();
                    }
                }
            });

            show_hraci.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DBOpenHelper dbo = new DBOpenHelper(getApplicationContext());
                    generateTable(dbo.getAllPlayers());
                }
            });
        }
        Button share = (Button) findViewById(R.id.fb_share_button);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TableLayout table_players = (TableLayout) findViewById(R.id.table_players);
                ScrollView data = (ScrollView) findViewById(R.id.scrollView_data);
                table_players.setDrawingCacheEnabled(true);
                table_players.buildDrawingCache(true);
                table_players.setBackgroundColor(Color.WHITE);
                Bitmap image = loadBitmapFromView(table_players, table_players.getWidth(), table_players.getHeight());
                table_players.setDrawingCacheEnabled(false);
                SharePhoto photo = new SharePhoto.Builder()
                        .setBitmap(image)
                        .setCaption("#Testing")
                        .build();
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();

                shareDialog.show(content);  // Show facebook ShareDialog
                ShareApi.share(content,null);
            }

        });

        if(Profile.getCurrentProfile() != null){
            share.setVisibility(View.VISIBLE);
        }else{
            share.setVisibility(View.INVISIBLE);
        }

    }


    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        callbackManager.onActivityResult(requestCode, responseCode, data);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                if (isNetworkAvailable()) {
                    Intent intent = new Intent(MyTeamActivity.this, SettingsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                } else {
                    throwError();
                    return false;
                }


            case R.id.action_adresar:
                if (isNetworkAvailable()) {
                    Intent adresar = new Intent(MyTeamActivity.this, AdresarActivity.class);
                    adresar.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(adresar);
                    return true;
                } else {
                    throwError();
                    return false;
                }

            case R.id.action_home:
                Intent intent1 = new Intent(MyTeamActivity.this, MainActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
                return true;

            case R.id.action_vypisy:
                if (isNetworkAvailable()) {
                    Intent intent2 = new Intent(MyTeamActivity.this, VypisyActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent2);
                    return true;
                } else {
                    throwError();
                    return false;
                }

            case R.id.action_myTeam:
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    private void throwError() {
        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(MyTeamActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("nenalezeno internetove spojeni");
        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEUTRAL, "UKONČIT",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                });
        alertDialog.show();


    }

    private void FavNotSet() {
        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(MyTeamActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Oblíbený tým nebyl nastaven. \n prosím nastavte v Nastavení");
        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


        alertDialog.show();


    }


    private void showLoading() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar_loading);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void dismissLoading() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar_loading);
        progressBar.setVisibility(View.GONE);
    }


    public void generateTable(final Cursor cursor) {
        final DBOpenHelper dbo = new DBOpenHelper(getApplicationContext());
        TableLayout table = (TableLayout) findViewById(R.id.table_players);
        if (table.getChildCount() > 0) {
            table.removeAllViews();
        }

        if (cursor.getCount() == 0) {
            TableRow row_add = new TableRow(this);
            Button button_add_player = new Button(this);
            button_add_player.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DBOpenHelper dbo = new DBOpenHelper(getApplicationContext());
                    LayoutInflater li = LayoutInflater.from(MyTeamActivity.this);
                    final View promptsView = li.inflate(R.layout.add_dialog, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MyTeamActivity.this);
                    alertDialogBuilder.setView(promptsView);
                    alertDialogBuilder.setTitle("Přidej hráče");
                    final AlertDialog alertDialog = alertDialogBuilder.create();
                    final EditText jmeno = (EditText) promptsView.findViewById(R.id.editText_name);
                    final EditText dres = (EditText) promptsView.findViewById(R.id.editText2);
                    Button pridej = (Button) promptsView.findViewById(R.id.button_add_hrac);

                    pridej.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DBOpenHelper dbo = new DBOpenHelper(getApplicationContext());
                            String name = jmeno.getText().toString();
                            if (dres.getText().equals("")) {
                                System.err.println("DEBUG : INTEGER ERROR");
                            } else {
                                if (dres.getText().toString().length() > 0 && jmeno.getText().toString().length() > 1) {
                                    dbo.addPlayer(jmeno.getText().toString(), Integer.valueOf(dres.getText().toString()));
                                }
                            }
                            alertDialog.dismiss();
                            generateTable(dbo.getAllPlayers());
                        }
                    });
                    alertDialog.show();
                }
            });
            button_add_player.setText("Přidej hráče");
            row_add.addView(button_add_player);

            table.addView(row_add);
        } else {
            TableRow.LayoutParams lp = new TableRow.LayoutParams();
            lp.setMargins(0, 20, 0, 20);
            GridLayout grid = new GridLayout(this);
            TableRow popis = new TableRow(this);
            TextView popis_jmeno = new TextView(this);
            TextView popis_dres = new TextView(this);
            TextView popis_goly = new TextView(this);
            popis_jmeno.setText("Jméno");
            popis_jmeno.setWidth(400);
            popis_dres.setText("č. dresu");
            popis_dres.setWidth(300);
            popis_goly.setText("počet gólů");
            grid.addView(popis_jmeno);
            grid.addView(popis_dres);
            grid.addView(popis_goly);
            popis.addView(grid, lp);
            table.addView(popis);
            DBOpenHelper db = new DBOpenHelper(getApplicationContext());

            //System.err.println("DEBUG : CURSOUR COUNT = " + cursor.getCount());


            while (cursor.moveToNext()) {
                TableRow tr = new TableRow(this);
                tr.setBackground(getResources().getDrawable(R.drawable.cellborder));
                GridLayout grid_popis = new GridLayout(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.setGravity(Gravity.CENTER);
                grid_popis.setLayoutParams(params);
                tr.setGravity(Gravity.CENTER);
                final TextView name = new TextView(this);
                name.setWidth(400);
                name.setPadding(0, 60, 0, 0);
                final TextView dres = new TextView(this);
                dres.setWidth(280);
                final TextView goly = new TextView(this);
                final int id_hrace = Integer.valueOf(cursor.getString(0));
                System.err.println("DEBUG : id_hrace = " + id_hrace);

                Cursor hracGoly = db.getHracTotalGoly(id_hrace);
                System.err.println("DEBUG : Cursor count = " + hracGoly.getCount());

                while (hracGoly.moveToNext()) {
                    if (hracGoly.getString(0) == null) {
                        goly.setText("0");
                    } else {
                        //   System.err.println("DEBUG : goly = " + hracGoly.getString(0));
                        //  System.err.println("DEBUG : cc = " + hracGoly.getColumnCount());
                        goly.setText(hracGoly.getString(0));
                    }
                }

                final int id = Integer.valueOf(cursor.getString(0));
                //System.err.println("DEBUG : CURSOR = " + cursor.getString(1));
                dres.setText(cursor.getString(2));
                goly.setTextSize(20);
                grid_popis.addView(name);
                grid_popis.addView(dres);
                grid_popis.addView(goly);
                name.setText(cursor.getString(1));
                name.setTextColor(Color.BLACK);

                tr.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final DBOpenHelper dbo = new DBOpenHelper(getApplicationContext());
                        LayoutInflater li = LayoutInflater.from(MyTeamActivity.this);
                        final View promptsView = li.inflate(R.layout.delete_dialog, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MyTeamActivity.this);
                        alertDialogBuilder.setView(promptsView);
                        final TextView textView_delete_name = (TextView) promptsView.findViewById(R.id.textView_delete_name);
                        textView_delete_name.setText(name.getText());
                        final Button odeber = (Button) promptsView.findViewById(R.id.button_odeber);

                        alertDialogBuilder.setView(promptsView);
                        alertDialogBuilder.setTitle("uprav hráče");
                        final AlertDialog alertDialog = alertDialogBuilder.create();
                        odeber.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dbo.removePlayer(id);
                                alertDialog.dismiss();
                                generateTable(dbo.getAllPlayers());

                            }
                        });

                        final Button uprav = (Button) promptsView.findViewById(R.id.button_uprav);
                        uprav.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                textView_delete_name.setVisibility(View.INVISIBLE);
                                final EditText upravText = (EditText) promptsView.findViewById(R.id.editText_uprav);
                                final TextView uprav_popis_name = (TextView) promptsView.findViewById(R.id.textView_delete_popis_name);
                                uprav_popis_name.setVisibility(View.VISIBLE);
                                final TextView uprav_popis_dres = (TextView) promptsView.findViewById(R.id.textView_delete_num_popis);
                                uprav_popis_dres.setVisibility(View.VISIBLE);
                                final EditText uprav_dres = (EditText) promptsView.findViewById(R.id.editText_uprav_dres);
                                uprav_dres.setVisibility(View.VISIBLE);
                                uprav_dres.setText(dres.getText());
                                upravText.setVisibility(View.VISIBLE);
                                odeber.setVisibility(View.INVISIBLE);
                                uprav.setVisibility(View.INVISIBLE);
                                upravText.setText(textView_delete_name.getText().toString());
                                Button OK = (Button) promptsView.findViewById(R.id.button_uprav_ok);
                                OK.setVisibility(View.VISIBLE);
                                OK.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dbo.updatePlayer(id, upravText.getText().toString(), Integer.valueOf(uprav_dres.getText().toString()));
                                        alertDialog.dismiss();
                                        generateTable(dbo.getAllPlayers());
                                    }
                                });
                            }
                        });


                        alertDialog.show();
                    }
                });
                tr.addView(grid_popis, lp);
                table.addView(tr);
            }

            TableRow row_add = new TableRow(this);
            Button button_add_player = new Button(this);
            button_add_player.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DBOpenHelper dbo = new DBOpenHelper(getApplicationContext());
                    LayoutInflater li = LayoutInflater.from(MyTeamActivity.this);
                    final View promptsView = li.inflate(R.layout.add_dialog, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MyTeamActivity.this);
                    alertDialogBuilder.setView(promptsView);
                    alertDialogBuilder.setTitle("přidej hráče");
                    final AlertDialog alertDialog = alertDialogBuilder.create();
                    final EditText jmeno = (EditText) promptsView.findViewById(R.id.editText_name);
                    final EditText dres = (EditText) promptsView.findViewById(R.id.editText2);
                    Button pridej = (Button) promptsView.findViewById(R.id.button_add_hrac);

                    pridej.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DBOpenHelper dbo = new DBOpenHelper(getApplicationContext());
                            String name = jmeno.getText().toString();
                            System.err.println("DEBUG : dres = " + dres.getText().toString() + " jmeno = " + jmeno.getText().toString());
                            if (dres.getText().equals("")) {
                                System.err.println("DEBUG : INTEGER ERROR");
                            } else {
                                if (dres.getText().toString().length() > 0 && jmeno.getText().toString().length() > 1) {
                                    dbo.addPlayer(jmeno.getText().toString(), Integer.valueOf(dres.getText().toString()));
                                }
                            }
                            alertDialog.dismiss();
                            generateTable(dbo.getAllPlayers());
                        }
                    });
                    alertDialog.show();
                }
            });
            button_add_player.setText("přidej hráče");
            row_add.addView(button_add_player);

            table.addView(row_add);
        }

    }


    private void generateZapasy(Cursor cursor) {
        TableLayout table01 = (TableLayout) findViewById(R.id.table_players);
        if (table01.getChildCount() > 0) {
            table01.removeAllViews();
        }
        DBOpenHelper check = new DBOpenHelper(getApplicationContext());
        SharedPrefs sp = new SharedPrefs(getApplicationContext());
        if (sp.readConfigString("favTeam").equals("")) {
            FavNotSet();
        } else {
            if (check.checkFavTeam(sp.readConfigString("favTeam"))) {
                while (cursor.moveToNext()) {
                    TableRow tr = new TableRow(this);
                    tr.setBackground(getResources().getDrawable(R.drawable.cellborder));
                    tr.setMinimumHeight(150);
                    TextView text = new TextView(this);
                    text.setWidth(650);
                    TextView datum = new TextView(this);
                    final String ID_ZAPASU = cursor.getString(0);
                    final String ZAPAS = cursor.getString(1);
                    text.setText(cursor.getString(1));
                    datum.setText(cursor.getString(2));
                    tr.addView(text);
                    DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
                    Date matchDate, ted;


                    try {
                        String timeStamp = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
                        matchDate = df.parse(cursor.getString(2));
                        ted = df.parse(timeStamp);
                        if (matchDate.compareTo(ted) > 0) {
                            System.err.println(timeStamp + " AFTER " + cursor.getString(2));
                        } else if (matchDate.compareTo(ted) < 0 || matchDate.compareTo(ted) == 0) {
                            System.err.println(timeStamp + " BEFORE " + cursor.getString(2));
                            tr.setBackgroundColor(getResources().getColor(R.color.zapas_editable));
                            tr.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent update = new Intent(MyTeamActivity.this, UpdateActivity.class);
                                    update.putExtra("ID_ZAPASU", ID_ZAPASU);
                                    update.putExtra("ZAPAS", ZAPAS);
                                    startActivity(update);

                                }
                            });

                        } else {
                            System.err.println("NEROZUMIM " + cursor.getString(2));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    tr.addView(datum);
                    table01.addView(tr);
                }
            } else {
                check.clearZapasy();
                check.clearScoreTable();
                Button getZapasy = (Button) findViewById(R.id.button_loadZapasy);
                getZapasy.setVisibility(View.VISIBLE);
                Button showZapasy = (Button) findViewById(R.id.button_zapasy);
                showZapasy.setVisibility(View.INVISIBLE);
            }
        }
    }


    public boolean getWWWData() {
        SharedPrefs sp = new SharedPrefs(getApplicationContext());
        //dialog = ProgressDialog.show(MyTeamActivity.this, "","Loading. Please wait...", false);
        if (sp.readConfigString("favTeam").equals("")) {
            FavNotSet();
            return false;
        } else {

            DBOpenHelper check = new DBOpenHelper(this);
            if (check.checkFavTeam(sp.readConfigString("favTeam")) && check.getCountZapasy() < 13) {
                System.err.println("DEBUG : ALERT DIALOG BEFORE");


                //LoadingDialog loading = new LoadingDialog();


                System.err.println("DEBUG : ALERT DIALOG AFTER");
                System.err.println("DEBUG : " + sp.readConfigString("favTeam"));
                DateFormat dateFormat = new SimpleDateFormat("MM");
                Date date = new Date();
                int mesic = Integer.valueOf(dateFormat.format(date));
                String obdobi;
                if (mesic >= 8) {
                    obdobi = "p";
                } else {
                    obdobi = "j";
                }

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                try {
                    DBOpenHelper db = new DBOpenHelper(this);
                    if ((db.getCountZapasy() < 13 && obdobi.equals("p")) || (db.getCountZapasy() < 26 && obdobi.equals("j"))) {
                        for (int x = 1; x < 14; x++) {
                            StrictMode.setThreadPolicy(policy);
                            HttpGet httpget = new HttpGet("http://www.mfolomouc.cz/rozlosovani/?kolo=" + x + "&obdobi=" + obdobi);
                            System.err.println("DEBUG : " + obdobi + " - " + x);
                            CloseableHttpClient httpclient = HttpClients.createDefault();
                            String[] output = new String[1];

                            CloseableHttpResponse httpresponse = httpclient.execute(httpget);
                            HttpEntity httpentity = httpresponse.getEntity();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(httpentity.getContent()));
                            StringBuilder total = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                total.append(line);
                            }
                            output[0] = total.toString();
                            String tabulka = output[0];
                            int start = tabulka.indexOf("hlavicka");
                            int end = tabulka.indexOf("cleared", start);
                            tabulka = tabulka.substring(start, end);
                            //System.err.println("DEBUG : " + tabulka);
                            String[] tmp = tabulka.split("</table>");
                            for (int a = 0; a < tmp.length; a++) {
                                //System.err.println("DEBUG : " + tmp[a]);
                                if (tmp[a].contains(sp.readConfigString("favTeam"))) {
                                    String[] tmp2 = tmp[a].split("</tr>");
                                    //System.err.println("DEBUG : tmp = " + tmp[4]);
                                    for (int b = 0; b < tmp2.length; b++) {
                                        if (tmp2[b].contains(sp.readConfigString("favTeam"))) {
                                            //System.err.println("DEBUG : tmp2 = " + tmp2[b]);
                                            String[] tmp3 = tmp2[b].split("</td>");
                                            String out = tmp3[3].replaceAll("<td>", "");
                                            String datum = tmp3[0].replaceAll("<tr class='lichy'>", "");
                                            datum = datum.replaceAll("<td class='datum'>", "");
                                            datum = datum.replaceAll("<td>", "");
                                            datum = datum.replaceAll("<tr>", "");
                                            System.err.println("DEBUG : datum + out = " + datum + " - " + out);
                                            db.insertZapas(out, datum, obdobi);
                                            db.close();
                                            //ProgressBar loading = (ProgressBar) findViewById(R.id.progressBar_loading);
                                            //loading.setVisibility(View.VISIBLE);

                                        }
                                    }
                                }
                            }

                            EntityUtils.consume(httpentity);
                            httpresponse.close();
                            httpclient.close();
                        }
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
                //Button getZapasy = (Button) findViewById(R.id.button_loadZapasy);
                //getZapasy.setVisibility(View.INVISIBLE);
                //Button showZapasy = (Button) findViewById(R.id.button_zapasy);
                //showZapasy.setVisibility(View.VISIBLE);


                //ProgressBar loading = (ProgressBar) findViewById(R.id.progressBar_loading);
                //loading.setVisibility(View.INVISIBLE);
                //dialog.dismiss();
                return true;
            } else {
                DBOpenHelper dbo = new DBOpenHelper(getApplicationContext());
                dbo.clearZapasy();

                dbo.clearScoreTable();
                getWWWData();
                //ProgressBar loading = (ProgressBar) findViewById(R.id.progressBar_loading);
                //loading.setVisibility(View.INVISIBLE);
                return true;
            }
        }
        // return output[0];
    }

    private class AsyncWWW extends AsyncTask<Void, Void, Boolean> {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar_loading);
        Button nacti = (Button) findViewById(R.id.button_loadZapasy);
        private Trace mTrace;
        @Override
        protected void onPreExecute() {
            mTrace = FirebasePerformance.getInstance().newTrace("startup_trace");
            mTrace.start();
            nacti.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            if (getWWWData()) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            progressBar.setVisibility(View.GONE);
            mTrace.stop();
            if (result) {
                Button getZapasy = (Button) findViewById(R.id.button_loadZapasy);
                getZapasy.setVisibility(View.INVISIBLE);
                Button showZapasy = (Button) findViewById(R.id.button_zapasy);
                showZapasy.setVisibility(View.VISIBLE);
                generateTable(new DBOpenHelper(getApplicationContext()).getAllPlayers());
            } else {
                nacti.setEnabled(true);
            }
        }
    }

}


