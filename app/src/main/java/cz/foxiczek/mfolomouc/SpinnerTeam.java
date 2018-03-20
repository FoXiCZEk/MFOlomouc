package cz.foxiczek.mfolomouc;

import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

/**
 * Created by liskbpet on 6. 9. 2017.
 */

public class SpinnerTeam  implements AdapterView.OnItemSelectedListener {
    TextView tv;
    public SpinnerTeam(TextView tv){
        this.tv = tv;

    }


    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        tv.setText("oznaceno : " + parent.getSelectedItem().toString());
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

}
