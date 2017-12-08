package com.coremobile.coreyhealth.analytics;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.base.CMN_AppBaseActivity;
import com.coremobile.coreyhealth.errorhandling.CMN_ErrorMessages;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Nitij Katiyar
 */
public class CMN_DrawAnalyticsGraphActivityCMN extends CMN_AppBaseActivity {

    Intent intent;
    RelativeLayout hbar;
    LinearLayout barLayout, hBarLayout;
    LinearLayout pieLayout;

    public static String TAG = "Corey_DrawGraph";
    CMN_GraphModel CNMGraphModel;
    TextView noData;
    TableLayout table;
    HorizontalScrollView tableLayout;
    com.github.mikephil.charting.charts.BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyticsgraph);

        barLayout = (LinearLayout) findViewById(R.id.bar_chart);
        barChart = (com.github.mikephil.charting.charts.BarChart) findViewById(R.id.vbarChart);

        hBarLayout = (LinearLayout) findViewById(R.id.hbar_chart);
        hbar = (RelativeLayout) findViewById(R.id.hbar);
        pieLayout = (LinearLayout) findViewById(R.id.pi_chart);
        tableLayout = (HorizontalScrollView) findViewById(R.id.table_layout);

        table = (TableLayout) findViewById(R.id.table);

        noData = (TextView) findViewById(R.id.textNoData);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle(
                "" + getResources().getString(R.string.analytics));
        getActionBar().setDisplayHomeAsUpEnabled(true);
        intent = getIntent();

        if (intent.hasExtra("data")) {
            CNMGraphModel = (CMN_GraphModel) intent.getSerializableExtra("data");

            getActionBar().setTitle(
                    "" + intent.getStringExtra("title"));

            if (CNMGraphModel.getGraphOptions() != null && CNMGraphModel.getGraphOptions().getType() != null) {
                //Log.e("getType", "" + CNMGraphModel.getGraphOptions().getType());
                if (CNMGraphModel.getGraphOptions().getType().equalsIgnoreCase("Pie")) {
                    drawPieChart(CNMGraphModel);
                    noData.setVisibility(View.GONE);
                } else if (CNMGraphModel.getGraphOptions().getType().equalsIgnoreCase("VBar")) {
                    drawVerticalMPBarChart(CNMGraphModel);

                } else if (CNMGraphModel.getGraphOptions().getType().equalsIgnoreCase("HBar")) {
                    drawHorizontalBarChart(CNMGraphModel);
                    noData.setVisibility(View.GONE);
                } else if (CNMGraphModel.getGraphOptions().getType().equalsIgnoreCase("Table") || CNMGraphModel.getGraphOptions().getType().equalsIgnoreCase("None")) {
                    if (CNMGraphModel != null && CNMGraphModel.getGraphDataValuesModel().getCNMGraphDataValuesModels().size() != 0) {
                        drawTable(CNMGraphModel);
                    } else {
                        noData.setVisibility(View.VISIBLE);

                        noData.setText(CMN_ErrorMessages.getInstance().getValue(25));

                    }

                }
            }
        } else if (intent.hasExtra("nodata")) {
            noData.setText(CMN_ErrorMessages.getInstance().getValue(25));
            noData.setVisibility(View.VISIBLE);
        }

    }

    private void drawVerticalMPBarChart(CMN_GraphModel cnmGraphModel) {
        ArrayList<String> xLables = new ArrayList<>();
        List<CMN_GraphDataValuesModel> grprowset = cnmGraphModel.getGraphDataValuesModel().getCNMGraphDataValuesModels();
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        for (int i = 0; i < grprowset.size(); i++) {
            xLables.add(grprowset.get(i).getRowName());
            List<CMN_GraphDataRowValuesModel> graphvalues = grprowset.get(i).getCNMGraphDataRowValuesModelList();
            ArrayList<BarEntry> bargroup = new ArrayList<>();
            List<String> filterValue = new ArrayList<>();
            int size = grprowset.size();
            if(size>graphvalues.size()){
                size = graphvalues.size();
            }
            for (int j = 0; j < size; j++) {
                bargroup.add(new BarEntry(Float.parseFloat(graphvalues.get(j).getValue()), j));
                filterValue.add(graphvalues.get(j).getColumn());
            }
            try {
                BarDataSet barDataSet = new BarDataSet(bargroup, filterValue.get(i));
                String color = cnmGraphModel.getGraphOptions().getColors().get(i);
                if (!color.startsWith("#")) {
                    color = "#" + cnmGraphModel.getGraphOptions().getColors().get(i);
                }
                barDataSet.setColor(Color.parseColor(color));
                dataSets.add(barDataSet);
            } catch (Exception e) {
                e.getMessage();
            }
        }

        BarData data = new BarData(xLables, dataSets);
        barChart.setData(data);
        barChart.animate();
        barChart.setDescription("");
        barChart.zoomIn();
        barLayout.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }


    public void drawHorizontalBarChart(CMN_GraphModel CNMGraphModel) {
        if (CNMGraphModel.getGraphDataValuesModel().getCNMGraphDataValuesModels().size() == 0) {
            noData.setVisibility(View.VISIBLE);

            noData.setText(CMN_ErrorMessages.getInstance().getValue(25));

            return;
        } else {
            noData.setVisibility(View.GONE);
        }
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(CNMGraphModel.getGraphOptions().getTitle());
        hbar.setVisibility(View.VISIBLE);
        ArrayList<String> stringArrayList = new ArrayList<String>();

        for (int i = 0; i < CNMGraphModel.getGraphDataValuesModel().getCNMGraphDataValuesModels().size(); i++) {
            stringArrayList.add(CNMGraphModel.getGraphDataValuesModel().getCNMGraphDataValuesModels().get(i).getCNMGraphDataRowValuesModelList().get(0).getValue()); //add to arraylist
        }
        String[] patientIds = stringArrayList.toArray(new String[stringArrayList.size()]);

        ListView listView = (ListView) findViewById(R.id.list);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
                R.layout.horizontal_bar_graph_listitem, android.R.id.text1, patientIds);
        listView.setAdapter(adapter1);

        for (int i = 0; i < CNMGraphModel.getGraphDataValuesModel().getCNMGraphDataValuesModels().size(); i++) {
            boolean shouldMonthVisible = false;
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.horizontalview, null);
            final CMN_HorizontalView listView1 = (CMN_HorizontalView) view.findViewById(R.id.list);
            List<CMN_GraphDataRowValuesModel> models = new ArrayList<CMN_GraphDataRowValuesModel>();
            for (int j = 1; j < CNMGraphModel.getGraphDataValuesModel().getCNMGraphDataValuesModels().get(i).getCNMGraphDataRowValuesModelList().size(); j++) {
                CMN_GraphDataRowValuesModel valuesModel = new CMN_GraphDataRowValuesModel();
                valuesModel.setValue(CNMGraphModel.getGraphDataValuesModel().getCNMGraphDataValuesModels().get(i).getCNMGraphDataRowValuesModelList().get(j).getValue());
                valuesModel.setColumn(CNMGraphModel.getGraphDataValuesModel().getCNMGraphDataValuesModels().get(i).getCNMGraphDataRowValuesModelList().get(j).getColumn());
                models.add(valuesModel);
            }
            if (i == (CNMGraphModel.getGraphDataValuesModel().getCNMGraphDataValuesModels().size() - 1)) {
                shouldMonthVisible = true;
            } else {
                shouldMonthVisible = false;
            }
            CMN_HorizontalBarGraphAdapter adapter = new CMN_HorizontalBarGraphAdapter(CMN_DrawAnalyticsGraphActivityCMN.this, models, shouldMonthVisible);
            justifyListViewHeightBasedOnChildren(listView1);
            listView1.setAdapter(adapter);

            hBarLayout.addView(view);

        }


    }

    public void justifyListViewHeightBasedOnChildren(CMN_HorizontalView listView) {

        Adapter adapter = listView.getAdapter();

        if (adapter == null) {
            return;
        }
        ViewGroup vg = listView;
        int totalWidth = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0, 0);
            totalWidth += listItem.getMeasuredWidth();
        }
        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.width = totalWidth + ((adapter.getCount() - 1));
        listView.setLayoutParams(par);
        listView.requestLayout();
    }

    public void drawPieChart(CMN_GraphModel CNMGraphModel) {
        List<CMN_GraphDataRowValuesModel> lst = CNMGraphModel.getGraphDataValuesModel().getCNMGraphDataValuesModels().get(0).getCNMGraphDataRowValuesModelList();
        if (lst.size() == 0) {

            noData.setText(CMN_ErrorMessages.getInstance().getValue(25));

            noData.setVisibility(View.VISIBLE);
            return;
        } else {
            noData.setVisibility(View.GONE);
        }
        ArrayList<Integer> seriescolor = new ArrayList<Integer>();
        seriescolor.clear();
        int rcol = 60;
        int gcol = 130;
        int bcol = 230;
        for (int i = 0; i < lst.size(); i++) {
            seriescolor.add(Color.rgb(rcol, gcol, bcol));
            rcol = rcol + 60;
            gcol = gcol + 60;
            bcol = bcol + 60;
        }


        CategorySeries distributionSeries = new CategorySeries("Pie Chart");
        for (int i = 0; i < lst.size(); i++) {
            // Adding a slice with its values and name to the Pie Chart'
            //Log.e("val", "" + lst.get(i).getColumn() + "..." + lst.get(i).getValue());
            double val = Double.parseDouble(lst.get(i).getValue());
            distributionSeries.add(lst.get(i).getColumn(), val);
        }

        // Instantiating a renderer for the Pie Chart
        DefaultRenderer defaultRenderer = new DefaultRenderer();
        for (int i = 0; i < lst.size(); i++) {
            SimpleSeriesRenderer seriesRenderer = new SimpleSeriesRenderer();
            seriesRenderer.setColor(seriescolor.get(i));
            seriesRenderer.setDisplayChartValues(true);
            // Adding a renderer for a slice
            defaultRenderer.addSeriesRenderer(seriesRenderer);
        }

        defaultRenderer.setChartTitle("" + CNMGraphModel.getGraphOptions().getTitle());
        defaultRenderer.setChartTitleTextSize(20);
        defaultRenderer.setZoomButtonsVisible(true);
        defaultRenderer.setBackgroundColor(Color.WHITE);
        defaultRenderer.setApplyBackgroundColor(true);
        defaultRenderer.setLabelsTextSize(25);
        defaultRenderer.setLegendTextSize(25);

        GraphicalView gv = ChartFactory.getPieChartView(getBaseContext(), distributionSeries, defaultRenderer);

        pieLayout.addView(gv, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        pieLayout.setVisibility(View.VISIBLE);

    }

    /*
    public void drawDoughnutChart(ArrayList<String[]> lst)
    {
        textView.setVisibility(View.GONE);
        ArrayList<Integer> seriescolor = new ArrayList<Integer>() ;
    //	ArrayList<Integer> seriesvalue = new ArrayList<Integer>();
        ArrayList<Double> seriesvalue = new ArrayList<Double>();
        seriescolor.clear();
        seriesvalue.clear();
        String [] Distributionlabel=lst.get(0);
        String [] Distributionvalue= lst.get(1);
        int rcol=60;
        int gcol=130;
        int bcol=230;
        for(int i=0; i<lst.get(1).length; i++)
        {

            seriesvalue.add(Double.parseDouble(lst.get(1)[i]));
            seriescolor.add(Color.rgb(rcol, gcol, bcol));
            rcol=rcol+60;
            gcol=gcol+60;
            bcol=bcol+60;
        }


        MultipleCategorySeries distributionSeries = new MultipleCategorySeries("Doughnut Chart");
        for(int i=0 ;i < Distributionvalue.length;i++) {
            // Adding a slice with its values and name to the Pie Chart
        //	distributionSeries.add(Distributionlabel[i], seriesvalue.get(i));
        }

        // Instantiating a renderer for the Pie Chart
        DefaultRenderer defaultRenderer  = new DefaultRenderer();
        for(int i = 0 ;i<Distributionlabel.length;i++){
            SimpleSeriesRenderer seriesRenderer = new SimpleSeriesRenderer();
            seriesRenderer.setColor(seriescolor.get(i));
            seriesRenderer.setDisplayChartValues(true);
            // Adding a renderer for a slice
            defaultRenderer.addSeriesRenderer(seriesRenderer);
        }

        defaultRenderer.setChartTitle("Pie Chart ");
        defaultRenderer.setChartTitleTextSize(20);
        defaultRenderer.setZoomButtonsVisible(true);
        defaultRenderer.setBackgroundColor(Color.WHITE);
        defaultRenderer.setApplyBackgroundColor(true);
        defaultRenderer.setLabelsTextSize(25);
        defaultRenderer.setLegendTextSize(25);

                GraphicalView gv= ChartFactory.getDoughnutChartView(getBaseContext(), distributionSeries, defaultRenderer);

        pieLayout.addView(gv, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        pieLayout.setVisibility(View.VISIBLE);

    } */
    public void drawHorizontalBarChart(ArrayList<String[]> lst) {

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void drawTable(CMN_GraphModel CNMGraphModel) {
        if (CNMGraphModel.getGraphDataValuesModel().getCNMGraphDataValuesModels().size() == 0) {

            noData.setText(CMN_ErrorMessages.getInstance().getValue(25));

            noData.setVisibility(View.VISIBLE);
            return;
        } else {
            noData.setVisibility(View.GONE);
        }
        for (int i = 0; i < CNMGraphModel.getGraphDataValuesModel().getCNMGraphDataValuesModels().size(); i++) {


            List<CMN_GraphDataRowValuesModel> lst = CNMGraphModel.getGraphDataValuesModel().getCNMGraphDataValuesModels().get(i).getCNMGraphDataRowValuesModelList();

            tableLayout.setVisibility(View.VISIBLE);
            if (lst.size() == 0) {

                noData.setVisibility(View.VISIBLE);
                return;

            }
            TableRow row1 = new TableRow(this);
            TableRow row2 = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row1.setLayoutParams(lp);
            row2.setLayoutParams(lp);

            for (int j = 0; j < lst.size(); j++) {
                TextView textView1 = new TextView(this);
                textView1.setText(lst.get(j).getColumn());
                textView1.setTextColor(getResources().getColor(R.color.black));
                textView1.setTextSize(16);
                textView1.setGravity(Gravity.CENTER);
                textView1.setPadding(5, 5, 5, 5);
                textView1.setBackground(getResources().getDrawable(R.drawable.border));
                TextView textView2 = new TextView(this);
                textView2.setText(lst.get(j).getValue());
                textView2.setTextColor(getResources().getColor(R.color.black));
                textView2.setTextSize(16);
                textView2.setGravity(Gravity.CENTER);
                textView2.setPadding(5, 5, 5, 5);
                textView2.setBackground(getResources().getDrawable(R.drawable.border));
                row1.addView(textView1);
                row2.addView(textView2);
            }
            if (i == 0) {
                table.addView(row1);
            }
            table.addView(row2);
        }

        noData.setVisibility(View.GONE);
    }


}
