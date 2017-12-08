package com.coremobile.coreyhealth;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.coremobile.coreyhealth.aesencryption.CMN_AES;
import com.coremobile.coreyhealth.clientcertificate.CMN_HTTPSClient;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.loggerUtility.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import static com.coremobile.coreyhealth.Utils.convertStreamToString;

public class GetXML extends AsyncTask<String, Void, String> {
    private String TAG = "Corey_GetXML";
    Context mContext;
    private String mPurpose;
    ProgressDialog mDialog;
    Intent broadcastIntent, myIntent;
    IPullDataFromServer mService;
    String mStatus;
    private HashMap<String, String> mData = null;// post data
    boolean isPartialPull = false;

    GetXML(Context _context, String _purpose, IPullDataFromServer _service, HashMap<String, String> _data/*,Handler _handler*/) {
        mContext = _context;
        mPurpose = _purpose;
        mService = _service;
        mData = _data;
        MyApplication application = (MyApplication) mContext
                .getApplicationContext();
        //    broadcastIntent = new Intent("com.coremobile.coreyhealth.downloadcomplete");
        broadcastIntent = new Intent(application.AppConstants.getDownloadCompleteIntent());
        //   myIntent = new Intent("com.urbanairship.push.PUSH_RECEIVED");

    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        // TODO Auto-generated method stub
        /* Get a SAXParser from the SAXPArserFactory. */
        try {
            isPartialPull = Boolean.parseBoolean(params[1]);
        } catch (Exception e) {
            e.getMessage();
        }

        Log.d(TAG, "starting xml download");
        ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
        Iterator<String> it = mData.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            /*if(key == "messageid"){
                int value = Integer.parseInt(mData.get(key));
                nameValuePair.add(new BasicNameValuePair(key, value));
            }*/
            nameValuePair.add(new BasicNameValuePair(key, mData.get(key)));
            if (key != null && !key.equalsIgnoreCase("password")) {
                Log.d(TAG, key + " -> " + mData.get(key));
            } else {
                Log.d(TAG, key + " -> ****");
            }
        }
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp;
        XMLReader xr;
        CloseableHttpClient httpclient = new CMN_HTTPSClient(MyApplication.INSTANCE, params[0]).getClient();
        HttpResponse httpResponse = null;
        //HttpRequestBase httpReq = new HttpGet(url);
        HttpPost httpReq = new HttpPost(params[0]);

        InputStream inputStream = null;

        try {
            String urlString = params[0];
            URL url = new URL(urlString);
            Logger.Error(MyApplication.INSTANCE, "Get XML ", urlString);
            httpReq.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
            httpResponse = httpclient.execute(httpReq);


            inputStream = new TeeInputStream(httpResponse.getEntity().getContent());
            String result;
            if (AppConfig.isAESEnabled && isPartialPull) {
                result = CMN_AES.decryptData(convertStreamToString(inputStream), CMN_Preferences.getUserToken(mContext));
            } else {
                result = convertStreamToString(inputStream);
            }
            inputStream = new TeeInputStream(new ByteArrayInputStream(result.getBytes("utf-8")));

            Logger.Error(MyApplication.INSTANCE, "Get XML Response", result);
            sp = spf.newSAXParser();
                /* Get the XMLReader of the SAXParser we created. */
            xr = sp.getXMLReader();
//            xr.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            xr.setFeature("http://xml.org/sax/features/external-general-entities", false);
            xr.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
                /* Create a new ContentHandler and apply it to the XML-Reader*/
            if (mPurpose.equals("Analytics")) {
                XmlHandlerAnalytics myXmlHandler = new XmlHandlerAnalytics(mContext);
                xr.setContentHandler(myXmlHandler);
                Log.d(TAG, "Parser set to xmlhandlerAnalytics " + url);
            } else {
                XmlHandlerCoreySurge myXmlHandler = new XmlHandlerCoreySurge(mContext);
                xr.setContentHandler(myXmlHandler);
            }

            InputSource source;
//                if (mPurpose.equals("Auth")) {
//                    source = new InputSource(mContext.getResources().openRawResource(R.raw.details_correct));
//                } else {
            source = new InputSource(inputStream);
//                }

            source.setEncoding("utf-8");
            xr.parse(source);
            mStatus = "success";
            Log.d(TAG, "Parsing completed");
            //EntityUtils.consume(httpResponse.getEntity());

        } catch (ParserConfigurationException e1) {
            Log.d(TAG, "parserconfig exception");
            e1.getMessage();
        } catch (SAXException e1) {
            Log.d(TAG, "SAX exception");
            e1.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, "IO exception");
            e.getMessage();
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    e.getMessage();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.getMessage();
                }
            }
        }


        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        Log.d(TAG, "xml parsing complete");
        mService.finishedParsing(mStatus);

        //Log.d(TAG, "Msgtabactivty stat" + MyApplication.INSTANCE.MsgTabActivityState);
        //     mContext.sendBroadcast(myIntent);
        //Log.d(TAG, "calling back" + mService.toString());
        Intent intent = new Intent(mContext.getPackageName() + ".update");
        intent.putExtra("Status", "success");
        mContext.sendBroadcast(intent);
    }


}
