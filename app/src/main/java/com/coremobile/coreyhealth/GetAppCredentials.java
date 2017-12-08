package com.coremobile.coreyhealth;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class GetAppCredentials extends AsyncTask<String, Void, JSONObject>
{
    String TAG = "Corey_GetAppCredentials";

    SharedPreferences mcurrentUserPref;
    public static final String CURRENT_USER = "CurrentUser";
    SharedPreferences.Editor editor;

    private boolean is_auth_xml = false;
    private boolean in_login = false;
    private boolean in_password = false;
    private boolean in_context = false;
    Context mContext;
    private IServerConnect mActivity = null;

    String ApplicationName = "";
    String Login = "";
    String Password;
    String Context = "";

    GetAppCredentials(Context _context, IServerConnect activity)
    {
        mContext = _context;
        mActivity = activity;
    }

    @Override
    protected JSONObject doInBackground(String... params)
    {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp;
        XMLReader xr;
        try
        {
            URL url = new URL(params[0]);
            sp = spf.newSAXParser();
            /* Get the XMLReader of the SAXParser we created. */
            xr = sp.getXMLReader();
            //xr.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING,true);
            xr.setFeature("http://xml.org/sax/features/external-general-entities", false);
            xr.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            /* Create a new ContentHandler and apply it to the XML-Reader */
            xmlhandler myXmlHandler = new xmlhandler();
            xr.setContentHandler(myXmlHandler);
            InputSource source = new InputSource(url.openStream());
            try
            {
                xr.parse(source);
                //Log.i(TAG, "Parsing completed");
            }
            catch (Exception e)
            {
                //Log.i(TAG, "XML returned null");
                e.getMessage();
            }
        }
        catch (ParserConfigurationException e1)
        {
            // TODO Auto-generated catch block
            e1.getMessage();
        }
        catch (SAXException e1)
        {
            // TODO Auto-generated catch block
            e1.getMessage();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.getMessage();
        }

        CredentialsJSONObject cJson = new CredentialsJSONObject();
        return cJson;

    }

    protected void onPreExecute()
    {
        //Log.e(TAG, "Creating Dialog");
        mActivity.showDialog();
    }
    protected void onPostExecute(JSONObject json)
    {
        //Log.v(TAG, "onPostExecute json: " + json);
        mActivity.closeDialog();
        mActivity.gotUserInfoFromServer(json);
    }

    private class xmlhandler extends DefaultHandler
    {

        public xmlhandler()
        {
            mcurrentUserPref = mContext.getSharedPreferences(CURRENT_USER, 0);
            editor = mcurrentUserPref.edit();
            LocalPrefs.INSTANCE.setHasAppCredentials(false);
        }



        @Override
        public void endDocument() throws SAXException
        {
            // TODO Auto-generated method stub
            super.endDocument();
            //Log.i(TAG,"endDocument");
        }



        @Override
        public void startDocument() throws SAXException
        {
            // TODO Auto-generated method stub
            super.startDocument();
            //Log.i(TAG,"startDocument");
        }



        @Override
        public void characters(char[] ch, int start, int length)
        throws SAXException
        {
            if (in_login)
            {
                if (is_auth_xml)
                {
                    Login = new String(ch, start, length);
                    Log.d(TAG,ApplicationName.toLowerCase() + "Login: " + Login);
                    editor.putString(ApplicationName.toLowerCase() + "Login", Login);
                    editor.commit();
                }

            }
            else if (in_password)
            {
                if (is_auth_xml)
                {
                    Password = new String(ch, start, length);
                    Log.d(TAG,ApplicationName.toLowerCase() + "Password: ****");
                    //editor.putString(ApplicationName.toLowerCase() + "Password", Password);
                    editor.commit();
                }

            }
            else if (in_context)
            {
                if (is_auth_xml)
                {
                    Context = new String(ch, start, length);
                    Log.d(TAG,ApplicationName.toLowerCase() + "Context: " + Context);
                    editor.putString(ApplicationName.toLowerCase() + "Context", Context);
                    editor.commit();
                }

            }
        }

        @Override
        public void endElement(String uri, String localName, String qName)
        throws SAXException
        {
            if (localName.equalsIgnoreCase("Application"))
            {
                is_auth_xml = false;
            }
            else if (localName.equalsIgnoreCase("Login"))
            {
                in_login = false;
            }
            else if (localName.equalsIgnoreCase("Password"))
            {
                in_password = false;
            }
            else if (localName.equalsIgnoreCase("Context"))
            {
                in_context = false;
            }
        }

        @Override
        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes) throws SAXException
        {
            if (localName.equalsIgnoreCase("Application"))
            {
                /* TODO:mahesh handle the case
                if (((MyApplication)(mActivity.getApplication())).getLastMessageid() == -1) {
                    mContext.sendBroadcast(new Intent("com.coremobile.corey.showdialog"));
                }
                */

                Log.d(TAG,"startElement Application Node");
                ApplicationName = attributes.getValue("Name");
                //editor.putBoolean("NewUser", false);
                LocalPrefs.INSTANCE.setHasAppCredentials(true);
                editor.putString("ApplicationName", ApplicationName);
                editor.commit();
                is_auth_xml = true;
            }
            else if (localName.equalsIgnoreCase("Login"))
            {
                in_login = true;
            }
            else if (localName.equalsIgnoreCase("Password"))
            {
                in_password = true;
            }
            else if (localName.equalsIgnoreCase("Context"))
            {
                in_context = true;
            }
        }

    }

}
