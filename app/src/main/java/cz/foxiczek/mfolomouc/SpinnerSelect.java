package cz.foxiczek.mfolomouc;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liskbpet on 6. 9. 2017.
 */

public class SpinnerSelect implements AdapterView.OnItemSelectedListener {
    Spinner spinner, spinner_team;
    TextView tv;
    Context context;

    public SpinnerSelect(Spinner spinner, TextView tv, Spinner spinner_team, Context context) {
        this.spinner = spinner;
        this.spinner_team = spinner_team;
        this.tv = tv;
        this.context = context;
    }


    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        //tv.setText(spinner.getSelectedItem().toString());
        SharedPrefs sf = new SharedPrefs(context);
        int select;

        String oznac;
        String tmp = spinner.getSelectedItem().toString();
        String tmp2 = tmp.replaceAll("liga", "");
        tmp2 = tmp2.replaceAll(" ", "");
        tmp = tmp2.replaceAll("\\.", "");
        oznac = "liga_" + tmp;
        System.err.println("DEBUG : " + oznac);
        select = context.getResources().getIdentifier(oznac, "array", context.getPackageName());

        System.err.println("DEBUG : " + select);
        String[] teamy = context.getResources().getStringArray(select);
        List<String> list_teamy = new ArrayList<String>();
        for (String team : teamy) {
            list_teamy.add(team);
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list_teamy);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_team.setAdapter(dataAdapter);

        spinner_team.setOnItemSelectedListener(new SpinnerTeam(tv));
        spinner_team.setSelection(0);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

}
