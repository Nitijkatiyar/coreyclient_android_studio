package com.coremobile.coreyhealth.native_epro;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.base.CMN_AppBaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitij on 06-01-2017.
 */
public class CMN_DependentePROsActivity extends CMN_AppBaseActivity {

    List<CMN_eproModel> eproModels;
    List<String> eprosList;
    Spinner selectdependentePROs;
    RadioGroup radioGroup;
    ListView listView;
    Button addepro;
    Intent intent;
    boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eprosdependent);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle(
                "Select Dependency");
        getActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (ListView) findViewById(R.id.listview);
        selectdependentePROs = (Spinner) findViewById(R.id.selectdependentePROs);
        radioGroup = (RadioGroup) findViewById(R.id.epro_new_dependent_response);
        addepro = (Button) findViewById(R.id.buttonAddePROs);
        intent = getIntent();
        if (intent != null && intent.hasExtra("isEdit")) {
            isEdit = true;
            showList();
        }


        eprosList = new ArrayList<>();
        for (int i = 0; i < CMN_EproListActivity.eproModels.size(); i++) {
            eprosList.add(CMN_EproListActivity.eproModels.get(i).getQuestion());
        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, eprosList); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectdependentePROs.setAdapter(spinnerArrayAdapter);

        selectdependentePROs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                radioGroup.removeAllViews();
                int size = Integer.parseInt(CMN_EproListActivity.eproModels.get(pos).getOptionsCount());
                for (int i = 0; i < size; i++) {
                    RadioButton rdbtn = new RadioButton(CMN_DependentePROsActivity.this);
                    rdbtn.setId(i + 1);
                    rdbtn.setText(CMN_EproListActivity.eproModels.get(pos).getOptions().get(i));
                    radioGroup.addView(rdbtn);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        addepro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CMN_eproModel model = CMN_EproListActivity.eproModels.get(selectdependentePROs.getSelectedItemPosition());
                CMN_ePROsDependentModel model1 = new CMN_ePROsDependentModel();
                model1.setId(Integer.parseInt(model.getID()));
                model1.setQuestion(model.getQuestion());
                model1.setDependentResponseOption(radioGroup.getCheckedRadioButtonId());

                CMN_AddNewEproActivity.dependentModels.add(model1);
                showList();
            }
        });
    }

    public void showList() {
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < CMN_AddNewEproActivity.dependentModels.size(); i++) {
            stringList.add(CMN_AddNewEproActivity.dependentModels.get(i).getQuestion());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, stringList);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
