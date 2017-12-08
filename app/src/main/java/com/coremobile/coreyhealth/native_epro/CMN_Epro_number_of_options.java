package com.coremobile.coreyhealth.native_epro;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.base.CMN_AppBaseActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by nitij on 09-01-2017.
 */
public class CMN_Epro_number_of_options extends CMN_AppBaseActivity {

    NumberPicker numberPicker;
    EditText editText1, editText2, editText3, editText4, editText5;
    List<EditText> editTexts;
    Intent intent;
    int numberoptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.epro_number_of_option);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle(
                "" + getResources().getString(R.string.options));
        getActionBar().setDisplayHomeAsUpEnabled(true);

        editTexts = new ArrayList<>();
        intent = getIntent();

        numberPicker = (NumberPicker) findViewById(R.id.epro_add_numberofoptions_picker);
        numberPicker.setMinValue(2);
        numberPicker.setMaxValue(5);

        editText1 = (EditText) findViewById(R.id.add_epro_option1);
        editText2 = (EditText) findViewById(R.id.add_epro_option2);
        editText3 = (EditText) findViewById(R.id.add_epro_option3);
        editText4 = (EditText) findViewById(R.id.add_epro_option4);
        editText5 = (EditText) findViewById(R.id.add_epro_option5);

        editText1.clearFocus();
        editText2.clearFocus();
        editText3.clearFocus();
        editText4.clearFocus();
        editText5.clearFocus();

        editTexts.add(editText1);
        editTexts.add(editText2);
        editTexts.add(editText3);
        editTexts.add(editText4);
        editTexts.add(editText5);

        editText3.setVisibility(View.GONE);
        editText4.setVisibility(View.GONE);
        editText5.setVisibility(View.GONE);

        if (intent != null && intent.hasExtra("numberofoptions")) {
            numberoptions = intent.getIntExtra("numberofoptions", 0);
            showhideedittexts(numberoptions);
            numberPicker.setValue(numberoptions);
        }
        List<String> items = Arrays.asList(intent.getStringExtra("data").toString().split("\\s*,\\s*"));
        for (int i = 0; i < items.size(); i++) {
            editTexts.get(i).setText(items.get(i));
        }

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                editText1.setVisibility(View.GONE);
                editText2.setVisibility(View.GONE);
                editText3.setVisibility(View.GONE);
                editText4.setVisibility(View.GONE);
                editText5.setVisibility(View.GONE);

                showhideedittexts(newVal);
            }
        });

    }

    private void showhideedittexts(int value) {
        for (int i = 0; i < value; i++) {
            editTexts.get(i).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.savesurveydata, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;

            case R.id.action_save:
                String options = "";
                for (int i = 0; i < numberPicker.getValue(); i++) {
                    if (i == numberPicker.getValue() - 1) {
                        options = options + editTexts.get(i).getText().toString();
                    } else {
                        options = options + editTexts.get(i).getText().toString() + ",";
                    }
                }
                Intent intent = new Intent();
                intent.putExtra("optionsCount", numberPicker.getValue());
                intent.putExtra("optionsValue", "" + options);
                setResult(RESULT_OK, intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
