package com.coremobile.coreyhealth.patientactivitylog;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.surveylog.SurveyModel;

import java.text.NumberFormat;
import java.util.List;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.LongPressAwareTableDataAdapter;


public class SurveyLogDataAdapter extends LongPressAwareTableDataAdapter<SurveyModel> {

    private static final int TEXT_SIZE = 14;
    private static final NumberFormat PRICE_FORMATTER = NumberFormat.getNumberInstance();


    public SurveyLogDataAdapter(final Context context, final List<SurveyModel> data, final TableView<SurveyModel> tableView) {
        super(context, data, tableView);
    }

    @Override
    public View getDefaultCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        final SurveyModel surveyModel = getRowData(rowIndex);
        View renderedView = null;

        switch (columnIndex) {
            case 0:
                //              renderedView = renderProducerLogo(car, parentView);
                renderedView = renderPatient(surveyModel);
                break;
            case 1:
                renderedView = renderSurgeon(surveyModel, parentView);
                break;
            case 2:
                renderedView = renderDOSurgery(surveyModel);
                break;
            case 3:
                renderedView = renderDOSurvey(surveyModel);
                break;

            case 4:
                renderedView = renderviewform(surveyModel,parentView);
                break;
        }

        return renderedView;
    }

    @Override
    public View getLongPressCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        final SurveyModel car = getRowData(rowIndex);
        View renderedView = null;

        switch (columnIndex) {
            case 1:
                break;
            default:
                renderedView = getDefaultCellView(rowIndex, columnIndex, parentView);
        }

        return renderedView;
    }


    private View renderPatient(final SurveyModel car) {


       /* if (car.getPrice() < 50000) {
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.table_price_low));
        } else if (car.getPrice() > 100000) {
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.table_price_high));
        }*/

        return renderString(car.getPatientName());
    }

    private View renderSurgeon(final SurveyModel car, final ViewGroup parentView) {
        /*final View view = getLayoutInflater().inflate(R.layout.table_cell_power, parentView, false);
        final TextView kwView = (TextView) view.findViewById(R.id.surgeonVw);

        kwView.setText(car.getSurgeon());
        return view;*/
        return renderString(car.getSurgeon());
    }

    private View renderDOSurgery(final SurveyModel car) {

        return renderString(car.getDos());
    }

    private View renderDOSurvey(final SurveyModel car) {


       /* if (car.getPrice() < 50000) {
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.table_price_low));
        } else if (car.getPrice() > 100000) {
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.table_price_high));
        }*/

        return renderString(car.getDateofsurvey());
    }

    private View renderviewform(final SurveyModel model, final ViewGroup parentView) {

        final View view = getLayoutInflater().inflate(R.layout.table_cell_power, parentView, false);
        final TextView viewFormVw = (TextView) view.findViewById(R.id.viewform);

        if(!model.getViewForm().isEmpty())
        viewFormVw.setText("Click Here");
        return view;
    }
/*
    private View renderProducerLogo(final Car car, final ViewGroup parentView) {
        final View view = getLayoutInflater().inflate(R.layout.table_cell_image, parentView, false);
        final ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        imageView.setImageResource(car.getProducer().getLogo());
        return view;
    }*/

    private View renderString(final String value) {
        final TextView textView = new TextView(getContext());
        textView.setText(value);
        textView.setPadding(20, 10, 20, 10);
        textView.setTextSize(TEXT_SIZE);
        textView.setTextColor(Color.parseColor("#000000"));
        return textView;
    }


}
