package com.coremobile.coreyhealth.analytics;

/**
 * Created by Akhilesh on 30/11/2015.
 */
public class CMN_NumericParser {
    private static CMN_NumericParser NUMERIC_PARSER = null;

    private CMN_NumericParser() {
    }

    public static CMN_NumericParser getNumericParser() {
        if (NUMERIC_PARSER == null)
            NUMERIC_PARSER = new CMN_NumericParser();

        return NUMERIC_PARSER;
    }

    /**
     * Method convert string value to int.
     *
     * @param intString
     * @return converted int to zero in case of exception
     */
    public int getInt(String intString) {
        int value = 0;
        // if (intString == null || intString.equals("null"))
        //   return value;

        try {
            if (intString.contains(".")) {
                value = (int) Float.parseFloat(intString);
            } else {
                value = Integer.parseInt(intString);
            }
        } catch (NumberFormatException e) {
            value = 0;
            //e.getMessage();
        } finally {

        }
        return value;
    }

    /**
     * Method convert string value to long.
     *
     * @param intString
     * @return converted long to zero in case of exception
     */

}
