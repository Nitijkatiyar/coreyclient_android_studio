package com.coremobile.coreyhealth.analytics;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coremobile.coreyhealth.MyApplication;
import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.Utils;
import com.coremobile.coreyhealth.base.CMN_AppBaseActivity;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;


/**
 * @author Nitij Katiyar
 */
public class CMN_AnalyticsGraphActivityCMN extends CMN_AppBaseActivity implements CMN_IDownloadCSV {

    private SharedPreferences mCurrentUserPref;
    public static final String CURRENT_USER = "CurrentUser";
    String TAG = "Corey_CMN_AnalyticsGraphActivityCMN";
    private String organizationName;
    String password, context, user_category, context_name;
    String userName;
    Intent intent;
    TextView textView;
    CMN_AnalyticGraphModel graphModel;
    String APP_PATH;
    LinearLayout barLayout ;
    LinearLayout pieLayout;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyticsgraph);

        textView = (TextView) findViewById(R.id.text);
        barLayout = (LinearLayout) findViewById(R.id.bar_chart);
        //	barLayout.addView(getBarChart(), new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        pieLayout = (LinearLayout) findViewById(R.id.pi_chart);
        //	pieLayout.addView(getPieChart(), new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle(
                "" + getResources().getString(R.string.analytics));
        getActionBar().setDisplayHomeAsUpEnabled(true);
        intent = getIntent();
        if (intent.hasExtra("data")) {
            graphModel = (CMN_AnalyticGraphModel) intent
                    .getSerializableExtra("data");
    /*		textView.setText("" + CNMGraphModel.getDisplayText() + "\n"
					+ CNMGraphModel.getGraphType() + "\n"
					+ CNMGraphModel.getDataFile()); */

            APP_PATH = MyApplication.INSTANCE.AppConstants.getAppFilesPath();
            //Log.d(TAG, "csv file" + graphModel);

            if (graphModel.getGraphType().equalsIgnoreCase("Bar") ||
                    graphModel.getGraphType().equalsIgnoreCase("Pie")) {
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setTitle("Downloading Data");
                mProgressDialog.setMessage("Please Wait ..");
                mProgressDialog.setCancelable(false);
                mProgressDialog.getWindow().addFlags(
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                mProgressDialog.show();
                downloadCSV(graphModel.getDataFile());
            } else if (graphModel.getGraphType().equalsIgnoreCase("HorizontalBAR"))
                textView.setText(" This feature will be available in next release");
            else if (graphModel.getGraphType().equalsIgnoreCase("Table"))
                textView.setText(" This feature will be available in next release");

        }

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

    @Override
    public void buildUI(ArrayList<String[]> lst) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        if (graphModel.getGraphType().equalsIgnoreCase("Bar"))
            drawBarChart(lst);
        else if (graphModel.getGraphType().equalsIgnoreCase("Pie"))
            drawPieChart(lst);
        else if (graphModel.getGraphType().equalsIgnoreCase("HorizontalBAR")) {
        }
        //drawPieChart(lst);
        else if (graphModel.getGraphType().equalsIgnoreCase("Table")) {
        }
        //drawTable(lst);
    }

    protected void downloadCSV(String url) {


        if (Utils.checkInternetConnection()) {
            //Log.i(TAG, "start csv download");
            try {
                new CMN_GetCSV(this).execute(url);

            } catch (Exception e) {
                //Log.v(TAG, "CMN_GetCSV failed to download" + url);
            }
        }
    }

    public void drawBarChart(ArrayList<String[]> lst) {
        String[] Xaxislabel = {};
        textView.setVisibility(View.GONE);
        //	ArrayList<String> Xlabel = new ArrayList<String>();
        ArrayList<String> SeriesNames = new ArrayList<String>();

        ArrayList<ArrayList<Integer>> seriesdata = new ArrayList<ArrayList<Integer>>();
        ArrayList<XYSeries> SeriesArray = new ArrayList<XYSeries>();
        ArrayList<XYSeriesRenderer> RendererArray = new ArrayList<XYSeriesRenderer>();
        for (int i = 0; i < lst.size(); i++) {
            if (i == 0) Xaxislabel = lst.get(i);
            else {
                ArrayList<Integer> series = new ArrayList();
                //series.clear();
                for (int j = 0; j < lst.get(i).length; j++) {
                    //Log.d(TAG, "site values are =" + lst.get(i)[j]);
                    if (j != 0) {
                        int serval = Integer.parseInt(lst.get(i)[j]);
                        //Log.d(TAG, "Parsed value =" + serval);
                        series.add(Integer.parseInt(lst.get(i)[j]));
                    } else SeriesNames.add(lst.get(i)[j]);
                }
                seriesdata.add(series);
            }
        }
        for (ArrayList<Integer> sdata : seriesdata) {
            int len = sdata.size();
            for (int i = 0; i < len; i++) {
                //Log.d(TAG, "int are " + sdata.get(i));
            }
        }
        for (int i = 0; i < seriesdata.size(); i++) {
            //XYSeries ("series"+i) = new XYSeries("");
            SeriesArray.add(new XYSeries(SeriesNames.get(i)));
            for (int k = 0; k < seriesdata.get(i).size(); k++) {
                SeriesArray.get(i).add(k, seriesdata.get(i).get(k));
            }
        }
        // Creating a dataset to hold each series
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        int rcol = 60;
        int gcol = 130;
        int bcol = 230;
        for (XYSeries xys : SeriesArray) {

            dataset.addSeries(xys);
            XYSeriesRenderer xysr = new XYSeriesRenderer();
            xysr.setColor(Color.rgb(rcol, gcol, bcol));
            xysr.setFillPoints(true);
            xysr.setLineWidth(2);
            xysr.setDisplayChartValues(true);

            RendererArray.add(xysr);
            rcol = rcol + 60;
            gcol = gcol + 60;
            bcol = bcol + 60;
        }

        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setXLabels(0);
        multiRenderer.setChartTitle("Bar Chart");
        multiRenderer.setXTitle("months");
        multiRenderer.setYTitle("#patients");
        multiRenderer.setZoomButtonsVisible(true);
        multiRenderer.setLabelsTextSize(25);
        multiRenderer.setLegendTextSize(25);
        multiRenderer.setAxisTitleTextSize(25);
        multiRenderer.setShowGridX(true);
        //multiRenderer.setFitLegend(true);
        multiRenderer.setApplyBackgroundColor(true);
        multiRenderer.setBackgroundColor(Color.WHITE);
        multiRenderer.setMargins(new int[]{30, 30, 30, 30});

        for (int i = 0; i < Xaxislabel.length; i++) {
            multiRenderer.addXTextLabel(i, Xaxislabel[i]);
        }

        // Creating an intent to plot bar chart using dataset and multipleRenderer
        for (XYSeriesRenderer xyrend : RendererArray) {
            multiRenderer.addSeriesRenderer(xyrend);
        }
        GraphicalView gv = ChartFactory.getBarChartView(getApplicationContext(), dataset, multiRenderer, BarChart.Type.DEFAULT);
        barLayout.addView(gv, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        barLayout.setVisibility(View.VISIBLE);

    }

    public void drawPieChart(ArrayList<String[]> lst) {
        textView.setVisibility(View.GONE);
        ArrayList<Integer> seriescolor = new ArrayList<Integer>();
        ArrayList<Integer> seriesvalue = new ArrayList<Integer>();
        seriescolor.clear();
        seriesvalue.clear();
        String[] Distributionlabel = lst.get(0);
        String[] Distributionvalue = lst.get(1);
        int rcol = 60;
        int gcol = 130;
        int bcol = 230;
        for (int i = 0; i < lst.get(1).length; i++) {
            seriesvalue.add(Integer.parseInt(lst.get(1)[i]));
            seriescolor.add(Color.rgb(rcol, gcol, bcol));
            rcol = rcol + 60;
            gcol = gcol + 60;
            bcol = bcol + 60;
        }


        CategorySeries distributionSeries = new CategorySeries("Pie Chart");
        for (int i = 0; i < Distributionvalue.length; i++) {
            // Adding a slice with its values and name to the Pie Chart
            distributionSeries.add(Distributionlabel[i], seriesvalue.get(i));
        }

        // Instantiating a renderer for the Pie Chart
        DefaultRenderer defaultRenderer = new DefaultRenderer();
        for (int i = 0; i < Distributionlabel.length; i++) {
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

    public void drawTable(ArrayList<String[]> lst) {

    }


}
