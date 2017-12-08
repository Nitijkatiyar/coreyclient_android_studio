package com.coremobile.coreyhealth.googleAnalytics;

/**
 * This Interface is used to notify the fragment about  the connection of google fit.
 */
public interface GooglefitCallback {

    /**
     * when google fir is connected this method is called.
     */
    public void connect();

    /**
     * when google fit is disconnected this method is callled.
     */
    public void disconnect();
}
