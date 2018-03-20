package cz.foxiczek.mfolomouc;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class UpdateActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        final Bundle bundle = getIntent().getExtras();
        DBOpenHelper dbo = new DBOpenHelper(getApplicationContext());
        TextView name_zapas = (TextView) findViewById(R.id.textView_zapas_zapas);
        name_zapas.setTextSize(20f);
        name_zapas.setText(bundle.getString("ZAPAS"));
        generateTable(dbo.getAllPlayers());
        //test
    }

    private void generateTable(Cursor cursor) {
        TableLayout table = (TableLayout) findViewById(R.id.table_update_goly);
        if (table.getChildCount() > 0) {
            table.removeAllViews();
        }

        while (cursor.moveToNext()) {
            DBOpenHelper db = new DBOpenHelper(getApplicationContext());
            Bundle bundle = getIntent().getExtras();
            final int id_zapasu = Integer.valueOf(bundle.getString("ID_ZAPASU"));
            TableRow tr = new TableRow(this);
            tr.setBackground(getResources().getDrawable(R.drawable.cellborder));
            GridLayout grid = new GridLayout(this);
            TextView jmeno = new TextView(this);
            jmeno.setPadding(0,40,0,0);

            final int id_hrace = Integer.valueOf(cursor.getString(0));
            jmeno.setText(cursor.getString(1));
            final TextView goly = new TextView(this);
            Cursor queryGoly = db.getHracZapas(id_hrace,id_zapasu);
            goly.setPadding(0,40,0,0);
            if (db.getHracZapas(id_hrace,id_zapasu).getCount() > 0) {
                while(queryGoly.moveToNext()){
                    goly.setText(queryGoly.getString(2));
                }
            } else {
                goly.setText("0");
            }

            jmeno.setWidth(400);
            ImageButton plus = new ImageButton(this);
            ImageButton minus = new ImageButton(this);
            plus.setImageResource(R.mipmap.plus_custom);
            minus.setImageResource(R.mipmap.minus_custom);
            plus.setScaleX(0.6f);
            plus.setScaleY(0.6f);
            minus.setScaleX(0.6f);
            minus.setScaleY(0.6f);
            plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DBOpenHelper db = new DBOpenHelper(getApplicationContext());
                    Bundle bundle = getIntent().getExtras();
                    final int num_goly = Integer.valueOf(goly.getText().toString()) + 1;
                    final int id_zapasu = Integer.valueOf(bundle.getString("ID_ZAPASU"));
                    db.updateGoly(id_hrace, Integer.valueOf(bundle.getString("ID_ZAPASU")), num_goly);
                    System.err.println("DEBUG : " + id_hrace + " - " + bundle.getString("ID_ZAPASU") + " - " + num_goly);
                    Cursor cursor = db.getHracZapas(id_hrace, id_zapasu);
                    while (cursor.moveToNext()) {
                        System.err.println(cursor.getString(2));
                        String gol = cursor.getString(2);
                        goly.setText(gol);
                    }

                }
            });

            minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DBOpenHelper db = new DBOpenHelper(getApplicationContext());
                    Bundle bundle = getIntent().getExtras();
                    final int num_goly = Integer.valueOf(goly.getText().toString()) - 1;
                    if(num_goly >= 0) {
                        final int id_zapasu = Integer.valueOf(bundle.getString("ID_ZAPASU"));
                        db.updateGoly(id_hrace, Integer.valueOf(bundle.getString("ID_ZAPASU")), num_goly);
                        System.err.println("DEBUG : " + id_hrace + " - " + bundle.getString("ID_ZAPASU") + " - " + num_goly);
                        Cursor cursor = db.getHracZapas(id_hrace, id_zapasu);
                        while (cursor.moveToNext()) {
                            System.err.println(cursor.getString(2));
                            String gol = cursor.getString(2);
                            goly.setText(gol);
                        }
                    }
                }
            });
            grid.addView(jmeno);
            grid.addView(minus);
            grid.addView(goly);
            grid.addView(plus);
            tr.addView(grid);
            table.addView(tr);

        }


    }

}
