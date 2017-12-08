package com.coremobile.coreyhealth.clientcertificate;

import android.content.Context;

import com.coremobile.coreyhealth.AppConfig;
import com.coremobile.coreyhealth.R;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;

public class CMN_HTTPSClient {

    final Context context;
    String url;

    public CMN_HTTPSClient(Context context, String url) {
        this.context = context;
        this.url = url;
    }

    public CloseableHttpClient getClient() {
        CloseableHttpClient httpClient = null;

        if (AppConfig.isClientCertEnabled) {
            if (url.toLowerCase().contains("registerdeviceid_s.aspx")) {
                KeyStore trusted = null;
                InputStream in = null;
                try {
                    trusted = KeyStore.getInstance("BKS");

                    in = context.getResources().openRawResource(R.raw.clientcert);

                    trusted.load(in, "coremobile".toCharArray());

                    SSLContext sslcontext = SSLContexts.custom()
                            .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                            .loadKeyMaterial(trusted, "coremobile".toCharArray())
                            .useTLS()
                            .build();

                    // Allow TLSv1 protocol only
                    SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                            sslcontext,
                            new String[]{"TLSv1"}, // supported protocols
                            null,  // supported cipher suites
                            SSLConnectionSocketFactory.STRICT_HOSTNAME_VERIFIER);

                    httpClient = HttpClients.custom()
                            .setSSLSocketFactory(sslsf)
                            .build();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (CertificateException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (UnrecoverableKeyException e) {
                    e.printStackTrace();
                } catch (KeyStoreException e) {
                    e.printStackTrace();
                } catch (KeyManagementException e) {
                    e.printStackTrace();
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                httpClient = HttpClients.createDefault();
            }
        } else {
            httpClient = HttpClients.createDefault();
        }
        return httpClient;

    }
}