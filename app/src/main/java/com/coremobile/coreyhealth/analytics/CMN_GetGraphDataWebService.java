package com.coremobile.coreyhealth.analytics;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

import com.coremobile.coreyhealth.MyApplication;
import com.coremobile.coreyhealth.clientcertificate.CMN_HTTPSClient;
import com.coremobile.coreyhealth.errorhandling.CMN_ErrorMessages;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.newui.MessageTabActivityCMN;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class CMN_GetGraphDataWebService extends AsyncTask<String, String, JSONObject> {
    private Activity mContext;
    ProgressDialog pbar;
    public static final String TAG = "Corey_GetGraphData";
    List<CMN_GraphDataRowValuesModel> CNMGraphDataRowValuesModels1;
    List<CMN_GraphDataValuesModel> CNMGraphDataValuesModels1;
    List<CMN_GraphDataRowValuesModel> CNMGraphDataRowValuesModels2;
    List<CMN_GraphDataValuesModel> CNMGraphDataValuesModels2;
    CMN_GraphModel graphDataModel;
    String title = "Analytics";


    public CMN_GetGraphDataWebService(Activity context) {
        mContext = context;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();


        pbar = new ProgressDialog(mContext);
        pbar.setCancelable(false);
        pbar.setMessage(CMN_ErrorMessages.getInstance().getValue(131));
        pbar.show();

    }

    @Override
    protected JSONObject doInBackground(String... params) {

        String url = null;
        try {
            url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "GetGraphData.aspx?token=" + URLEncoder.encode(CMN_Preferences.getUserToken(mContext), "UTF-8") + "&graphid=" + URLEncoder.encode(params[0], "UTF-8");
        } catch (UnsupportedEncodingException e) {
            //e.getMessage();
        }

        title = params[1];
        if (params[0].equalsIgnoreCase("")) {
            return null;
        }

        //Log.e(TAG, "url.." + url);
        CloseableHttpClient httpclient = new CMN_HTTPSClient(MyApplication.INSTANCE, url).getClient();
        JSONObject json = null;
        HttpResponse response = null;
        if (url != null) {
            HttpGet httpGet = new HttpGet(url);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("type", "web_server"));


            try {

                httpGet.setHeader("Content-Type",
                        "application/x-www-form-urlencoded");
                response = httpclient.execute(httpGet);
                //Log.e(TAG, "POST response: " + response.getStatusLine());
                json = new JSONObject(EntityUtils.toString(response.getEntity()));
                graphDataModel = new CMN_GraphModel();
                if (json.getJSONObject("Result").getInt("Code") == 0) {
                    JSONObject Data = json.getJSONObject("Data");
                    JSONObject option = Data.getJSONObject("Options");
                    CMN_GraphOptionsModel optionsModel = new CMN_GraphOptionsModel();
                    optionsModel.setTitle(option.getString("Title"));
                    optionsModel.setType(option.getString("Type"));
                    optionsModel.setXAxisTitle(option.getString("XAxisTitle"));
                    optionsModel.setYAxisTitle(option.getString("YAxisTitle"));
                    JSONArray graphColors = option.getJSONArray("Colors");
                    List<String> colors = new ArrayList<>();
                    for (int i = 0; i < graphColors.length(); i++) {
                        colors.add(graphColors.getString(i));
                    }
                    optionsModel.setColors(colors);
                    JSONObject GraphData = Data.getJSONObject("GraphData");
                    CMN_GraphDataModel dataModel = new CMN_GraphDataModel();
                    JSONArray values1 = GraphData.getJSONArray("Values");
                    CNMGraphDataValuesModels1 = new ArrayList<CMN_GraphDataValuesModel>();
                    for (int i = 0; i < values1.length(); i++) {
                        JSONObject object1 = values1.getJSONObject(i);
                        CMN_GraphDataValuesModel valuesModel1 = new CMN_GraphDataValuesModel();
                        valuesModel1.setRowName("" + object1.getString("RowName"));

                        CNMGraphDataRowValuesModels1 = new ArrayList<CMN_GraphDataRowValuesModel>();
                        JSONArray RowValues1 = object1.getJSONArray("RowValues");
                        for (int j = 0; j < RowValues1.length(); j++) {
                            JSONObject rowValuesObject1 = RowValues1.getJSONObject(j);
                            CMN_GraphDataRowValuesModel rowValuesModel1 = new CMN_GraphDataRowValuesModel();
                            rowValuesModel1.setColumn("" + rowValuesObject1.getString("Column"));
                            rowValuesModel1.setValue("" + rowValuesObject1.getString("Value"));
                            CNMGraphDataRowValuesModels1.add(rowValuesModel1);
                        }
                        valuesModel1.setCNMGraphDataRowValuesModelList(CNMGraphDataRowValuesModels1);
                        CNMGraphDataValuesModels1.add(valuesModel1);
                    }
                    dataModel.setCNMGraphDataValuesModels(CNMGraphDataValuesModels1);
                    graphDataModel.setGraphDataValuesModel(dataModel);

                    JSONObject DetailedGraphData = Data.getJSONObject("DetailedGraphData");
                    CMN_DetailGraphDataModel DetaileddataModel = new CMN_DetailGraphDataModel();
                    JSONArray Detailedvalues = DetailedGraphData.getJSONArray("Values");
                    CNMGraphDataValuesModels2 = new ArrayList<CMN_GraphDataValuesModel>();
                    for (int i = 0; i < Detailedvalues.length(); i++) {
                        JSONObject object2 = Detailedvalues.getJSONObject(i);
                        CMN_GraphDataValuesModel valuesModel2 = new CMN_GraphDataValuesModel();
                        valuesModel2.setRowName("" + object2.getString("RowName"));

                        CNMGraphDataRowValuesModels2 = new ArrayList<CMN_GraphDataRowValuesModel>();
                        JSONArray RowValues2 = object2.getJSONArray("RowValues");
                        for (int j = 0; j < RowValues2.length(); j++) {
                            JSONObject rowValuesObject2 = RowValues2.getJSONObject(j);
                            CMN_GraphDataRowValuesModel rowValuesModel2 = new CMN_GraphDataRowValuesModel();
                            rowValuesModel2.setColumn("" + rowValuesObject2.getString("Column"));
                            rowValuesModel2.setValue("" + rowValuesObject2.getString("Value"));
                            CNMGraphDataRowValuesModels2.add(rowValuesModel2);

                        }
                        valuesModel2.setCNMGraphDataRowValuesModelList(CNMGraphDataRowValuesModels2);
                        CNMGraphDataValuesModels2.add(valuesModel2);
                    }

                    DetaileddataModel.setCNMGraphDataValuesModels(CNMGraphDataValuesModels2);
                    graphDataModel.setGraphOptions(optionsModel);
                    graphDataModel.setDetailGraphDataValuesModel(DetaileddataModel);

                }
            } catch (JSONException e) {
                //e.getMessage();
            } catch (ClientProtocolException e) {
                //e.getMessage();
            } catch (IOException e) {
                //e.getMessage();
            } finally {
                if (httpclient != null) {
                    try {
                        httpclient.close();
                    } catch (IOException e) {
                        e.getMessage();
                    }
                }
            }
        }
        return json;

    }


    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        if (pbar != null && pbar.isShowing()) {
            pbar.dismiss();
        }
        Intent intent = new Intent(mContext, CMN_DrawAnalyticsGraphActivityCMN.class);
        if (graphDataModel.getDetailGraphDataValuesModel() != null && graphDataModel.getDetailGraphDataValuesModel().getCNMGraphDataValuesModels() != null && graphDataModel.getDetailGraphDataValuesModel().getCNMGraphDataValuesModels().size() > 0) {
            intent.putExtra("data", graphDataModel);
            intent.putExtra("title", title);
        } else {
            intent.putExtra("nodata", "nodata");
        }
        mContext.startActivity(intent);
    }

    private String getJSON(int rawID) {
        //TODO Remove this lines with actual scenario
        InputStream is = mContext.getResources().openRawResource(rawID);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException e) {
            //e.getMessage();
        } catch (IOException e) {
            //e.getMessage();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                //e.getMessage();
            }
        }

        String jsonString = writer.toString();
        return jsonString;
    }
}