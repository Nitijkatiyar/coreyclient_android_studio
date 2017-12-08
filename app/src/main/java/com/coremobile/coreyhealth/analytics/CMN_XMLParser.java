package com.coremobile.coreyhealth.analytics;

import android.content.Context;
import android.widget.Toast;

import com.coremobile.coreyhealth.MyApplication;
import com.coremobile.coreyhealth.clientcertificate.CMN_HTTPSClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class CMN_XMLParser {

    public String getXmlFromUrl(String url) {
        String xml = null;
        CloseableHttpClient httpclient = null;
        try {
            httpclient = new CMN_HTTPSClient(MyApplication.INSTANCE, url).getClient();
            HttpPost httpPost = new HttpPost(url);
            HttpResponse httpResponse = httpclient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            xml = EntityUtils.toString(httpEntity);
        } catch (UnsupportedEncodingException e) {
            //Log.e("XmlParser", "Unsupported encoding", e);
        } catch (ClientProtocolException e) {
            //Log.e("XmlParser", "ClientProtocolException", e);
        } catch (IOException e) {
            //Log.e("XmlParser", "IOException", e);
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    e.getMessage();
                }
            }
        }
        return xml;
    }

    public Document getDomElement(String xml, Context mContext) throws ParserConfigurationException {
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        //dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        //dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        //dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            doc = db.parse(is);
            return doc;
        } catch (ParserConfigurationException e) {
            Toast.makeText(mContext, "1", Toast.LENGTH_SHORT).show();
        } catch (SAXException e) {
            Toast.makeText(mContext, "2", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(mContext, "3", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(mContext, "4", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    public String getValue(Element item, String str) {
        NodeList n = item.getElementsByTagName(str);
        //Log.e("value", "value." + this.getElementValue(n.item(0)));
        return this.getElementValue(n.item(0));
    }

    public String getElementValue(Node elem) {
        Node child = null;
        if (elem != null) {
            if (elem.hasChildNodes()) {
                for (child = elem.getFirstChild(); child != null; child = elem
                        .getNextSibling()) {
                    if (child.getNodeType() == Node.TEXT_NODE) {
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }
}