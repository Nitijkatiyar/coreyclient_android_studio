package com.coremobile.coreyhealth.genericviewandsurvey;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.coremobile.coreyhealth.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitij on 20-04-2016.
 */
public class CMN_GenerateViewComponents {

    private static CMN_GenerateViewComponents COMPONENT_GENERATOR;


    private CMN_GenerateViewComponents() {
    }

    public static synchronized CMN_GenerateViewComponents getComponentGenerator() {
        if (COMPONENT_GENERATOR == null)
            COMPONENT_GENERATOR = new CMN_GenerateViewComponents();

        return COMPONENT_GENERATOR;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public View createComponent(Context context, CMN_GenericFieldModel surveyFieldModel) {
        String viewType = surveyFieldModel.getType();
        View componentView = null;
        if (viewType != null) {
            LinearLayout parent = new LinearLayout(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            parent.setLayoutParams(layoutParams);
            parent.setOrientation(LinearLayout.VERTICAL);

            TextView textView = new TextView(context);
            parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            textView.setTextColor(Color.GRAY);
            textView.setTextSize(18);
            textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            textView.setText(surveyFieldModel.getDisplayText());
            parent.addView(textView);
            if (viewType.equals(CMN_JsonConstants.ViewType.TextField.toString())) {

                EditText editText = new EditText(context);
                parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                editText.setTextSize(18);
                editText.setMinHeight(40);
                editText.setTextColor(Color.BLACK);
                if (surveyFieldModel.getValue().equalsIgnoreCase("null") || surveyFieldModel.getValue() == null) {
                    editText.setText("");
                } else {
                    editText.setText(surveyFieldModel.getValue());
                }
                editText.setBackgroundResource(R.drawable.survey_editbackground);
                editText.setPadding(15, 10, 10, 10);
                if (!surveyFieldModel.isEdit()) {
                    editText.setEnabled(false);
                }
                if (!surveyFieldModel.isDisplay()) {
                    parent.setVisibility(View.GONE);
                }
                if (surveyFieldModel.getValidation().equalsIgnoreCase("phone")) {
                    editText.setInputType(InputType.TYPE_CLASS_PHONE);
                } else {
                    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                }
                if (surveyFieldModel.getMaxLimmit() > 0) {
                    InputFilter[] FilterArray = new InputFilter[1];
                    FilterArray[0] = new InputFilter.LengthFilter(surveyFieldModel.getMaxLimmit());
                    editText.setFilters(FilterArray);
                }
                CMN_ViewFieldModel viewFieldModel = new CMN_ViewFieldModel();
                viewFieldModel.setView(editText);
                viewFieldModel.setSurveyFieldModel(surveyFieldModel);
                viewFieldModel.setInitialValue(surveyFieldModel.getValue());
                CMN_ViewSurveyActivity.viewMap.put("" + surveyFieldModel.getId(), viewFieldModel);
//                CMN_ViewSurveyActivity.initialValueMap.put("" + surveyFieldModel.getId(), viewFieldModel);
                parent.addView(editText);


            } else if (viewType.equals(CMN_JsonConstants.ViewType.ListValue.toString()) && surveyFieldModel.getListValuesData() != null) {

                int pos = 0;
                List<String> options = new ArrayList<String>();
                for (int i = 0; i < surveyFieldModel.getListValuesData().size(); i++) {
                    options.add(surveyFieldModel.getListValuesData().get(i).getValue());
                    if (surveyFieldModel.getValue().equalsIgnoreCase("" + surveyFieldModel.getListValuesData().get(i).getValue())) {
                        pos = i;
                    }
                }


                Spinner spinner = new Spinner(context);
                LinearLayout.LayoutParams spinnerlayoutparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                spinnerlayoutparams.setMargins(0, 10, 0, 10);
                spinner.setLayoutParams(spinnerlayoutparams);
                // Creating adapter for spinner
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, options);

                // Drop down layout style - list view with radio button
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                spinner.setAdapter(dataAdapter);
                spinner.setTag(surveyFieldModel.getDisplayText());
                spinner.setSelection(pos);

                if (!surveyFieldModel.isEdit()) {
                    spinner.setEnabled(false);
                }

                CMN_ViewFieldModel viewFieldModel = new CMN_ViewFieldModel();
                viewFieldModel.setView(spinner);
                viewFieldModel.setInitialValue(surveyFieldModel.getValue());
                viewFieldModel.setSurveyFieldModel(surveyFieldModel);
                CMN_ViewSurveyActivity.viewMap.put("" + surveyFieldModel.getId(), viewFieldModel);
                parent.addView(spinner);

            } else if (viewType.equals(CMN_JsonConstants.ViewType.Switch.toString())) {
                textView.setTextSize(16);

                ToggleButton tb = new ToggleButton(context);
                tb.setTextOn("");
                tb.setBackground(context.getResources().getDrawable(R.drawable.toggleseelctors));
                tb.setTextOff("");
                tb.setChecked(Boolean.parseBoolean(surveyFieldModel.getValue()));
                LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams((int) context.getResources().getDimension(R.dimen.survey_switch_width), (int) context.getResources().getDimension(R.dimen.survey_switch_height));
                layoutparams.setMargins(0, 10, 0, 10);
                tb.setLayoutParams(layoutparams);
                parent.addView(tb);
                if (!surveyFieldModel.isEdit()) {
                    tb.setEnabled(false);
                }
                CMN_ViewFieldModel viewFieldModel = new CMN_ViewFieldModel();
                viewFieldModel.setView(tb);
                viewFieldModel.setInitialValue(surveyFieldModel.getValue());
                viewFieldModel.setSurveyFieldModel(surveyFieldModel);
                CMN_ViewSurveyActivity.viewMap.put("" + surveyFieldModel.getId(), viewFieldModel);
            } else if (viewType.equals(CMN_JsonConstants.ViewType.DateTimePicker.toString())) {

                textView.setTextSize(18);


                TextView textView1 = new TextView(context);
                LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutparams.setMargins(0, 10, 0, 10);
                textView1.setLayoutParams(layoutparams);
                textView1.setTextColor(Color.BLACK);
                textView1.setTextSize(18);
                textView1.setBackgroundResource(R.drawable.survey_editbackground);
                textView1.setText(surveyFieldModel.getValue());
                parent.addView(textView1);

                if (!surveyFieldModel.isEdit()) {
                    textView1.setEnabled(false);
                }
                if (!surveyFieldModel.isDisplay()) {
                    parent.setVisibility(View.GONE);
                }

                CMN_ViewFieldModel viewFieldModel = new CMN_ViewFieldModel();
                viewFieldModel.setSurveyFieldModel(surveyFieldModel);
                viewFieldModel.setView(textView1);
                viewFieldModel.setInitialValue(surveyFieldModel.getValue());
                CMN_ViewSurveyActivity.viewMap.put("" + surveyFieldModel.getId(), viewFieldModel);
            } else if (viewType.equals(CMN_JsonConstants.ViewType.DateTimeYearPicker.toString())) {

                textView.setTextSize(16);


                TextView textView1 = new TextView(context);
                LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutparams.setMargins(0, 10, 0, 10);
                textView1.setLayoutParams(layoutparams);
                textView1.setTextColor(Color.BLACK);
                textView1.setTextSize(18);
                textView1.setBackgroundResource(R.drawable.survey_editbackground);
                textView1.setText(surveyFieldModel.getValue());
                parent.addView(textView1);

                if (!surveyFieldModel.isEdit()) {
                    textView1.setEnabled(false);
                }
                if (!surveyFieldModel.isDisplay()) {
                    parent.setVisibility(View.GONE);
                }

                CMN_ViewFieldModel viewFieldModel = new CMN_ViewFieldModel();
                viewFieldModel.setSurveyFieldModel(surveyFieldModel);
                viewFieldModel.setView(textView1);
                viewFieldModel.setInitialValue(surveyFieldModel.getValue());
                CMN_ViewSurveyActivity.viewMap.put("" + surveyFieldModel.getId(), viewFieldModel);
            }
            componentView = parent;
        }

        return componentView;
    }
}
